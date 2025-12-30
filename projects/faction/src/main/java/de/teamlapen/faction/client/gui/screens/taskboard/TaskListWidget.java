package de.teamlapen.faction.client.gui.screens.taskboard;

import de.teamlapen.faction.api.factions.tasks.ITaskInstance;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.world.inventory.ITaskMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractContainerWidget;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TaskListWidget extends AbstractContainerWidget {

    private final ITaskMenu menu;
    private final IFactionPlayer<?> factionPlayer;
    private final List<TaskEntryWidget> entries = new ArrayList<>();
    private double scrollOffset = 0;
    private int contentHeight = 0;
    private final Map<UUID, Boolean> expandedStates = new HashMap<>();
    @Nullable
    private Component emptyMessage;

    public TaskListWidget(ITaskMenu menu, IFactionPlayer<?> factionPlayer, int width, int height) {
        super(0, 0, width, height, Component.empty());
        this.menu = menu;
        this.factionPlayer = factionPlayer;
    }

    public void refreshEntries(List<ITaskInstance> tasks) {
        // Save current expanded states
        for (TaskEntryWidget entry : this.entries) {
            expandedStates.put(entry.getTaskInstanceId(), entry.isExpanded());
        }

        // Remove states for tasks that no longer exist
        Set<UUID> currentTaskIds = new HashSet<>();
        for (ITaskInstance task : tasks) {
            currentTaskIds.add(task.getId());
        }
        expandedStates.keySet().removeIf(id -> !currentTaskIds.contains(id));

        this.entries.clear();
        for (ITaskInstance task : tasks) {
            TaskEntryWidget entry = new TaskEntryWidget(this.menu, this.factionPlayer, task, getWidth() - 8, this::onEntryHeightChanged);
            // Restore expanded state if it was previously expanded
            if (expandedStates.getOrDefault(task.getId(), false)) {
                entry.setExpanded(true);
            }
            this.entries.add(entry);
        }

        repositionEntries();
        clampScrollOffset();
    }

    private void repositionEntries() {
        int yOffset = 0;
        for (TaskEntryWidget entry : entries) {
            entry.setPosition(getX() + 4, (int) (getY() + yOffset - scrollOffset));
            yOffset += entry.getHeight() + 1;
        }
        this.contentHeight = yOffset;
    }

    public void setEmptyMessage(@Nullable Component emptyMessage) {
        this.emptyMessage = emptyMessage;
    }

    private void onEntryHeightChanged() {
        repositionEntries();
        clampScrollOffset();
    }

    private void clampScrollOffset() {
        if (this.contentHeight <= getHeight()) {
            this.scrollOffset = 0;
        } else {
            int maxScroll = this.contentHeight - getHeight();
            this.scrollOffset = Math.max(0, Math.min(maxScroll, this.scrollOffset));
        }
        repositionEntries();
    }

    @Override
    protected int contentHeight() {
        return 20;
    }

    @Override
    protected double scrollRate() {
        return 10;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {

        guiGraphics.fill(getX() + 4, getY(), getX() + getWidth() - 4, getY() + getHeight(), 0x55000000);

        if (entries.isEmpty() && emptyMessage != null) {
            guiGraphics.drawCenteredString(
                    net.minecraft.client.Minecraft.getInstance().font,
                    emptyMessage,
                    getX() + getWidth() / 2,
                    getY() + getHeight() / 2 - 4,
                    0x404040
            );
        } else {
            guiGraphics.enableScissor(getX(), getY(), getX() + getWidth(), getY() + getHeight());
            for (TaskEntryWidget entry : this.entries) {
                if (entry.getY() + entry.getHeight() > getY() && entry.getY() < getY() + getHeight()) {
                    entry.render(guiGraphics, mouseX, mouseY, partialTick);
                }
            }
            guiGraphics.disableScissor();

            // Render scrollbar if needed
            if (this.contentHeight > getHeight()) {
                renderScrollbar(guiGraphics);
            }
        }
    }

    private void renderScrollbar(GuiGraphics graphics) {
        int scrollbarX = getX() + getWidth() - 4;
        int maxScroll = Math.max(1, this.contentHeight - getHeight());
        int scrollbarHeight = Math.max(20, (int) ((float) getHeight() / this.contentHeight * getHeight()));
        int scrollbarY = getY() + (int) ((this.scrollOffset / maxScroll) * (getHeight() - scrollbarHeight));

        graphics.fill(scrollbarX, getY(), scrollbarX + 4, getY() + getHeight(), 0x40cccccc);
        graphics.fill(scrollbarX, scrollbarY, scrollbarX + 4, scrollbarY + scrollbarHeight, 0xFF999999);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (isMouseOver(mouseX, mouseY) && this.contentHeight > getHeight()) {
            int maxScroll = this.contentHeight - getHeight();
            this.scrollOffset = Math.max(0, Math.min(maxScroll, this.scrollOffset - scrollY * 10));
            repositionEntries();
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (isMouseOver(event.x(), event.y())) {
            for (TaskEntryWidget entry : this.entries) {
                if (entry.isMouseOver(event.x(), event.y())) {
                    return entry.mouseClicked(event, doubleClick);
                }
            }
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // Narration for accessibility
    }

    @Override
    public @NotNull List<? extends AbstractWidget> children() {
        return this.entries;
    }
}