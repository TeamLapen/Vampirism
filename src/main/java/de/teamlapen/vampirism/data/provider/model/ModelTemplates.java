package de.teamlapen.vampirism.data.provider.model;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

import static de.teamlapen.vampirism.api.util.VResourceLocation.mod;

public class ModelTemplates extends net.minecraft.client.data.models.model.ModelTemplates {

    public static final ModelTemplate GARLIC_DIFFUSER = create(mod("garlic_diffuser"), TextureSlots.GARLIC);
    public static final ModelTemplate ALTAR_PILLAR_FILLED = create(mod("altar_pillar_filled"), TextureSlots.FILLER);
    public static final ModelTemplate CANDLE_MODEL = create(mod("candle_stick_filled"), TextureSlots.CANDLE);
    public static final ModelTemplate WALL_CANDLE_MODEL = create(mod("wall_candle_stick_filled"), TextureSlots.CANDLE);
    public static final ModelTemplate COFFIN = create(mod("coffin"), TextureSlots.TEXTURE0);
    public static final ModelTemplate COFFIN_BOTTOM = create(mod("coffin_bottom"), TextureSlots.TEXTURE0);
    public static final ModelTemplate COFFIN_TOP = create(mod("coffin_top"), TextureSlots.TEXTURE0);
    public static final ModelTemplate TENT = create(mod("tent"), TextureSlots.FLOOR);
    public static final ModelTemplate BLOOD_SIEVE = create(mod("blood_sieve"), TextureSlots.FILTER);
    public static final ModelTemplate TOTEM = create(mod("totem_top"), TextureSlots.OUTER);
    public static final ModelTemplate BEACON_MODEL = create("beacon", TextureSlots.BEACON).extend().renderType(VResourceLocation.mc("cutout")).build();
    public static final ModelTemplate ALCHEMICAL_CAULDRON = create(mod("alchemy_cauldron_liquid"), TextureSlots.LIQUID);
    public static final ModelTemplate DIRT_PATH = create("dirt_path", TextureSlot.UP, TextureSlot.DOWN, TextureSlot.SIDE, TextureSlot.PARTICLE);
    public static final ModelTemplate CRUCIFIX = createItem(mod("crucifix"), TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate HUNTER_INTEL = createItem(mod("hunter_intel"));
    public static final ModelTemplate HUNTER_AXE = createItem(mod("hunter_axe"), TextureSlot.TEXTURE);
    public static final ModelTemplate HEART_STRIKER = createItem(mod("heart_striker_model"), TextureSlots.TEXTURE_2);
    public static final ModelTemplate HEART_SEEKER = createItem(mod("heart_seeker_model"), TextureSlots.TEXTURE_3);
    public static final ModelTemplate CROSSBOW = createItem(mod("crossbow"), TextureSlot.TEXTURE, TextureSlots.STRING, TextureSlots.ARROW);
    public static final ModelTemplate CROSSBOW_UNLOADED = createItem(mod("crossbow_unloaded"), TextureSlot.TEXTURE, TextureSlots.STRING);
    public static final ModelTemplate DOUBLE_CROSSBOW = createItem(mod("double_crossbow"), TextureSlot.TEXTURE, TextureSlots.STRING, TextureSlots.ARROW);
    public static final ModelTemplate DOUBLE_CROSSBOW_UNLOADED = createItem(mod("double_crossbow_unloaded"), TextureSlot.TEXTURE, TextureSlots.STRING);
    public static final ModelTemplate TECH_CROSSBOW = createItem(mod("tech_crossbow"), TextureSlot.TEXTURE, TextureSlots.STRING);
    public static final ModelTemplate TECH_CROSSBOW_UNLOADED = createItem(mod("tech_crossbow_unloaded"), TextureSlot.TEXTURE, TextureSlots.STRING);
    public static final ModelTemplate GARLIC_DIFFUSER_CORE = createItem(mod("garlic_diffuser_core"), TextureSlot.TEXTURE);

    public static ModelTemplate createItem(ResourceLocation modelName, TextureSlot... textures) {
        return net.minecraft.client.data.models.model.ModelTemplates.createItem(modelName.toString(), textures);
    }

    public static ModelTemplate create(ResourceLocation modelName, TextureSlot... textures) {
        return net.minecraft.client.data.models.model.ModelTemplates.create(modelName.toString(), textures);
    }

    public static ModelTemplate copy(ModelTemplate template, ResourceLocation renderType) {
        return template.extend().renderType(renderType).build();
    }
}
