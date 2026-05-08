package de.teamlapen.faction.api.factions.lord;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ILordPlayer<T extends ILordPlayer<T>> extends IFactionPlayer<T> {

    IPlayableFaction.TitleGender titleGender();

    int getLordLevel();

    default int getMaxLordLevel() {
        return getFaction().value().getHighestLordLevel();
    }

    int getMaxMinions();

    @Nullable
    Component getLordTitle();

    @Nullable
    Component getLordTitleShort();


    default Optional<ISkillPlayer<?>> asSkillPlayer() {
        return Optional.ofNullable(this instanceof ISkillPlayer<?> skillPlayer ? skillPlayer : null);
    }
}
