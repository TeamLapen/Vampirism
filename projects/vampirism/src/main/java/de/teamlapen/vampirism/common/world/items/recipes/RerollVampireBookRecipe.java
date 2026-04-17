package de.teamlapen.vampirism.common.world.items.recipes;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.tags.ModVampireBookTags;
import de.teamlapen.vampirism.common.world.items.VampireBookItem;
import de.teamlapen.vampirism.common.world.items.component.VampireBook;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class RerollVampireBookRecipe extends CustomRecipe {

    private static final RandomSource RANDOM = RandomSource.create();

    public RerollVampireBookRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (level.registryAccess().lookup(VampirismRegistries.Keys.VAMPIRE_BOOK).isEmpty()) return false;

        int bookCount = 0;

        for (int i = 0; i < input.size(); ++i) {
            ItemStack itemStack = input.getItem(i);
            if (!itemStack.isEmpty()) {
                if (itemStack.is(ModItems.VAMPIRE_BOOK)) {
                    IVampireBook vampireBook = VampireBook.get(itemStack);
                    if (vampireBook.isEmpty()) {
                        bookCount++;
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        }

        return bookCount == 1;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        Optional<? extends HolderLookup.RegistryLookup<IVampireBook>> registryLookup = registries.lookup(VampirismRegistries.Keys.VAMPIRE_BOOK);

        if (registryLookup.isPresent()) {
            List<Holder.Reference<IVampireBook>> list = registryLookup.get().listElements().filter(vampireBook -> !vampireBook.is(ModVampireBookTags.NON_TREASURE) && vampireBook.is(ModVampireBookTags.IS_GENERAL)).toList();
            return VampireBookItem.createBook(list.get(RANDOM.nextInt(0, list.size())).value());
        }

        return VampireBookItem.createBook(VampireBook.EMPTY);
    }

    @Override
    public RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.REROLL_VAMPIRE_BOOK.get();
    }
}
