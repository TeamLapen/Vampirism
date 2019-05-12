package de.teamlapen.vampirism.player.vampire.actions;

import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultVampireAction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.items.ItemHunterCoat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;

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
        EntityPlayer player = vampire.getRepresentingPlayer();
        List l = player.getEntityWorld().getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().grow(10, 5, 10), vampire.getNonFriendlySelector(true, false)::test);
        for (Object o : l) {
            if (o instanceof EntityBlindingBat) continue;
            if (!(o instanceof EntityLivingBase)) continue;
            if (o instanceof EntityPlayer && ItemHunterCoat.isFullyEquipped((EntityPlayer) o)) continue;
            EntityLivingBase e = (EntityLivingBase) o;
            e.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, Balance.vpa.FREEZE_DURATION * 20, 10));
            e.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, Balance.vpa.FREEZE_DURATION * 20, 10));
            e.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, Balance.vpa.FREEZE_DURATION * 20, 128));
            VampLib.proxy.getParticleHandler().spawnParticles(player.getEntityWorld(), ModParticles.GENERIC_PARTICLE, e.posX, e.posY + e.height / 2, e.posZ, 20, 1, e.getRNG(), 2, 20, 0xF0F0F0, 0.4);
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
