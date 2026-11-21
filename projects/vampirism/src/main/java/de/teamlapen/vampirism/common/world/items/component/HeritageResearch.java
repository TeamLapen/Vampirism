package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * Research progress of a heritage book.
 *
 * @param subject  the book name the research was started for. Renaming the book invalidates the research
 * @param progress research time in ticks
 */
public record HeritageResearch(String subject, int progress) {

    public static final Codec<HeritageResearch> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("subject").forGetter(HeritageResearch::subject),
            Codec.INT.fieldOf("progress").forGetter(HeritageResearch::progress)
    ).apply(instance, HeritageResearch::new));
    public static final StreamCodec<ByteBuf, HeritageResearch> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, HeritageResearch::subject,
            ByteBufCodecs.VAR_INT, HeritageResearch::progress,
            HeritageResearch::new
    );
}
