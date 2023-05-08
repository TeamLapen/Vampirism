package de.teamlapen.vampirism.entity.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.core.ModSounds;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.particle.GenericParticleOptions;
import net.minecraft.resources.ResourceLocation;
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
        Player player = vampire.getRepresentingPlayer();
        List<LivingEntity> l = player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10, 5, 10), vampire.getNonFriendlySelector(true, false));
        for (LivingEntity e : l) {
            if (player.equals(e)) continue;
            if (e instanceof BlindingBatEntity) continue;
            if (e instanceof Player && VampirismPlayerAttributes.get((Player) e).getHuntSpecial().fullHunterCoat != null) {
                continue;
            }
            int dur = VampirismConfig.BALANCE.vaFreezeDuration.get() * 20;
            if (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.FREEZE_DURATION.get())) {
                dur *= VampirismConfig.BALANCE.vrFreezeDurationMod.get();
            }
            e.addEffect(new MobEffectInstance(ModEffects.FREEZE.get(), dur));
            e.getCommandSenderWorld().playSound(null, e.getX(), e.getY(), e.getZ(), ModSounds.FREEZE.get(), SoundSource.PLAYERS, 1f, 1f);
            ModParticles.spawnParticlesServer(player.getCommandSenderWorld(), new GenericParticleOptions(new ResourceLocation("minecraft", "generic_2"), 20, 0xF0F0F0, 0.4F), e.getX(), e.getY(), e.getZ(), 20, 1, 1, 1, 0);
        }
        return l.size() > 0;
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
