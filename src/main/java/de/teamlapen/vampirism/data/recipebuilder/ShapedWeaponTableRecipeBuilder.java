package de.teamlapen.vampirism.data.recipebuilder;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.teamlapen.vampirism.advancements.SkillUnlockedTrigger;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRecipes;
import de.teamlapen.vampirism.player.hunter.skills.HunterSkills;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.IRequirementsStrategy;
import net.minecraft.advancements.criterion.RecipeUnlockedTrigger;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ShapedWeaponTableRecipeBuilder extends ShapedRecipeBuilder {

    public static ShapedWeaponTableRecipeBuilder shapedWeaponTable(IItemProvider item) {
        return new ShapedWeaponTableRecipeBuilder(item, 1, null);
    }

    public static ShapedWeaponTableRecipeBuilder shapedWeaponTable(IItemProvider item, int count) {
        return new ShapedWeaponTableRecipeBuilder(item, count, null);
    }

    public static ShapedWeaponTableRecipeBuilder shapedWeaponTable(IItemProvider item, int count, JsonObject nbt) {
        return new ShapedWeaponTableRecipeBuilder(item, count, nbt);
    }
    private final JsonObject extraNbt;
    private int lava = 1;
    private ISkill[] skills;
    private int level = 1;

    public ShapedWeaponTableRecipeBuilder(IItemProvider item, int count, @Nullable JsonObject extraNbt) {
        super(item, count);
        this.extraNbt = extraNbt;
    }

    @Override
    public ShapedWeaponTableRecipeBuilder define(Character symbol, IItemProvider itemIn) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, itemIn);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder define(Character symbol, Ingredient ingredientIn) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, ingredientIn);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder define(Character symbol, ITag<Item> tagIn) {
        return (ShapedWeaponTableRecipeBuilder) super.define(symbol, tagIn);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder group(String groupIn) {
        return (ShapedWeaponTableRecipeBuilder) super.group(groupIn);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder pattern(String patternIn) {
        return (ShapedWeaponTableRecipeBuilder) super.pattern(patternIn);
    }

    public ShapedWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public ShapedWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public void save(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "weapontable/" + id.getPath());
        this.advancement.addCriterion("has_skill", SkillUnlockedTrigger.builder(this.skills != null && this.skills.length >= 1 ? this.skills[0] : HunterSkills.WEAPON_TABLE.get()));
        this.ensureValid(id);
        this.advancement
                .parent(new ResourceLocation("recipes/root"))
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
                .requirements(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group, this.rows, this.key, this.advancement, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getItemCategory().getRecipeFolderName() + "/" + id.getPath()), this.lava, this.skills != null ? this.skills : new ISkill[]{}, this.level, this.extraNbt));
    }

    @Override
    public ShapedWeaponTableRecipeBuilder unlockedBy(String name, ICriterionInstance criterionIn) {
        return (ShapedWeaponTableRecipeBuilder) super.unlockedBy(name, criterionIn);
    }

    public ShapedWeaponTableRecipeBuilder skills(ISkill... skills) {
        this.skills = skills;
        return this;
    }

    private class Result extends ShapedRecipeBuilder.Result {
        private final int lava;
        private final ISkill[] skills;
        private final int level;
        private final JsonObject extraNbt;

        public Result(ResourceLocation id, Item item, int count, String group, List<String> pattern, Map<Character, Ingredient> ingredients, Advancement.Builder advancementBuilder, ResourceLocation advancementId, int lava, ISkill[] skills, int level, JsonObject extraNbt) {
            super(id, item, count, group, pattern, ingredients, advancementBuilder, advancementId);
            this.lava = lava;
            this.skills = skills;
            this.level = level;
            this.extraNbt = extraNbt;
        }

        @Nonnull
        @Override
        public IRecipeSerializer<?> getType() {
            return ModRecipes.SHAPED_CRAFTING_WEAPONTABLE.get();
        }

        @Override
        public void serializeRecipeData(JsonObject jsonObject) {
            super.serializeRecipeData(jsonObject);
            jsonObject.addProperty("lava", this.lava);
            JsonArray skills = new JsonArray();
            for (ISkill skill : this.skills) {
                skills.add(skill.getRegistryName().toString());
            }
            jsonObject.add("skill", skills);
            jsonObject.addProperty("level", this.level);
            if (extraNbt != null) {
                jsonObject.get("result").getAsJsonObject().add("nbt", extraNbt);
            }
        }
    }
}
