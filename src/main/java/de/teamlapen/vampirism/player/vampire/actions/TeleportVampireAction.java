package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;


public class TeleportVampireAction extends DefaultVampireAction {


    public TeleportVampireAction() {
        super();
    }

    @Override
    public boolean activate(IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        RayTraceResult target = UtilLib.getPlayerLookingSpot(player, VampirismConfig.BALANCE.vaTeleportMaxDistance.get());
        double ox = player.getPosX();
        double oy = player.getPosY();
        double oz = player.getPosZ();
        if (target.getType() == RayTraceResult.Type.MISS) {
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASS, 1, 1);
            return false;
        }
        BlockPos pos = null;
        if (target.getType() == RayTraceResult.Type.BLOCK) {
            if (player.getEntityWorld().getBlockState(((BlockRayTraceResult) target).getPos()).getMaterial().blocksMovement()) {
                pos = ((BlockRayTraceResult) target).getPos().up();
            }
        } else {//TODO better solution / remove
            if (player.getEntityWorld().getBlockState(((EntityRayTraceResult) target).getEntity().getPosition()).getMaterial().blocksMovement()) {
                pos = ((EntityRayTraceResult) target).getEntity().getPosition();
            }
        }

        if (pos != null) {
            player.setPosition(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (!(!player.getEntityWorld().containsAnyLiquid(player.getBoundingBox()) && player.getEntityWorld().func_226668_i_(player))) { //isEntityColliding
                pos = null;
            }
        }


        if (pos == null) {
            player.setPosition(ox, oy, oz);
            player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 1);
            return false;
        }
        if (player instanceof ServerPlayerEntity) {
            ServerPlayerEntity playerMp = (ServerPlayerEntity) player;
            playerMp.disconnect();
            playerMp.setPositionAndUpdate(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        }
        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.particle_cloud, player.getEntityWorld());
        particleCloud.setPosition(ox, oy, oz);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        player.getEntityWorld().addEntity(particleCloud);
        player.getEntityWorld().playSound(ox, oy, oz, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
        player.getEntityWorld().playSound(player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1, 1, false);
        return true;
    }

    @Override
    public boolean canBeUsedBy(IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.bat);
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaTeleportCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaTeleportEnabled.get();
    }
}
