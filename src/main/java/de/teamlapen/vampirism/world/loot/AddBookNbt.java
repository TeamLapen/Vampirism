package de.teamlapen.vampirism.world.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.entity.hunter.AdvancedHunterEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.items.VampireBookItem;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Loot function that adds a random vampire text to a book stack
 */
public class AddBookNbt extends LootItemConditionalFunction {

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(AddBookNbt::new);
    }

    public AddBookNbt(LootItemCondition @NotNull [] conditions) {
        super(conditions);
    }

    @NotNull
    @Override
    public LootItemFunctionType getType() {
        return ModLoot.add_book_nbt.get();
    }

    @NotNull
    @Override
    public ItemStack run(@NotNull ItemStack itemStack, @NotNull LootContext lootContext) {
        Entity victim = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
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

    public static class Serializer extends LootItemConditionalFunction.Serializer<AddBookNbt> {

        @NotNull
        @Override
        public AddBookNbt deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext, @NotNull LootItemCondition[] iLootConditions) {
            return new AddBookNbt(iLootConditions);
        }
    }
}
