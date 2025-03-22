package net.cathienova.haventrivia.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.cathienova.haventrivia.HavenTrivia;
import net.cathienova.haventrivia.config.HavenConfig;
import net.cathienova.haventrivia.util.MessagesUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trivia")
                .then(Commands.literal("about")
                        .executes(ctx -> {
                            var server = ctx.getSource().getServer();
                            var pool = HavenTrivia.triviaManager.getQuestionPool();
                            var rewards = HavenTrivia.rewardManager.getRewardTotals();

                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.about.header"), false);

                            for (String category : pool.keySet()) {
                                int qCount = pool.get(category).size();
                                int rCount = rewards.getOrDefault(category, 0);

                                ctx.getSource().sendSuccess(() ->
                                        MessagesUtil.message("message.haventrivia.trivia.about.entry",
                                                "{category}", category,
                                                "{questions}", String.valueOf(qCount),
                                                "{rewards}", String.valueOf(rCount)), false);
                            }

                            return 1;
                        }))
                .then(Commands.literal("leaderboard")
                        .executes(ctx -> {
                            var server = ctx.getSource().getServer();
                            var top = HavenTrivia.leaderboardManager.getTop(10);

                            if (top.isEmpty()) {
                                ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.noscores"), false);
                                return 1;
                            }

                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.leaderboard"), false);
                            for (var entry : top) {
                                var name = server.getProfileCache().get(entry.getKey())
                                        .map(p -> p.getName())
                                        .orElse("Unknown");
                                ctx.getSource().sendSuccess(() ->
                                        MessagesUtil.message("message.haventrivia.trivia.score",
                                                "{player}", name,
                                                "{score}", String.valueOf(entry.getValue())), false);
                            }
                            return 1;
                        }))
                .then(Commands.literal("add")
                        .requires(ModCommands::hasPermission)
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("all");
                                    ctx.getSource().getServer().getPlayerList().getPlayers()
                                            .forEach(p -> builder.suggest(p.getName().getString()));
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String target = StringArgumentType.getString(ctx, "target");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            var server = ctx.getSource().getServer();

                                            if (target.equalsIgnoreCase("all")) {
                                                var players = server.getPlayerList().getPlayers();
                                                players.forEach(p -> {
                                                    for (int i = 0; i < amount; i++) {
                                                        HavenTrivia.leaderboardManager.addPoint(p);
                                                    }
                                                });
                                                ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.added.all",
                                                        "{points}", String.valueOf(amount)), false);
                                                return 1;
                                            }

                                            ServerPlayer player = server.getPlayerList().getPlayers().stream()
                                                    .filter(p -> p.getName().getString().equalsIgnoreCase(target))
                                                    .findFirst().orElse(null);
                                            if (player == null) {
                                                ctx.getSource().sendFailure(MessagesUtil.message("message.haventrivia.trivia.notfound", "{player}", target));
                                                return 0;
                                            }

                                            for (int i = 0; i < amount; i++) {
                                                HavenTrivia.leaderboardManager.addPoint(player);
                                            }
                                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.added",
                                                    "{player}", target,
                                                    "{points}", String.valueOf(amount)), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("remove")
                        .requires(ModCommands::hasPermission)
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("all");
                                    HavenTrivia.leaderboardManager.getTop(100).forEach(entry -> {
                                        var server = ctx.getSource().getServer();
                                        var name = server.getProfileCache().get(entry.getKey()).map(p -> p.getName()).orElse(null);
                                        if (name != null) builder.suggest(name);
                                    });
                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String target = StringArgumentType.getString(ctx, "target");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            var server = ctx.getSource().getServer();

                                            if (target.equalsIgnoreCase("all")) {
                                                var top = HavenTrivia.leaderboardManager.getTop(100);
                                                for (var entry : top) {
                                                    HavenTrivia.leaderboardManager.removePoints(entry.getKey(), amount);
                                                }
                                                ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.removed.all",
                                                        "{points}", String.valueOf(amount)), false);
                                                return 1;
                                            }

                                            var profile = server.getProfileCache().get(target);
                                            if (profile.isEmpty()) {
                                                ctx.getSource().sendFailure(MessagesUtil.message("message.haventrivia.trivia.notfound", "{player}", target));
                                                return 0;
                                            }

                                            HavenTrivia.leaderboardManager.removePoints(profile.get().getId(), amount);
                                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.removed",
                                                    "{player}", target,
                                                    "{points}", String.valueOf(amount)), false);
                                            return 1;
                                        }))))
                .then(Commands.literal("reset")
                        .requires(ModCommands::hasPermission)
                        .then(Commands.argument("target", StringArgumentType.word())
                                .suggests((ctx, builder) -> {
                                    builder.suggest("all");
                                    HavenTrivia.leaderboardManager.getTop(100).forEach(entry -> {
                                        var server = ctx.getSource().getServer();
                                        var name = server.getProfileCache().get(entry.getKey()).map(p -> p.getName()).orElse(null);
                                        if (name != null) builder.suggest(name);
                                    });
                                    return builder.buildFuture();
                                })
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target");
                                    var server = ctx.getSource().getServer();

                                    if (target.equalsIgnoreCase("all")) {
                                        HavenTrivia.leaderboardManager.resetAll();
                                        ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.reset.all"), false);
                                        return 1;
                                    }

                                    var profile = server.getProfileCache().get(target);
                                    if (profile.isEmpty()) {
                                        ctx.getSource().sendFailure(MessagesUtil.message("message.haventrivia.trivia.notfound", "{player}", target));
                                        return 0;
                                    }

                                    HavenTrivia.leaderboardManager.reset(profile.get().getId());
                                    ctx.getSource().sendSuccess(() -> MessagesUtil.message("message.haventrivia.trivia.reset.single",
                                            "{player}", target), false);
                                    return 1;
                                })))
                .then(Commands.literal("start")
                        .requires(ModCommands::hasPermission)
                        .executes(ctx -> {
                            if (HavenConfig.interval <= 0) {
                                ctx.getSource().sendFailure(MessagesUtil.message("command.haventrivia.disabled"));
                                return 0;
                            }
                            HavenTrivia.triviaManager.stopAuto();
                            HavenTrivia.triviaManager.startAuto(ctx.getSource().getServer());
                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("command.haventrivia.start"), false);
                            return 1;
                        }))
                .then(Commands.literal("ask")
                        .requires(ModCommands::hasPermission)
                        .executes(ctx -> {
                            HavenTrivia.triviaManager.start(ctx.getSource().getServer());
                            //ctx.getSource().sendSuccess(() -> MessagesUtil.message("command.haventrivia.ask"), false);
                            return 1;
                        }))
                .then(Commands.literal("stop")
                        .requires(ModCommands::hasPermission)
                        .executes(ctx -> {
                            HavenTrivia.triviaManager.stopAuto();
                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("command.haventrivia.stop"), false);
                            return 1;
                        }))
                .then(Commands.literal("timeout")
                        .requires(ModCommands::hasPermission)
                        .executes(ctx -> {
                            HavenTrivia.triviaManager.timeout(ctx.getSource().getServer());
                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("command.haventrivia.timeout"), false);
                            return 1;
                        }))
                .then(Commands.literal("reload")
                        .requires(ModCommands::hasPermission)
                        .executes(ctx -> {
                            HavenTrivia.triviaManager.loadQuestions(ctx.getSource().getServer());
                            HavenTrivia.rewardManager.loadRewards(ctx.getSource().getServer());
                            ctx.getSource().sendSuccess(() -> MessagesUtil.message("command.haventrivia.reload"), false);
                            return 1;
                        }))
        );
    }

    private static boolean hasPermission(CommandSourceStack source) {
        if (!(source.getEntity() instanceof ServerPlayer player)) return source.hasPermission(2);
        try {
            return PermissionAPI.getPermission(player, TriviaPermissions.TRIVIA_ADMIN);
        } catch (Exception e) {
            return source.hasPermission(2);
        }
    }
}
