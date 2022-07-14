package de.teamlapen.vampirism.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.stream.Stream;

public class NBTIngredient extends Ingredient {

    private final ItemStack[] stacks;

    public NBTIngredient(ItemStack... stack) {
        super(Stream.of(stack).map(Ingredient.ItemValue::new));
        this.stacks = stack;
    }

    @Override
    public boolean test(@Nullable ItemStack input)
    {
        if (input == null) {
            return false;
        }
        for (ItemStack stack : stacks) {
            if(stack.getItem() == input.getItem() && stack.getDamageValue() == input.getDamageValue() && stack.areShareTagsEqual(input)) {
                return true;
            }
        }
        return false;
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
        if (this.stacks.length == 1) {
            json.addProperty("item", RegUtil.id(stacks[0].getItem()).toString());
            json.addProperty("count", stacks[0].getCount());
            if (stacks[0].hasTag()) {
                json.add("nbt", new JsonParser().parse(stacks[0].getTag().toString()).getAsJsonObject());
            }
        } else {
            JsonArray array = new JsonArray();
            for (ItemStack stack : this.stacks) {
                JsonObject obj = new JsonObject();
                obj.addProperty("item", RegUtil.id(stack.getItem()).toString());
                obj.addProperty("count", stack.getCount());
                if (stack.hasTag()) {
                    obj.add("nbt", new JsonParser().parse(stack.getTag().toString()).getAsJsonObject());
                }
                array.add(obj);
            }
            json.add("items", array);
        }

        return json;
    }

    public static class Serializer implements IIngredientSerializer<NBTIngredient> {
        public static final Serializer INSTANCE = new Serializer();

        @Nonnull
        @Override
        public NBTIngredient parse(FriendlyByteBuf buffer) {
            int length = buffer.readVarInt();
            ItemStack[] stacks = new ItemStack[length];
            for (int i = 0; i < stacks.length; i++) {
                stacks[i] = buffer.readItem();
            }
            return new NBTIngredient(stacks);
        }

        @Nonnull
        @Override
        public NBTIngredient parse(@Nonnull JsonObject json) {
            if (json.has("items")) {
                JsonArray items = json.get("items").getAsJsonArray();
                ItemStack[] stacks = new ItemStack[items.size()];
                for (int i = 0; i < stacks.length; i++) {
                    stacks[i] = CraftingHelper.getItemStack(items.get(i).getAsJsonObject(), true);
                }
                return new NBTIngredient(stacks);
            } else {
                return new NBTIngredient(CraftingHelper.getItemStack(json, true));
            }
        }

        @Override
        public void write(FriendlyByteBuf buffer, NBTIngredient ingredient) {
            buffer.writeVarInt(ingredient.stacks.length);
            for (ItemStack stack : ingredient.stacks) {
                buffer.writeItem(stack);
            }
        }
    }
}
