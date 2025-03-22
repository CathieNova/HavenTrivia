package net.cathienova.haventrivia.trivia;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class LeaderboardManager {
    private final Map<UUID, Integer> scores = new HashMap<>();
    private File leaderboardFile;

    public void load(MinecraftServer server) {
        Path dir = server.getWorldPath(LevelResource.ROOT).resolve("serverconfig/haventrivia");
        leaderboardFile = dir.resolve("leaderboard.json").toFile();
        scores.clear();

        if (!dir.toFile().exists()) dir.toFile().mkdirs();
        if (!leaderboardFile.exists()) save();

        try (FileReader reader = new FileReader(leaderboardFile)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                scores.put(UUID.fromString(entry.getKey()), entry.getValue().getAsInt());
            }
        } catch (IOException ignored) {}
    }

    public void save() {
        if (leaderboardFile == null) return;

        JsonObject root = new JsonObject();
        for (var entry : scores.entrySet()) {
            root.addProperty(entry.getKey().toString(), entry.getValue());
        }

        try (FileWriter writer = new FileWriter(leaderboardFile)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(root, writer);
        } catch (IOException ignored) {}
    }

    public void addPoint(ServerPlayer player) {
        scores.merge(player.getUUID(), 1, Integer::sum);
        save();
    }

    public List<Map.Entry<UUID, Integer>> getTop(int limit) {
        return scores.entrySet().stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .limit(limit)
                .toList();
    }

    public int getScore(UUID uuid) {
        return scores.getOrDefault(uuid, 0);
    }

    public void removePoints(UUID uuid, int amount) {
        scores.merge(uuid, -amount, Integer::sum);
        if (scores.get(uuid) <= 0) scores.remove(uuid);
        save();
    }

    public void reset(UUID uuid) {
        scores.remove(uuid);
        save();
    }

    public void resetAll() {
        scores.clear();
        save();
    }

}
