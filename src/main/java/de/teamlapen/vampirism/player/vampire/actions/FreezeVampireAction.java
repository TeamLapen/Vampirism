package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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
        List<LivingEntity> l = player.getEntityWorld().getEntitiesWithinAABB(LivingEntity.class, player.getBoundingBox().grow(10, 5, 10), vampire.getNonFriendlySelector(true, false));
        for (LivingEntity e : l) {
            if (player.equals(e)) continue;
            if (e instanceof BlindingBatEntity) continue;
            if (e instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) e)) continue;
            int dur = VampirismConfig.BALANCE.vaFreezeDuration.get();
            e.addPotionEffect(new EffectInstance(Effects.SLOWNESS, dur * 20, 10));
            e.addPotionEffect(new EffectInstance(Effects.RESISTANCE, dur * 20, 10));
            e.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, dur * 20, 128));
            ModParticles.spawnParticlesServer(player.getEntityWorld(), new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "generic_2"), 20, 0xF0F0F0, 0.4F), e.posX, e.posY, e.posZ, 20, 1, 1, 1, 0);
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

}
