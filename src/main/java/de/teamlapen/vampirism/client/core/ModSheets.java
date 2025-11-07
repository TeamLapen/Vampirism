package de.teamlapen.vampirism.client.core;

import net.minecraft.client.renderer.MaterialMapper;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.Comparator;

public class ModSheets {

//    public static final ResourceLocation BED_SHEET = VResourceLocation.mod("textures/atlas/coffin.png");

    public static final MaterialMapper COFFIN_MAPPER = new MaterialMapper(TextureAtlas.LOCATION_BLOCKS, "block/coffin");

    private static final Material[] COFFIN_TEXTURES = Arrays.stream(DyeColor.values())
            .sorted(Comparator.comparingInt(DyeColor::getId))
            .map(ModSheets::createCoffinMaterial)
            .toArray(Material[]::new);

    public static Material createCoffinMaterial(DyeColor color) {
        return COFFIN_MAPPER.apply(Sheets.colorToResourceMaterial(color));
    }

    public static Material getBedMaterial(DyeColor color) {
        return COFFIN_TEXTURES[color.getId()];
    }
}
