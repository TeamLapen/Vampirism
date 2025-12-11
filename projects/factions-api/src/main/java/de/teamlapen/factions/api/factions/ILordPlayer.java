package de.teamlapen.factions.api.factions;

import de.teamlapen.factions.api.entities.player.IFactionPlayer;
import de.teamlapen.factions.api.skills.ISkillPlayer;
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
