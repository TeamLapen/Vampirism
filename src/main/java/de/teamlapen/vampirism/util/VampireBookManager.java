package de.teamlapen.vampirism.util;


import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampireBookItem;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VampireBookManager {
    private static final Logger LOGGER = LogManager.getLogger();

    private static VampireBookManager instance;

    public static @NotNull VampireBookManager getInstance() {
        if (instance == null) {
            instance = new VampireBookManager();
        }
        return instance;
    }

    private VampireBookManager() {

    }

    private final Map<String, BookContext> idToBook = new HashMap<>();
    private final List<BookContext> nonUnique = new ArrayList<>();
    private final BookContext DUMMY = new BookContext(new BookInfo("Unknown", "Unknown", "Failed to load"), "error", false);
    private final BookContext OLD = new BookContext(new BookInfo("Unknown", "Unknown", "☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰☰"), OLD_ID, false);
    public final static String OLD_ID = "old";

    public BookInfo getBookById(String id) {
        return idToBook.getOrDefault(id, DUMMY).book;
    }

    public BookContext getBookContextById(String id) {
        return idToBook.getOrDefault(id, DUMMY);
    }

    public BookContext getRandomBook(@NotNull RandomSource rng) {
        return !nonUnique.isEmpty() ? nonUnique.get(rng.nextInt(nonUnique.size())) : DUMMY;
    }

    public @NotNull ItemStack getRandomBookItem(@NotNull RandomSource rng) {
        ItemStack book = new ItemStack(ModItems.VAMPIRE_BOOK.get(), 1);
        book.setTag(VampireBookItem.createTagFromContext(getRandomBook(rng)));
        return book;
    }

    public @NotNull Collection<ItemStack> getAllBookItems() {
        return idToBook.values().stream().filter(s -> s != DUMMY && s != OLD).map(context -> ModItems.VAMPIRE_BOOK.get().contentInstance(context)).toList();
    }

    public void init() {
        try (InputStream inputStream = VampirismMod.class.getResourceAsStream("/vampireBooks.json")) {
            if (inputStream == null) {
                throw new IOException("Could not find 'vampireBooks.json' in resources");
            }
            try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
                JsonElement jsonElement = JsonParser.parseReader(reader);
                List<BookContext> books = Lists.newArrayList(BookContext.CODEC.listOf().parse(new Dynamic<>(JsonOps.INSTANCE, jsonElement)).getOrThrow(false, LOGGER::error));
                idToBook.clear();
                idToBook.put(OLD_ID, OLD);
                for (BookContext b : books) {
                    idToBook.put(b.id, b);
                    if (!b.unique) {
                        nonUnique.add(b);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("----------------------------------------");
            LOGGER.error("Failed to load vampire books from JSON", e);
            LOGGER.warn("----------------------------------------");
            if (VampirismMod.inDev) {
                throw new RuntimeException(e);
            }
        }
    }

    public record BookContext(BookInfo book, String id, boolean unique, String... tags) {
        @SuppressWarnings("CodeBlock2Expr")
        public static final Codec<BookContext> CODEC = RecordCodecBuilder.create((item) -> {
            return item.group(BookInfo.CODEC.fieldOf("book").forGetter((bookContext) -> {
                return bookContext.book;
            }), Codec.STRING.fieldOf("id").forGetter((bookContext) -> {
                return bookContext.id;
            }), Codec.BOOL.fieldOf("unique").orElse(false).forGetter((bookContext) -> {
                return bookContext.unique;
            }), Codec.STRING.listOf().fieldOf("tags").forGetter((bookContext) -> {
                return Arrays.asList(bookContext.tags);
            })).apply(item, BookContext::new);
        });

        public BookContext(BookInfo book, String id, Boolean unique, @NotNull List<String> tags) {
            this(book, id, unique, tags.toArray(new String[0]));
        }
    }

    public record BookInfo(String title, String author, String... content) {
        @SuppressWarnings("CodeBlock2Expr")
        public static final Codec<BookInfo> CODEC = RecordCodecBuilder.create((item) -> {
            return item.group(Codec.STRING.fieldOf("title").forGetter((bookInfo) -> {
                return bookInfo.title;
            }), Codec.STRING.fieldOf("author").forGetter((bookInfo) -> {
                return bookInfo.author;
            }), Codec.STRING.listOf().fieldOf("content").forGetter((bookInfo) -> {
                return Arrays.asList(bookInfo.content);
            })).apply(item, BookInfo::new);
        });

        public BookInfo(String title, String author, @NotNull List<String> content) {
            this(title, author, content.toArray(new String[0]));
        }
    }
}
