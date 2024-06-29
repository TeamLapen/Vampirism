package de.teamlapen.vampirism.world.loot.functions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.VampireBookLootProvider;
import de.teamlapen.vampirism.core.ModLoot;
import de.teamlapen.vampirism.items.component.VampireBookContents;
import de.teamlapen.vampirism.util.VampireBookManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Loot function that adds a random vampire text to a book stack
 */
public class AddBookNbtFunction extends LootItemConditionalFunction {

    public static final MapCodec<AddBookNbtFunction> CODEC = RecordCodecBuilder.mapCodec(inst -> commonFields(inst).apply(inst, AddBookNbtFunction::new));

    public static @NotNull Builder<?> builder() {
        return simpleBuilder(AddBookNbtFunction::new);
    }

    AddBookNbtFunction(@NotNull List<LootItemCondition> conditions) {
        super(conditions);
    }

    @NotNull
    @Override
    public LootItemFunctionType<AddBookNbtFunction> getType() {
        return ModLoot.ADD_BOOK_NBT.get();
    }

    @NotNull
    @Override
    public ItemStack run(@NotNull ItemStack itemStack, @NotNull LootContext lootContext) {
        Entity victim = lootContext.getParamOrNull(LootContextParams.THIS_ENTITY);
        Optional<String> id = Optional.empty();
        if (victim instanceof VampireBookLootProvider provider) {
            id = provider.getBookLootId();
        }
        VampireBookManager.BookContext bookContext = id.map(VampireBookManager.getInstance()::getBookContextById).orElseGet(() -> VampireBookManager.getInstance().getRandomBook(lootContext.getRandom()));
        VampireBookContents.addFromBook(itemStack, bookContext);
        return itemStack;
    }
}
