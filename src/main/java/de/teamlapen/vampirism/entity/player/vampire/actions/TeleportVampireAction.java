package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import de.teamlapen.vampirism.entity.player.runnable.DispatchedDash;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public class TeleportVampireAction extends DefaultVampireAction {


    public TeleportVampireAction() {
        super();
    }

    @Override
    public boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.getRepresentingPlayer();
        int dist = VampirismConfig.BALANCE.vaTeleportMaxDistance.get();
        if (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE.get())) {
            dist *= VampirismConfig.BALANCE.vrTeleportDistanceMod.get();
        }
        HitResult target = UtilLib.getPlayerLookingSpot(player, dist);
        double ox = player.getX();
        double oy = player.getY();
        double oz = player.getZ();
        if (target.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.get(), 1, 1);
            return false;
        }
        BlockPos pos = null;
        Level level = player.getCommandSenderWorld();
        if (target.getType() == HitResult.Type.BLOCK) {
            if (level.getBlockState(((BlockHitResult) target).getBlockPos()).getMaterial().blocksMotion()) {
                pos = ((BlockHitResult) target).getBlockPos().relative(((BlockHitResult) target).getDirection());
            }
        } else {//TODO better solution / remove
            if (level.getBlockState(((EntityHitResult) target).getEntity().blockPosition()).getMaterial().blocksMotion()) {
                pos = ((EntityHitResult) target).getEntity().blockPosition();
            }
        }

        if (pos != null) {
            player.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (level.containsAnyLiquid(player.getBoundingBox()) || !level.isUnobstructed(player)) { //isEntityColliding
                pos = null;
            }
            player.setPos(ox, oy, oz);
        }


        if (pos == null) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.get(), 1, 1);
            return false;
        }
        if (player instanceof ServerPlayer playerMp) {
            BlockPos finalPos = pos;
            VampirePlayer.getOpt(playerMp).ifPresent(s -> s.dispatchAction(new DispatchedDash(new Vec3(finalPos.getX() + 0.5, finalPos.getY(), finalPos.getZ() + 0.5))));
        }
        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), level);
        particleCloud.setPos(ox, oy, oz);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        level.addFreshEntity(particleCloud);
        return true;
    }

    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.BAT.get());
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        return (int) ((player.getSkillHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE.get()) ? 0.5 : 1) * VampirismConfig.BALANCE.vaTeleportCooldown.get() * 20);
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaTeleportEnabled.get();
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
