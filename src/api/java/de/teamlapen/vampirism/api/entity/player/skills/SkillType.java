package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public enum SkillType implements ISkillType {
    LEVEL(new ResourceLocation("vampirism", "level"),"", faction -> true, faction -> true),
    LORD(new ResourceLocation("vampirism", "lord"),"_lord", IPlayableFaction::hasLordSkills , handler -> handler.getLordLevel() > 0);

    public final ResourceLocation id;
    public final String nameSuffix;
    public final Predicate<IPlayableFaction<?>> isForFaction;
    public final Predicate<IFactionPlayerHandler> isUnlocked;

    SkillType(ResourceLocation id, String nameSuffix, Predicate<IPlayableFaction<?>> isForFaction, Predicate<IFactionPlayerHandler> isUnlocked) {
        this.id = id;
        this.nameSuffix = nameSuffix;
        this.isForFaction = isForFaction;
        this.isUnlocked = isUnlocked;
    }

    public ResourceLocation createIdForFaction(@NotNull ResourceLocation id) {
        return new ResourceLocation(id.getNamespace(), id.getPath() + this.nameSuffix);
    }

    @Override
    public boolean isForFaction(@NotNull IPlayableFaction<?> faction) {
        return this.isForFaction.test(faction);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return this.id;
    }

    @Override
    public boolean isUnlocked(IFactionPlayerHandler handler) {
        return this.isUnlocked.test(handler);
    }

}
