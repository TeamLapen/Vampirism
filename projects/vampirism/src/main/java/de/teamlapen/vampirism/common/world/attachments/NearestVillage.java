package de.teamlapen.vampirism.common.world.attachments;

import com.google.common.base.Stopwatch;
import de.teamlapen.factions.common.world.blockentity.TotemBlockEntity;
import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.core.ModBiomes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Util;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class NearestVillage {

    private static final Logger LOGGER = LogManager.getLogger();

    public static NearestVillage get(@NotNull BlockEntity level) {
        return level.getData(ModAttachments.NEAREST_VILLAGE);
    }

    public NearestVillage(ServerLevel level, BlockPos center) {
        setupVampireForestSearch(level, center);
    }

    private CompletableFuture<BlockPos> closestVampireForest = null;

    private void setupVampireForestSearch(@NotNull ServerLevel level, @NotNull BlockPos center) {
        if (closestVampireForest == null) {
            final ResourceKey<Biome> biomeId = ModBiomes.VAMPIRE_FOREST;
            closestVampireForest = CompletableFuture.supplyAsync(() -> {
                Stopwatch stopwatch = Stopwatch.createStarted(Util.TICKER);
                com.mojang.datafixers.util.Pair<BlockPos, Holder<Biome>> location = level.findClosestBiome3d(b -> b.is(biomeId), center, 5000, 8, 16);
                LOGGER.debug("Looking for vampire forest took {}s", (double) stopwatch.stop().elapsed(TimeUnit.MILLISECONDS) / 1000.0D);
                return location == null ? null : location.getFirst();
            }, Util.backgroundExecutor()).handle((result, exception) -> result);
        }
    }

    public Optional<BlockPos> getClosestVampireForest() {
        return Optional.ofNullable(closestVampireForest).filter(CompletableFuture::isDone).map(CompletableFuture::join);
    }

    public static class Factory implements Function<IAttachmentHolder, NearestVillage> {

        @Override
        public NearestVillage apply(IAttachmentHolder holder) {
            if (holder instanceof TotemBlockEntity entity && entity.getLevel() instanceof ServerLevel level) {
                return new NearestVillage(level, entity.getBlockPos());
            }
            throw new IllegalArgumentException("Cannot create nearest village attachment for holder " + holder.getClass() + ". Expected server TotemBlockEntity");
        }
    }
}
