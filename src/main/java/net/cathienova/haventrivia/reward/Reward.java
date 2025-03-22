package net.cathienova.haventrivia.reward;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class Reward {
    public final String itemName;
    public final String displayName;
    public final int quantity;
    public final ItemStack item;
    public final String command;
    public final String rewardText;

    public Reward(String itemName, String displayName, int quantity) {
        this.itemName = itemName;
        this.displayName = displayName;
        this.quantity = quantity;
        this.command = null;
        this.rewardText = displayName;

        var base = BuiltInRegistries.ITEM.getOptional(new ResourceLocation(itemName)).orElse(Items.AIR);
        this.item = base == Items.AIR ? null : new ItemStack(base, quantity);
        if (item != null && !displayName.isEmpty())
            item.setHoverName(Component.literal(displayName));
    }

    public Reward(String command, String displayName) {
        this.command = command;
        this.displayName = displayName;
        this.itemName = null;
        this.quantity = 0;
        this.item = null;
        this.rewardText = displayName;
    }
}
