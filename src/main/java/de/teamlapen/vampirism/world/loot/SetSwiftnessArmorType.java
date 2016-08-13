package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import de.teamlapen.vampirism.items.ItemArmorOfSwiftness;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;

import java.util.Random;

/**
 * Set's the {@link de.teamlapen.vampirism.items.ItemArmorOfSwiftness} type
 */
public class SetSwiftnessArmorType extends LootFunction {
    private final ItemArmorOfSwiftness.TYPE type;

    protected SetSwiftnessArmorType(LootCondition[] conditionsIn, ItemArmorOfSwiftness.TYPE type) {
        super(conditionsIn);
        this.type = type;
    }

    @Override
    public ItemStack apply(ItemStack stack, Random rand, LootContext context) {

        return ItemArmorOfSwiftness.setType(stack, type);
    }

    public static class Serializer extends LootFunction.Serializer<SetSwiftnessArmorType> {

        protected Serializer() {
            super(new ResourceLocation(REFERENCE.MODID, "set_swiftness_armor_type"), SetSwiftnessArmorType.class);
        }

        @Override
        public SetSwiftnessArmorType deserialize(JsonObject object, JsonDeserializationContext deserializationContext, LootCondition[] conditionsIn) {
            String name = object.get("type").getAsString();
            ItemArmorOfSwiftness.TYPE type = ItemArmorOfSwiftness.TYPE.NORMAL;
            for (ItemArmorOfSwiftness.TYPE t : ItemArmorOfSwiftness.TYPE.values()) {
                if (t.getName().equals(name)) {
                    type = t;
                    break;
                }
            }
            return new SetSwiftnessArmorType(conditionsIn, type);
        }

        @Override
        public void serialize(JsonObject object, SetSwiftnessArmorType functionClazz, JsonSerializationContext serializationContext) {
            object.addProperty("count", functionClazz.type.getName());
        }
    }
}
