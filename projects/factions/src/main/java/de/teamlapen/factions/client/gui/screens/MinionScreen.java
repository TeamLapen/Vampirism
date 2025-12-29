package de.teamlapen.factions.client.gui.screens;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.client.gui.GuiRenderer;
import de.teamlapen.factions.client.gui.components.DropdownWidget;
import de.teamlapen.factions.client.gui.components.IRenderLast;
import de.teamlapen.factions.common.world.inventory.MinionContainer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.*;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class MinionScreen extends AbstractContainerScreen<MinionContainer> {

    private static final Identifier BACKGROUND = FResourceLocation.mod("textures/gui/container/minion/inventory.png");
    private static final Identifier LOCKED_SPRITE = FResourceLocation.mod("container/minion_inventory/locked");
    private static final WidgetSprites APPEARANCE_SPRITES = new WidgetSprites(FResourceLocation.mod("widget/settings"), FResourceLocation.mod("widget/settings_highlighted"));
    private static final WidgetSprites STATS_SPRITES = new WidgetSprites(FResourceLocation.mod("widget/skill_points"), FResourceLocation.mod("widget/skill_points_highlighted"));
    private final int extraSlots;
    private DropdownWidget taskList;
    private Button taskButton;
    private Button appearanceButton;
    private Button statButton;
    private LockIconButton lockActionButton;

    public MinionScreen(MinionContainer screenContainer, @NotNull Inventory inv, @NotNull Component titleIn) {
        super(screenContainer, inv, titleIn);
        this.imageWidth = 214;
        this.imageHeight = 185;
        this.extraSlots = screenContainer.getExtraSlots();
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double p_97752_, double p_97753_) {
        this.taskList.mouseDragged(event, p_97752_, p_97753_);
        return super.mouseDragged(event, p_97752_, p_97753_);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderContents(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.renderContents(guiGraphics, mouseX, mouseY, partialTick);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof IRenderLast last) {
                last.renderLast(guiGraphics, mouseX, mouseY, partialTick);
            }
        }
    }

    @Override
    protected void init() {
        super.init();
        this.appearanceButton = this.addRenderableWidget(new ImageButton(this.leftPos + 6, this.topPos + 21, 18, 18, APPEARANCE_SPRITES, this::onConfigurePressed));
        this.lockActionButton = this.addRenderableWidget(new LockIconButton(this.leftPos + 99, this.topPos + 19, this::toggleActionLock));
        this.statButton = this.addRenderableWidget(new ImageButton(this.leftPos + 6, this.topPos + 40, 18, 18, STATS_SPRITES, this::onStatsPressed));
        this.lockActionButton.setLocked(this.menu.isTaskLocked());
        var availableTasks = List.of(menu.getAvailableTasks());
        var taskNames = availableTasks.stream().map(x -> (Component) x.getName()).toList();

        this.taskList = this.addRenderableWidget(DropdownWidget.builder(this.leftPos + 120, this.topPos + 20).width(88).itemHeight(18).maxVisibleItems(4).onSelect(this::setActiveTask).items(taskNames).build().setSelectedIndex(availableTasks.indexOf(menu.getSelectedTask())));
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
        graphics.drawString(this.font, Component.translatable("gui.factions.minion.active_task"), 120, 10, 0x404040, false);

    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        if (this.lockActionButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.factions.minion.lock_action"), mouseX, mouseY);
        } else if (this.appearanceButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.factions.minion.appearance"), mouseX, mouseY);
        } else if (this.statButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.factions.minion_stats"), mouseX, mouseY);
        } else {
            super.renderTooltip(graphics, mouseX, mouseY);
        }
    }

    private void drawButtonTip(@NotNull GuiGraphics graphics, Component text, int mouseX, int mouseY) {
        graphics.setTooltipForNextFrame(this.font, Collections.singletonList(text), Optional.empty(), mouseX, mouseY);
    }

    private void setActiveTask(int id) {
        this.menu.setTaskToActivate(id);
    }

    private void onConfigurePressed(Button b) {
        menu.openConfigurationScreen();
    }

    private void onStatsPressed(Button b) {
        menu.openStatsScreen();
    }

    private void toggleActionLock(Button b) {
        lockActionButton.setLocked(!lockActionButton.isLocked());
        menu.setTaskLocked(lockActionButton.isLocked());
    }
}