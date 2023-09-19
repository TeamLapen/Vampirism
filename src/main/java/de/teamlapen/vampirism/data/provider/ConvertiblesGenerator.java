package de.teamlapen.vampirism.data.provider;

import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.data.reloadlistener.ConvertiblesReloadListener;
import de.teamlapen.vampirism.entity.converted.converter.SpecialConverter;
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

        consumer.accept(EntityType.COW, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_COW), overlay.apply("cow")));
        consumer.accept(EntityType.LLAMA, new ConvertiblesReloadListener.EntityEntry(overlay.apply("llama")));
        consumer.accept(EntityType.OCELOT, new ConvertiblesReloadListener.EntityEntry(overlay.apply("cat")));
        consumer.accept(EntityType.PANDA, new ConvertiblesReloadListener.EntityEntry(overlay.apply("panda")));
        consumer.accept(EntityType.PIG, new ConvertiblesReloadListener.EntityEntry(overlay.apply("pig")));
        consumer.accept(EntityType.POLAR_BEAR, new ConvertiblesReloadListener.EntityEntry(overlay.apply("polarbear")));
        consumer.accept(EntityType.RABBIT, new ConvertiblesReloadListener.EntityEntry(overlay.apply("rabbit")));
        consumer.accept(EntityType.SHEEP, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_SHEEP), overlay.apply("sheep")));
        consumer.accept(EntityType.VILLAGER, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.VILLAGER_CONVERTED), overlay.apply("villager")));
        consumer.accept(EntityType.HORSE, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_HORSE), overlay.apply("horse")));
        consumer.accept(EntityType.DONKEY, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_DONKEY), overlay.apply("horse")));
        consumer.accept(EntityType.MULE, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_MULE), overlay.apply("horse")));
        consumer.accept(EntityType.FOX, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_FOX), overlay.apply("fox")));
        consumer.accept(EntityType.GOAT, new ConvertiblesReloadListener.EntityEntry(new SpecialConverter<>(ModEntities.CONVERTED_GOAT), overlay.apply("goat")));
    }

    @Override
    public @NotNull String getName() {
        return "Vampirism Convertible Entities";
    }
}
