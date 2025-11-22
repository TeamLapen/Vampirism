package de.teamlapen.vampirism.data.provider.base;

import de.teamlapen.vampirism.data.reloadlistener.vampirebook.BookBackground;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
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

        return DataProvider.saveAll(output, BookBackground.CODEC, this.pathProvider::json, map);
    }

    @Override
    public @NotNull String getName() {
        return "Vampire Book Backgrounds";
    }
}
