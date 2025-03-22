package net.cathienova.haventrivia.commands;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;
import net.minecraftforge.server.permission.nodes.PermissionTypes;

import java.util.UUID;

public class TriviaPermissions {
    public static final PermissionNode<Boolean> TRIVIA_ADMIN = new PermissionNode<>(
            new ResourceLocation("haventrivia", "admin"),
            PermissionTypes.BOOLEAN,
            (ServerPlayer player, UUID uuid, PermissionDynamicContext<?>... context) -> {
                return player != null && player.hasPermissions(2);
            }
    );
}