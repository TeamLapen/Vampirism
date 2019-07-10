package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.config.Configs;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.client.renderer.texture.ITickable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.List;
import java.util.function.Predicate;

public class TileSunscreenBeacon extends TileEntity implements ITickable {

    private BlockPos oldPos;
    private Predicate<PlayerEntity> selector;

    public TileSunscreenBeacon() {
        super(ModTiles.sunscreen_beacon);
    }

    @Override
    public void tick() {
        if (this.world.getGameTime() % 80L == 0L) {
            this.updateBeacon();
        }
    }

    private void updateBeacon() {

        if (this.world != null && !this.world.isRemote) {
            //Position check is probably not necessary, but not sure
            if (oldPos == null || selector == null || !oldPos.equals(this.pos)) {
                oldPos = this.pos;
                final BlockPos center = new BlockPos(this.pos.getX(), 0, this.pos.getZ());
                final int distSq = Configs.sunscreen_beacon_distance * Configs.sunscreen_beacon_distance;
                selector = input -> {
                    if (input == null) return false;
                    BlockPos player = new BlockPos(input.posX, 0, input.posZ);
                    return player.distanceSq(center) < distSq;
                };
            }

            List<? extends PlayerEntity> list = this.world.getPlayers();

            for (PlayerEntity player : list) {
                if (selector.test(player))
                    continue;
                if (VampirePlayer.get(player).getLevel() > 0) {
                    player.addPotionEffect(new EffectInstance(ModPotions.sunscreen, 160, 5, true, false));
                }
            }
        }
    }
}