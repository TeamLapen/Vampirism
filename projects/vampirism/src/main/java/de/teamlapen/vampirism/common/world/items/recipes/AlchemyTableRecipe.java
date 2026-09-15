package de.teamlapen.vampirism.common.world.items.recipes;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.factions.skills.ISkillHandler;
import de.teamlapen.faction.api.util.SafeCast;
import de.teamlapen.vampirism.common.core.ModRecipes;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.PlacementInfo;
import net.minecraft.world.item.crafting.RecipeBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AlchemyTableRecipe extends AbstractBrewingRecipe {

    private final List<Holder<? extends ISkill<?>>> requiredSkills;
    @Nullable
    private PlacementInfo placementInfo;

    public AlchemyTableRecipe(CommonInfo commonInfo, String group, Ingredient ingredient, Ingredient input, ItemStackTemplate result, List<Holder<? extends ISkill<?>>> skills) {
        super(ModRecipes.ALCHEMICAL_TABLE_TYPE.get(), commonInfo, group, ingredient, input, result);
        this.requiredSkills = skills;
    }

    public boolean isInput(@NotNull ItemStack input) {
        return this.input.test(input);
    }

    public boolean isIngredient(@NotNull ItemStack ingredient) {
        return this.ingredient.test(ingredient);
    }

    @NotNull
    public ItemStack getResult(@NotNull ItemStack input, @NotNull ItemStack ingredient) {
        return isInput(input) && isIngredient(ingredient) ? this.result.create() : ItemStack.EMPTY;
    }

    @Override
    public @NotNull PlacementInfo placementInfo() {
        if (this.placementInfo == null) {
            this.placementInfo = PlacementInfo.create(List.of(this.ingredient, this.input));
        }
        return this.placementInfo;
    }

    @Override
    public @NotNull RecipeBookCategory recipeBookCategory() {
        return ModRecipes.ALCHEMICAL_TABLE_CATEGORY.get();
    }

    public List<Holder<? extends ISkill<?>>> getRequiredSkills() {
        return requiredSkills;
    }

    public boolean canBeBrewed(@Nullable ISkillHandler<?> skillHandler) {
        if (this.requiredSkills.isEmpty()) return true;
        return skillHandler != null && skillHandler.areSkillsEnabled(this.requiredSkills);
    }

    @NotNull
    @Override
    public RecipeSerializer<AlchemyTableRecipe> getSerializer() {
        return ModRecipes.ALCHEMICAL_TABLE.get();
    }

    public static final MapCodec<AlchemyTableRecipe> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            CommonInfo.MAP_CODEC.fieldOf("common_info").forGetter(p -> p.commonInfo),
            Codec.STRING.optionalFieldOf("group", "").forGetter(p_300832_ -> p_300832_.group),
            Ingredient.CODEC.fieldOf("ingredient").forGetter(p_300831_ -> p_300831_.ingredient),
            Ingredient.CODEC.fieldOf("input").forGetter(p_300830_ -> p_300830_.input),
            ItemStackTemplate.CODEC.fieldOf("result").forGetter(p_300829_ -> p_300829_.result),
            ISkill.CODEC.listOf().optionalFieldOf("skill", Collections.emptyList()).forGetter(p -> p.requiredSkills)
    ).apply(inst, AlchemyTableRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, AlchemyTableRecipe> STREAM_CODEC = StreamCodec.composite(
            CommonInfo.STREAM_CODEC, s -> s.commonInfo,
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).map(s -> s.orElse(""), Optional::of), s -> s.group,
            Ingredient.CONTENTS_STREAM_CODEC, s -> s.ingredient,
            Ingredient.CONTENTS_STREAM_CODEC, s -> s.input,
            ItemStackTemplate.STREAM_CODEC, s -> s.result,
            SafeCast.<StreamCodec<RegistryFriendlyByteBuf, Holder<? extends ISkill<?>>>>cast(ByteBufCodecs.holderRegistry(FactionRegistries.Keys.SKILL)).apply(ByteBufCodecs.list()), s -> s.requiredSkills,
            AlchemyTableRecipe::new
    );
}
