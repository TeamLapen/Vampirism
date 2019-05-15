package de.teamlapen.vampirism.potion.blood;

import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionPropertyRandomizer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nonnull;
import java.util.Random;


class BloodPotionEffect implements IBloodPotionEffect {

    private final ResourceLocation id;
    private final Potion potion;
    private final boolean isBad;
    private final int weight;
    private final IBloodPotionPropertyRandomizer propertyRandomizer;

    BloodPotionEffect(ResourceLocation id, Potion potion, boolean isBad, int weight, IBloodPotionPropertyRandomizer propertyRandomizer) {
        this.id = id;
        this.potion = potion;
        this.isBad = isBad;
        this.propertyRandomizer = propertyRandomizer;
        this.weight = weight;
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
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public ITextComponent getLocName(NBTTagCompound properties) {
        return potion.getDisplayName();
    }

    @Override
    public NBTTagCompound getRandomProperties(Random rng) {
        return propertyRandomizer.getRandomProperties(rng);
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean isBad() {
        return isBad;
    }

    @Override
    public void onActivated(EntityLivingBase hunter, NBTTagCompound nbt, float durationMult) {
        hunter.addPotionEffect(new PotionEffect(potion, (int) (nbt.getInt("duration") * durationMult), nbt.getInt("amplifier")));
    }

    @Override
    public String toString() {
        return "BloodPotionEffect{" +
                "potion=" + potion +
                ", isBad=" + isBad +
                '}';
    }
}
