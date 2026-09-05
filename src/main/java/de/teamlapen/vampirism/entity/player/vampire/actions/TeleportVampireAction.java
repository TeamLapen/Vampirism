package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
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
        if (target.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.get(), 1, 1);
            return false;
        }
        Vec3 oldPosition = player.position();
        Vec3 hitLocation = target.getLocation();
        Vec3 targetPosition = hitLocation.add(hitLocation.vectorTo(oldPosition).normalize().scale(0.5));


        player.setPos(targetPosition);
        if (player.getCommandSenderWorld().containsAnyLiquid(player.getBoundingBox()) || !player.getCommandSenderWorld().isUnobstructed(player)) { //isEntityColliding
            player.setPos(oldPosition);
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.get(), 1, 1);
            return false;
        }

        if (player instanceof ServerPlayer playerMp) {
            playerMp.removeVehicle();
            playerMp.teleportTo(targetPosition.x(), targetPosition.y(), targetPosition.z());
        }

        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), player.getCommandSenderWorld());
        particleCloud.setPos(oldPosition);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        particleCloud.setColor(0xFFA00000);
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
