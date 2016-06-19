package de.teamlapen.vampirism.potion.blood;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nonnull;
import java.util.Random;


class BloodPotionEffect implements IBloodPotionEffect {

    private final String id;
    private final Potion potion;
    private final boolean isBad;
    private final IBloodPotionPropertyRandomizer propertyRandomizer;

    BloodPotionEffect(String id, Potion potion, boolean isBad, int weight, IBloodPotionPropertyRandomizer propertyRandomizer) {
        this.id = id;
        this.potion = potion;
        this.isBad = isBad;
        this.propertyRandomizer = propertyRandomizer;
    }

    @Override
    public boolean canCoexist(@Nonnull IBloodPotionEffect other) {
        if (this.isBad() != other.isBad()) return true;
        if (other instanceof BloodPotionEffect) {
            return !this.potion.equals(((BloodPotionEffect) other).potion);
        }
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocName(NBTTagCompound properties) {
        return UtilLib.translateToLocal(potion.getName());
    }

    @Override
    public NBTTagCompound getRandomProperties(Random rng) {
        return propertyRandomizer.getRandomProperties(rng);
    }

    @Override
    public boolean isBad() {
        return isBad;
    }

    @Override
    public void onActivated(EntityLivingBase hunter, NBTTagCompound nbt, float durationMult) {
        hunter.addPotionEffect(new PotionEffect(potion, (int) (nbt.getInteger("duration") * durationMult), nbt.getInteger("amplifier")));
    }

    @Override
    public String toString() {
        return "BloodPotionEffect{" +
                "potion=" + potion +
                ", isBad=" + isBad +
                '}';
    }
}
