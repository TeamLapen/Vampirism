package de.teamlapen.vampirism.data.provider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class ConvertiblesGenerator implements DataProvider {

    protected final PackOutput.PathProvider pathProvider;
    private final String modId;

    public ConvertiblesGenerator(PackOutput packOutput, String modId) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "vampirism");
        this.modId = modId;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        Map<EntityType<?>, ConvertiblesReloadListener.EntityEntry> entries = new HashMap<>();
        this.registerConvertibles((EntityType<? extends PathfinderMob> type, ConvertiblesReloadListener.EntityEntry entry) -> {
            if (entries.containsKey(type)) {
                throw new IllegalStateException("Duplicate entry for " + type);
            } else {
                entries.put(type, entry);
            }
        });
        return CompletableFuture.allOf(entries.entrySet().stream().map(entry -> {
            ResourceLocation id = RegUtil.id(entry.getKey());
            JsonElement jsonElement = ConvertiblesReloadListener.EntityEntry.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow(false, System.err::println);
            return DataProvider.saveStable(pOutput, jsonElement, pathProvider.json(new ResourceLocation(this.modId, "convertibles/" + id.getNamespace() + "/" + id.getPath())));
        }).toArray(CompletableFuture[]::new));
    }

    private void registerConvertibles(BiConsumer<EntityType<? extends PathfinderMob>, ConvertiblesReloadListener.EntityEntry> consumer) {
        Function<String, ResourceLocation> overlay = (String name) -> new ResourceLocation(REFERENCE.MODID, String.format("textures/entity/vanilla/%s_overlay.png", name));

        consumer.accept(EntityType.COW, new ConvertiblesReloadListener.EntityEntry(overlay.apply("cow"), ModEntities.COW_CONVERTING_HANDLER));
        consumer.accept(EntityType.LLAMA, new ConvertiblesReloadListener.EntityEntry(overlay.apply("llama")));
        consumer.accept(EntityType.OCELOT, new ConvertiblesReloadListener.EntityEntry(overlay.apply("ocelot")));
        consumer.accept(EntityType.PANDA, new ConvertiblesReloadListener.EntityEntry(overlay.apply("panda")));
        consumer.accept(EntityType.PIG, new ConvertiblesReloadListener.EntityEntry(overlay.apply("pig")));
        consumer.accept(EntityType.POLAR_BEAR, new ConvertiblesReloadListener.EntityEntry(overlay.apply("polar_bear")));
        consumer.accept(EntityType.RABBIT, new ConvertiblesReloadListener.EntityEntry(overlay.apply("rabbit")));
        consumer.accept(EntityType.SHEEP, new ConvertiblesReloadListener.EntityEntry(overlay.apply("sheep"), ModEntities.SHEEP_CONVERTING_HANDLER));
        consumer.accept(EntityType.VILLAGER, new ConvertiblesReloadListener.EntityEntry(ModEntities.VILLAGER_CONVERTING_HANDLER));
        consumer.accept(EntityType.HORSE, new ConvertiblesReloadListener.EntityEntry(overlay.apply("horse"), ModEntities.HORSE_CONVERTING_HANDLER));
        consumer.accept(EntityType.DONKEY, new ConvertiblesReloadListener.EntityEntry(overlay.apply("donkey"), ModEntities.DONKEY_CONVERTING_HANDLER));
        consumer.accept(EntityType.MULE, new ConvertiblesReloadListener.EntityEntry(overlay.apply("mule"), ModEntities.MULE_CONVERTING_HANDLER));
        consumer.accept(EntityType.FOX, new ConvertiblesReloadListener.EntityEntry(overlay.apply("fox"), ModEntities.FOX_CONVERTING_HANDLER));
        consumer.accept(EntityType.GOAT, new ConvertiblesReloadListener.EntityEntry(overlay.apply("goat"), ModEntities.GOAT_CONVERTING_HANDLER));
    }

    @Override
    public @NotNull String getName() {
        return "Vampirism Convertible Entities";
    }
}
