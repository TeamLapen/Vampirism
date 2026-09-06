package de.teamlapen.vampirism.common.world.entity.player.vampire.actions;

import de.teamlapen.faction.api.factions.actions.IActionResult;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModRefinements;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.util.UtilLib;
import de.teamlapen.vampirism.common.world.entity.AreaParticleCloud;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.portal.TeleportTransition;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Set;


public class TeleportVampireAction extends DefaultVampireAction {


    public TeleportVampireAction() {
        super();
    }

    @Override
    public IActionResult activateServer(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        int dist = ModConfig.balance().vaTeleportMaxDistance.get();
        if (vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE)) {
            dist *= ModConfig.balance().vrTeleportDistanceMod.get();
        }
        HitResult target = UtilLib.getPlayerLookingSpot(player, dist);
        if (target.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, 1);
            return IActionResult.fail(Component.translatable("message.vampirism.action.teleport.no_target"));
        }
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return IActionResult.SUCCESS;
        }

        // Teleport to slightly in front of the visual hit spot, towards the player. Executed similar to an ender pearl to reduce the risk of getting stuck.
        Vec3 origin = serverPlayer.position();
        Vec3 hitLocation = target.getLocation();
        Vec3 destination = hitLocation.add(hitLocation.vectorTo(origin).normalize().scale(0.5));

        if (serverPlayer.isPassenger()) {
            serverPlayer.stopRiding();
        }
        serverPlayer.teleport(new TeleportTransition((ServerLevel) serverPlayer.level(), destination, Vec3.ZERO, serverPlayer.getYRot(), serverPlayer.getXRot(), Set.of(), TeleportTransition.DO_NOTHING));
        serverPlayer.resetFallDistance();

        AreaParticleCloud particleCloud = new AreaParticleCloud(ModEntities.PARTICLE_CLOUD.get(), serverPlayer.level());
        particleCloud.setPos(origin.x(), origin.y(), origin.z());
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(serverPlayer.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        particleCloud.setParticle(ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0xFF500000));
        serverPlayer.level().addFreshEntity(particleCloud);
        serverPlayer.level().playSound(null, origin.x(), origin.y(), origin.z(), ModSounds.TELEPORT_AWAY.get(), SoundSource.PLAYERS, 1f, 1f);
        serverPlayer.level().playSound(null, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), ModSounds.TELEPORT_HERE.get(), SoundSource.PLAYERS, 1f, 1f);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return IActionResult.otherAction(vampire.getActionHandler(), VampireActions.BAT);
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        int cooldown = ModConfig.balance().vaTeleportCooldown.get() * 20;
        if (player.getRefinementHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE)) {
            cooldown = (int)(cooldown * ModConfig.balance().vrTeleportCooldownMod.get());
        }
        return cooldown;
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.balance().vaTeleportEnabled.get();
    }

}
