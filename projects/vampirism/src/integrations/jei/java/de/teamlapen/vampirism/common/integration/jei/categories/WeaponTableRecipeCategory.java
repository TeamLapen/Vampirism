package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.common.util.Color;
import de.teamlapen.vampirism.common.world.items.recipes.IWeaponTableRecipe;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.integration.jei.extension.WeaponTableCategoryExtension;
import de.teamlapen.vampirism.common.integration.jei.extension.WeaponTableCraftingHelper;
import de.teamlapen.vampirism.common.util.UtilLib;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.ICraftingGridHelper;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.List;

/**
 * Recipe category for {@link IWeaponTableRecipe}
 */
public class WeaponTableRecipeCategory implements IRecipeCategory<RecipeHolder<IWeaponTableRecipe>> {

    private final Component localizedName;
    private final IDrawable icon;
    private final IDrawable bucket;
    private final ICraftingGridHelper craftingGridHelper;
    private final IGuiHelper guiHelper;
    private final WeaponTableCategoryExtension weaponTableCategoryExtension = new WeaponTableCategoryExtension();


    public WeaponTableRecipeCategory(IGuiHelper guiHelper) {
        this.localizedName = Component.translatable(ModBlocks.WEAPON_TABLE.get().getDescriptionId());
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WEAPON_TABLE.get()));
        this.bucket = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.LAVA_BUCKET));
        this.craftingGridHelper = WeaponTableCraftingHelper.INSTANCE;
        this.guiHelper = guiHelper;
    }

    @Override
    public void draw(RecipeHolder<IWeaponTableRecipe> holder, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor graphics, double mouseX, double mouseY) {
//        this.background.draw(graphics);

        IDrawableStatic recipeArrow = this.guiHelper.getRecipeArrow();

        recipeArrow.draw(graphics, 80, 28);

        IWeaponTableRecipe recipe = holder.value();
        int x = 2;
        int y = 80;
        Minecraft minecraft = Minecraft.getInstance();
        if (recipe.getRequiredLavaUnits() > 0) {
            this.bucket.draw(graphics, 83, 11);
        }
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.alchemical_cauldron.level", recipe.getRequiredLevel());

            graphics.text(minecraft.font, level, x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight + 2;
        }
        List<Holder<? extends ISkill<?>>> requiredSkills = recipe.getRequiredSkills();
        if (!requiredSkills.isEmpty()) {
            MutableComponent skillText = Component.translatable("gui.vampirism.skill_required", " ");

            for (Holder<? extends ISkill<?>> skill : recipe.getRequiredSkills()) {
                skillText.append(skill.value().getName()).append(" ");

            }
            y += UtilLib.renderMultiLine(minecraft.font, graphics, skillText, 132, x, y, Color.GRAY.getRGB());

        }
    }

    @Override
    public int getHeight() {
        return 110;
    }

    @Override
    public int getWidth() {
        return 134;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public IRecipeType<RecipeHolder<IWeaponTableRecipe>> getRecipeType() {
        return VampirismJEIPlugin.WEAPON_TABLE;
    }

    @Override
    public Component getTitle() {
        return localizedName;
    }


    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<IWeaponTableRecipe> holder, IFocusGroup focuses) {
        this.weaponTableCategoryExtension.setRecipe(holder, builder, this.craftingGridHelper, focuses);
    }

    @Override
    public boolean isHandled(RecipeHolder<IWeaponTableRecipe> recipe) {
        return this.weaponTableCategoryExtension.isHandled(recipe);
    }
}
