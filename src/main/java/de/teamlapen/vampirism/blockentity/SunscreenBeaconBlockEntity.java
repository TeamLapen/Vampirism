package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.function.Predicate;

public class SunscreenBeaconBlockEntity extends BlockEntity {

    private BlockPos oldPos;
    private Predicate<Player> selector;

    public SunscreenBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.sunscreen_beacon.get(), pos, state);
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, SunscreenBeaconBlockEntity blockEntity) {
        if (level.getGameTime() % 80L == 0L) {
            //Position check is probably not necessary, but not sure
            if (blockEntity.oldPos == null || blockEntity.selector == null || !blockEntity.oldPos.equals(pos)) {
                blockEntity.oldPos = pos;
                final BlockPos center = new BlockPos(pos.getX(), 0, pos.getZ());
                final int distSq = VampirismConfig.SERVER.sunscreenBeaconDistance.get() * VampirismConfig.SERVER.sunscreenBeaconDistance.get();
                blockEntity.selector = input -> {
                    if (input == null) return false;
                    BlockPos player = new BlockPos(input.getX(), 0, input.getZ());
                    return player.distSqr(center) < distSq;
                };
            }

            List<? extends Player> list = level.players();

            for (Player player : list) {
                if (player.isAlive() && blockEntity.selector.test(player)) {
                    if (VampirismPlayerAttributes.get(player).vampireLevel > 0) {
                        player.addEffect(new MobEffectInstance(ModEffects.sunscreen.get(), 160, 5, true, false));
                    }
                }
            }
        }
    }

}