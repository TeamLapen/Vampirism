package de.teamlapen.vampirism.util;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.VampireBookItem;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class VampireBookManager {
    private static final Gson GSON = new GsonBuilder().create();
    private static final Logger LOGGER = LogManager.getLogger();

    private static VampireBookManager instance;

    public static VampireBookManager getInstance() {
        if (instance == null) {
            instance = new VampireBookManager();
        }
        return instance;
    }

    private VampireBookManager() {

    }

    public static class BookContext {
        public final BookInfo book;
        public final String id;
        public final boolean unique;

        public BookContext(BookInfo book, String id, boolean unique) {
            this.book = book;
            this.id = id;
            this.unique = unique;
        }
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

    public BookContext getRandomBook(Random rng) {
        return nonUnique.size() > 0 ? nonUnique.get(rng.nextInt(nonUnique.size())) : DUMMY;
    }

    public ItemStack getRandomBookItem(Random rng) {
        ItemStack book = new ItemStack(ModItems.VAMPIRE_BOOK.get(), 1);
        book.setTag(VampireBookItem.createTagFromContext(getRandomBook(rng)));
        return book;
    }

    public void init() {
        InputStream inputStream = null;
        try {
            inputStream = VampirismMod.class.getResourceAsStream("/vampireBooks.json");
            if (inputStream == null) {
                throw new IOException("Could not find 'vampireBooks.json' in resources");
            }
            Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BookContext[] books = GSON.fromJson(reader, BookContext[].class);
            idToBook.clear();
            idToBook.put(OLD_ID, OLD);
            for (BookContext b : books) {
                idToBook.put(b.id, b);
                if (!b.unique) {
                    nonUnique.add(b);
                }
            }
        } catch (JsonParseException e) {
            LOGGER.warn("----------------------------------------");
            LOGGER.error("Failed to load vampire books from JSON", e);
            LOGGER.warn("----------------------------------------");
            if (VampirismMod.inDev) {
                throw e;
            }
        } catch (IOException e) {
            LOGGER.error("Failed to read vampire books from resources", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("Failed to close InputStream", e);
                }
            }
        }
    }

    public static class BookInfo {


        public BookInfo(String title, String author, String... content) {
            this.title = title;
            this.author = author;
            this.content = content;
        }

        private final String title;
        private final String author;
        private final String[] content;


        public String getAuthor() {
            return author;
        }

        public String[] getContent() {
            return content;
        }

        public String getTitle() {
            return title;
        }
    }
}
