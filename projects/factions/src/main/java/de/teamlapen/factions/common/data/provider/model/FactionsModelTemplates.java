package de.teamlapen.factions.common.data.provider.model;

import de.teamlapen.factions.api.util.FResourceLocation;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.ResourceLocation;

public class FactionsModelTemplates {

    public static final ModelTemplate TOTEM = create(FResourceLocation.mod("totem_top"), FactionsTextureSlot.OUTER);

    public static ModelTemplate create(ResourceLocation modelName, TextureSlot... textures) {
        return ModelTemplates.create(modelName.toString(), textures);
    }
}
