package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;

/**
 * Loot function that adds a random vampire text to a book stack
 */
public class AddBookNbt extends LootFunction {

    public AddBookNbt(ILootCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    public ItemStack doApply(@Nonnull ItemStack itemStack, LootContext lootContext) {
        VampireBookManager.getInstance().applyRandomBook(itemStack, lootContext.getRandom());
        return itemStack;
    }

    public static Builder<?> builder() {
        return builder(AddBookNbt::new);
    }

    @Nonnull
    @Override
    public LootFunctionType getFunctionType() {
        return ModLoot.add_book_nbt;
    }

    public static class Serializer extends LootFunction.Serializer<AddBookNbt> {

        @Nonnull
        @Override
        public AddBookNbt deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            return new AddBookNbt(iLootConditions);
        }
    }
}
