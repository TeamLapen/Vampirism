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

        Level level = player.getCommandSenderWorld();
        BlockPos targetPos = switch (target.getType()) {
            case BLOCK -> {
                var pos = ((BlockHitResult) target).getBlockPos().relative(((BlockHitResult) target).getDirection());
                yield level.getBlockState(pos.below()).getMaterial().blocksMotion() ? pos : pos.below();
            }
            case ENTITY -> {
                var pos = ((EntityHitResult) target).getEntity().blockPosition();
                yield level.getBlockState(pos).getMaterial().blocksMotion() ? null : pos;
            }
            default -> null;
        };

        if (targetPos == null) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.get(), 1, 1);
            return false;
        }

        Vec3 currentPosition = player.position();
        player.setPos(targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5);
        if (level.containsAnyLiquid(player.getBoundingBox()) || !level.isUnobstructed(player)) {
            targetPos = null;
        }
        player.setPos(currentPosition);


        if (targetPos == null) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.get(), 1, 1);
            return false;
        }

        if (player instanceof ServerPlayer playerMp) {
            BlockPos finalPos = targetPos;
            VampirePlayer.getOpt(playerMp).ifPresent(s -> s.dispatchAction(new DispatchedDash(new Vec3(finalPos.getX() + 0.5, finalPos.getY(), finalPos.getZ() + 0.5))));
        }
        spawnCloud(level, player.position(), player.getBbHeight());
        return true;
    }

    private void spawnCloud(Level level, Vec3 position, float height) {
        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), level);
        particleCloud.setPos(position);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(height);
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        level.addFreshEntity(particleCloud);
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
