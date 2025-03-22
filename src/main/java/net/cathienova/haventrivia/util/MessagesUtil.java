package net.cathienova.haventrivia.util;

import net.cathienova.haventrivia.config.HavenConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;

public class MessagesUtil {
    public static void broadcast(MinecraftServer server, String key, String... replacements) {
        Component msg = Component.literal(resolve(key, replacements));
        server.getPlayerList().broadcastSystemMessage(msg, false);
    }

    private static String resolve(String key, String... replacements) {
        String prefix = HavenConfig.prefix;
        String msg = Component.translatable("message.haventrivia." + key).getString();
        for (int i = 0; i + 1 < replacements.length; i += 2)
            msg = msg.replace(replacements[i], replacements[i + 1]);
        return prefix + msg;
    }

    public static Component message(String key, String... replacements) {
        String prefix = HavenConfig.prefix;
        String raw = Component.translatable(key).getString();
        for (int i = 0; i + 1 < replacements.length; i += 2)
            raw = raw.replace(replacements[i], replacements[i + 1]);
        return Component.literal(prefix + raw);
    }
}
