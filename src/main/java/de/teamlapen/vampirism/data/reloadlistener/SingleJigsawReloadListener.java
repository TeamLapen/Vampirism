package de.teamlapen.vampirism.data.reloadlistener;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.util.MixinHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SingleJigsawReloadListener implements PreparableReloadListener {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final String directory = "vampirism";
    private static final String fileName = "single_jigsaw_pieces.json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();

    public static final Codec<List<ResourceLocation>> CODEC = RecordCodecBuilder.create(inst -> inst.group(ResourceLocation.CODEC.listOf().fieldOf("single_pieces").forGetter(list -> list)).apply(inst, a -> a));

    @Override
    public @NotNull CompletableFuture<Void> reload(@NotNull PreparationBarrier pPreparationBarrier, @NotNull ResourceManager pResourceManager, @NotNull ProfilerFiller pPreparationsProfiler, @NotNull ProfilerFiller pReloadProfiler, @NotNull Executor pBackgroundExecutor, @NotNull Executor pGameExecutor) {
        return prepare(pResourceManager, pBackgroundExecutor).thenCompose(pPreparationBarrier::wait).thenAcceptAsync(MixinHooks::replaceSingleInstanceStructure);
    }

    public @NotNull CompletableFuture<List<ResourceLocation>> prepare(@NotNull ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> load(manager), executor);
    }

    public @NotNull List<ResourceLocation> load(ResourceManager manager) {
        List<ResourceLocation> locations = new ArrayList<>();
        for (Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks(directory, location -> location.getPath().endsWith(fileName)).entrySet()) {
            ResourceLocation resourceName = new ResourceLocation(entry.getKey().getNamespace(), entry.getKey().getPath().substring(directory.length() + 1, entry.getKey().getPath().length() - PATH_SUFFIX_LENGTH));
            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    List<ResourceLocation> newLocations = CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(false, LOGGER::error);
                    locations.addAll(newLocations);
                } catch (Exception e) {
                    LOGGER.error("Could not read single jigsaw pieces file {} from {}", resourceName, resource.sourcePackId(), e);
                }
            }
        }

        return locations;
    }

}
