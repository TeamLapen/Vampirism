package de.teamlapen.vampirism.data.recipebuilder;

import net.minecraft.resources.ResourceLocation;

public interface SkillNodeOutput {

    ResourceLocation accept(ResourceLocation id, FinishedSkillNode skillNode);
}
