package de.teamlapen.faction.client.gui.screens;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.gui.components.DropdownWidget;
import de.teamlapen.gui.components.IRenderLast;
import de.teamlapen.faction.common.world.inventory.MinionContainer;
import net.minecraft.client.gui.GuiGraphicsExtractor;
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

    private static final Identifier BACKGROUND = FIdentifier.mod("textures/gui/container/minion/inventory.png");
    private static final Identifier LOCKED_SPRITE = FIdentifier.mod("container/minion_inventory/locked");
    private static final WidgetSprites APPEARANCE_SPRITES = new WidgetSprites(FIdentifier.mod("widget/settings"), FIdentifier.mod("widget/settings_highlighted"));
    private static final WidgetSprites STATS_SPRITES = new WidgetSprites(FIdentifier.mod("widget/skill_points"), FIdentifier.mod("widget/skill_points_highlighted"));
    private final int extraSlots;
    private DropdownWidget taskList;
    private Button taskButton;
    private Button appearanceButton;
    private Button statButton;
    private LockIconButton lockActionButton;

    public MinionScreen(MinionContainer screenContainer, @NotNull Inventory inv, @NotNull Component titleIn) {
        super(screenContainer, inv, titleIn, 214, 185);
        this.extraSlots = screenContainer.getExtraSlots();
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double p_97752_, double p_97753_) {
        this.taskList.mouseDragged(event, p_97752_, p_97753_);
        return super.mouseDragged(event, p_97752_, p_97753_);
    }

    @Override
    public void extractContents(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        super.extractContents(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof IRenderLast last) {
                last.renderLast(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
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

        this.taskList = this.addRenderableWidget(DropdownWidget.simple(this.leftPos + 120, this.topPos + 20).width(88).itemHeight(18).maxVisibleItems(4).onSelect(this::setActiveTask).simpleItems(taskNames).build().setSelectedIndex(availableTasks.indexOf(menu.getSelectedTask())));
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics,int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);

        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        GuiRenderer.blit(graphics, BACKGROUND, i, j, this.imageWidth, this.imageHeight);
        for (int k = extraSlots; k < 15; k++) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, LOCKED_SPRITE, i + 29 + 18 * (k / 3), j + 44 + 18 * (k % 3), 13, 13);
        }
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(this.font, title, 5, 6, 0xff404040, false);
        graphics.text(this.font, Component.translatable("gui.factionapi.minion.active_task"), 120, 10, 0xff404040, false);
    }

    @Override
    protected void extractTooltip(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (this.lockActionButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.factionapi.minion.lock_action"), mouseX, mouseY);
        } else if (this.appearanceButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.factionapi.minion.appearance"), mouseX, mouseY);
        } else if (this.statButton.isMouseOver(mouseX, mouseY)) {
            drawButtonTip(graphics, Component.translatable("gui.factionapi.minion_stats"), mouseX, mouseY);
        } else {
            super.extractTooltip(graphics, mouseX, mouseY);
        }
    }

    private void drawButtonTip(@NotNull GuiGraphicsExtractor graphics, Component text, int mouseX, int mouseY) {
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