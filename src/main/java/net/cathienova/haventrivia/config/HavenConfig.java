package net.cathienova.haventrivia.config;

import net.cathienova.haventrivia.HavenTrivia;
import net.minecraftforge.fml.config.ModConfig;

public class HavenConfig
{
    public static String prefix;
    public static int interval;
    public static int maxTime;
    public static int minPlayers;

    public static void bake(ModConfig config)
    {
        prefix = HavenTrivia.c_config.prefix.get();
        interval = HavenTrivia.c_config.interval.get();
        maxTime = HavenTrivia.c_config.maxTime.get();
        minPlayers = HavenTrivia.c_config.minPlayers.get();
    }
}
