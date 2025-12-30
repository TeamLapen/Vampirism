package de.teamlapen.vampirism.data.reloadlistener.vampirebook;

import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import de.teamlapen.vampirism.api.general.IBookContents;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModVampireBooks;
import io.netty.handler.codec.DecoderException;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class VampireBookContentsReloadListener extends SimplePreparableReloadListener<Map<Identifier, VampireBookContentsReloadListener.TranslatedBookContent>> {

    public static final Identifier ID = VIdentifier.mod("vampire_book_contents");
    private static final Logger LOGGER = LogUtils.getLogger();

    private Map<Identifier, VampireBookContentsReloadListener.TranslatedBookContent> translatedBookContents = Map.of();

    @Override
    protected @NotNull Map<Identifier, VampireBookContentsReloadListener.TranslatedBookContent> prepare(ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        Map<Identifier, Resource> vampireBooks = resourceManager.listResources("vampire_books", x -> x.getPath().endsWith(".json"));
        var contentByBook = vampireBooks.keySet().stream().collect(Collectors.groupingBy(x -> x.withPath(path -> {
            var parts = path.split("/");
            if (parts.length != 3){
                return null;
            }
            return parts[1];
        })));

        return contentByBook.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, x -> {
            Map<String, IBookContents> rawLanguageMap = x.getValue().stream().<Pair<String, IBookContents>>mapMulti((id, resources) -> {
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
            }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));

            IBookContents base = rawLanguageMap.get("en_us");
            Map<String, IBookContents> mergedLanguageMap = new HashMap<>();

            for (Map.Entry<String, IBookContents> entry : rawLanguageMap.entrySet()) {
                String lang = entry.getKey();
                IBookContents localized = entry.getValue();

                if (lang.equals("en_us") && base != null) {
                    mergedLanguageMap.put(lang, base);
                    continue;
                }

                if (base != null) {
                    Identifier background = !localized.background().equals(ModVampireBooks.DIARY_BACKGROUND) ? localized.background() : base.background();

                    Map<Integer, IBookContents.IImageEntry> baseImages = base.images().stream().collect(Collectors.toMap(IBookContents.IImageEntry::id, image -> image));
                    for (IBookContents.IImageEntry localizedImage : localized.images()) {
                        baseImages.put(localizedImage.id(), localizedImage);
                    }

                    mergedLanguageMap.put(lang, new BookContents(localized.contents(), background, baseImages.values().stream().toList()));
                } else {
                    mergedLanguageMap.put(lang, localized);
                }
            }

            return new TranslatedBookContent(mergedLanguageMap);
        }));
    }

    @Override
    protected void apply(@NotNull Map<Identifier, VampireBookContentsReloadListener.TranslatedBookContent> object, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        this.translatedBookContents = Collections.unmodifiableMap(object);
    }

    public Map<Identifier, TranslatedBookContent> getTranslatedBookContents() {
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
