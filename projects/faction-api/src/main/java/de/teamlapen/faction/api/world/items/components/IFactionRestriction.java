package de.teamlapen.faction.api.world.items.components;

import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.factions.IFactionPlayerHandler;
import de.teamlapen.faction.api.factions.skills.ISkill;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;

import java.util.Optional;

public interface IFactionRestriction {

    /**
     * Factions that are allowed to use this object
     */
    HolderSet<IFaction<?>> factions();

    /**
     * Skills that are required to use this object.
     */
    Optional<HolderSet<ISkill<?>>> skills();

    /**
     * Minimum level required to use this object.
     */
    Optional<Integer> minLevel();

    Result canUse(IFactionPlayerHandler player);

    record Result(Optional<Component> message, boolean success) {
        public static final Result SUCCESS = new Result(Optional.empty(), true);
        public static final Result WRONG_FACTION = new Result(Optional.of(Component.translatable("text.factionapi.restriction.can_not_be_used_faction")), false);
        public static final Result MISSING_SKILLS = new Result(Optional.of(Component.translatable("text.factionapi.restriction.can_not_be_used_skill")), false);
        public static final Result MISSING_LEVEL = new Result(Optional.of(Component.translatable("text.factionapi.restriction.can_not_be_used_level")), false);
    }
}
