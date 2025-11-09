package de.teamlapen.vampirism.api.util;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.core.HolderSet;

import java.util.Optional;

public interface IFactionRestriction {

    HolderSet<IFaction<?>> factions();

    Optional<HolderSet<ISkill<?>>> skills();

    Optional<Integer> minLevel();
}
