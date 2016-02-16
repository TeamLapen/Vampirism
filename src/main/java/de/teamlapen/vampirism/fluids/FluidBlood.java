package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;


public class FluidBlood extends Fluid {
    public FluidBlood() {
        super(VampirismAPI.FLUID_BLOOD_NAME, new ResourceLocation(REFERENCE.MODID, "blocks/" + VampirismAPI.FLUID_BLOOD_NAME + "_still"), new ResourceLocation(REFERENCE.MODID, "blocks/" + VampirismAPI.FLUID_BLOOD_NAME + "_flow"));
        this.setDensity(1300);
        this.setTemperature(309);
        this.setViscosity(3000);
        this.setRarity(EnumRarity.UNCOMMON);
        this.setUnlocalizedName(REFERENCE.MODID + "." + VampirismAPI.FLUID_BLOOD_NAME);
    }
}
