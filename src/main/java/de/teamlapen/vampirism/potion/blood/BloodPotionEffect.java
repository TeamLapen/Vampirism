package de.teamlapen.vampirism.potion.blood;

import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import java.util.Random;


class BloodPotionEffect implements IBloodPotionEffect {

    private final String id;
    private final Potion potion;
    private final boolean isBad;
    private final IPropertyRandomizer propertyRandomizer;

    BloodPotionEffect(String id, Potion potion, boolean isBad, int weight, IPropertyRandomizer propertyRandomizer) {
        this.id = id;
        this.potion = potion;
        this.isBad = isBad;
        this.propertyRandomizer = propertyRandomizer;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getLocName(int duration, int amplifier) {
        return potion.getName();
    }

    @Override
    public int[] getRandomProperties(Random rnd) {
        return propertyRandomizer.getRandomProperties(rnd);
    }

    @Override
    public boolean isBad() {
        return isBad;
    }

    @Override
    public void onActivated(EntityLivingBase hunter, int duration, int amplifier) {
        hunter.addPotionEffect(new PotionEffect(potion, duration, amplifier));
    }

    @Override
    public String toString() {
        return "BloodPotionEffect{" +
                "potion=" + potion +
                ", isBad=" + isBad +
                '}';
    }
}
