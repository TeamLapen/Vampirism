package de.teamlapen.vampirism.inventory.recipes;

import com.google.gson.JsonObject;

import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.core.ModRegistries;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

/**
 * 1.14
 */
public class AlchemicalCauldronRecipe extends AbstractCookingRecipe {//TODO 1.14 fluidsystem
    private static final ISkill[] EMPTY_SKILLS = {};
    private final ItemStack fluid;
    @Nullable
    private final ISkill[] skills;
    private final int reqLevel;

    public AlchemicalCauldronRecipe(ResourceLocation idIn, String groupIn, Ingredient ingredientIn, ItemStack fluidIn, ItemStack resultIn, ISkill[] skillsIn, int reqLevelIn, int cookTimeIn, float exp) {
        super(ModRecipes.ALCHEMICAL_CAULDRON_TYPE, idIn, groupIn, ingredientIn, resultIn, 0.2F, 400);//TODO 1.14 default cooktime 200?
        this.fluid = fluidIn;
        this.skills = skillsIn;
        this.reqLevel = reqLevelIn;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.ALCHEMICAL_CAULDRON;
    }

    @Override
    public boolean matches(IInventory inv, World worldIn) {
        boolean match = this.ingredient.test(inv.getStackInSlot(2));
        boolean fluidMatch = true;//TODO 1.14 fluids
        return match & fluidMatch;
    }

    public boolean canBeCooked(int level, ISkillHandler<IHunterPlayer> skillHandler) {
        if (level < reqLevel) return false;
        if (skills == null) return true;
        for (ISkill s : skills) {
            if (!skillHandler.isSkillEnabled(s)) return false;
        }
        return true;
    }

    public ItemStack getFluid() {
        return fluid;
    }

    public int getRequiredLevel() {
        return reqLevel;
    }

    @SuppressWarnings("unchecked")
    @Nonnull
    public ISkill[] getRequiredSkills() {
        return (skills == null) ? EMPTY_SKILLS : skills;
    }

    @Override
    public String toString() {
        return "AlchemicalCauldronRecipe{" +
                "cookingTime=" + cookTime +
                ", skills=" + Arrays.toString(skills) +
                ", output=" + result +
                ", ingredient=" + ingredient +
                ", reqLevel=" + reqLevel +
                ", experience=" + experience +
                ", fluidStack=" + fluid +
                '}';
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<AlchemicalCauldronRecipe> {
        @Override
        public AlchemicalCauldronRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Ingredient ingredients = Ingredient.deserialize(JSONUtils.isJsonArray(json, "ingredient") ? JSONUtils.getJsonArray(json, "ingredient") : JSONUtils.getJsonObject(json, "ingredient"));
            int level = JSONUtils.getInt(json, "level", 1);
            ISkill[] skills = VampirismRecipeHelper.deserializeSkills(JSONUtils.getJsonArray(json, "skill", null));
            ItemStack result = VampirismRecipeHelper.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            ItemStack fluid = VampirismRecipeHelper.deserializeItem(JSONUtils.getJsonObject(json, "fluid"));
            int cookTime = JSONUtils.getInt(json, "cookTime", 400);
            float exp = JSONUtils.getFloat(json, "experience", 0.2F);
            return new AlchemicalCauldronRecipe(recipeId, group, ingredients, fluid, result, skills, level, cookTime, exp);
        }

        @Override
        public AlchemicalCauldronRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String group = buffer.readString(32767);
            ItemStack result = buffer.readItemStack();
            Ingredient ingredient = Ingredient.read(buffer);
            ItemStack fluid = buffer.readItemStack();
            float exp = buffer.readFloat();
            int cookingtime = buffer.readVarInt();
            int level = buffer.readVarInt();
            ISkill[] skills = new ISkill[buffer.readVarInt()];
            for (int i = 0; i < skills.length; i++) {
                skills[i] = ModRegistries.SKILLS.getValue(new ResourceLocation(buffer.readString(32767)));
            }
            return new AlchemicalCauldronRecipe(recipeId, group, ingredient, fluid, result, skills, level, cookingtime, exp);
        }

        @Override
        public void write(PacketBuffer buffer, AlchemicalCauldronRecipe recipe) {
            buffer.writeString(recipe.group);
            buffer.writeItemStack(recipe.result);
            recipe.ingredient.write(buffer);
            buffer.writeItemStack(recipe.fluid);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.cookTime);
            buffer.writeVarInt(recipe.reqLevel);
            buffer.writeVarInt(recipe.skills.length);
            for (ISkill skill : recipe.skills) {
                buffer.writeString(skill.getRegistryName().toString());
            }
        }

    }
}
