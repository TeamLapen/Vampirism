package de.teamlapen.vampirism.api.world.entity.player.hunter;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.factions.lord.ILordPlayer;
import de.teamlapen.faction.api.factions.skills.ISkillPlayer;
import de.teamlapen.faction.api.factions.tasks.ITaskPlayer;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.world.entity.hunter.IHunter;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.NotNull;

/**
 * Interface for the hunter player data
 * Attached to all players as capability
 */
public interface IHunterPlayer extends IFactionPlayer<IHunterPlayer>, IHunter, ISkillPlayer<IHunterPlayer>, ITaskPlayer<IHunterPlayer>, ILordPlayer<IHunterPlayer> {

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
