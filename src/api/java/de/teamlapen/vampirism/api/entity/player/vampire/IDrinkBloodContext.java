package de.teamlapen.vampirism.api.entity.player.vampire;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

/**
 * Context for {@link de.teamlapen.vampirism.api.entity.vampire.IVampire#drinkBlood(int, float, boolean, IDrinkBloodContext)}
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

    @Deprecated(forRemoval = true)
    static IDrinkBloodContext none() {
        return new IDrinkBloodContext() {
            @Override
            public Optional<LivingEntity> getEntity() {
                return Optional.empty();
            }

            @Override
            public Optional<ItemStack> getStack() {
                return Optional.empty();
            }

            @Override
            public Optional<BlockState> getBlockState() {
                return Optional.empty();
            }

            @Override
            public Optional<BlockPos> getBlockPos() {
                return Optional.empty();
            }
        };
    }
}
