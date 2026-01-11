package de.teamlapen.vampirism.common.world.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IEntityInteractable {

    InteractionResult onEntityInteract(ItemStack stack, Entity target, Player player, Level level, InteractionHand hand);
}
