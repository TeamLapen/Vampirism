package de.teamlapen.vampirism.data.provider;

import com.google.common.collect.Sets;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.data.reloadlistener.SingleJigsawReloadListener;
import de.teamlapen.vampirism.world.gen.VanillaStructureModifications;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class SingleJigsawPiecesGenerator implements DataProvider {

    private static final Logger LOGGER = LogManager.getLogger();

    protected final PackOutput.PathProvider pathProvider;
    private final String modId;

    public SingleJigsawPiecesGenerator(PackOutput packOutput, String modId) {
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.DATA_PACK, "vampirism");
        this.modId = modId;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput pOutput) {
        Set<ResourceLocation> set = Sets.newHashSet();
        this.registerSingleJigsawPieces(set::add);

        return DataProvider.saveStable(pOutput, SingleJigsawReloadListener.CODEC.encodeStart(JsonOps.INSTANCE, new ArrayList<>(set)).getOrThrow(false, LOGGER::error), pathProvider.json(new ResourceLocation(modId, "single_jigsaw_pieces")));
    }

    @Override
    public @NotNull String getName() {
        return "Single Jigsaw Pieces";
    }

    protected void registerSingleJigsawPieces(Consumer<ResourceLocation> consumer) {
        consumer.accept(new ResourceLocation("vampirism", "village/totem"));
        Arrays.stream(VanillaStructureModifications.BiomeType.values()).map((type) -> new ResourceLocation("vampirism", "village/" + type.path + "/houses/hunter_trainer")).forEach(consumer);
    }
}
