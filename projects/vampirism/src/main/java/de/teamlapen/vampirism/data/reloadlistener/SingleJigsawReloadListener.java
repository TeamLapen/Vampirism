package de.teamlapen.vampirism.data.reloadlistener;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.util.MixinHooks;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class SingleJigsawReloadListener implements PreparableReloadListener {

    public static final Identifier SINGLE_JIGSAW_ID = VIdentifier.mod("single_jigsaw_pieces");
    private static final Logger LOGGER = LogManager.getLogger();
    private static final String DIRECTORY = "vampirism";
    private static final String FILE_NAME = "single_jigsaw_pieces.json";
    private static final int PATH_SUFFIX_LENGTH = ".json".length();

    public static final Codec<List<Identifier>> CODEC = RecordCodecBuilder.create(inst -> inst.group(Identifier.CODEC.listOf().fieldOf("single_pieces").forGetter(list -> list)).apply(inst, a -> a));

    @Override
    public CompletableFuture<Void> reload(SharedState sharedState, Executor executor, PreparationBarrier barrier, Executor applyExecutor) {
        return prepare(sharedState.resourceManager(), executor).thenCompose(barrier::wait).thenAcceptAsync(MixinHooks::replaceSingleInstanceStructure);
    }

    public @NotNull CompletableFuture<List<Identifier>> prepare(@NotNull ResourceManager manager, Executor executor) {
        return CompletableFuture.supplyAsync(() -> load(manager), executor);
    }

    public @NotNull List<Identifier> load(ResourceManager manager) {
        List<Identifier> locations = new ArrayList<>();
        for (Map.Entry<Identifier, List<Resource>> entry : manager.listResourceStacks(DIRECTORY, location -> location.getPath().endsWith(FILE_NAME)).entrySet()) {
            Identifier resourceName = VIdentifier.loc(entry.getKey().getNamespace(), entry.getKey().getPath().substring(DIRECTORY.length() + 1, entry.getKey().getPath().length() - PATH_SUFFIX_LENGTH));
            for (Resource resource : entry.getValue()) {
                try (Reader reader = resource.openAsReader()) {
                    JsonElement jsonElement = JsonParser.parseReader(reader);
                    List<Identifier> newLocations = CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(DecoderException::new);
                    locations.addAll(newLocations);
                } catch (Exception e) {
                    LOGGER.error("Could not read single jigsaw pieces file {} from {}", resourceName, resource.sourcePackId(), e);
                }
            }
        }

        return locations;
    }
}
