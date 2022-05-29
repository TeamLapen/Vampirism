package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModRefinements;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import de.teamlapen.vampirism.particle.GenericParticleData;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;

import java.util.List;

/**
 * Freeze Skill
 */
public class FreezeVampireAction extends DefaultVampireAction {

    public FreezeVampireAction() {
        super();
    }

    @Override
    public boolean activate(final IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        List<LivingEntity> l = player.getCommandSenderWorld().getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(10, 5, 10), vampire.getNonFriendlySelector(true, false));
        for (LivingEntity e : l) {
            if (player.equals(e)) continue;
            if (e instanceof BlindingBatEntity) continue;
            if (e instanceof PlayerEntity && VampirismPlayerAttributes.get((PlayerEntity) e).getHuntSpecial().fullHunterCoat != null)
                continue;
            int dur = VampirismConfig.BALANCE.vaFreezeDuration.get() * 20;
            if (vampire.getSkillHandler().isRefinementEquipped(ModRefinements.freeze_duration)) {
                dur *= VampirismConfig.BALANCE.vrFreezeDurationMod.get();
            }
            e.addEffect(new EffectInstance(ModEffects.FREEZE.get(), dur));
            ModParticles.spawnParticlesServer(player.getCommandSenderWorld(), new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "generic_2"), 20, 0xF0F0F0, 0.4F), e.getX(), e.getY(), e.getZ(), 20, 1, 1, 1, 0);
        }
        return l.size() > 0;
    }

    @Override
    public int getCooldown() {
        return VampirismConfig.BALANCE.vaFreezeCooldown.get() * 20;
    }

    @Override
    public boolean isEnabled() {
        return VampirismConfig.BALANCE.vaFreezeEnabled.get();
    }

    @Override
    public boolean showHudCooldown(PlayerEntity player) {
        return true;
    }
}
