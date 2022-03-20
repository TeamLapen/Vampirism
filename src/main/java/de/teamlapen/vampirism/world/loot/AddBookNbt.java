package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.items.VampireBookItem;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootFunction;
import net.minecraft.loot.LootFunctionType;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Loot function that adds a random vampire text to a book stack
 */
public class AddBookNbt extends LootFunction {

    public static Builder<?> builder() {
        return simpleBuilder(AddBookNbt::new);
    }

    public AddBookNbt(ILootCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    public LootFunctionType getType() {
        return ModLoot.add_book_nbt;
    }

    @Nonnull
    @Override
    public ItemStack run(@Nonnull ItemStack itemStack, LootContext lootContext) {
        Entity victim = lootContext.getParamOrNull(LootParameters.THIS_ENTITY);
        Optional<String> id = Optional.empty();
        if (victim instanceof AdvancedHunterEntity) {
            id = ((AdvancedHunterEntity) victim).getBookLootId();
        } else if (victim instanceof AdvancedVampireEntity) {
            id = ((AdvancedVampireEntity) victim).getBookLootId();
        }
        VampireBookManager.BookContext bookContext = id.map(VampireBookManager.getInstance()::getBookContextById).orElseGet(() -> VampireBookManager.getInstance().getRandomBook(lootContext.getRandom()));
        itemStack.setTag(VampireBookItem.createTagFromContext(bookContext));
        return itemStack;
    }

    public static class Serializer extends LootFunction.Serializer<AddBookNbt> {

        @Nonnull
        @Override
        public AddBookNbt deserialize(@Nonnull JsonObject jsonObject, @Nonnull JsonDeserializationContext jsonDeserializationContext, @Nonnull ILootCondition[] iLootConditions) {
            return new AddBookNbt(iLootConditions);
        }
    }
}
