package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nonnull;
import java.util.Optional;

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
        Entity victim = lootContext.get(LootParameters.THIS_ENTITY);
        Optional<String> id = Optional.empty();
        if(victim instanceof AdvancedHunterEntity){
            id = ((AdvancedHunterEntity) victim).getBookLootId();
        } else if(victim instanceof AdvancedVampireEntity){
            id = ((AdvancedVampireEntity) victim).getBookLootId();
        }
        CompoundNBT data = id.flatMap(str -> VampireBookManager.getInstance().getBookData(str)).orElseGet(()->VampireBookManager.getInstance().getRandomBookData(lootContext.getRandom()));
        itemStack.setTag(data);
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
