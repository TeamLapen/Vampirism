package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.neoforged.neoforge.common.crafting.NBTIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class AlchemyTableRecipe extends AbstractBrewingRecipe {

    private final List<ISkill<?>> requiredSkills;

    public AlchemyTableRecipe(String group, Ingredient ingredient, Ingredient input, ItemStack result, List<ISkill<?>> skills) {
        super(ModRecipes.ALCHEMICAL_TABLE_TYPE.get(), group, ingredient, input, result);
        this.requiredSkills = skills;
    }

    public boolean isInput(@NotNull ItemStack input) {
        return UtilLib.matchesItem(this.input, input);
    }

    public boolean isIngredient(@NotNull ItemStack ingredient) {
        return this.ingredient.test(ingredient);
    }

    @NotNull
    public ItemStack getResult(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? this.result.copy() : ItemStack.EMPTY;
    }

    public List<ISkill<?>> getRequiredSkills() {
        return requiredSkills;
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMICAL_TABLE.get();
    }

    public static class Serializer implements RecipeSerializer<AlchemyTableRecipe> {
        private static final Codec<Ingredient> INGREDIENT_CODEC = Codec.either(NBTIngredient.CODEC_NONEMPTY, Ingredient.CODEC_NONEMPTY).xmap(either -> either.map(l -> l, r -> r), x -> x instanceof NBTIngredient nbt ? Either.left(nbt) : Either.right(x));
        public static final Codec<AlchemyTableRecipe> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(p_300832_ -> p_300832_.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(p_300831_ -> p_300831_.ingredient),
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(p_300830_ -> p_300830_.input),
                ItemStack.CODEC.fieldOf("result").forGetter(p_300829_ -> p_300829_.result),
                ExtraCodecs.strictOptionalField(ModRegistries.SKILLS.byNameCodec().listOf(), "skill", Collections.emptyList()).forGetter(p -> p.requiredSkills)
                ).apply(inst, AlchemyTableRecipe::new));

        @Override
        public @NotNull Codec<AlchemyTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull AlchemyTableRecipe fromNetwork(FriendlyByteBuf buffer) {
            return buffer.readJsonWithCodec(CODEC);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull AlchemyTableRecipe recipe) {
            buffer.writeJsonWithCodec(CODEC, recipe);
        }
    }
}
