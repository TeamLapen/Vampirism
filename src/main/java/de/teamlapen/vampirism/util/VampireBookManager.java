package de.teamlapen.vampirism.util;

import com.google.common.io.ByteStreams;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/**
 * Handles loading of texts for ancient vampire books
 */
public class VampireBookManager {
    private static final String TAG = "VampireBookManager";
    private static VampireBookManager ourInstance = new VampireBookManager();

    public static VampireBookManager getInstance() {
        return ourInstance;
    }

    private NBTTagCompound[] bookTags = null;

    private VampireBookManager() {
    }

    public void applyRandomBook(ItemStack stack, Random rnd) {
        NBTTagCompound nbt = (bookTags == null || bookTags.length == 0) ? new NBTTagCompound() : bookTags[rnd.nextInt(bookTags.length)];
        stack.setTagCompound(nbt);
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
        } catch (NBTException e) {
            VampirismMod.log.w(TAG, "----------------------------------------");
            VampirismMod.log.e(TAG, e, "Failed to convert vampire books to NBT");
            VampirismMod.log.w(TAG, "----------------------------------------");
        } catch (IOException e) {
            VampirismMod.log.e(TAG, e, "Failed to read vampire books from resources");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    VampirismMod.log.e(TAG, e, "Failed to close InputStream");
                }
            }
        }
    }

    private void parseBooks(String data) throws NBTException {

        ArrayList<NBTTagCompound> books = new ArrayList<>();
        String[] lines = data.split("\n");
        for (String line : lines) {
            NBTTagCompound nbt = JsonToNBT.getTagFromJson(line);
            books.add(nbt);
        }
        bookTags = books.toArray(new NBTTagCompound[books.size()]);
    }
}
