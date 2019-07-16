package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.BlindingBatEntity;
import de.teamlapen.vampirism.items.HunterCoatItem;
import de.teamlapen.vampirism.particle.GenericParticleData;
import net.minecraft.entity.Entity;
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
        super(null);
    }

    @Override
    public boolean activate(final IVampirePlayer vampire) {
        PlayerEntity player = vampire.getRepresentingPlayer();
        List<Entity> l = player.getEntityWorld().getEntitiesInAABBexcluding(player, player.getBoundingBox().grow(10, 5, 10), vampire.getNonFriendlySelector(true, false)::test);
        for (Entity o : l) {
            if (o instanceof BlindingBatEntity) continue;
            if (!(o instanceof LivingEntity)) continue;
            if (o instanceof PlayerEntity && HunterCoatItem.isFullyEquipped((PlayerEntity) o)) continue;
            LivingEntity e = (LivingEntity) o;
            e.addPotionEffect(new EffectInstance(Effects.SLOWNESS, Balance.vpa.FREEZE_DURATION * 20, 10));
            e.addPotionEffect(new EffectInstance(Effects.RESISTANCE, Balance.vpa.FREEZE_DURATION * 20, 10));
            e.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, Balance.vpa.FREEZE_DURATION * 20, 128));
            ModParticles.spawnParticles(player.getEntityWorld(), new GenericParticleData(ModParticles.generic, new ResourceLocation("minecraft", "generic_2"), 20, 0xF0F0F0, 0.4F), e.posX, e.posY, e.posZ, 20, 1, e.getRNG());//TODO particle textureindex: 2
        }
        return l.size() > 0;
    }

    @Override
    public int getCooldown() {
        return Balance.vpa.FREEZE_COOLDOWN * 20;
    }

    @Override
    public int getMinU() {
        return 144;
    }

    @Override
    public int getMinV() {
        return 0;
    }

    @Override
    public String getTranslationKey() {
        return "action.vampirism.vampire.freeze";
    }

    @Override
    public boolean isEnabled() {
        return Balance.vpa.FREEZE_ENABLED;
    }

}
