package de.teamlapen.faction.data.provider.model;

import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.resources.Identifier;

public class FactionsModelTemplates {

    public static final ModelTemplate TOTEM = create(FIdentifier.mod("totem_top"), FactionsTextureSlot.OUTER);

    public static ModelTemplate create(Identifier modelName, TextureSlot... textures) {
        return ModelTemplates.create(modelName.toString(), textures);
    }
}
