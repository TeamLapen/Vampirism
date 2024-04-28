package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
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
        Player player = vampire.asEntity();
        List<LivingEntity> l = player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10, 5, 10), vampire.getNonFriendlySelector(true, false));
        for (LivingEntity entity : l) {
            if (player.equals(entity)) continue;

            entity.getCommandSenderWorld().playSound(null, entity.getX(), entity.getY(), entity.getZ(), ModSounds.FREEZE.get(), SoundSource.PLAYERS, 1f, 1f);
            ModParticles.spawnParticlesServer(player.level(), ParticleTypes.SNOWFLAKE, entity.getX(), entity.getY(), entity.getZ(), 20, 1, 1, 1, 0);

            if (!entity.canFreeze()) continue;

            int dur = VampirismConfig.BALANCE.vaFreezeDuration.get() * 20;
            if (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.FREEZE_DURATION.get())) {
                dur *= VampirismConfig.BALANCE.vrFreezeDurationMod.get();
            }
            entity.addEffect(new MobEffectInstance(ModEffects.FREEZE, dur));
            entity.setSharedFlagOnFire(false);
        }
        return !l.isEmpty();
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
