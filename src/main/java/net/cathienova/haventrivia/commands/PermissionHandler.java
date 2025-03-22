package net.cathienova.haventrivia.commands;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;

public class PermissionHandler {
    @SubscribeEvent
    public void registerPermissions(PermissionGatherEvent.Nodes event) {
        event.addNodes(TriviaPermissions.TRIVIA_ADMIN);
    }
}