package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.actions.IActionResult;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.AreaParticleCloudEntity;
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
        int dist = VampirismConfig.BALANCE.vaTeleportMaxDistance.get();
        if (vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE)) {
            dist *= VampirismConfig.BALANCE.vrTeleportDistanceMod.get();
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
            if (player.getCommandSenderWorld().getBlockState(((BlockHitResult) target).getBlockPos()).blocksMotion()) {
                pos = ((BlockHitResult) target).getBlockPos().above();
            }
        } else {//TODO better solution / remove
            if (player.getCommandSenderWorld().getBlockState(((EntityHitResult) target).getEntity().blockPosition()).blocksMotion()) {
                pos = ((EntityHitResult) target).getEntity().blockPosition();
            }
        }

        if (pos != null) {
            player.setPos(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5);
            if (player.getCommandSenderWorld().containsAnyLiquid(player.getBoundingBox()) || !player.getCommandSenderWorld().isUnobstructed(player)) { //isEntityColliding
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
        AreaParticleCloudEntity particleCloud = new AreaParticleCloudEntity(ModEntities.PARTICLE_CLOUD.get(), player.getCommandSenderWorld());
        particleCloud.setPos(ox, oy, oz);
        particleCloud.setRadius(0.7F);
        particleCloud.setHeight(player.getBbHeight());
        particleCloud.setDuration(5);
        particleCloud.setSpawnRate(15);
        player.getCommandSenderWorld().addFreshEntity(particleCloud);
        player.getCommandSenderWorld().playSound(null, ox, oy, oz, ModSounds.TELEPORT_AWAY.get(), SoundSource.PLAYERS, 1f, 1f);
        player.getCommandSenderWorld().playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.TELEPORT_HERE.get(), SoundSource.PLAYERS, 1f, 1f);
        return IActionResult.SUCCESS;
    }

    @Override
    public IActionResult canBeUsedBy(@NotNull IVampirePlayer vampire) {
        return IActionResult.otherAction(vampire.getActionHandler(), VampireActions.BAT);
    }

    @Override
    public int getCooldown(@NotNull IVampirePlayer player) {
        return (int) ((player.getRefinementHandler().isRefinementEquipped(ModRefinements.TELEPORT_DISTANCE) ? 0.5 : 1) * VampirismConfig.BALANCE.vaTeleportCooldown.get() * 20);
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
