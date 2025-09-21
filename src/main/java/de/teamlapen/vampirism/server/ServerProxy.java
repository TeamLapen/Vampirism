package de.teamlapen.vampirism.server;

import de.teamlapen.vampirism.common.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.blocks.TentBlock;
import de.teamlapen.vampirism.common.proxy.CommonProxy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static de.teamlapen.vampirism.common.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.common.blocks.TentBlock.POSITION;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {


    @Nullable
    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        return null;
    }

    @Override
    public void handleSleepClient(@NotNull Player player) {
        if (player.isSleeping()) {
            player.getSleepingPos().ifPresent(pos -> {
                BlockState state = player.level().getBlockState(pos);
                if (state.getBlock() instanceof TentBlock) {
                    TentBlock.setTentSleepPosition(player, pos, player.level().getBlockState(pos).getValue(POSITION), player.level().getBlockState(pos).getValue(FACING));
                } else if (state.getBlock() instanceof CoffinBlock) {
                    CoffinBlock.setCoffinSleepPosition(player, pos, state);
                }
            });
        }
    }

    @Override
    public @NotNull RecipeMap recipeMap(Level level) {
        return level instanceof ServerLevel serverLevel ? serverLevel.recipeAccess().recipeMap() : RecipeMap.EMPTY;
    }

    @Override
    public <I extends RecipeInput, T extends Recipe<I>> Optional<RecipeHolder<T>> getRecipeFor(RecipeType<T> type, I input, Level level, RecipeManager.CachedCheck<I, T> check) {
        return check.getRecipeFor(input, (ServerLevel) level);
    }
}
