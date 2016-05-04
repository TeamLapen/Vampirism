package de.teamlapen.vampirism.potion;

import de.teamlapen.vampirism.util.Helper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class PotionSanguinareEffect extends PotionEffect {
    public PotionSanguinareEffect(Potion potion, int effectDuration) {
        super(potion, effectDuration, 0, false, true);
    }

    @Override
    public void combine(PotionEffect other) {
        //Sanguinare cannot be combined
    }

    @Override
    public boolean onUpdate(EntityLivingBase entityIn) {
        if (this.getDuration() % 10 == 0 && entityIn instanceof EntityPlayer) {
            if (!Helper.canBecomeVampire((EntityPlayer) entityIn)) {
                return false;
            }
        }
        return super.onUpdate(entityIn);
    }
}
