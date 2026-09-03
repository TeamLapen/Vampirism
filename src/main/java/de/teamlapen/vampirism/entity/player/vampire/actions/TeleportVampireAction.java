package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


public class TeleportVampireAction extends DefaultVampireAction {


    public TeleportVampireAction() {
        super();
    }

    @Override
    public boolean activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        int dist = VampirismConfig.BALANCE.vaTeleportMaxDistance.get();
        if (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE.get())) {
            dist *= VampirismConfig.BALANCE.vrTeleportDistanceMod.get();
        }
        HitResult target = UtilLib.getPlayerLookingSpot(player, dist);
        if (target.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, 1);
            return false;
        }

        //Attempt to teleport to 0.5 blocks in front of the visual hit
        Vec3 oldPosition = player.position();
        Vec3 hitLocation = target.getLocation();
        Vec3 targetPosition = hitLocation.add(hitLocation.vectorTo(oldPosition).normalize().scale(0.5));
        if (player instanceof ServerPlayer serverPlayer) {
            if (player.isPassenger()) {
                player.unRide();
            }
            if (serverPlayer.connection.isAcceptingMessages()) {
                serverPlayer.changeDimension(
                        new DimensionTransition(
                                serverPlayer.serverLevel(), targetPosition, Vec3.ZERO, serverPlayer.getYRot(), serverPlayer.getXRot(), DimensionTransition.DO_NOTHING
                        )
                );
                serverPlayer.resetFallDistance();
                serverPlayer.resetCurrentImpulseContext();
            }
        }

        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), player.getCommandSenderWorld());
        particleCloud.setPos(oldPosition);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        particleCloud.setParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFF500000));
        player.getCommandSenderWorld().addFreshEntity(particleCloud);
        player.getCommandSenderWorld().playSound(null, oldPosition.x(), oldPosition.y(), oldPosition.z(), ModSounds.TELEPORT_AWAY.get(), SoundSource.PLAYERS, 1f, 1f);
        player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(),ModSounds.TELEPORT_HERE.get(), SoundSource.PLAYERS, 1f, 1f);
        return true;
    }

    @Override
    public boolean canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return !vampire.getActionHandler().isActionActive(VampireActions.BAT.get());
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        int cooldown = VampirismConfig.BALANCE.vaTeleportCooldown.get() * 20;
        if (player.getSkillHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE.get())) {
            cooldown = (int)(cooldown * VampirismConfig.BALANCE.vrTeleportCooldownMod.get());
        }
        return cooldown;
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
