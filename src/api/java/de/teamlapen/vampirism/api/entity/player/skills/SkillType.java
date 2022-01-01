package de.teamlapen.vampirism.api.entity.player.skills;

import net.minecraft.util.ResourceLocation;

public enum SkillType {
    LEVEL(""), LORD("_lord");

    public final String nameSuffix;

    SkillType(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    public ResourceLocation id(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), id.getPath() + this.nameSuffix);
    }
}
