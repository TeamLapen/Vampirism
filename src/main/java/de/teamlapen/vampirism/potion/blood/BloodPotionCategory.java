package de.teamlapen.vampirism.potion.blood;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IBloodPotionCategory;
import de.teamlapen.vampirism.api.items.IBloodPotionEffect;
import de.teamlapen.vampirism.api.items.IBloodPotionRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;


class BloodPotionCategory implements IBloodPotionCategory {

    private final
    @Nullable
    String unlocDesc;
    private final String id;
    private final List<ItemStack> exactItems = NonNullList.create();
    private final List<Item> items = Lists.newLinkedList();
    private final List<IBloodPotionRegistry.WeightedEffect> effects = Lists.newArrayList();

    BloodPotionCategory(String id, @Nullable String unlocDesc) {
        this.unlocDesc = unlocDesc;
        this.id = id;


    }

    public void addEffect(IBloodPotionEffect effect, int weight) {
        effects.add(new IBloodPotionRegistry.WeightedEffect(effect, weight));
    }

    @Override
    public void addItem(Item item) {
        items.add(item);
    }

    @Override
    public void addItem(Block block) {
        items.add(Item.getItemFromBlock(block));
    }

    @Override
    public void addItemExact(@Nonnull ItemStack stack) {
        exactItems.add(stack);
    }

    @Override
    public void addItems(Object... items) {
        for (Object item : items) {
            if (item instanceof Item) {
                addItem((Item) item);
            } else if (item instanceof Block) {
                addItem((Block) item);
            } else if (item instanceof ItemStack) {
                addItemExact((ItemStack) item);
            } else {
                VampirismMod.log.w("BloodPotionCategory", "Failed to add item '%s' of unkown type", item);
            }
        }
    }

    @Override
    public boolean containsItem(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        if (items.contains(item)) {
            return true;
        }
        for (ItemStack next : exactItems) {
            if (stack.getItem() != next.getItem()) continue;
            if (stack.getItemDamage() != next.getItemDamage()) continue;
            if (!ItemStack.areItemStackTagsEqual(stack, next)) continue;
            return true;
        }
        return false;
    }

    @Override
    public List<IBloodPotionRegistry.WeightedEffect> getEffectsCopy() {
        List<IBloodPotionRegistry.WeightedEffect> copied = Lists.newArrayList();
        for (IBloodPotionRegistry.WeightedEffect effect : effects) {
            copied.add(effect.copy());
        }
        return copied;
    }

    public String getId() {
        return id;
    }

    @Override
    public String getUnlocDescription() {
        return unlocDesc == null ? "" : unlocDesc;
    }

    @Override
    public String toString() {
        return "BloodPotionCategory{" +
                "items=" + items +
                ", unlocDesc='" + unlocDesc + '\'' +
                ", exactItems=" + exactItems +
                '}';
    }
}
