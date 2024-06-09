package de.teamlapen.vampirism.util;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public class FactionCodec {

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Codec<ISkill<T>> skillCodec() {
        return ModRegistries.SKILLS.byNameCodec().xmap(s -> (ISkill<T>) s, s -> s);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Codec<Holder<ISkill<T>>> skillHolderCodec() {
        return ModRegistries.SKILLS.holderByNameCodec().xmap(s -> (Holder<ISkill<T>>) (Object) s, s -> (Holder<ISkill<?>>) (Object) s);
    }

    public static <B,T extends IFactionPlayer<T>> StreamCodec<RegistryFriendlyByteBuf,ISkill<T>> skillStreamCodec() {
        return ByteBufCodecs.registry(VampirismRegistries.Keys.SKILL).map(s -> (ISkill<T>) s, s -> s);
    }

    @SuppressWarnings("unchecked")
    public static <B,T extends IFactionPlayer<T>> StreamCodec<RegistryFriendlyByteBuf,Holder<ISkill<T>>> skillHolderStreamCodec() {
        return ByteBufCodecs.holderRegistry(VampirismRegistries.Keys.SKILL).map(s -> (Holder<ISkill<T>>) (Object) s, s -> (Holder<ISkill<?>>) (Object) s);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Codec<IAction<T>> actionCodec() {
        return ModRegistries.ACTIONS.byNameCodec().xmap(s -> (IAction<T>) s, s -> s);
    }

    public static Codec<Holder<? extends IPlayableFaction<?>>> playable() {
        return ModRegistries.FACTIONS.holderByNameCodec().xmap(s -> (Holder<? extends IPlayableFaction<?>>) (Object) s, s -> (Holder<IFaction<?>>) (Object) s);
    }

    public static Codec<Holder<? extends IFaction<?>>> faction() {
        return ModRegistries.FACTIONS.holderByNameCodec().xmap(s -> (Holder<? extends IFaction<?>>) (Object) s, s -> (Holder<IFaction<?>>) (Object) s);
    }
}
