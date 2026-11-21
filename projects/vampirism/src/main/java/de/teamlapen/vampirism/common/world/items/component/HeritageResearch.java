package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Optional;
import java.util.UUID;

/**
 * Research progress of a heritage book.
 *
 * @param subject    the book name the research was started for. Renaming the book invalidates the research
 * @param progress   research time in ticks
 * @param researcher the player who last placed the book on a lectern for research
 */
public record HeritageResearch(String subject, int progress, Optional<UUID> researcher) {

    public static final Codec<HeritageResearch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("subject").forGetter(HeritageResearch::subject),
            Codec.INT.fieldOf("progress").forGetter(HeritageResearch::progress),
            UUIDUtil.CODEC.optionalFieldOf("researcher").forGetter(HeritageResearch::researcher)
    ).apply(instance, HeritageResearch::new));
    public static final StreamCodec<ByteBuf, HeritageResearch> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HeritageResearch::subject,
            ByteBufCodecs.VAR_INT, HeritageResearch::progress,
            ByteBufCodecs.optional(UUIDUtil.STREAM_CODEC), HeritageResearch::researcher,
            HeritageResearch::new
    );
}
