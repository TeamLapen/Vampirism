package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

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
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {

    }


}
