package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Predicate;

public class SunscreenBeaconTileEntity extends TileEntity implements ITickableTileEntity {

    private BlockPos oldPos;
    private Predicate<PlayerEntity> selector;

    public SunscreenBeaconTileEntity() {
        super(ModTiles.SUNSCREEN_BEACON.get());
    }

    @Override
    public void tick() {
        if (level == null) return;
        if (this.level.getGameTime() % 80L == 0L) {
            this.updateBeacon();
        }
    }

    private void updateBeacon() {

        if (this.level != null && !this.level.isClientSide) {
            //Position check is probably not necessary, but not sure
            if (oldPos == null || selector == null || !oldPos.equals(this.worldPosition)) {
                oldPos = this.worldPosition;
                final BlockPos center = new BlockPos(this.worldPosition.getX(), 0, this.worldPosition.getZ());
                final int distSq = VampirismConfig.SERVER.sunscreenBeaconDistance.get() * VampirismConfig.SERVER.sunscreenBeaconDistance.get();
                selector = input -> {
                    if (input == null) return false;
                    BlockPos player = new BlockPos(input.getX(), 0, input.getZ());
                    return player.distSqr(center) < distSq;
                };
            }

            List<? extends PlayerEntity> list = this.level.players();

            for (PlayerEntity player : list) {
                if (player.isAlive() && selector.test(player)) {
                    if (VampirismPlayerAttributes.get(player).vampireLevel > 0) {
                        player.addEffect(new EffectInstance(ModEffects.SUNSCREEN.get(), 160, 5, true, false));
                    }
                }
            }
        }
    }
}