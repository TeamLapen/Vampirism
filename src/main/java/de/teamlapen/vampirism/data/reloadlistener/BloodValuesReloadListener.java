package de.teamlapen.vampirism.data.reloadlistener;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.data.reloadlistener.bloodvalues.BloodValueBuilder;
import de.teamlapen.vampirism.data.reloadlistener.bloodvalues.BloodValueReader;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BloodValuesReloadListener implements PreparableReloadListener {

    public final ConvertiblesReloadListener entities = new ConvertiblesReloadListener(new BloodValueReader(this::applyNewEntitiesResources, "vampirism/bloodvalues/entities", "entities"));
    public final BloodValueReader items = new BloodValueReader(BloodConversionRegistry::applyNewItemResources, "vampirism/bloodvalues/items", "items");
    public final BloodValueReader fluids = new BloodValueReader(BloodConversionRegistry::applyNewFluidResources, "vampirism/bloodvalues/fluids", "fluids");

    @NotNull
    @Override
    public CompletableFuture<Void> reload(@NotNull PreparationBarrier stage, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler1, @NotNull ProfilerFiller profiler2, @NotNull Executor pBackgroundExecutor, @NotNull Executor pGameExecutor) {
        var entities = this.entities.prepare(resourceManager, pBackgroundExecutor);
        CompletableFuture<Map<String, BloodValueBuilder>> items = this.items.prepare(resourceManager, pBackgroundExecutor);
        CompletableFuture<Map<String, BloodValueBuilder>> fluids = this.fluids.prepare(resourceManager, pBackgroundExecutor);
        return CompletableFuture.allOf(entities, items, fluids).thenCompose(stage::wait).thenAcceptAsync(o -> {
            this.entities.apply(entities.join());
            this.items.load(items.join());
            this.fluids.load(fluids.join());
        }, pGameExecutor);
    }

    private void applyNewEntitiesResources(@NotNull Map<ResourceLocation, Float> map) {
        BloodConversionRegistry.applyNewEntitiesResources(map);
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(map);
    }
}
