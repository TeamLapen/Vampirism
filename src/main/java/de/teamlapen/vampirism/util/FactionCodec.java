package de.teamlapen.vampirism.util;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;

public class FactionCodec {

    @SuppressWarnings({"RedundantCast", "unchecked"})
    public static Codec<Holder<? extends IPlayableFaction<?>>> playable() {
        return ModRegistries.FACTIONS.holderByNameCodec().xmap(s -> (Holder<? extends IPlayableFaction<?>>) (Object) s, s -> (Holder<IFaction<?>>) (Object) s);
    }

    @SuppressWarnings("unchecked")
    public static Codec<Holder<? extends IFaction<?>>> faction() {
        return (Codec<Holder<? extends IFaction<?>>>) (Object)ModRegistries.FACTIONS.holderByNameCodec();
    }
}
