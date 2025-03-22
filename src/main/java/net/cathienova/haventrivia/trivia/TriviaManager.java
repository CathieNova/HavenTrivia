package net.cathienova.haventrivia.trivia;

import com.google.gson.*;
import net.cathienova.haventrivia.HavenTrivia;
import net.cathienova.haventrivia.config.HavenConfig;
import net.cathienova.haventrivia.reward.Reward;
import net.cathienova.haventrivia.util.MessagesUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;

import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class TriviaManager {
    private final List<TriviaQuestion> pool = new ArrayList<>();
    private TriviaQuestion current = null;
    private long startTime = 0;
    private Timer autoTimer;
    private Timer questionTimeoutTimer;

    public void loadQuestions(MinecraftServer server) {
        pool.clear();
        Path dir = server.getWorldPath(LevelResource.ROOT).resolve("serverconfig/haventrivia");
        File file = dir.resolve("questions.json").toFile();

        if (!dir.toFile().exists()) dir.toFile().mkdirs();

        if (!file.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/config/haventrivia/questions.json");
                 OutputStream out = new FileOutputStream(file)) {
                if (in != null) in.transferTo(out);
            } catch (IOException e) {
                HavenTrivia.Log("Failed to generate default questions.json");
            }
        }

        try (FileReader reader = new FileReader(file)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            for (String category : root.keySet()) {
                for (JsonElement e : root.getAsJsonArray(category)) {
                    JsonObject obj = e.getAsJsonObject();
                    String q = obj.get("question").getAsString();
                    List<String> a = new ArrayList<>();
                    for (JsonElement ans : obj.getAsJsonArray("answers"))
                        a.add(ans.getAsString().toLowerCase());
                    pool.add(new TriviaQuestion(q, a, category));
                }
            }
        } catch (Exception e) {
            HavenTrivia.Log("Failed to load trivia questions");
        }
        HavenTrivia.leaderboardManager.load(server);
    }

    public void timeout(MinecraftServer server) {
        if (current == null) return;

        MessagesUtil.broadcast(server, "trivia.timeout",
                               "{prefix}", HavenConfig.prefix,
                               "{answer}", String.join(", ", current.answers));

        current = null;

        if (questionTimeoutTimer != null) {
            questionTimeoutTimer.cancel();
            questionTimeoutTimer = null;
        }
    }

    public boolean answer(MinecraftServer server, ServerPlayer player, String input) {
        if (current == null) return false;

        String guess = input.trim().toLowerCase();

        for (String a : current.answers) {
            if (a.equalsIgnoreCase(guess)) {
                long time = (System.currentTimeMillis() - startTime) / 1000;

                Reward reward = HavenTrivia.rewardManager.giveReward(player, current.category);

                MessagesUtil.broadcast(server, "trivia.correct",
                                       "{prefix}", HavenConfig.prefix,
                                       "{player}", player.getName().getString(),
                                       "{answer}", a,
                                       "{time}", String.valueOf(time));

                if (reward != null) {
                    player.sendSystemMessage(MessagesUtil.message("message.haventrivia.trivia.reward",
                                                                  "{reward}", reward.displayName));
                }

                HavenTrivia.leaderboardManager.addPoint(player);
                current = null;

                if (questionTimeoutTimer != null) {
                    questionTimeoutTimer.cancel();
                    questionTimeoutTimer = null;
                }

                return true;
            }
        }
        return false;
    }

    public void start(MinecraftServer server) {
        if (current != null || pool.isEmpty()) return;

        int minPlayersRequired = HavenConfig.minPlayers;
        int onlinePlayers = server.getPlayerList().getPlayers().size();

        if (onlinePlayers < minPlayersRequired) {
            //HavenTrivia.Log("Not enough players online (min: " + minPlayersRequired + "). Trivia won't start.");
            return;
        }

        current = pool.get(new Random().nextInt(pool.size()));
        startTime = System.currentTimeMillis();

        MessagesUtil.broadcast(server, "trivia.ask",
                               "{prefix}", HavenConfig.prefix,
                               "{question}", current.question);

        if (questionTimeoutTimer != null) questionTimeoutTimer.cancel();

        questionTimeoutTimer = new Timer();
        questionTimeoutTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                timeout(server);
            }
        }, HavenConfig.maxTime * 1000L);
    }

    public void startAuto(MinecraftServer server) {
        stopAuto();
        loadQuestions(server);

        int interval = HavenConfig.interval;
        if (interval <= 0) return;

        autoTimer = new Timer();
        autoTimer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                int minPlayersRequired = HavenConfig.minPlayers;
                int onlinePlayers = server.getPlayerList().getPlayers().size();

                if (onlinePlayers < minPlayersRequired) {
                    //HavenTrivia.Log("Not enough players online (min: " + minPlayersRequired + "). Trivia waiting for players...");
                    return;
                }
                start(server);
            }
        }, interval * 1000L, interval * 1000L);
    }

    public void stopAuto() {
        if (autoTimer != null) autoTimer.cancel();
        if (questionTimeoutTimer != null) questionTimeoutTimer.cancel();
    }

    public Map<String, List<TriviaQuestion>> getQuestionPool() {
        Map<String, List<TriviaQuestion>> categorized = new LinkedHashMap<>();
        for (TriviaQuestion q : pool) {
            categorized.computeIfAbsent(q.category, k -> new ArrayList<>()).add(q);
        }
        return categorized;
    }
}
