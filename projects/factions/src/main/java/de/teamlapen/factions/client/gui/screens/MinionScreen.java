package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.client.gui.components.SimpleList;
import de.teamlapen.factions.common.inventory.MinionContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.LockIconButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class MinionScreen extends AbstractContainerScreen<MinionContainer> {

    private static final ResourceLocation BACKGROUND = FResourceLocation.mod("textures/gui/container/minion_inventory.png");
    private static final ResourceLocation LOCKED_SPRITE = FResourceLocation.mod("container/minion_inventory/locked");
    private static final WidgetSprites APPEARANCE_SPRITES = new WidgetSprites(FResourceLocation.mod("widget/settings"), FResourceLocation.mod("widget/settings_highlighted"));
    private static final WidgetSprites STATS_SPRITES = new WidgetSprites(FResourceLocation.mod("widget/skill_points"), FResourceLocation.mod("widget/skill_points_highlighted"));
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
    public boolean mouseDragged(MouseButtonEvent p_446671_, double p_97752_, double p_97753_) {
        this.taskList.mouseDragged(p_446671_, p_97752_, p_97753_);
        return super.mouseDragged(p_446671_, p_97752_, p_97753_);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);

    }

    @Override
    protected void init() {
        super.init();
        this.appearanceButton = this.addRenderableWidget(new ImageButton(this.leftPos + 6, this.topPos + 21, 18, 18, APPEARANCE_SPRITES, this::onConfigurePressed));
        this.lockActionButton = this.addRenderableWidget(new LockIconButton(this.leftPos + 99, this.topPos + 19, this::toggleActionLock));
        this.statButton = this.addRenderableWidget(new ImageButton(this.leftPos + 6, this.topPos + 40, 18, 18, STATS_SPRITES, this::onStatsPressed));
        this.lockActionButton.setLocked(this.menu.isTaskLocked());
        var taskNames = Arrays.stream(menu.getAvailableTasks()).map(x -> (Component) x.getName()).toList();

        this.taskList = this.addRenderableWidget(SimpleList.builder(this.leftPos + 119, this.topPos + 19 + 19, 88, Math.min(3 * 18, taskNames.size() * 18) + 2).componentsWithClick(taskNames, this::selectTask).build());
        this.taskButton = this.addRenderableWidget(new ExtendedButton(this.leftPos + 119, this.topPos + 19, 88, 20, getActiveTaskName(), button -> taskList.visible = !taskList.visible));
        this.taskList.visible = false;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        GuiRenderer.blit(graphics, BACKGROUND, i, j, this.imageWidth, this.imageHeight);
        for (int k = extraSlots; k < 15; k++) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LOCKED_SPRITE, i + 29 + 18 * (k / 3), j + 44 + 18 * (k % 3), 13, 13);
        }
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, title, 5, 6, 0x404040, false);
        graphics.drawString(this.font, Component.translatable("gui.vampirism.minion.active_task"), 120, 10, 0x404040, false);

    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.lockActionButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.vampirism.minion.lock_action"), mouseX, mouseY);
        } else if (this.appearanceButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.vampirism.minion.appearance"), mouseX, mouseY);
        } else if (this.statButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.vampirism.minion_stats"), mouseX, mouseY);
        } else {
            super.renderTooltip(graphics, mouseX, mouseY);
        }
    }


    private void drawButtonTip(@NotNull GuiGraphics graphics, Component text, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(this.font, Collections.singletonList(text), Optional.empty(), mouseX, mouseY);
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
        this.taskList.visible = false;
        this.menu.setTaskToActivate(id);
        this.taskButton.setMessage(getActiveTaskName());
    }

    private void toggleActionLock(Button b) {
        lockActionButton.setLocked(!lockActionButton.isLocked());
        menu.setTaskLocked(lockActionButton.isLocked());
    }
}