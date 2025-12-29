package de.teamlapen.factions.api.factions.lord;

import de.teamlapen.factions.api.factions.IPlayableFaction;
import de.teamlapen.factions.api.factions.skills.ISkillPlayer;
import de.teamlapen.factions.api.world.entities.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ILordPlayer<T extends ILordPlayer<T>> extends IFactionPlayer<T> {

    IPlayableFaction.TitleGender titleGender();

    int getLordLevel();

    int getMaxMinions();

    @Nullable
    Component getLordTitle();

    @Nullable
    Component getLordTitleShort();


    @SuppressWarnings("NullableProblems")
    default Optional<ISkillPlayer<?>> asSkillPlayer() {
        return Optional.ofNullable(this instanceof ISkillPlayer<?> skillPlayer ? skillPlayer : null);
    }
}
