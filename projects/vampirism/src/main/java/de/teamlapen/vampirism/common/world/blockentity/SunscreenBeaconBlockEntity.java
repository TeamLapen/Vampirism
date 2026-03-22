package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModBlockEntities;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class SunscreenBeaconBlockEntity extends BlockEntity {

    @Nullable
    private BlockPos oldPos;
    @Nullable
    private Predicate<@Nullable Player> selector;

    public SunscreenBeaconBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SUNSCREEN_BEACON.get(), pos, state);
    }


    public static void serverTick(Level level, BlockPos pos, BlockState state, SunscreenBeaconBlockEntity blockEntity) {
        if (level.getGameTime() % 80L == 0L) {
            //Position check is probably not necessary, but not sure
            if (blockEntity.oldPos == null || blockEntity.selector == null || !blockEntity.oldPos.equals(pos)) {
                blockEntity.oldPos = pos;
                final BlockPos center = new BlockPos(pos.getX(), 0, pos.getZ());
                final int distSq = ModConfig.server().sunscreenBeaconRadius.get() * ModConfig.server().sunscreenBeaconRadius.get();
                blockEntity.selector = input -> {
                    if (input == null) return false;
                    BlockPos player = new BlockPos((int) input.getX(), 0, (int) input.getZ());
                    return player.distSqr(center) < distSq;
                };
            }

            List<? extends Player> list = level.players();

            for (Player player : list) {
                if (player.isAlive() && blockEntity.selector.test(player)) {
                    if (VampirePlayer.get(player).getLevel() > 0) {
                        player.addEffect(new MobEffectInstance(ModEffects.SUNSCREEN, 160, 5, true, false));
                    }
                }
            }
        }
    }

}