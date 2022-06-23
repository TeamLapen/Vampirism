package de.teamlapen.vampirism.config;

import de.teamlapen.lib.lib.config.BloodValueLoaderDynamic;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.general.BloodConversionRegistry;
import de.teamlapen.vampirism.config.bloodvalues.BloodValueBuilder;
import de.teamlapen.vampirism.config.bloodvalues.BloodValueReader;
import de.teamlapen.vampirism.entity.converted.VampirismEntityRegistry;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class BloodValues implements IFutureReloadListener {

    public final BloodValueReader<ResourceLocation> entities = new BloodValueReader<>(this::applyNewEntitiesResources, "vampirism/bloodvalues/entities", "entities");
    public final BloodValueReader<ResourceLocation> items = new BloodValueReader<>(BloodConversionRegistry::applyNewItemResources, "vampirism/bloodvalues/items", "items");
    public final BloodValueReader<ResourceLocation> fluids = new BloodValueReader<>(BloodConversionRegistry::applyNewFluidResources, "vampirism/bloodvalues/fluids", "fluids");

    @Nonnull
    @Override
    public CompletableFuture<Void> reload(IStage stage, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler1, @Nonnull IProfiler profiler2, @Nonnull Executor executor1, @Nonnull Executor executor2) {
        CompletableFuture<Map<ResourceLocation, BloodValueBuilder>> entities = this.entities.prepare(resourceManager, executor1);
        CompletableFuture<Map<ResourceLocation, BloodValueBuilder>> items = this.items.prepare(resourceManager, executor1);
        CompletableFuture<Map<ResourceLocation, BloodValueBuilder>> fluids = this.fluids.prepare(resourceManager, executor1);
        return CompletableFuture.allOf(entities, items, fluids).thenCompose(stage::wait).thenAcceptAsync(o -> {
            this.entities.load(entities.join());
            this.items.load(items.join());
            this.fluids.load(fluids.join());
        });
    }

    private void applyNewEntitiesResources(Map<ResourceLocation, Float> map) {
        BloodConversionRegistry.applyNewEntitiesResources(map);
        ((VampirismEntityRegistry) VampirismAPI.entityRegistry()).applyNewResources(map);
    }

    public static List<BloodValueLoaderDynamic> getDynamicLoader() {
        return BloodValueLoaderDynamic.getDynamicBloodLoader();
    }
}
