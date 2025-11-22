package de.teamlapen.factions.api.skills;

import com.mojang.serialization.Codec;
import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

public interface ISkillPointProvider {

    Codec<ISkillPointProvider> CODEC = Codec.lazyInitialized(() -> FactionRegistries.SKILL_POINT_PROVIDER.get().byNameCodec());

    /**
     * Get all skill points for the given player using this provider
     *
     * @param factionPlayer the player for which the skill points are checked
     * @return the skill points for the given player
     */
    int getSkillPoints(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> tree);

    /**
     * Should the player be able to unlock every skill without using skill points?
     *
     * @param factionPlayer the player for which the skill points are checked
     * @return true if the player should be able to unlock every skill without using skill points
     */
    default boolean ignoreSkillPointLimit(IFactionPlayer<?> factionPlayer, Holder<ISkillTree> tree) {
        return false;
    }
}
