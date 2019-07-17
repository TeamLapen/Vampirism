package de.teamlapen.vampirism.api.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Represents a blood potion category. There are an arbitrary number of items associated with this category.
 */
public interface IBloodPotionCategory {

    /**
     * Adds an item to this category, ignoring meta and nbt
     */
    void addItem(Item item);

    /**
     * Adds a block's item to this category, ignoring meta and nbt
     */
    void addItem(Block block);

    /**
     * Adds an item to this category NOT ignoring meta and nbt
     */
    void addItemExact(@Nonnull ItemStack stack);

    /**
     * Adds all items in the list. See {@link IBloodPotionCategory#addItem(Item)},{@link IBloodPotionCategory#addItem(Block)},{@link IBloodPotionCategory#addItemExact(ItemStack)}
     */
    void addItems(Object... items);

    /**
     * @param stack may be EMPTY
     */
    boolean containsItem(@Nonnull ItemStack stack);


    List<IBloodPotionRegistry.WeightedEffect> getImmutableEffectsList();

    String getDescTranslationKey();
}
