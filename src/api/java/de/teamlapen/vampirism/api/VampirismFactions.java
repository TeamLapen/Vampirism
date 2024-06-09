package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.registries.DeferredFaction;
import net.minecraft.resources.ResourceLocation;

import static de.teamlapen.vampirism.api.APIUtil.*;

public class VampirismFactions {
    public static final DeferredFaction<IVampirePlayer, IPlayableFaction<IVampirePlayer>> VAMPIRE = factionHolder(Keys.VAMPIRE);
    public static final DeferredFaction<IHunterPlayer, IPlayableFaction<IHunterPlayer>> HUNTER = factionHolder(Keys.HUNTER);

    public static class Keys {
        public static final ResourceLocation VAMPIRE = new ResourceLocation(VReference.MODID, "vampire");
        public static final ResourceLocation HUNTER = new ResourceLocation(VReference.MODID, "hunter");
    }
}
