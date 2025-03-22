package net.cathienova.haventrivia.listener;

import net.cathienova.haventrivia.HavenTrivia;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraft.server.level.ServerPlayer;

public class ChatListener {
    @SubscribeEvent
    public void onPlayerChat(ServerChatEvent event) {
        ServerPlayer player = event.getPlayer();
        String message = event.getMessage().getString();

        if (HavenTrivia.triviaManager.answer(player.getServer(), player, message))
        {
            event.setCanceled(true);
        }
    }
}
