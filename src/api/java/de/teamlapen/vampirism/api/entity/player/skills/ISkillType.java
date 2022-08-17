package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public interface ISkillType {

    /**
     * Creates an id for this skill type depending on the faction id.
     * <br>
     * Commonly this appends a suffix to the faction id
     *
     * @param id the faction id
     * @return skill type id
     */
    ResourceLocation createIdForFaction(@NotNull ResourceLocation id);

    /**
     * Tests if this skill type is used for the given faction
     *
     * @param faction the faction to test
     * @return {@code true} if the faction can use this skill type
     */
    boolean isForFaction(@NotNull IPlayableFaction<?> faction);

    /**
     * @return the unique identifier of this skill type
     */
    ResourceLocation getRegistryName();

    /**
     * Checks if the player can use this skill type
     *
     * @param handler the faction player handler of the player
     * @return {@code true} fit the player can use this skill type
     */
    boolean isUnlocked(IFactionPlayerHandler handler);
}
