package de.teamlapen.vampirism.modcompat.jei;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexSorting;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.lib.util.Color;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.items.IWeaponTableRecipe;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.recipes.ShapelessWeaponTableRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Recipe category for {@link IWeaponTableRecipe}
 */
public class WeaponTableRecipeCategory implements IRecipeCategory<IWeaponTableRecipe> {

    private final static ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_clean.png");
    private static final ItemStack lavaStack = new ItemStack(Items.LAVA_BUCKET);
    private final @NotNull Component localizedName;
    private final @NotNull IDrawable background;
    private final @NotNull IDrawable icon;
    private final @NotNull IDrawable bucket;

    WeaponTableRecipeCategory(@NotNull IGuiHelper guiHelper) {
        this.localizedName = Component.translatable(ModBlocks.WEAPON_TABLE.get().getDescriptionId());
        this.background = guiHelper.drawableBuilder(BACKGROUND, 32, 14, 134, 77).addPadding(0, 33, 0, 0).build();
        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.WEAPON_TABLE.get()));
        this.bucket = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Items.LAVA_BUCKET));
    }

    @Override
    public void draw(@NotNull IWeaponTableRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics graphics, double mouseX, double mouseY) {

        int x = 2;
        int y = 80;
        Minecraft minecraft = Minecraft.getInstance();
        if (recipe.getRequiredLavaUnits() > 0) {
            this.bucket.draw(graphics, 83, 13);
            PoseStack pose = graphics.pose();
            pose.pushPose();
            RenderSystem.backupProjectionMatrix();
            RenderSystem.setProjectionMatrix(pose.last().pose(), VertexSorting.ORTHOGRAPHIC_Z);
            graphics.renderItem(lavaStack, 83, 13);
            RenderSystem.restoreProjectionMatrix();
            pose.popPose();
        }
        if (recipe.getRequiredLevel() > 1) {
            Component level = Component.translatable("gui.vampirism.hunter_weapon_table.level", recipe.getRequiredLevel());

            graphics.drawString(minecraft.font, level, x, y, Color.GRAY.getRGB(), false);
            y += minecraft.font.lineHeight + 2;
        }
        ISkill<?>[] requiredSkills = recipe.getRequiredSkills();
        if (requiredSkills.length > 0) {
            MutableComponent skillText = Component.translatable("gui.vampirism.skill_required", " ");

            for (ISkill<?> skill : recipe.getRequiredSkills()) {
                skillText.append(skill.getName()).append(" ");

            }
            y += UtilLib.renderMultiLine(minecraft.font, graphics, skillText, 132, x, y, Color.GRAY.getRGB());

        }
    }

    @NotNull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @NotNull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public @NotNull RecipeType<IWeaponTableRecipe> getRecipeType() {
        return VampirismJEIPlugin.WEAPON_TABLE;
    }

    @NotNull
    @Override
    public Component getTitle() {
        return localizedName;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, IWeaponTableRecipe recipe, IFocusGroup focuses) {
        if (recipe instanceof ShapelessWeaponTableRecipe) {
            builder.setShapeless();
        }
        List<List<ItemStack>> list = recipe.getIngredients().stream().map(s -> List.of(s.getItems())).toList();
        IRecipeSlotBuilder output = builder.addSlot(RecipeIngredientRole.OUTPUT, 112, 32);
        output.addItemStack(RecipeUtil.getResultItem(recipe));

        List<IRecipeSlotBuilder> inputSlots = new ArrayList<>();
        for (int y = 0; y < 4; ++y) {
            for (int x = 0; x < 4; ++x) {
                IRecipeSlotBuilder slot = builder.addSlot(RecipeIngredientRole.INPUT, 2 + x * 19, 2 + y * 19);
                inputSlots.add(slot);
            }
        }

        int height = recipe instanceof IShapedRecipe<?> shaped ? shaped.getRecipeHeight() : 4;
        int width = recipe instanceof IShapedRecipe<?> shaped ? shaped.getRecipeWidth() : 4;

        for (int i = 0; i < height; i++) {
            for (int i1 = 0; i1 < width; i1++) {
                IRecipeSlotBuilder slot = inputSlots.get(i1 + i * 4);

                List<ItemStack> itemList = list.get(i1 + i * width);
                if (itemList != null) {
                    slot.addIngredients(VanillaTypes.ITEM_STACK, itemList);
                }
            }
        }
    }
}
