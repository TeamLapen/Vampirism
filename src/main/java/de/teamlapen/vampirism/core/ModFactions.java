package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismFactions;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.registries.DeferredFaction;
import de.teamlapen.vampirism.api.registries.DeferredFactionRegister;
import de.teamlapen.vampirism.entity.factions.PlayableFaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@SuppressWarnings("deprecation")
public class ModFactions {

    public static final DeferredFactionRegister FACTIONS = DeferredFactionRegister.create(REFERENCE.MODID);

    public static final DeferredFaction<IVampirePlayer, IPlayableFaction<IVampirePlayer>> VAMPIRE = FACTIONS.registerFaction(VampirismFactions.Keys.VAMPIRE.getPath(), () -> VReference.VAMPIRE_FACTION);
    public static final DeferredFaction<IHunterPlayer, IPlayableFaction<IHunterPlayer>> HUNTER = FACTIONS.registerFaction(VampirismFactions.Keys.HUNTER.getPath(), () -> VReference.HUNTER_FACTION);

    static void register(IEventBus bus) {
        FACTIONS.register(bus);
    }

}
