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
import net.minecraft.tags.Tag;
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

    private int lava = 1;
    private ISkill[] skills;
    private int level = 1;
    private JsonObject extraNbt;

    public ShapedWeaponTableRecipeBuilder(IItemProvider item, int count, @Nullable JsonObject extraNbt) {
        super(item, count);
        this.extraNbt = extraNbt;
    }

    public static ShapedWeaponTableRecipeBuilder shapedWeaponTable(IItemProvider item) {
        return new ShapedWeaponTableRecipeBuilder(item, 1, null);
    }

    public static ShapedWeaponTableRecipeBuilder shapedWeaponTable(IItemProvider item, int count) {
        return new ShapedWeaponTableRecipeBuilder(item, count, null);
    }

    public static ShapedWeaponTableRecipeBuilder shapedWeaponTable(IItemProvider item, int count, JsonObject nbt) {
        return new ShapedWeaponTableRecipeBuilder(item, count, nbt);
    }

    public ShapedWeaponTableRecipeBuilder lava(int amount) {
        this.lava = amount;
        return this;
    }

    public ShapedWeaponTableRecipeBuilder skills(ISkill... skills) {
        this.skills = skills;
        return this;
    }

    public ShapedWeaponTableRecipeBuilder level(int level) {
        this.level = level;
        return this;
    }

    @Override
    public ShapedWeaponTableRecipeBuilder key(Character p_200469_1_, Tag<Item> p_200469_2_) {
        return (ShapedWeaponTableRecipeBuilder) super.key(p_200469_1_, p_200469_2_);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder key(Character p_200462_1_, IItemProvider p_200462_2_) {
        return (ShapedWeaponTableRecipeBuilder) super.key(p_200462_1_, p_200462_2_);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder key(Character p_200471_1_, Ingredient p_200471_2_) {
        return (ShapedWeaponTableRecipeBuilder) super.key(p_200471_1_, p_200471_2_);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder patternLine(String p_200472_1_) {
        return (ShapedWeaponTableRecipeBuilder) super.patternLine(p_200472_1_);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder addCriterion(String p_200465_1_, ICriterionInstance p_200465_2_) {
        return (ShapedWeaponTableRecipeBuilder) super.addCriterion(p_200465_1_, p_200465_2_);
    }

    @Override
    public ShapedWeaponTableRecipeBuilder setGroup(String p_200473_1_) {
        return (ShapedWeaponTableRecipeBuilder) super.setGroup(p_200473_1_);
    }

    @Override
    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id) {
        id = new ResourceLocation(id.getNamespace(), "weapontable/" + id.getPath());
        this.advancementBuilder.withCriterion("has_skill", SkillUnlockedTrigger.builder(this.skills != null && this.skills.length >= 1? this.skills[0]:HunterSkills.weapon_table));
        this.validate(id);
        this.advancementBuilder
                .withParentId(new ResourceLocation("recipes/root"))
                .withCriterion("has_the_recipe", new RecipeUnlockedTrigger.Instance(id))
                .withRewards(net.minecraft.advancements.AdvancementRewards.Builder.recipe(id))
                .withRequirementsStrategy(IRequirementsStrategy.OR);
        consumer.accept(new Result(id, this.result, this.count, this.group == null ? "" : this.group, this.pattern, this.key, this.advancementBuilder, new ResourceLocation(id.getNamespace(), "recipes/" + this.result.getGroup().getPath() + "/" + id.getPath()), this.lava, this.skills != null ? this.skills : new ISkill[]{}, this.level, this.extraNbt));
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

        @Override
        public void serialize(JsonObject jsonObject) {
            super.serialize(jsonObject);
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

        @Nonnull
        @Override
        public IRecipeSerializer<?> getSerializer() {
            return ModRecipes.shaped_crafting_weapontable;
        }
    }
}
