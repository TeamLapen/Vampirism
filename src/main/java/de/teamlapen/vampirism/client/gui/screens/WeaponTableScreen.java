package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.gui.screens.recipebook.WeaponTableRecipeBookGui;
import de.teamlapen.vampirism.inventory.WeaponTableMenu;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
@OnlyIn(Dist.CLIENT)
public class WeaponTableScreen extends AbstractContainerScreen<WeaponTableMenu> implements RecipeUpdateListener {

    private static final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_lava.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_MISSING_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_missing_lava.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookComponent recipeBookGui = new WeaponTableRecipeBookGui();
    private boolean widthTooNarrow;

    public WeaponTableScreen(@NotNull WeaponTableMenu inventorySlotsIn, @NotNull Inventory inventoryPlayer, @NotNull Component name) {
        super(inventorySlotsIn, inventoryPlayer, name);
        this.imageWidth = 196;
        this.imageHeight = 191;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @NotNull
    @Override
    public RecipeBookComponent getRecipeBookComponent() {
        return recipeBookGui;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int p_mouseClicked_5_) {
        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, p_mouseClicked_5_)) {
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() || super.mouseClicked(mouseX, mouseY, p_mouseClicked_5_);
        }
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderBackground(stack);
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.renderBg(stack, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(stack, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookGui.render(stack, mouseX, mouseY, partialTicks);
            super.render(stack, mouseX, mouseY, partialTicks);
            this.recipeBookGui.renderGhostRecipe(stack, this.leftPos, this.topPos, true, partialTicks);
        }
        this.renderTooltip(stack, mouseX, mouseY);
        this.recipeBookGui.renderTooltip(stack, this.leftPos, this.topPos, mouseX, mouseY);
        this.magicalSpecialHackyFocus(this.recipeBookGui);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.recipeBookGui.tick();
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.imageWidth) || mouseY >= (double) (guiTopIn + this.imageHeight);
        return this.recipeBookGui.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, mouseButton) && flag;
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth - 18);
        this.addRenderableOnly(this.recipeBookGui);
        this.setInitialFocus(this.recipeBookGui);
        this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeBookGui.initVisuals();
            this.recipeBookGui.toggleVisibility();
            this.leftPos = this.recipeBookGui.updateScreenPosition(this.width, this.imageWidth - 18);
            button.setPosition(this.leftPos + 5, this.height / 2 - 49);
        }));
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull PoseStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TABLE_GUI_TEXTURES);
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        if (menu.hasLava()) {
            RenderSystem.setShaderTexture(0, TABLE_GUI_TEXTURES_LAVA);
            this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
        if (menu.isMissingLava()) {
            RenderSystem.setShaderTexture(0, TABLE_GUI_TEXTURES_MISSING_LAVA);
            this.blit(stack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
    }

    @Override
    protected void slotClicked(@Nullable Slot slotIn, int slotId, int mouseButton, @NotNull ClickType type) {
        //noinspection ConstantConditions
        super.slotClicked(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

}
