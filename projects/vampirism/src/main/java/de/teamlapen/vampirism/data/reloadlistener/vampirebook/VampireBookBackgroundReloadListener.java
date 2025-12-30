package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class VampireBookBackgroundReloadListener extends SimpleJsonResourceReloadListener<BookBackground> {

    public static final Identifier ID = VIdentifier.mod("vampire_book_backgrounds");

    private Map<Identifier, BookBackground> backgrounds = Map.of();

    protected VampireBookBackgroundReloadListener() {
        super(BookBackground.CODEC, FileToIdConverter.json("vampire_book_backgrounds"));
    }

    @Override
    protected void apply(@NotNull Map<Identifier, BookBackground> backgrounds, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        this.backgrounds = Collections.unmodifiableMap(backgrounds);
    }

    public Map<Identifier, BookBackground> getBackgrounds() {
        return this.backgrounds;
    }
}
