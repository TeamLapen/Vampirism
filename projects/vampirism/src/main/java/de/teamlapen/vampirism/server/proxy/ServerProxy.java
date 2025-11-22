package de.teamlapen.vampirism.server.proxy;

import de.teamlapen.vampirism.common.blocks.CoffinBlock;
import de.teamlapen.vampirism.common.blocks.TentBlock;
import de.teamlapen.vampirism.common.proxy.CommonProxy;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static de.teamlapen.vampirism.common.blocks.TentBlock.FACING;
import static de.teamlapen.vampirism.common.blocks.TentBlock.POSITION;

/**
 * Serverside proxy
 */
public class ServerProxy extends CommonProxy {


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
}
