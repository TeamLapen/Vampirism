package de.teamlapen.vampirism.common.entity.player.vampire.actions;

import de.teamlapen.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.common.config.ModConfig;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModRefinements;
import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.entity.AreaParticleCloud;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;


public class TeleportVampireAction extends DefaultVampireAction {


    public TeleportVampireAction() {
        super();
    }

    @Override
    public IActionResult activate(@NotNull IVampirePlayer vampire, ActivationContext context) {
        Player player = vampire.asEntity();
        int dist = ModConfig.BALANCE.vaTeleportMaxDistance.get();
        if (vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE)) {
            dist *= ModConfig.BALANCE.vrTeleportDistanceMod.get();
        }
        HitResult target = UtilLib.getPlayerLookingSpot(player, dist);
        double ox = player.getX();
        double oy = player.getY();
        double oz = player.getZ();
        if (target.getType() == HitResult.Type.MISS) {
            player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), 1, 1);
            return IActionResult.fail(Component.translatable("text.vampirism.action.teleport.no_target"));
        }
        BlockPos pos = null;
        if (target.getType() == HitResult.Type.BLOCK) {
            if (player.level().getBlockState(((BlockHitResult) target).getBlockPos()).blocksMotion()) {
                pos = ((BlockHitResult) target).getBlockPos().above();
            }
        } else {//TODO better solution / remove
            if (player.level().getBlockState(((EntityHitResult) target).getEntity().blockPosition()).blocksMotion()) {
                pos = ((EntityHitResult) target).getEntity().blockPosition();
            }
        }

        if (pos != null) {
            player.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (player.level().containsAnyLiquid(player.getBoundingBox()) || !player.level().isUnobstructed(player)) { //isEntityColliding
                pos = null;
            }
        }


        if (pos == null) {
            player.setPos(ox, oy, oz);
            player.playSound(SoundEvents.NOTE_BLOCK_BASEDRUM.value(), 1, 1);
            return IActionResult.fail(Component.translatable("text.vampirism.action.teleport.no_target"));
        }
        if (player instanceof ServerPlayer playerMp) {
            playerMp.disconnect();
            playerMp.teleportTo(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
        }
        AreaParticleCloud particleCloud = new AreaParticleCloud(ModEntities.PARTICLE_CLOUD.get(), player.level());
        particleCloud.setPos(ox, oy, oz);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        player.level().addFreshEntity(particleCloud);
        player.level().playSound(null, ox, oy, oz, ModSounds.TELEPORT_AWAY.get(), SoundSource.PLAYERS, 1f, 1f);
        player.level().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TELEPORT_HERE.get(), SoundSource.PLAYERS, 1f, 1f);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return IActionResult.otherAction(vampire.getActionHandler(), VampireActions.BAT);
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        return (int) ((player.getRefinementHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE) ? 0.5 : 1) * ModConfig.BALANCE.vaTeleportCooldown.get() * 20);
    }

    @Override
    public boolean isEnabled() {
        return ModConfig.BALANCE.vaTeleportEnabled.get();
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
