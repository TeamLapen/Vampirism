package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.registries.DeferredFaction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.resources.ResourceLocation;

import static de.teamlapen.vampirism.api.APIUtil.factionHolder;

public class VampirismFactions {
    public static final DeferredFaction<IVampirePlayer, IPlayableFaction<IVampirePlayer>> VAMPIRE = factionHolder(Keys.VAMPIRE);
    public static final DeferredFaction<IHunterPlayer, IPlayableFaction<IHunterPlayer>> HUNTER = factionHolder(Keys.HUNTER);

    public static class Keys {
        public static final ResourceLocation VAMPIRE = VResourceLocation.mod("vampire");
        public static final ResourceLocation HUNTER = VResourceLocation.mod("hunter");
    }
}
