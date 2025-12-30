package de.teamlapen.vampirism.api;

import de.teamlapen.faction.api.factions.IPlayableFaction;
import de.teamlapen.faction.api.registries.factions.DeferredFaction;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import net.minecraft.resources.Identifier;

import static de.teamlapen.vampirism.api.APIUtil.factionHolder;


public class VampirismFactions {
    public static final DeferredFaction<IVampirePlayer, IPlayableFaction<IVampirePlayer>> VAMPIRE = factionHolder(Keys.VAMPIRE);
    public static final DeferredFaction<IHunterPlayer, IPlayableFaction<IHunterPlayer>> HUNTER = factionHolder(Keys.HUNTER);

    public static class Keys {
        public static final Identifier VAMPIRE = VIdentifier.mod("vampire");
        public static final Identifier HUNTER = VIdentifier.mod("hunter");
    }
}
