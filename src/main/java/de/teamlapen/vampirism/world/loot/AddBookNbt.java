package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.IRandomRange;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

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

    public static Builder<?> builder(){
        return builder(AddBookNbt::new);
    }

    public static class Serializer extends LootFunction.Serializer<AddBookNbt> {

        public Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "add_book_nbt"), AddBookNbt.class);
        }

        @Override
        public void serialize(@Nonnull JsonObject object, @Nonnull AddBookNbt functionClazz, @Nonnull JsonSerializationContext serializationContext) {
            super.serialize(object, functionClazz, serializationContext);
        }

        @Nonnull
        @Override
        public AddBookNbt deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            return new AddBookNbt(iLootConditions);
        }
    }
}
