package de.teamlapen.vampirism.fluids;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.EnumRarity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.Loader;


public class FluidBlood extends Fluid {
    public FluidBlood(String name) {
        super(name, new ResourceLocation(REFERENCE.MODID, "blocks/" + name + "_still"), new ResourceLocation(REFERENCE.MODID, "blocks/" + name + "_flow"));
        this.setDensity(1300);
        this.setTemperature(309);
        this.setViscosity(3000);
        this.setRarity(EnumRarity.UNCOMMON);
        if (Loader.isModLoaded(REFERENCE.INTEGRATIONS_MODID)) {
            this.setUnlocalizedName(REFERENCE.MODID + "." + name + ".vampirism");
        } else {
            this.setUnlocalizedName(REFERENCE.MODID + "." + name);
        }
    }

    @Override
    public int getColor() {
        return 0xEEFF1111;
    }
}
