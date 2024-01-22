package de.teamlapen.vampirism.world.fog;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.core.ModAttachments;
import de.teamlapen.vampirism.util.CodecUtil;
import de.teamlapen.vampirism.world.garlic.GarlicLevel;
import de.teamlapen.vampirism.world.garlic.GarlicServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FogLevel implements IFogHandler {

    public static Optional<FogLevel> getOpt(@NotNull Level level) {
        return Optional.ofNullable(level.getData(ModAttachments.LEVEL_FOG));
    }

    public static FogLevel get(@NotNull Level level) {
        return level.getData(ModAttachments.LEVEL_FOG);
    }

    protected final Map<BlockPos, Emitter> fogAreas = new ConcurrentHashMap<>();
    protected final Map<BlockPos, Emitter> tmpFogAreas = new ConcurrentHashMap<>();

    @Override
    public boolean isInsideArtificialVampireFogArea(@NotNull BlockPos blockPos) {
        return Stream.concat(this.fogAreas.entrySet().stream(), this.tmpFogAreas.entrySet().stream()).anyMatch(entry -> entry.getValue().box.contains(blockPos.getCenter()));
    }

    @Override
    public void updateArtificialFogBoundingBox(@NotNull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            this.fogAreas.remove(totemPos);
            this.notifyRemove(totemPos, false);
            updateTemporaryArtificialFog(totemPos, null);
        } else {
            Emitter emitter = new Emitter(totemPos, box, false);
            this.fogAreas.put(totemPos, emitter);
            this.notifyChange(emitter);
        }
    }

    @Override
    public void updateTemporaryArtificialFog(@NotNull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            this.tmpFogAreas.remove(totemPos);
            this.notifyRemove(totemPos, true);
        } else {
            Emitter emitter = new Emitter(totemPos, box, true);
            this.tmpFogAreas.put(totemPos, emitter);
            this.notifyChange(emitter);

        }
    }

    @Override
    public void clearCache() {
        this.fogAreas.clear();
        this.tmpFogAreas.clear();
        this.notifyClear();
    }

    protected void notifyChange(Emitter emitter) {

    }

    protected void notifyRemove(BlockPos pos, boolean temp) {

    }

    protected void notifyClear() {

    }

    public void updatePlayer(ServerPlayer player) {

    }

    public void fill(List<Emitter> emitters, List<Emitter> emitters1) {
        this.clearCache();
        this.fogAreas.putAll(emitters.stream().collect(Collectors.toMap(x -> x.totemPos, x -> x)));
        this.tmpFogAreas.putAll(emitters1.stream().collect(Collectors.toMap(x -> x.totemPos, x -> x)));
    }

    public void add(Emitter emitter) {
        if (emitter.temp) {
            this.tmpFogAreas.put(emitter.totemPos, emitter);
        } else {
            this.fogAreas.put(emitter.totemPos, emitter);
        }
    }

    public void remove(BlockPos emitter, boolean temp) {
        if (!temp) {
            this.fogAreas.remove(emitter);
        }
        this.tmpFogAreas.remove(emitter);

    }

    public record Emitter(BlockPos totemPos, AABB box, boolean temp) {
        public static final Codec<Emitter> CODEC = RecordCodecBuilder.create(inst ->
                inst.group(
                        BlockPos.CODEC.fieldOf("totemPos").forGetter(s -> s.totemPos),
                        CodecUtil.AABB.fieldOf("box").forGetter(s -> s.box),
                        Codec.BOOL.fieldOf("temp").forGetter(s -> s.temp)
                ).apply(inst, Emitter::new)
        );
    }

    public static class Factory implements Function<IAttachmentHolder, FogLevel> {
        @Override
        public FogLevel apply(IAttachmentHolder holder) {
            if (holder instanceof Level) {
                if (holder instanceof ServerLevel serverLevel) {
                    return new FogServerLevel(serverLevel);
                } else {
                    return new FogLevel();
                }
            }
            throw new IllegalArgumentException("Cannot create level fog for holder " + holder.getClass() + ". Expected Level");
        }
    }
}
