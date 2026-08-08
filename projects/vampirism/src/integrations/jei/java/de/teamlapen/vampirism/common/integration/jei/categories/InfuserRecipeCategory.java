package de.teamlapen.vampirism.common.integration.jei.categories;

import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import de.teamlapen.vampirism.common.integration.jei.VampirismJEIPlugin;
import de.teamlapen.vampirism.common.world.items.component.BloodCharged;
import de.teamlapen.vampirism.common.world.items.component.PureLevel;
import de.teamlapen.vampirism.common.world.items.recipes.InfuserRecipe;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.Objects;
import java.util.Optional;

public class InfuserRecipeCategory implements IRecipeCategory<RecipeHolder<InfuserRecipe>> {

    private static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/container/infuser.png");

    private final Component title;
    private final IDrawable icon;
    private final IDrawable background;

    public InfuserRecipeCategory(IGuiHelper helper) {
        this.title = ModBlocks.INFUSER.get().getName();
        this.icon = helper.createDrawableItemStack(new ItemStack(ModBlocks.INFUSER.get()));
        this.background = helper.drawableBuilder(BACKGROUND, 0,0,176, 181).trim(15,96,6,5).build();
    }

    @Override
    public IRecipeType<RecipeHolder<InfuserRecipe>> getRecipeType() {
        return VampirismJEIPlugin.INFUSER;
    }

    @Override
    public Component getTitle() {
        return this.title;
    }

    @Override
    public int getWidth() {
        return this.background.getWidth();
    }

    @Override
    public int getHeight() {
        return this.background.getHeight();
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<InfuserRecipe> recipe, IFocusGroup focuses) {
        InfuserRecipe value = recipe.value();
        var item1 = builder.addSlot(RecipeIngredientRole.INPUT, 97-6, 33+9-15);
        item1.add(value.ingredient());

        var input1 = builder.addSlot(RecipeIngredientRole.INPUT, 7-6, 16-15);
        if (value.ingredient1().isPresent()) input1.add(value.ingredient1().get());
        var input2 = builder.addSlot(RecipeIngredientRole.INPUT, 29-6, 16-15);
        if (value.ingredient2().isPresent()) input2.add(value.ingredient2().get());
        var input3 = builder.addSlot(RecipeIngredientRole.INPUT, 51-6, 16-15);
        if (value.ingredient3().isPresent()) input3.add(value.ingredient3().get());
        var input4 = builder.addSlot(RecipeIngredientRole.INPUT, 73-6, 16-15);
        if (value.ingredient4().isPresent()) input4.add(value.ingredient4().get());

        var result1 = builder.addSlot(RecipeIngredientRole.OUTPUT, 150-6, 32+9-15);
        Optional<ItemStackTemplate> result = value.result();
        if (result.isPresent()) {
            result1.add(result.get());
        } else {
            if (value.ingredient().isCustom()) {
                ContextMap contextmap = SlotDisplayContext.fromLevel(Objects.requireNonNull(Minecraft.getInstance().level));

                var stacks = value.ingredient().display().resolveForStacks(contextmap);
                for (ItemStack stack : stacks) {
                    var level = stack.getOrDefault(ModDataComponents.PURE_LEVEL, PureLevel.EMPTY);
                    result1.add(PureLevel.pureBlood(stack, Math.min(level.level() + 1, 4)));
                }
            } else {
                value.ingredient().getValues().stream().map(x -> PureLevel.template(x, 0)).forEach(result1::add);
            }
        }

        builder.addSlot(RecipeIngredientRole.OUTPUT, 18-6, 59+9-15).add(value.result1());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 40-6, 59+9-15).add(value.result2());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 62-6, 59+9-15).add(value.result3());
    }

    @Override
    public void draw(RecipeHolder<InfuserRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics);
    }
}
