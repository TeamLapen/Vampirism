package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.client.gui.recipebook.WeaponTableRecipeBookGui;
import de.teamlapen.vampirism.inventory.container.WeaponTableContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Gui for the weapon table. Only draws the background and the lava status
 */
@OnlyIn(Dist.CLIENT)
public class WeaponTableScreen extends ContainerScreen<WeaponTableContainer> implements IRecipeShownListener {

    private static final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_lava.png");
    private static final ResourceLocation TABLE_GUI_TEXTURES_MISSING_LAVA = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table_missing_lava.png");
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");
    private final RecipeBookGui recipeBookGui = new WeaponTableRecipeBookGui();
    private boolean widthTooNarrow;

    public WeaponTableScreen(WeaponTableContainer inventorySlotsIn, PlayerInventory inventoryPlayer, ITextComponent name) {
        super(inventorySlotsIn, inventoryPlayer, name);
        this.xSize = 196;
        this.ySize = 191;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        super.render(stack, mouseX, mouseY, partialTicks);
        this.renderBackground(stack);
        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.drawGuiContainerBackgroundLayer(stack, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(stack, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookGui.render(stack, mouseX, mouseY, partialTicks);
            super.render(stack, mouseX, mouseY, partialTicks);
            this.recipeBookGui.func_230477_a_(stack, this.guiLeft, this.guiTop, true, partialTicks);
        }
        this.renderHoveredTooltip(stack, mouseX, mouseY);
        this.recipeBookGui.func_238924_c_(stack, this.guiLeft, this.guiTop, mouseX, mouseY);
        this.setListenerDefault(this.recipeBookGui);
    }

    @Override
    public void tick() {
        super.tick();
        this.recipeBookGui.tick();
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
    public void onClose() {
        this.recipeBookGui.removed();
        super.onClose();
    }

    @Override
    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(x, y, width, height, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = this.guiLeft;
        int j = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
        this.blit(stack, i, j, 0, 0, this.xSize, this.ySize);
        if (container.hasLava()) {
            this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES_LAVA);
            this.blit(stack, i, j, 0, 0, this.xSize, this.ySize);
        }
        if (container.isMissingLava()) {
            this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES_MISSING_LAVA);
            this.blit(stack, i, j, 0, 0, this.xSize, this.ySize);
        }
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double)guiLeftIn || mouseY < (double)guiTopIn || mouseX >= (double)(guiLeftIn + this.xSize) || mouseY >= (double)(guiTopIn + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    @Override
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    @Override
    protected void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
        this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize - 18);
        this.children.add(this.recipeBookGui);
        this.setFocusedDefault(this.recipeBookGui);
        this.addButton(new ImageButton(this.guiLeft + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) -> {
            this.recipeBookGui.initSearchBar(this.widthTooNarrow);
            this.recipeBookGui.toggleVisibility();
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize - 18);
            ((ImageButton) button).setPosition(this.guiLeft + 5, this.height / 2 - 49);
        }));
    }

    @Override
    public RecipeBookGui getRecipeGui() {
        return recipeBookGui;
    }

}
