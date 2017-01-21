package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;


public class FluidBlood extends Fluid {
    public FluidBlood() {
        super(VReference.FLUID_BLOOD_NAME, new ResourceLocation(REFERENCE.MODID, "blocks/" + VReference.FLUID_BLOOD_NAME + "_still"), new ResourceLocation(REFERENCE.MODID, "blocks/" + VReference.FLUID_BLOOD_NAME + "_flow"));
        this.setDensity(1300);
        this.setTemperature(309);
        this.setViscosity(3000);
        this.setRarity(EnumRarity.UNCOMMON);
        this.setUnlocalizedName(REFERENCE.MODID + "." + VReference.FLUID_BLOOD_NAME);
    }

    @Override
    public int getColor() {
        return 0xEEFF1111;
    }
}
