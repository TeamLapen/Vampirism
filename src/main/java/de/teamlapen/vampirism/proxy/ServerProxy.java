package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.blocks.CoffinBlock;
import de.teamlapen.vampirism.blocks.TentBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import javax.annotation.Nullable;

import static de.teamlapen.vampirism.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.blocks.TentBlock.POSITION;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {


    @Nullable
    @Override
    public PlayerEntity getClientPlayer() {
        return null;
    }

    @Nullable
    @Override
    public Entity getMouseOverEntity() {
        return null;
    }

    @Override
    public void handleSleepClient(PlayerEntity player) {
        if (player.isSleeping()) {
            player.getSleepingPos().ifPresent(pos -> {
                BlockState state = player.level.getBlockState(pos);
                if (state.getBlock() instanceof TentBlock) {
                    TentBlock.setTentSleepPosition(player, pos, player.level.getBlockState(pos).getValue(POSITION), player.level.getBlockState(pos).getValue(FACING));
                } else if (state.getBlock() instanceof CoffinBlock) {
                    CoffinBlock.setCoffinSleepPosition(player,pos, state);
                }
            });
        }
    }

    @Override
    public void renderScreenFullColor(int ticksOn, int ticksOff, int color) {

    }


}
