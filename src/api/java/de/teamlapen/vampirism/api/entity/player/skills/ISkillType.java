package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public interface ISkillType {

    /**
     * creates a id for this skill type depending on the faction id
     *
     * @param id the faction id
     * @return skill type id
     */
    ResourceLocation createIdForFaction(@Nonnull ResourceLocation id);

    boolean isForFaction(@Nonnull IPlayableFaction faction);

    ResourceLocation getRegistryName();

    boolean isUnlocked(IFactionPlayerHandler handler);
}
