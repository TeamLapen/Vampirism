package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.client.gui.ScrollableListButton;
import de.teamlapen.vampirism.entity.minion.management.MinionTask;
import de.teamlapen.vampirism.inventory.container.MinionContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.gui.GuiUtils;
import net.minecraftforge.fml.client.gui.widget.ExtendedButton;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class MinionScreen extends ContainerScreen<MinionContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/minion_inventory.png");
    private final int extraSlots;
    private ScrollableListButton taskList;
    private Button taskButton;
    private Button appearanceButton;
    private LockIconButton lockActionButton;

    public MinionScreen(MinionContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(Objects.requireNonNull(screenContainer), inv, titleIn);
        this.xSize = 214;
        this.ySize = 185;
        this.extraSlots = screenContainer.getExtraSlots();
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(mStack, mouseX, mouseY);

    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(GUI_TEXTURE);
        this.blit(mStack, i, j, 0, 0, this.xSize, this.ySize);
        for (int k = extraSlots; k < 15; k++) {
            this.blit(mStack, i + 29 + 18 * (k / 3), j + 44 + 18 * (k % 3), 236, 80, 13, 13);
        }

    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.func_243246_a(mStack, title, 26, 6.0F, 0x404040);
        this.font.func_243246_a(mStack, new TranslationTextComponent("gui.vampirism.minion.active_task"), 119, 10.0F, 0x404040);

    }

    @Override
    protected void init() {
        super.init();
        this.appearanceButton = this.addButton(new ImageButton(this.guiLeft + 4, this.guiTop + 19, 20, 20, 236, 0, 20, GUI_TEXTURE, this::onConfigurePressed));
        this.lockActionButton = this.addButton(new LockIconButton(this.guiLeft + 99, this.guiTop + 19, this::toggleActionLock));
        ITextComponent[] taskNames = Arrays.stream(container.getAvailableTasks()).map(MinionTask::getNameOfTask).toArray(ITextComponent[]::new);

        this.taskList = this.addButton(new ScrollableListButton(this.guiLeft + 119, this.guiTop + 19 + 19, 87, Math.min(80, 20 * taskNames.length), taskNames.length, taskNames, new StringTextComponent(""), this::selectTask, false));
        this.taskList.visible = false;
        this.taskButton = this.addButton(new ExtendedButton(this.guiLeft + 119, this.guiTop + 19, 88, 20, getActiveTaskName(), (button -> {
            this.taskList.visible = !this.taskList.visible;
        })));
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack mStack, int mouseX, int mouseY) {
        if (this.lockActionButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(mStack, new TranslationTextComponent("gui.vampirism.minion.lock_action"), mouseX, mouseY);
        } else if (this.appearanceButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(mStack, new TranslationTextComponent("gui.vampirism.minion.appearance"), mouseX, mouseY);
        } else {
            super.renderHoveredTooltip(mStack, mouseX, mouseY);
        }
    }


    private void drawButtonTip(MatrixStack mStack, ITextComponent text, int mouseX, int mouseY) {
        GuiUtils.drawHoveringText(ItemStack.EMPTY, mStack, Collections.singletonList(text), mouseX, mouseY, minecraft.getMainWindow().getScaledWidth(), minecraft.getMainWindow().getScaledHeight(), -1, font);
    }

    private ITextComponent getActiveTaskName() {
        int i = container.getTaskToActivate();
        return i >= 0 && i < container.getAvailableTasks().length ? MinionTask.getNameOfTask(container.getAvailableTasks()[i]) : new TranslationTextComponent("minioncommand.vampirism.none");
    }

    private void onConfigurePressed(Button b) {

    }

    private void selectTask(int id) {
        this.container.setTaskToActivate(id);
        this.taskButton.setMessage(getActiveTaskName());
    }

    private void toggleActionLock(Button b) {
        lockActionButton.setLocked(!lockActionButton.isLocked());
    }
}