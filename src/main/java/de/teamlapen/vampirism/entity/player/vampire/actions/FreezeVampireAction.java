package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Freeze Skill
 */
public class FreezeVampireAction extends DefaultVampireAction {

    public FreezeVampireAction() {
        super();
    }

    @Override
    public boolean activate(final @NotNull IVampirePlayer vampire, ActivationContext context) {
        if (vampire.asEntity().level().dimensionType().ultraWarm()) {
            return true;
        }
        freezeEntities(vampire);
        freezeBlocks(vampire);
        Player player = vampire.asEntity();
        ModParticles.spawnParticlesServer(player.level(), ParticleTypes.SNOWFLAKE, player.getX(), player.getY(), player.getZ(), 60, 7, 4, 7, 0);
        return true;
    }

    protected void freezeEntities(@NotNull IVampirePlayer vampire) {
        Player player = vampire.asEntity();
        List<LivingEntity> l = player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(7, 4, 7), vampire.getNonFriendlySelector(true, false));
        for (LivingEntity entity : l) {
            if (player.equals(entity)) continue;
            entity.getCommandSenderWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.FREEZE.get(), SoundSource.PLAYERS, 1f, 1f);
            ModParticles.spawnParticlesServer(player.level(), ParticleTypes.SNOWFLAKE, entity.getX(), entity.getY(), entity.getZ(), 20, 1, 1, 1, 0);

            if (!entity.canFreeze()) continue;

            int dur = VampirismConfig.BALANCE.vaFreezeDuration.get() * 20;
            if (vampire.getRefinementHandler().isRefinementEquipped(ModRefinements.FREEZE_DURATION)) {
                dur *= VampirismConfig.BALANCE.vrFreezeDurationMod.get();
            }
            entity.addEffect(new MobEffectInstance(ModEffects.FREEZE, dur));
            entity.setSharedFlagOnFire(false);
        }
    }

    protected void freezeBlocks(@NotNull IVampirePlayer vampire) {
        Player player = vampire.asEntity();
        Level level = player.level();
        for (int i = -7; i < 7; i++) {
            for (int j = -7; j < 7; j++) {
                for (int k = -4; k < 4; k++) {
                    BlockPos pos = new BlockPos(player.getBlockX() + i, player.getBlockY() + j, player.getBlockZ() + k);
                    BlockState blockState = level.getBlockState(pos);

                    if (blockState.hasProperty(BlockStateProperties.LIT) && blockState.getValue(BlockStateProperties.LIT)) {
                        level.setBlock(pos, blockState.setValue(BlockStateProperties.LIT, false), 3);
                    } else if (blockState.getBlock() instanceof BaseFireBlock s) {
                        level.destroyBlock(pos, false);
                    }
                }
            }
        }
    }

    @Override
    public int getCooldown(IVampirePlayer player) {
        return VampirismConfig.BALANCE.vaFreezeCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaFreezeEnabled.get();
    }

    @Override
    public boolean showHudCooldown(Player player) {
        return true;
    }
}
