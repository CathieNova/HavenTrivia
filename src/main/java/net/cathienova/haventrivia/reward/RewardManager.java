package net.cathienova.haventrivia.reward;

import com.google.gson.*;
import net.cathienova.haventrivia.HavenTrivia;
import net.cathienova.haventrivia.util.MessagesUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.registries.ForgeRegistries;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class RewardManager {
    private final Map<String, List<Reward>> items = new HashMap<>();
    private final Map<String, List<Reward>> commands = new HashMap<>();

    public void loadRewards(MinecraftServer server) {
        items.clear();
        commands.clear();

        Path dir = server.getWorldPath(LevelResource.ROOT).resolve("serverconfig/haventrivia");
        File file = dir.resolve("rewards.json").toFile();

        if (!dir.toFile().exists()) dir.toFile().mkdirs();

        if (!file.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/config/haventrivia/rewards.json");
                 OutputStream out = new FileOutputStream(file)) {
                if (in != null) in.transferTo(out);
            } catch (IOException e) {
                HavenTrivia.Log("Failed to generate default rewards.json");
            }
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (String category : root.keySet()) {
                JsonObject obj = root.getAsJsonObject(category);

                List<Reward> itemList = new ArrayList<>();
                for (JsonElement e : obj.getAsJsonArray("items")) {
                    JsonObject itemObj = e.getAsJsonObject();
                    String itemName = itemObj.get("item_name").getAsString();
                    var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemName));
                    if (item != Items.AIR) {
                        itemList.add(new Reward(
                                itemName,
                                itemObj.get("display_name").getAsString(),
                                itemObj.get("quantity").getAsInt()
                        ));
                    }
                }
                items.put(category, itemList);

                List<Reward> cmdList = new ArrayList<>();
                for (JsonElement e : obj.getAsJsonArray("commands")) {
                    JsonObject cmdObj = e.getAsJsonObject();
                    cmdList.add(new Reward(
                            cmdObj.get("command").getAsString(),
                            cmdObj.get("display_name").getAsString()
                    ));
                }
                commands.put(category, cmdList);
            }
        } catch (Exception e) {
            HavenTrivia.Log("Failed to load rewards.json");
        }
    }

    public Reward giveReward(ServerPlayer player, String category) {
        var itemList = items.getOrDefault(category, List.of());
        var cmdList = commands.getOrDefault(category, List.of());
        Random rand = new Random();

        boolean useItem = !itemList.isEmpty() && (cmdList.isEmpty() || rand.nextBoolean());

        Reward selectedReward = null;

        if (useItem) {
            selectedReward = itemList.get(rand.nextInt(itemList.size()));
            if (selectedReward.item != null) {
                ItemStack itemStack = selectedReward.item.copy();
                if (!player.getInventory().add(itemStack)) {
                    player.drop(itemStack, false);
                }
            }
        } else if (!cmdList.isEmpty()) {
            selectedReward = cmdList.get(rand.nextInt(cmdList.size()));
            String parsed = selectedReward.command.replace("{player}", player.getName().getString());
            player.getServer().getCommands().performPrefixedCommand(player.createCommandSourceStack(), parsed);
        }

        return selectedReward;
    }

    public Map<String, Integer> getRewardTotals() {
        Map<String, Integer> totals = new HashMap<>();
        for (String category : items.keySet()) {
            int itemCount = items.getOrDefault(category, List.of()).size();
            int commandCount = commands.getOrDefault(category, List.of()).size();
            totals.put(category, itemCount + commandCount);
        }
        return totals;
    }
}
