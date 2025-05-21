package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.misc.BookBackground;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class VampireBookBackgroundReloadListener extends SimpleJsonResourceReloadListener<BookBackground> {

    public static final ResourceLocation ID = VResourceLocation.mod("vampire_book_backgrounds");

    private Map<ResourceLocation, BookBackground> backgrounds = Map.of();

    protected VampireBookBackgroundReloadListener() {
        super(BookBackground.CODEC, FileToIdConverter.json("vampire_book_backgrounds"));
    }

    @Override
    protected void apply(@NotNull Map<ResourceLocation, BookBackground> backgrounds, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        this.backgrounds = Collections.unmodifiableMap(backgrounds);
    }

    public Map<ResourceLocation, BookBackground> getBackgrounds() {
        return this.backgrounds;
    }
}
