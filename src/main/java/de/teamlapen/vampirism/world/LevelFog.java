package de.teamlapen.vampirism.world;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.world.IFogHandler;
import de.teamlapen.vampirism.core.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class LevelFog implements IFogHandler {

    public static Optional<LevelFog> getOpt(@NotNull Level level) {
        return Optional.ofNullable(level.getData(ModAttachments.LEVEL_FOG));
    }

    private final Map<BlockPos, BoundingBox> fogAreas = new ConcurrentHashMap<>();
    private final Map<BlockPos, BoundingBox> tmpFogAreas = new ConcurrentHashMap<>();

    @Override
    public boolean isInsideArtificialVampireFogArea(@NotNull BlockPos blockPos) {
        return Stream.concat(this.fogAreas.entrySet().stream(), this.tmpFogAreas.entrySet().stream()).anyMatch(entry -> entry.getValue().isInside(blockPos));
    }

    @Override
    public void updateArtificialFogBoundingBox(@NotNull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            this.fogAreas.remove(totemPos);
            updateTemporaryArtificialFog(totemPos, null);
        } else {
            this.fogAreas.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }

    @Override
    public void updateTemporaryArtificialFog(@NotNull BlockPos totemPos, @Nullable AABB box) {
        if (box == null) {
            this.tmpFogAreas.remove(totemPos);
        } else {
            this.tmpFogAreas.put(totemPos, UtilLib.AABBtoMB(box));
        }
    }

    @Override
    public void clearCache() {
        this.fogAreas.clear();
        this.tmpFogAreas.clear();
    }
}
