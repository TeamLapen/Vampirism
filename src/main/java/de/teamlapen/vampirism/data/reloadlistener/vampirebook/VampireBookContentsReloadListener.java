package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import de.teamlapen.lib.VampLib;
import de.teamlapen.vampirism.api.components.IVampireBook;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.misc.BookContents;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class VampireBookContentsReloadListener extends SimplePreparableReloadListener<Map<ResourceLocation, VampireBookContentsReloadListener.TranslatedBookContent>> {
    public static final ResourceLocation ID = VResourceLocation.mod("vampire_book_contents");
    private static final Logger LOGGER = LogUtils.getLogger();

    private Map<ResourceLocation, VampireBookContentsReloadListener.TranslatedBookContent> translatedBookContents = Map.of();

    @Override
    protected @NotNull Map<ResourceLocation, VampireBookContentsReloadListener.TranslatedBookContent> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<ResourceLocation, Resource> vampireBooks = resourceManager.listResources("vampire_books", x -> x.getPath().endsWith(".json"));
        var contentByBook = vampireBooks.keySet().stream().collect(Collectors.groupingBy(x -> x.withPath(path -> {
            var parts = path.split("/");
            if (parts.length != 3){
                return null;
            }
            return parts[1];
        })));

        return contentByBook.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x -> new TranslatedBookContent(x.getValue().stream().<Pair<String, IBookContents>>mapMulti((id, resources) -> {
            Resource resource = vampireBooks.get(id);

            String[] split = id.getPath().split("/");
            if (split.length != 3) {
                return;
            }

            try (var open = resource.openAsReader()) {
                var lang = split[2].split("\\.")[0];

                IBookContents content = BookContents.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseReader(open)).getOrThrow(DecoderException::new);
                resources.accept(Pair.of(lang, content));
            } catch (Exception e) {
                LOGGER.error("Could not read vampire book contents file {} from {}", id, resource.sourcePackId(), e);
            }
        }).collect(Collectors.toMap(Pair::getKey, Pair::getValue)))));
    }

    @Override
    protected void apply(Map<ResourceLocation, VampireBookContentsReloadListener.TranslatedBookContent> object, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.translatedBookContents = Collections.unmodifiableMap(object);
    }

    public Map<ResourceLocation, TranslatedBookContent> getTranslatedBookContents() {
        return this.translatedBookContents;
    }

    public static class TranslatedBookContent {
        private final Map<String, IBookContents> contents;

        public TranslatedBookContent(Map<String, IBookContents> contents) {
            this.contents = contents;
        }

        @Nullable
        public IBookContents getContentsFor(String languageCode) {
            return contents.getOrDefault(languageCode, contents.get("en_us"));
        }
    }

}
