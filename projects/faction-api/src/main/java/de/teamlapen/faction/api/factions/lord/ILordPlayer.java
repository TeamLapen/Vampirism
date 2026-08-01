package de.teamlapen.faction.api.factions.lord;

import de.teamlapen.faction.api.FactionsApi;
import de.teamlapen.faction.api.factions.IFactionEntity;
import de.teamlapen.faction.api.factions.IFactionExtension;
import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.IPlayableFactionEntity;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.world.entities.extensions.IPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface ILordPlayer extends IPlayableFactionEntity, IPlayer, IFactionExtension {

    IPlayableFaction.TitleGender titleGender();

    int getLordLevel();

    int getMaxLordLevel();

    int getMaxMinions();

    @Nullable
    Component getLordTitle();

    @Nullable
    Component getLordTitleShort();

    void updateMinionAttributes(boolean increasedStats);

    /**
     * Reset all lord tasks that should be available again for the player's current lord level.
     */
    void resetLordTasks();

    boolean setTitleGender(IPlayableFaction.TitleGender gender);

    default boolean setTitleGender(boolean female) {
        return setTitleGender(female ? IPlayableFaction.TitleGender.FEMALE : IPlayableFaction.TitleGender.MALE);
    }
}
