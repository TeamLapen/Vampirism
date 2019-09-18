package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class AutoConvertGlassBottleCondition implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation(REFERENCE.MODID, "auto_convert_glass_bottle");

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test() {
        return VampirismConfig.SERVER.autoConvertGlassBottles.get();
    }

    public static class Serializer implements IConditionSerializer<AutoConvertGlassBottleCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public ResourceLocation getID() {
            return AutoConvertGlassBottleCondition.NAME;
        }

        @Override
        public AutoConvertGlassBottleCondition read(JsonObject json) {
            return new AutoConvertGlassBottleCondition();
        }

        @Override
        public void write(JsonObject json, AutoConvertGlassBottleCondition value) {
            json.addProperty("autoConvertGlassBottle", true);
        }
    }
}
