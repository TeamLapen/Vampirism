package de.teamlapen.faction.client.gui.screens.taskboard;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.client.gui.screens.ExtendedScreen;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.inventory.ITaskMenu;
import de.teamlapen.faction.common.world.inventory.TaskBoardMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageWidget;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.layouts.FrameLayout;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class TaskBoardScreen extends AbstractContainerScreen<TaskBoardMenu> implements ExtendedScreen {

    private static final Identifier BACKGROUND = FIdentifier.mod("background/default");

    private static final int SCREEN_WIDTH = 220;
    private static final int SCREEN_HEIGHT = 200;
    private static final int TASK_LIST_WIDTH = SCREEN_WIDTH - 8;
    private static final int TASK_LIST_HEIGHT = SCREEN_HEIGHT - 20 - 4;

    private final IFactionPlayer<?> factionPlayer;
    private final GridLayout mainLayout = new GridLayout();
    private TaskListWidget taskList;

    public TaskBoardScreen(@NotNull TaskBoardMenu container, @NotNull Inventory playerInventory, @NotNull Component containerName) {
        super(container, playerInventory, containerName);
        this.imageWidth = SCREEN_WIDTH;
        this.imageHeight = SCREEN_HEIGHT;
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Can't open container without faction"));
        this.menu.setReloadListener(this::refreshTaskList);
    }

    @Override
    protected void init() {
        super.init();
        buildLayout();
    }

    private void buildLayout() {
        this.mainLayout.defaultCellSetting().alignHorizontallyCenter().alignVerticallyTop();

        // Background
        this.mainLayout.addChild(ImageWidget.sprite(imageWidth, imageHeight, BACKGROUND), 0, 0);

        // Content container
        LinearLayout contentLayout = this.mainLayout.addChild(LinearLayout.vertical(), 0, 0, this.mainLayout.newCellSettings().padding(6));

        // Title
        contentLayout.addChild(new StringWidget(Component.translatable("gui.factionapi.task_board.tasks"), this.font), contentLayout.newCellSettings().alignHorizontallyCenter());
        contentLayout.addChild(new SeparatorWidget(TASK_LIST_WIDTH), contentLayout.newCellSettings().alignHorizontallyCenter().paddingVertical(2));

        // Task list
        this.taskList = contentLayout.addChild(new TaskListWidget(this.menu, this.factionPlayer, TASK_LIST_WIDTH, TASK_LIST_HEIGHT));

        // Position and add widgets
        this.mainLayout.arrangeElements();
        FrameLayout.centerInRectangle(this.mainLayout, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
        this.mainLayout.visitWidgets(this::addRenderableWidget);

        refreshTaskList();
    }

    private void refreshTaskList() {
        if (this.taskList != null) {
            this.taskList.refreshEntries(this.menu.getVisibleTasks());
        }
    }

    @Override
    protected void repositionElements() {
        this.mainLayout.arrangeElements();
        FrameLayout.centerInRectangle(this.mainLayout, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        // Background is rendered by the ImageWidget in the layout
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Labels are handled by StringWidgets in the layout
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.getChildAt(mouseX, mouseY)
                .filter(listener -> listener.mouseScrolled(mouseX, mouseY, scrollX, scrollY))
                .isPresent();
    }

    @Override
    public @NotNull ITaskMenu getTaskContainer() {
        return this.menu;
    }
}