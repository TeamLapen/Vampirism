package de.teamlapen.vampirism.common.util.supporter;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.Factions;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record Supporter(Identifier faction, Component name, String player, Map<String, String> appearance, Optional<String> bookId) {

    public static final Supporter FALLBACK = new Supporter(Factions.Keys.NEUTRAL, Component.empty(), "", Map.of(), Optional.empty());

    public static final MapCodec<Supporter> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            Identifier.CODEC.fieldOf("faction").forGetter(Supporter::faction),
            ComponentSerialization.CODEC.fieldOf("name").forGetter(Supporter::name),
            Codec.STRING.fieldOf("player").forGetter(Supporter::player),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).fieldOf("appearance").forGetter(Supporter::appearance),
            Codec.STRING.optionalFieldOf("bookId").forGetter(Supporter::bookId)
    ).apply(inst, Supporter::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, Supporter> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, Supporter::faction,
            ComponentSerialization.STREAM_CODEC, Supporter::name,
            ByteBufCodecs.STRING_UTF8, Supporter::player,
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, ByteBufCodecs.STRING_UTF8), Supporter::appearance,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8), Supporter::bookId,
            Supporter::new
    );

    public static Supporter defaultSupplier(IAttachmentHolder attachment) {
        return FALLBACK;
    }
}
