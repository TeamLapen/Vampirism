package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.IExtendedCreatureVampirism;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.IVampirismWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class VampirismCapabilities {

    public static final Capability<IExtendedCreatureVampirism> EXTENDED_CREATURE = get(new CapabilityToken<>() {});
    public static final Capability<IFactionPlayerHandler> FACTION_HANDLER_PLAYER = get(new CapabilityToken<>(){});
    public static final Capability<IVampirismWorld> WORLD = get(new CapabilityToken<>(){});
    public static final Capability<IVampirePlayer> VAMPIRE_PLAYER = get(new CapabilityToken<>(){});
    public static final Capability<IHunterPlayer> HUNTER_PLAYER = get(new CapabilityToken<>(){});
}
