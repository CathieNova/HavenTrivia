package net.cathienova.haventrivia.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class CommonConfig {
    public final ForgeConfigSpec.ConfigValue<String> prefix;
    public final ForgeConfigSpec.IntValue interval;
    public final ForgeConfigSpec.IntValue maxTime;
    public final ForgeConfigSpec.IntValue minPlayers;

    public CommonConfig(ForgeConfigSpec.Builder builder) {
        prefix = builder.comment("Prefix for trivia messages")
                .define("prefix", "§6[§5Haven§2Trivia§6] §r");

        interval = builder.comment("Interval in seconds between trivia questions")
                .defineInRange("interval", 300, 10, Integer.MAX_VALUE);

        maxTime = builder.comment("Maximum time in seconds to answer a trivia question")
                .defineInRange("maxTime", 30, 1, 999);

        minPlayers = builder.comment("Minimum number of players required to start a trivia question")
                .defineInRange("minPlayers", 1, 1, Integer.MAX_VALUE);
    }
}
