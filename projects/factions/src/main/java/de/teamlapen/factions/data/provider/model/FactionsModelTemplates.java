package de.teamlapen.factions.data.provider.model;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;

public class FactionsModelTemplates {

    public static final ModelTemplate TOTEM_TOP = create(FResourceLocation.mod("totem_top"));
    public static final ModelTemplate TOTEM_TOP_CRAFTED = create(FResourceLocation.mod("totem_top_crafted"));

    public static ModelTemplate create(Identifier modelName, TextureSlot... textures) {
        return ModelTemplates.create(modelName.toString(), textures);
    }
}
