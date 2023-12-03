package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DrinkBloodContext implements IDrinkBloodContext {

    private static final DrinkBloodContext NONE = new DrinkBloodContext();
    @Nullable
    private LivingEntity entity;
    @Nullable
    private ItemStack stack;
    @Nullable
    private BlockState blockState;
    @Nullable
    private BlockPos blockPos;

    public DrinkBloodContext(@NotNull LivingEntity entity) {
        this.entity = entity;
    }

    public DrinkBloodContext(@NotNull ItemStack stack) {
        this.stack = stack;
    }

    public DrinkBloodContext(@NotNull BlockState blockState, @NotNull BlockPos blockPos) {
        this.blockState = blockState;
        this.blockPos = blockPos;
    }

    private DrinkBloodContext() {
    }

    public static DrinkBloodContext none() {
        return NONE;
    }

    @Override
    public Optional<LivingEntity> getEntity() {
        return Optional.ofNullable(entity);
    }

    @Override
    public Optional<ItemStack> getStack() {
        return Optional.ofNullable(stack);
    }

    @Override
    public Optional<BlockState> getBlockState() {
        return Optional.ofNullable(blockState);
    }

    @Override
    public Optional<BlockPos> getBlockPos() {
        return Optional.ofNullable(blockPos);
    }
}
