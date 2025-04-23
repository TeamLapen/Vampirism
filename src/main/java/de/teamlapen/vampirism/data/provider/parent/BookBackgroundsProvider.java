package de.teamlapen.vampirism.data.provider.parent;

import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.items.component.VampireBook.BookBackground;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class BookBackgroundsProvider implements DataProvider {

    private final PackOutput.PathProvider pathProvider;

    public BookBackgroundsProvider(PackOutput output) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "vampire_book_backgrounds");
    }

    protected abstract void registerBackgrounds(BiConsumer<ResourceLocation, BookBackground> output);

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput output) {
        Map<ResourceLocation, BookBackground> map = new HashMap<>();

        registerBackgrounds((id, background) -> {
            if (map.putIfAbsent(id, background) != null) {
                throw new IllegalStateException("Tried to register vampire book background twice for id: " + id);
            }
        });

        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (Map.Entry<ResourceLocation, BookBackground> entry : map.entrySet()) {
            Path path = this.pathProvider.json(entry.getKey());
            BookBackground background = entry.getValue();
            BookBackground.CODEC.encodeStart(JsonOps.INSTANCE, background).result().ifPresent(jsonElement -> futures.add(DataProvider.saveStable(output, jsonElement, path)));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Vampire Book Backgrounds";
    }
}
