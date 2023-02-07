package de.teamlapen.vampirism.config;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.config.bloodvalues.BloodValueBuilder;
import de.teamlapen.vampirism.config.bloodvalues.BloodValueReader;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BloodValues implements PreparableReloadListener {

    public final BloodValueReader<ResourceLocation> entities = new BloodValueReader<>(this::applyNewEntitiesResources, "vampirism/bloodvalues/entities", "entities");
    public final BloodValueReader<ResourceLocation> items = new BloodValueReader<>(BloodConversionRegistry::applyNewItemResources, "vampirism/bloodvalues/items", "items");
    public final BloodValueReader<ResourceLocation> fluids = new BloodValueReader<>(BloodConversionRegistry::applyNewFluidResources, "vampirism/bloodvalues/fluids", "fluids");

    @NotNull
    @Override
    public CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler1, @NotNull ProfilerFiller profiler2, @NotNull Executor pBackgroundExecutor, @NotNull Executor pGameExecutor) {
        CompletableFuture<Map<String, BloodValueBuilder>> entities = this.entities.prepare(resourceManager, pBackgroundExecutor);
        CompletableFuture<Map<String, BloodValueBuilder>> items = this.items.prepare(resourceManager, pBackgroundExecutor);
        CompletableFuture<Map<String, BloodValueBuilder>> fluids = this.fluids.prepare(resourceManager, pBackgroundExecutor);
        return CompletableFuture.allOf(entities, items, fluids).thenCompose(stage::wait).thenAcceptAsync(o -> {
            this.entities.load(entities.join());
            this.items.load(items.join());
            this.fluids.load(fluids.join());
        }, pGameExecutor);
    }

    private void applyNewEntitiesResources(@NotNull Map<ResourceLocation, Float> map) {
        BloodConversionRegistry.applyNewEntitiesResources(map);
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(map);
    }
}
