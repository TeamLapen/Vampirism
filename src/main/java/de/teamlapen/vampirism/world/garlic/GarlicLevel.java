package de.teamlapen.vampirism.world.garlic;

import com.google.common.collect.Maps;
import com.ibm.icu.impl.duration.impl.DataRecord;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.world.IGarlicChunkHandler;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.util.CodecUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GarlicLevel implements IGarlicChunkHandler {

    @Deprecated
    public static Optional<GarlicLevel> getOpt(@NotNull Level level) {
        return Optional.of(level.getData(ModAttachments.LEVEL_GARLIC));
    }

    public static GarlicLevel get(@NotNull Level level) {
        return level.getData(ModAttachments.LEVEL_GARLIC);
    }

    private final HashMap<ChunkPos, EnumStrength> strengthHashMap = Maps.newHashMap();
    protected final HashMap<Integer, Emitter> emitterHashMap = Maps.newHashMap();

    @NotNull
    @Override
    public EnumStrength getStrengthAtChunk(ChunkPos pos) {
        EnumStrength s = strengthHashMap.get(pos);
        return s == null ? EnumStrength.NONE : s;
    }

    @Override
    public int registerGarlicBlock(EnumStrength strength, @NotNull List<ChunkPos> pos) {
        for (ChunkPos p : pos) {
            if (p == null) {
                throw new IllegalArgumentException("Garlic emitter position should not be null");
            }
        }
        Emitter e = new Emitter(strength, pos);
        int hash = e.hashCode();
        emitterHashMap.putIfAbsent(hash, e);
        notifyChange(e);
        rebuildStrengthMap();
        return hash;
    }

    protected void notifyChange(Emitter emitter) {

    }

    protected void notifyRemove(int emitter) {

    }

    protected void notifyClear() {

    }

    public void updatePlayer(ServerPlayer player) {

    }

    @Override
    public void removeGarlicBlock(int id) {
        Emitter e = emitterHashMap.remove(id);
        notifyRemove(id);
        rebuildStrengthMap();
    }

    private void rebuildStrengthMap() {
        strengthHashMap.clear();
        for (Emitter e : emitterHashMap.values()) {
            for (ChunkPos pos : e.pos) {
                EnumStrength old = strengthHashMap.get(pos);
                if (old == null || e.strength.isStrongerThan(old)) {
                    strengthHashMap.put(pos, e.strength);
                }

            }
        }
    }

    public void fill(List<Emitter> emitters) {
        this.clearCache();
        this.emitterHashMap.putAll(emitters.stream().collect(Collectors.toMap(Record::hashCode, x->x)));
        rebuildStrengthMap();
        notifyClear();
    }

    public void printDebug(@NotNull CommandSourceStack sender) {
        for (Emitter e : emitterHashMap.values()) {
            sender.sendSuccess(() -> Component.literal("E: " + e.toString()), true);
        }
        for (Map.Entry<ChunkPos, EnumStrength> e : strengthHashMap.entrySet()) {
            sender.sendSuccess(() -> Component.literal("S: " + e.toString()), true);
        }
    }

    @Override
    public void clearCache() {
        this.strengthHashMap.clear();
        this.emitterHashMap.clear();
        notifyClear();
    }

    public record Emitter(EnumStrength strength, List<ChunkPos> pos) {
        public static Codec<Emitter> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        StringRepresentable.fromEnum(EnumStrength::values).fieldOf("strength").forGetter(s -> s.strength),
                        CodecUtil.CHUNK_POS.listOf().fieldOf("pos").forGetter(x -> x.pos)
                ).apply(inst, Emitter::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, Emitter> STREAM_CODEC = StreamCodec.composite(
                NeoForgeStreamCodecs.enumCodec(EnumStrength.class), Emitter::strength,
                NeoForgeStreamCodecs.CHUNK_POS.apply(ByteBufCodecs.list()), Emitter::pos,
                Emitter::new
        );
    }

    public static class Factory implements Function<IAttachmentHolder, GarlicLevel> {
        @Override
        public GarlicLevel apply(IAttachmentHolder holder) {
            if (holder instanceof Level) {
                if (holder instanceof ServerLevel serverLevel) {
                    return new GarlicServerLevel(serverLevel);
                } else {
                    return new GarlicLevel();
                }
            }
            throw new IllegalArgumentException("Cannot create level garlic for holder " + holder.getClass() + ". Expected Level");
        }
    }
}
