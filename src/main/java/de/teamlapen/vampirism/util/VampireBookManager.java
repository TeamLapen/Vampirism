package de.teamlapen.vampirism.util;

import com.google.common.io.ByteStreams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Handles loading of texts for ancient vampire books
 */
public class VampireBookManager {
    private final static Logger LOGGER = LogManager.getLogger();
    private static final VampireBookManager ourInstance = new VampireBookManager();

    public static VampireBookManager getInstance() {
        return ourInstance;
    }

    private final Map<String, CompoundTag> booksById = new HashMap<>();
    private CompoundTag[] bookTags = null;

    private VampireBookManager() {
    }

    public Optional<CompoundTag> getBookData(String id) {
        CompoundTag nbt = booksById.get(id);
        return Optional.ofNullable(nbt);
    }

    /**
     * Return a vampire book with a randomly selected text and title
     */
    public ItemStack getRandomBook(Random rnd) {
        ItemStack book = new ItemStack(ModItems.VAMPIRE_BOOK.get(), 1);
        book.setTag(getRandomBookData(rnd));
        return book;
    }

    @Nonnull
    public CompoundTag getRandomBookData(Random rnd) {
        return (bookTags == null || bookTags.length == 0) ? new CompoundTag() : bookTags[rnd.nextInt(bookTags.length)];
    }

    @SuppressWarnings("UnstableApiUsage")
    public void init() {
        InputStream inputStream = null;
        try {
            inputStream = VampirismMod.class.getResourceAsStream("/vampireBooks.txt");
            if (inputStream == null) {
                throw new IOException("Could not find 'vampireBooks.txt' in resources");
            }
            String data = new String(ByteStreams.toByteArray(inputStream));

            parseBooks(data);
        } catch (CommandSyntaxException e) {
            LOGGER.warn("----------------------------------------");
            LOGGER.error("Failed to convert vampire books to NBT", e);
            LOGGER.warn("----------------------------------------");
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

    private void parseBooks(String data) throws CommandSyntaxException {
        ArrayList<CompoundTag> books = new ArrayList<>();
        String[] lines = data.split("\n");
        for (String line : lines) {
            String id = null;
            if (line.startsWith("id")) {
                int pos = line.indexOf(':');
                if (pos != -1) {
                    id = line.substring(2, pos);
                    line = line.substring(pos + 1);
                }
            }
            CompoundTag nbt = TagParser.parseTag(line);
            books.add(nbt);
            if (id != null) {
                booksById.put(id, nbt);
            }
        }
        bookTags = books.toArray(new CompoundTag[0]);
    }
}
