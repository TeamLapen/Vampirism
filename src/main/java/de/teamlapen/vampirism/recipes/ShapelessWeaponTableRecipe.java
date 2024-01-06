package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.FactionCodec;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;


public class ShapelessWeaponTableRecipe implements CraftingRecipe, IWeaponTableRecipe {
    protected static final int MAX_WIDTH = 4;
    protected static final int MAX_HEIGHT = 4;

    private final @NotNull CraftingBookCategory category;
    private final @NotNull String group;
    private final @NotNull NonNullList<Ingredient> recipeItems;
    private final @NotNull ItemStack recipeOutput;
    private final int requiredLevel;
    private final List<ISkill<IHunterPlayer>> requiredSkills;
    private final int requiredLava;
    private final boolean isSimple;

    public ShapelessWeaponTableRecipe(@NotNull String group, @NotNull CraftingBookCategory category, @NotNull NonNullList<Ingredient> ingredients, @NotNull ItemStack result, int level, int lava, @NotNull List<ISkill<IHunterPlayer>> skills) {
        this.category = category;
        this.group = group;
        this.recipeItems = ingredients;
        this.recipeOutput = result;
        this.requiredLevel = level;
        this.requiredLava = lava;
        this.requiredSkills = skills;
        this.isSimple = ingredients.stream().allMatch(Ingredient::isSimple);
    }

    @NotNull
    @Override
    public ItemStack assemble(@NotNull CraftingContainer inv, @NotNull RegistryAccess registryAccess) {
        return this.recipeOutput.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.recipeItems.size();
    }

    @NotNull
    @Override
    public String getGroup() {
        return group;
    }

    @NotNull
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return recipeItems;
    }

    @NotNull
    @Override
    public ItemStack getResultItem(@NotNull RegistryAccess registryAccess) {
        return recipeOutput;
    }

    public int getRequiredLavaUnits() {
        return requiredLava;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    @NotNull
    @Override
    public List<ISkill<IHunterPlayer>> getRequiredSkills() {
        return requiredSkills;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.SHAPELESS_CRAFTING_WEAPONTABLE.get();
    }

    @NotNull
    @Override
    public RecipeType<IWeaponTableRecipe> getType() {
        return ModRecipes.WEAPONTABLE_CRAFTING_TYPE.get();
    }

    @Override
    public boolean matches(@NotNull CraftingContainer inv, @NotNull Level worldIn) {
        StackedContents recipeitemhelper = new StackedContents();
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < inv.getHeight(); ++j) {
            for (int k = 0; k < inv.getWidth(); ++k) {
                ItemStack itemstack = inv.getItem(k + j * inv.getWidth());
                if (!itemstack.isEmpty()) {
                    ++i;
                    if (isSimple) {
                        recipeitemhelper.accountStack(new ItemStack(itemstack.getItem()));
                    } else {
                        inputs.add(itemstack);
                    }
                }
            }
        }

        return i == this.recipeItems.size() && (isSimple ? recipeitemhelper.canCraft(this, null) : net.neoforged.neoforge.common.util.RecipeMatcher.findMatches(inputs, this.recipeItems) != null);

    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return this.category;
    }

    public static class Serializer implements RecipeSerializer<ShapelessWeaponTableRecipe> {

        public static final Codec<ShapelessWeaponTableRecipe> CODEC = RecordCodecBuilder.create(inst -> {
            return inst.group(
                    ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(p_301127_ -> p_301127_.group),
                    CraftingBookCategory.CODEC.fieldOf("category").orElse(CraftingBookCategory.MISC).forGetter(p_301133_ -> p_301133_.category),
                    Ingredient.CODEC_NONEMPTY
                            .listOf()
                            .fieldOf("ingredients")
                            .flatXmap(
                                    p_301021_ -> {
                                        Ingredient[] aingredient = p_301021_
                                                .toArray(Ingredient[]::new); //Forge skip the empty check and immediatly create the array.
                                        if (aingredient.length == 0) {
                                            return DataResult.error(() -> "No ingredients for shapeless recipe");
                                        } else {
                                            return aingredient.length > MAX_WIDTH * MAX_HEIGHT
                                                    ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(MAX_WIDTH * MAX_HEIGHT))
                                                    : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                                        }
                                    },
                                    DataResult::success
                            )
                            .forGetter(p_300975_ -> p_300975_.recipeItems),
                    ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(p_301142_ -> p_301142_.recipeOutput),
                    ExtraCodecs.strictOptionalField(Codec.INT, "level", 1).forGetter(p -> p.requiredLevel),
                    ExtraCodecs.strictOptionalField(Codec.INT, "lava", 0).forGetter(p -> p.requiredLava),
                    ExtraCodecs.strictOptionalField(FactionCodec.<IHunterPlayer>skillCodec().listOf(), "skill", Collections.emptyList()).forGetter(p -> p.requiredSkills)
            ).apply(inst, ShapelessWeaponTableRecipe::new);
        });

        @Override
        public @NotNull Codec<ShapelessWeaponTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull ShapelessWeaponTableRecipe fromNetwork(FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull ShapelessWeaponTableRecipe recipe) {
            buffer.writeJsonWithCodec(CODEC, recipe);
        }

    }
}
