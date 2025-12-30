package de.teamlapen.vampirism.data.provider.models;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;

import static de.teamlapen.vampirism.api.util.VIdentifier.mod;

public class ModModelTemplates extends ModelTemplates {

    public static final ModelTemplate GARLIC_DIFFUSER = create(mod("garlic_diffuser"), ModTextureSlots.GARLIC);
    public static final ModelTemplate ALTAR_PILLAR_FILLED = create(mod("altar_pillar_filled"), ModTextureSlots.FILLER);
    public static final ModelTemplate CANDLE_STICK_FILLED = create(mod("candle_stick_filled"), ModTextureSlots.CANDLE);
    public static final ModelTemplate WALL_CANDLE_STICK_FILLED = create(mod("wall_candle_stick_filled"), ModTextureSlots.CANDLE);
    public static final ModelTemplate CANDELABRA_FILLED = create(mod("candelabra_filled"), ModTextureSlots.CANDLE);
    public static final ModelTemplate WALL_CANDELABRA_FILLED = create(mod("wall_candelabra_filled"), ModTextureSlots.CANDLE);
    public static final ModelTemplate CHANDELIER_FILLED = create(mod("chandelier_filled"), ModTextureSlots.CANDLE);
    public static final ModelTemplate COFFIN = create(mod("coffin"), ModTextureSlots.TEXTURE0);
    public static final ModelTemplate COFFIN_BOTTOM = create(mod("coffin_bottom"), ModTextureSlots.TEXTURE0);
    public static final ModelTemplate COFFIN_TOP = create(mod("coffin_top"), ModTextureSlots.TEXTURE0);
    public static final ModelTemplate TENT = create(mod("tent"), ModTextureSlots.FLOOR);
    public static final ModelTemplate BLOOD_SIEVE = create(mod("blood_sieve"), ModTextureSlots.FILTER);
    public static final ModelTemplate BEACON_MODEL = create("beacon", ModTextureSlots.BEACON).extend().renderType(VIdentifier.mc("cutout")).build();
    public static final ModelTemplate ALCHEMICAL_CAULDRON = create(mod("alchemy_cauldron_liquid"), ModTextureSlots.LIQUID);
    public static final ModelTemplate DIRT_PATH = create("dirt_path", TextureSlot.TOP, TextureSlot.BOTTOM, TextureSlot.SIDE, TextureSlot.PARTICLE);
    public static final ModelTemplate CRUCIFIX = createItem(mod("crucifix"), TextureSlot.TEXTURE, TextureSlot.PARTICLE);
    public static final ModelTemplate HUNTER_AXE = createItem(mod("hunter_axe"), TextureSlot.TEXTURE);
    public static final ModelTemplate HEART_STRIKER = createItem(mod("heart_striker_model"), ModTextureSlots.TEXTURE_2);
    public static final ModelTemplate HEART_SEEKER = createItem(mod("heart_seeker_model"), ModTextureSlots.TEXTURE_3);
    public static final ModelTemplate CROSSBOW = createItem(mod("crossbow"), TextureSlot.TEXTURE, ModTextureSlots.STRING, ModTextureSlots.ARROW);
    public static final ModelTemplate CROSSBOW_UNLOADED = createItem(mod("crossbow_unloaded"), TextureSlot.TEXTURE, ModTextureSlots.STRING);
    public static final ModelTemplate DOUBLE_CROSSBOW = createItem(mod("double_crossbow"), TextureSlot.TEXTURE, ModTextureSlots.STRING, ModTextureSlots.ARROW);
    public static final ModelTemplate DOUBLE_CROSSBOW_UNLOADED = createItem(mod("double_crossbow_unloaded"), TextureSlot.TEXTURE, ModTextureSlots.STRING);
    public static final ModelTemplate TECH_CROSSBOW = createItem(mod("tech_crossbow"), TextureSlot.TEXTURE, ModTextureSlots.STRING);
    public static final ModelTemplate TECH_CROSSBOW_UNLOADED = createItem(mod("tech_crossbow_unloaded"), TextureSlot.TEXTURE, ModTextureSlots.STRING);
    public static final ModelTemplate GARLIC_DIFFUSER_CORE = createItem(mod("garlic_diffuser_core"), TextureSlot.TEXTURE);

    public static ModelTemplate createItem(Identifier modelName, TextureSlot... textures) {
        return ModelTemplates.createItem(modelName.toString(), textures);
    }

    public static ModelTemplate create(Identifier modelName, TextureSlot... textures) {
        return ModelTemplates.create(modelName.toString(), textures);
    }

    public static ModelTemplate copy(ModelTemplate template, Identifier renderType) {
        return template.extend().renderType(renderType).build();
    }
}
