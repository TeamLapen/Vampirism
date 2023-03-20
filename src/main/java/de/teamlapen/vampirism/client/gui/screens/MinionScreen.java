package de.teamlapen.vampirism.client.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.components.SimpleList;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.minion.IMinionTask;
import de.teamlapen.vampirism.inventory.MinionContainer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@OnlyIn(Dist.CLIENT)
public class MinionScreen extends AbstractContainerScreen<MinionContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/minion_inventory.png");
    private final int extraSlots;
    private SimpleList<?> taskList;
    private Button taskButton;
    private Button appearanceButton;
    private Button statButton;
    private LockIconButton lockActionButton;

    public MinionScreen(@NotNull MinionContainer screenContainer, @NotNull Inventory inv, @NotNull Component titleIn) {
        super(Objects.requireNonNull(screenContainer), inv, titleIn);
        this.imageWidth = 214;
        this.imageHeight = 185;
        this.extraSlots = screenContainer.getExtraSlots();
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        this.taskList.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
    }

    @Override
    public void render(@NotNull PoseStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(mStack, mouseX, mouseY);

    }

    @Override
    protected void init() {
        super.init();
        this.appearanceButton = this.addRenderableWidget(new ImageButton(this.leftPos + 6, this.topPos + 21, 18, 18, 238, 0, 18, GUI_TEXTURE, this::onConfigurePressed));
        this.lockActionButton = this.addRenderableWidget(new LockIconButton(this.leftPos + 99, this.topPos + 19, this::toggleActionLock));
        this.statButton = this.addRenderableWidget(new ImageButton(this.leftPos + 6, this.topPos + 40, 18, 18, 220, 0, 18, GUI_TEXTURE, this::onStatsPressed));
        this.lockActionButton.setLocked(this.menu.isTaskLocked());
        List<Component> taskNames = Arrays.stream(menu.getAvailableTasks()).map(IMinionTask::getName).toList();

        this.taskList = this.addRenderableWidget(SimpleList.builder(this.leftPos + 119, this.topPos + 19 + 19, 88, Math.min(3 * 18, taskNames.size() * 18) + 2).componentsWithClick(taskNames, this::selectTask).build());
        this.taskButton = this.addRenderableWidget(new ExtendedButton(this.leftPos + 119, this.topPos + 19, 88, 20, getActiveTaskName(), button -> taskList.isVisible = !taskList.isVisible));
        this.taskList.isVisible = false;
    }

    @Override
    protected void renderBg(@NotNull PoseStack mStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        this.blit(mStack, i, j, 0, 0, this.imageWidth, this.imageHeight);
        for (int k = extraSlots; k < 15; k++) {
            this.blit(mStack, i + 29 + 18 * (k / 3), j + 44 + 18 * (k % 3), 236, 80, 13, 13);
        }

    }

    @Override
    protected void renderLabels(@NotNull PoseStack mStack, int mouseX, int mouseY) {
        this.font.draw(mStack, title, 5, 6.0F, 0x404040);
        this.font.draw(mStack, Component.translatable("gui.vampirism.minion.active_task"), 120, 10.0F, 0x404040);

    }

    @Override
    protected void renderTooltip(@NotNull PoseStack mStack, int mouseX, int mouseY) {
        if (this.lockActionButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(mStack, Component.translatable("gui.vampirism.minion.lock_action"), mouseX, mouseY);
        } else if (this.appearanceButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(mStack, Component.translatable("gui.vampirism.minion.appearance"), mouseX, mouseY);
        } else if (this.statButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(mStack, Component.translatable("gui.vampirism.minion_stats"), mouseX, mouseY);
        } else {
            super.renderTooltip(mStack, mouseX, mouseY);
        }
    }


    private void drawButtonTip(@NotNull PoseStack mStack, Component text, int mouseX, int mouseY) {
        this.renderTooltip(mStack, Collections.singletonList(text), Optional.empty(), mouseX, mouseY, font);
    }

    private Component getActiveTaskName() {
        return menu.getSelectedTask().getName();
    }

    private void onConfigurePressed(Button b) {
        menu.openConfigurationScreen();
    }

    private void onStatsPressed(Button b) {
        menu.openStatsScreen();
    }

    private void selectTask(int id) {
        this.taskList.isVisible = false;
        this.menu.setTaskToActivate(id);
        this.taskButton.setMessage(getActiveTaskName());
    }

    private void toggleActionLock(Button b) {
        lockActionButton.setLocked(!lockActionButton.isLocked());
        menu.setTaskLocked(lockActionButton.isLocked());
    }
}