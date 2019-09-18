package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.functions.ILootFunction;

/**
 * Loot function that adds a random vampire text to a book stack
 */
public class AddBookNbt implements ILootFunction {

    @Override
    public ItemStack apply(ItemStack itemStack, LootContext lootContext) {
        VampireBookManager.getInstance().applyRandomBook(itemStack, lootContext.getRandom());
        return itemStack;
    }

    public static class Serializer extends ILootFunction.Serializer<AddBookNbt> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "add_book_nbt"), AddBookNbt.class);
        }

        @Override
        public AddBookNbt deserialize(JsonObject p_212870_1_, JsonDeserializationContext p_212870_2_) {
            return new AddBookNbt();
        }

        @Override
        public void serialize(JsonObject object, AddBookNbt functionClazz, JsonSerializationContext serializationContext) {

        }
    }
}
