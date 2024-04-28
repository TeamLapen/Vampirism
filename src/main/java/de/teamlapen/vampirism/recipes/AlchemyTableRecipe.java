package de.teamlapen.vampirism.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
        public static final MapCodec<AlchemyTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
                Codec.STRING.optionalFieldOf("group", "").forGetter(p_300832_ -> p_300832_.group),
                Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(p_300831_ -> p_300831_.ingredient),
                Ingredient.CODEC_NONEMPTY.fieldOf("input").forGetter(p_300830_ -> p_300830_.input),
                ItemStack.CODEC.fieldOf("result").forGetter(p_300829_ -> p_300829_.result),
                ModRegistries.SKILLS.byNameCodec().listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(p -> p.requiredSkills)
        ).apply(inst, AlchemyTableRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyTableRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).map(s -> s.orElse(""), Optional::of), s -> s.group,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient,
                Ingredient.CONTENTS_STREAM_CODEC, s -> s.input,
                ItemStack.STREAM_CODEC, s -> s.result,
                ByteBufCodecs.registry(VampirismRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), s -> s.requiredSkills,
                AlchemyTableRecipe::new
        );

        @Override
        public @NotNull MapCodec<AlchemyTableRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AlchemyTableRecipe> streamCodec() {
            return STREAM_CODEC;
        }

    }
}
