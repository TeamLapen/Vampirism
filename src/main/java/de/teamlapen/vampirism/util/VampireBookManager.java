package de.teamlapen.vampirism.util;

import com.google.common.io.ByteStreams;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Handles loading of texts for ancient vampire books
 */
public class VampireBookManager {
    private final static Logger LOGGER = LogManager.getLogger();
    private static VampireBookManager ourInstance = new VampireBookManager();

    public static VampireBookManager getInstance() {
        return ourInstance;
    }

    private CompoundNBT[] bookTags = null;

    private VampireBookManager() {
    }

    public void applyRandomBook(ItemStack stack, Random rnd) {
        CompoundNBT nbt = (bookTags == null || bookTags.length == 0) ? new CompoundNBT() : bookTags[rnd.nextInt(bookTags.length)];
        stack.setTag(nbt);
    }

    /**
     * Return a vampire book with a randomly selected text and title
     *
     * @param rnd
     * @return
     */
    public ItemStack getRandomBook(Random rnd) {
        ItemStack book = new ItemStack(ModItems.vampire_book, 1);
        applyRandomBook(book, rnd);
        return book;
    }

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

        ArrayList<CompoundNBT> books = new ArrayList<>();
        String[] lines = data.split("\n");
        for (String line : lines) {
            CompoundNBT nbt = JsonToNBT.getTagFromJson(line);
            books.add(nbt);
        }
        bookTags = books.toArray(new CompoundNBT[0]);
    }
}
