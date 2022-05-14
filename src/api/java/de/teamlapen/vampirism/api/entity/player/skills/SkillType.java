package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.ResourceLocation;

import java.util.function.Predicate;

public enum SkillType implements ISkillType {
    LEVEL("", faction -> true),
    LORD("_lord", IPlayableFaction::hasLordSkills);

    public final String nameSuffix;
    public final Predicate<IPlayableFaction> isForFaction;

    SkillType(String nameSuffix, Predicate<IPlayableFaction> isForFaction) {
        this.nameSuffix = nameSuffix;
        this.isForFaction = isForFaction;
    }

    public ResourceLocation createIdForFaction(ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), id.getPath() + this.nameSuffix);
    }

    @Override
    public boolean isForFaction(IPlayableFaction faction) {
        return this.isForFaction.test(faction);
    }

}
