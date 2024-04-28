package de.teamlapen.vampirism.recipes;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.util.StreamCodecExtension;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlchemicalCauldronRecipe extends AbstractCookingRecipe {
    private final Either<Ingredient, FluidStack> fluid;
    @NotNull
    private final List<ISkill<?>> skills;
    private final int reqLevel;

    public AlchemicalCauldronRecipe(@NotNull String groupIn, CookingBookCategory category, @NotNull Ingredient ingredientIn, Either<Ingredient, FluidStack> fluidIn, @NotNull ItemStack resultIn, @NotNull List<ISkill<?>> skillsIn, int reqLevelIn, int cookTimeIn, float exp) {
        super(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get(), groupIn, category, ingredientIn, resultIn, exp, cookTimeIn);
        this.fluid = fluidIn;
        this.skills = skillsIn;
        this.reqLevel = reqLevelIn;
    }

    public boolean canBeCooked(int level, @NotNull ISkillHandler<IHunterPlayer> skillHandler) {
        if (level < reqLevel) return false;
        for (ISkill<?> s : skills) {
            if (!skillHandler.isSkillEnabled(s)) return false;
        }
        return true;
    }

    public Either<Ingredient, FluidStack> getFluid() {
        return fluid;
    }

    public @NotNull Ingredient getIngredient() {
        return ingredient;
    }

    public int getRequiredLevel() {
        return reqLevel;
    }

    @NotNull
    public List<ISkill<?>> getRequiredSkills() {
        return skills;
    }

    public ItemStack result() {
        return result;
    }

    public String group() {
        return super.getGroup();
    }

    @NotNull
    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMICAL_CAULDRON.get();
    }

    @Override
    public boolean matches(@NotNull Container inv, @NotNull Level worldIn) {
        boolean match = this.ingredient.test(inv.getItem(1));
        AtomicBoolean fluidMatch = new AtomicBoolean(true);
        fluid.ifLeft((ingredient1 -> fluidMatch.set(ingredient1.test(inv.getItem(0)))));
        fluid.ifRight((ingredient1 -> {
            fluidMatch.set(false);
            Optional<FluidStack> stack = FluidUtil.getFluidContained(inv.getItem(0));
            stack.ifPresent((handlerItem) -> fluidMatch.set(FluidStack.isSameFluidSameComponents(ingredient1, handlerItem) && ingredient1.getAmount() <= handlerItem.getAmount()));
        }));
        return match && fluidMatch.get();
    }

    public static class Serializer implements RecipeSerializer<AlchemicalCauldronRecipe> {

        public static final MapCodec<AlchemicalCauldronRecipe> CODEC = RecordCodecBuilder.mapCodec(inst ->
                inst.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(p_300832_ -> p_300832_.group),
                        CookingBookCategory.CODEC.fieldOf("category").orElse(CookingBookCategory.MISC).forGetter(p_300828_ -> p_300828_.category),
                        Ingredient.CODEC_NONEMPTY.fieldOf("ingredient").forGetter(p_300833_ -> p_300833_.ingredient),
                        Codec.either(Ingredient.CODEC_NONEMPTY, FluidStack.CODEC).fieldOf("fluid").forGetter(s -> s.fluid),
                        ItemStack.CODEC.fieldOf("result").forGetter(p_300827_ -> p_300827_.result),
                        ModRegistries.SKILLS.byNameCodec().listOf().optionalFieldOf( "skill", Collections.emptyList()).forGetter(p -> p.skills),
                        Codec.INT.optionalFieldOf( "level", 1).forGetter(p -> p.reqLevel),
                        Codec.INT.optionalFieldOf( "cookTime", 200).forGetter(p -> p.cookingTime),
                        Codec.FLOAT.optionalFieldOf("experience", 0.2F).forGetter(p -> p.experience)
                ).apply(inst, AlchemicalCauldronRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AlchemicalCauldronRecipe> STREAM_CODEC = StreamCodecExtension.composite(
                ByteBufCodecs.STRING_UTF8, AlchemicalCauldronRecipe::group,
                NeoForgeStreamCodecs.enumCodec(CookingBookCategory.class), AlchemicalCauldronRecipe::category,
                Ingredient.CONTENTS_STREAM_CODEC, AlchemicalCauldronRecipe::getIngredient,
                ByteBufCodecs.either(Ingredient.CONTENTS_STREAM_CODEC, FluidStack.STREAM_CODEC), AlchemicalCauldronRecipe::getFluid,
                ItemStack.STREAM_CODEC, AlchemicalCauldronRecipe::result,
                ByteBufCodecs.registry(VampirismRegistries.Keys.SKILL).apply(ByteBufCodecs.list()), AlchemicalCauldronRecipe::getRequiredSkills,
                ByteBufCodecs.INT, AlchemicalCauldronRecipe::getRequiredLevel,
                ByteBufCodecs.INT, AlchemicalCauldronRecipe::getCookingTime,
                ByteBufCodecs.FLOAT, AlchemicalCauldronRecipe::getExperience,
                AlchemicalCauldronRecipe::new
        );

        @Override
        public @NotNull MapCodec<AlchemicalCauldronRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AlchemicalCauldronRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
