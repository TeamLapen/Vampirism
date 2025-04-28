package de.teamlapen.vampirism.data.provider.parent;

import com.google.gson.JsonObject;
import de.teamlapen.vampirism.misc.BookBackground;
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
            JsonObject json = entry.getValue().encode();
            futures.add(DataProvider.saveStable(output, json, path));
        }

        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Vampire Book Backgrounds";
    }
}
