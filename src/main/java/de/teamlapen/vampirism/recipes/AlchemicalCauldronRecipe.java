package de.teamlapen.vampirism.recipes;

import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Either;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class AlchemicalCauldronRecipe extends AbstractCookingRecipe {
    private static final ISkill<?>[] EMPTY_SKILLS = {};
    private final Either<Ingredient, FluidStack> fluid;
    @NotNull
    private final ISkill<?>[] skills;
    private final int reqLevel;

    public AlchemicalCauldronRecipe(@NotNull ResourceLocation idIn, @NotNull String groupIn, @NotNull Ingredient ingredientIn, Either<Ingredient, FluidStack> fluidIn, @NotNull ItemStack resultIn, @NotNull ISkill<?>[] skillsIn, int reqLevelIn, int cookTimeIn, float exp) {
        super(ModRecipes.ALCHEMICAL_CAULDRON_TYPE.get(), idIn, groupIn, ingredientIn, resultIn, exp, cookTimeIn);
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
    public ISkill<?>[] getRequiredSkills() {
        return skills;
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
            stack.ifPresent((handlerItem) -> fluidMatch.set(ingredient1.isFluidEqual(handlerItem) && ingredient1.getAmount() <= handlerItem.getAmount()));
        }));
        return match && fluidMatch.get();
    }

    @Override
    public @NotNull String toString() {
        return "AlchemicalCauldronRecipe{" +
                "cookingTime=" + cookingTime +
                ", skills=" + Arrays.toString(skills) +
                ", output=" + result +
                ", ingredient=" + ingredient +
                ", reqLevel=" + reqLevel +
                ", experience=" + experience +
                ", fluid=" + fluid +
                '}';
    }

    public static class Serializer implements RecipeSerializer<AlchemicalCauldronRecipe> {
        @NotNull
        @Override
        public AlchemicalCauldronRecipe fromJson(@NotNull ResourceLocation recipeId, @NotNull JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Ingredient ingredients = Ingredient.fromJson(GsonHelper.isArrayNode(json, "ingredient") ? GsonHelper.getAsJsonArray(json, "ingredient") : GsonHelper.getAsJsonObject(json, "ingredient"));
            int level = GsonHelper.getAsInt(json, "level", 1);
            ISkill<?>[] skills = VampirismRecipeHelper.deserializeSkills(GsonHelper.getAsJsonArray(json, "skill", null));
            ItemStack result = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
            Either<Ingredient, FluidStack> fluid = VampirismRecipeHelper.getFluidOrItem(json);
            int cookTime = GsonHelper.getAsInt(json, "cookTime", 200);
            float exp = GsonHelper.getAsFloat(json, "experience", 0.2F);
            return new AlchemicalCauldronRecipe(recipeId, group, ingredients, fluid, result, skills, level, cookTime, exp);
        }

        @Override
        public AlchemicalCauldronRecipe fromNetwork(@NotNull ResourceLocation recipeId, @NotNull FriendlyByteBuf buffer) {
            String group = buffer.readUtf(32767);
            ItemStack result = buffer.readItem();
            Ingredient ingredient = Ingredient.fromNetwork(buffer);
            Either<Ingredient, FluidStack> fluid;
            if (buffer.readBoolean()) {
                fluid = Either.left(Ingredient.fromNetwork(buffer));
            } else {
                fluid = Either.right(FluidStack.readFromPacket(buffer));
            }
            float exp = buffer.readFloat();
            int cookingtime = buffer.readVarInt();
            int level = buffer.readVarInt();
            ISkill<?>[] skills = new ISkill[buffer.readVarInt()];
            for (int i = 0; i < skills.length; i++) {
                skills[i] = RegUtil.getSkill(buffer.readResourceLocation());
            }
            return new AlchemicalCauldronRecipe(recipeId, group, ingredient, fluid, result, skills, level, cookingtime, exp);
        }

        @Override
        public void toNetwork(@NotNull FriendlyByteBuf buffer, @NotNull AlchemicalCauldronRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.result);
            recipe.ingredient.toNetwork(buffer);
            if (recipe.fluid.left().isPresent()) {
                buffer.writeBoolean(true);
                recipe.fluid.left().get().toNetwork(buffer);
            } else {
                assert recipe.fluid.right().isPresent();
                buffer.writeBoolean(false);
                recipe.fluid.right().get().writeToPacket(buffer);
            }
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookingTime);
            buffer.writeVarInt(recipe.reqLevel);
            buffer.writeVarInt(recipe.skills.length);
            for (ISkill<?> skill : recipe.skills) {
                buffer.writeResourceLocation(RegUtil.id(skill));
            }
        }

    }
}
