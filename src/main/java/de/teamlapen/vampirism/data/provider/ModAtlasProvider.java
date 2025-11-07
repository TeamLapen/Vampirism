package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.client.core.ModAtlases;
import de.teamlapen.vampirism.client.core.ModSheets;
import net.minecraft.client.data.AtlasProvider;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class ModAtlasProvider extends AtlasProvider {

    public ModAtlasProvider(PackOutput output) {
        super(output);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput p_400189_) {
        return CompletableFuture.allOf(
                this.storeAtlas(p_400189_, ModAtlases.COFFINS, simpleMapper(ModSheets.COFFIN_MAPPER))
        );
    }
}
