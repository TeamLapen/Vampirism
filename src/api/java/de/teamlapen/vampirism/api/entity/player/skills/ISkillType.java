package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.util.ResourceLocation;

public interface ISkillType {

    /**
     * creates a id for this skill type depending on the faction id
     *
     * @param id the faction id
     * @return skill type id
     */
    ResourceLocation createIdForFaction(ResourceLocation id);

    boolean isForFaction(IPlayableFaction faction);
}
