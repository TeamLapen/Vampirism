package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.world.items.components.IVampireBook;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.core.ModRecipes;
import de.teamlapen.vampirism.common.tags.ModVampireBookTags;
import de.teamlapen.vampirism.common.world.items.VampireBookItem;
import de.teamlapen.vampirism.common.world.items.component.VampireBook;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public class RerollVampireBookRecipe extends CustomRecipe {

    public static final MapCodec<RerollVampireBookRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            RegistryCodecs.homogeneousList(VampirismRegistries.Keys.VAMPIRE_BOOK).fieldOf("vampire_books").forGetter(x -> x.vampireBooks)
    ).apply(inst, RerollVampireBookRecipe::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, RerollVampireBookRecipe> STREAM_CODEC = ByteBufCodecs.holderSet(VampirismRegistries.Keys.VAMPIRE_BOOK).map(RerollVampireBookRecipe::new, x -> x.vampireBooks);

    private static final RandomSource RANDOM = RandomSource.create();
    private final HolderSet<IVampireBook> vampireBooks;

    @Override
    public boolean matches(@NotNull CraftingInput input, @NotNull Level level) {
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

    public RerollVampireBookRecipe(HolderSet<IVampireBook> vampireBooks) {
        this.vampireBooks = vampireBooks;
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull CraftingInput input) {
        var book = vampireBooks.getRandomElement(RANDOM).map(Holder::value).orElse(VampireBook.EMPTY);

        return VampireBookItem.createBook(book);
    }

    @Override
    public @NotNull RecipeSerializer<? extends CustomRecipe> getSerializer() {
        return ModRecipes.REROLL_VAMPIRE_BOOK.get();
    }

}
