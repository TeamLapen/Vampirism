package de.teamlapen.vampirism.entity.player.vampire.skills;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.entity.player.vampire.DefaultSkill;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.config.Balance;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;

import java.util.List;

/**
 * Freeze Skill
 */
public class FreezeSkill extends DefaultSkill {

    public FreezeSkill() {
        super(null);
    }
//  TODO activate again
//    @Override
//    public boolean canBeUsedBy(IVampirePlayer vampire) {
//        return vampire.isVampireLord();
//    }

    @Override
    public int getCooldown() {
        return Balance.vps.FREEZE_COOLDOWN * 20;
    }

    @Override
    public int getMinLevel() {
        return Balance.vps.FREEZE_MIN_LEVEL;
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
    public String getUnlocalizedName() {
        return "skill.vampirism.freeze";
    }

    @Override
    public boolean onActivated(final IVampirePlayer vampire) {
        VampirismMod.log.t("Act");
        EntityPlayer player = vampire.getRepresentingPlayer();
        List l = player.worldObj.getEntitiesInAABBexcluding(player, player.getEntityBoundingBox().expand(10, 5, 10), vampire.getNonFriendlySelector(true));
        for (Object o : l) {
            if (o instanceof EntityBlindingBat) continue;
            EntityLivingBase e = (EntityLivingBase) o;
            e.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, Balance.vps.FREEZE_DURATION * 20, 10));
            e.addPotionEffect(new PotionEffect(Potion.resistance.id, Balance.vps.FREEZE_DURATION * 20, 10));
            e.addPotionEffect(new PotionEffect(Potion.jump.id, Balance.vps.FREEZE_DURATION * 20, 128));
            Helper.spawnParticlesAroundEntity(e, EnumParticleTypes.SNOW_SHOVEL, 1.5, 40);
        }
        return l.size() > 0;
    }

}
