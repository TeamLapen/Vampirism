package de.teamlapen.vampirism.api.entity.player.hunter;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.hunter.IHunter;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for the hunter player data
 * Attached to all players as capability
 */
public interface IHunterPlayer extends IFactionPlayer<IHunterPlayer>, IHunter {

    /**
     * Call when the player does something that would break their disguise (Call regardless of the current disguise state)
     */
    void breakDisguise();

    /**
     * updates attributes of all minions
     */
    void updateMinionAttributes(boolean increasedStats);

    @Override
    @NotNull
    default Holder<? extends IPlayableFaction<IHunterPlayer>> getFaction() {
        return VampirismFactions.HUNTER;
    }
}
