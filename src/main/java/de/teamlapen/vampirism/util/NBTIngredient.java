package de.teamlapen.vampirism.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class NBTIngredient extends Ingredient {

    private final ItemStack stack;

    public NBTIngredient(ItemStack stack) {
        super(Stream.of(new Ingredient.SingleItemList(stack)));
        this.stack = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input)
    {
        if (input == null)
            return false;
        //Can't use areItemStacksEqualUsingNBTShareTag because it compares stack size as well
        return this.stack.getItem() == input.getItem() && this.stack.getDamageValue() == input.getDamageValue() && this.stack.areShareTagsEqual(input);
    }

    @Override
    public boolean isSimple()
    {
        return false;
    }

    @Nonnull
    @Override
    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Nonnull
    @Override
    public JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID(Serializer.INSTANCE).toString());
        json.addProperty("item", stack.getItem().getRegistryName().toString());
        json.addProperty("count", stack.getCount());
        if (stack.hasTag())
            json.add("nbt", new JsonParser().parse(stack.getTag().toString()).getAsJsonObject());
        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Nonnull
        @Override
        public NBTIngredient parse(PacketBuffer buffer) {
            return new NBTIngredient(buffer.readItem());
        }

        @Nonnull
        @Override
        public NBTIngredient parse(@Nonnull JsonObject json) {
            return new NBTIngredient(CraftingHelper.getItemStack(json, true));
        }

        @Override
        public void write(PacketBuffer buffer, NBTIngredient ingredient) {
            buffer.writeItem(ingredient.stack);
        }
    }
}
