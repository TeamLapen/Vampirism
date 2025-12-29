package de.teamlapen.vampirism.api.world.entity.player.vampire;

import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Context for {@link IVampire#drinkBlood(int, float, boolean, IDrinkBloodContext)}
 */
public interface IDrinkBloodContext {

    /**
     * @return The entity that is the source of the blood if the blood is obtained from an entity
     */
    Optional<LivingEntity> getEntity();

    /**
     * @return The itemstack that is the source of the blood if the blood is obtained from an item
     */
    Optional<ItemStack> getStack();

    /**
     * @return The blockstate that is the source of the blood if the blood is obtained from a block
     */
    Optional<BlockState> getBlockState();

    /**
     * @return The blockpos that is the source of the blood if the blood is obtained from a block
     */
    Optional<BlockPos> getBlockPos();

    /**
     * @return Whether the player should have the extra blood that they haven't drunk poured into glass bottles in their inventory
     */
    boolean returnsSpareBlood();

    IDrinkBloodContext setReturnsSpareBlood(boolean returnsSpareBlood);
}
