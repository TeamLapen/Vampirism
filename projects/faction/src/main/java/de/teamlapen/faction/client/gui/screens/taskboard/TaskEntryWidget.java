package de.teamlapen.faction.client.gui.screens.taskboard;

import de.teamlapen.faction.api.factions.tasks.ITaskInstance;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.factions.tasks.TaskRequirement;
import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.util.Util;
import de.teamlapen.faction.common.world.inventory.ITaskMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.*;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskEntryWidget extends AbstractWidget {

    private static final Identifier TASK_ITEM_BACKGROUND = FIdentifier.mod("widget/task_background");
    private static final Identifier TASK_DETAIL_BACKGROUND = FIdentifier.mod("widget/task_background_2");

    private static final WidgetSprites ACCEPT_BUTTON = new WidgetSprites(
            FIdentifier.mod("widget/task_action_accept"),
            FIdentifier.mod("widget/task_action_accept_highlighted")
    );
    private static final WidgetSprites COMPLETE_BUTTON = new WidgetSprites(
            FIdentifier.mod("widget/task_action_complete"),
            FIdentifier.mod("widget/task_action_complete_highlighted")
    );
    private static final WidgetSprites ABORT_BUTTON = new WidgetSprites(
            FIdentifier.mod("widget/task_action_abort"),
            FIdentifier.mod("widget/task_action_abort_highlighted")
    );
    private static final WidgetSprites LOCATE_TASK_MASTER = new WidgetSprites(
            FIdentifier.mod("widget/locate_task_master"),
            FIdentifier.mod("widget/locate_task_master_highlighted")
    );

    private static final int COLLAPSED_HEIGHT = 21;
    private static final int EXPANDED_HEIGHT = 45;

    private final ITaskMenu menu;
    private final IFactionPlayer<?> factionPlayer;
    private final ITaskInstance taskInstance;
    private final Task task;
    private final Runnable onHeightChanged;
    private final Font font;

    private boolean expanded = false;
    private final List<TaskIconWidget> requirementWidgets = new ArrayList<>();
    private TaskIconWidget rewardWidget;
    private ImageButton actionButton;
    @Nullable
    private ImageButton locateButton;
    @Nullable
    private BlockPos lastKnownPosition;

    public TaskEntryWidget(ITaskMenu menu, IFactionPlayer<?> factionPlayer, ITaskInstance taskInstance, int width, Runnable onHeightChanged) {
        super(0, 0, width, COLLAPSED_HEIGHT, Component.empty());
        this.menu = menu;
        this.factionPlayer = factionPlayer;
        this.taskInstance = taskInstance;
        this.task = menu.getRegistry().getValue(taskInstance.getTask());
        this.onHeightChanged = onHeightChanged;
        this.font = Minecraft.getInstance().font;
        if (menu.showLocateTaskmaster()) {
            this.lastKnownPosition = menu.getLastKnownPosition(taskInstance);
        }
        buildWidgets();
    }

    private void sendCoordinateMessage() {
        Component message;
        if (lastKnownPosition != null) {
            int i = Mth.floor(Util.horizontalDistance(Minecraft.getInstance().player.blockPosition(), lastKnownPosition));
            MutableComponent itextcomponent = ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", lastKnownPosition.getX(), "~", lastKnownPosition.getZ())).withStyle((p_241055_1_) -> {
                return p_241055_1_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent.SuggestCommand("/tp @s " + lastKnownPosition.getX() + " ~ " + lastKnownPosition.getZ())).withHoverEvent(new HoverEvent.ShowText(Component.translatable("chat.coordinates.tooltip")));
            });
            message = Component.translatable("gui.factionapi.faction_menu.last_known_pos").append(itextcomponent.append(Component.translatable("gui.factionapi.faction_menu.distance", i)));
        } else {
            message = Component.translatable("gui.factionapi.faction_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD);
        }
        Minecraft.getInstance().player.sendSystemMessage(message);
    }

    private void buildWidgets() {
        // Requirement widgets
        for (TaskRequirement.Requirement<?> requirement : task.requirements().getAll()) {
            requirementWidgets.add(TaskIconWidget.createRequirement(menu, factionPlayer, taskInstance, requirement));
        }

        // Reward widget
        this.rewardWidget = TaskIconWidget.createReward(menu, taskInstance, task);

        if (menu.showLocateTaskmaster()) {
            this.locateButton = new ImageButton(0, 0, 8, 11, LOCATE_TASK_MASTER, button -> sendCoordinateMessage(), Component.empty());
            Component message;
            if (lastKnownPosition != null) {
                message = Component.translatable("gui.factionapi.faction_menu.last_known_pos").append(Component.literal("[" + lastKnownPosition.toShortString() + "]").withStyle(ChatFormatting.GREEN));
            } else {
                message = Component.translatable("gui.factionapi.faction_menu.last_known_pos.unknown").withStyle(ChatFormatting.GOLD);
            }

            this.locateButton.setTooltip(Tooltip.create(message));
        }

        // Action button
        this.actionButton = new ImageButton(0, 0, 14, 13, getActionSprites(), button -> {
            menu.pressButton(taskInstance);
        }, Component.empty());
    }

    private WidgetSprites getActionSprites() {
        return switch (menu.buttonAction(taskInstance)) {
            case COMPLETE -> COMPLETE_BUTTON;
            case ACCEPT -> ACCEPT_BUTTON;
            default -> ABORT_BUTTON;
        };
    }

    public void toggleExpand() {
        this.expanded = !expanded;
        this.setHeight(expanded ? EXPANDED_HEIGHT : COLLAPSED_HEIGHT);
        onHeightChanged.run();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        if (this.expanded !=expanded) {
            this.expanded = expanded;
            this.setHeight(expanded ? EXPANDED_HEIGHT : COLLAPSED_HEIGHT);
        }
    }

    public UUID getTaskInstanceId() {
        return taskInstance.getId();
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (expanded && actionButton.isMouseOver(event.x(), event.y())) {
            return actionButton.mouseClicked(event, doubleClick);
        }
        if (locateButton != null && locateButton.isMouseOver(event.x(), event.y())) {
            return locateButton.mouseClicked(event, doubleClick);
        }
        if (expanded) {
            for (TaskIconWidget widget : requirementWidgets) {
                if (widget.isMouseOver(event.x(), event.y()) && widget.mouseClicked(event, doubleClick)) {
                    return true;
                }
            }
            if (rewardWidget != null && rewardWidget.isMouseOver(event.x(), event.y())) {
                return rewardWidget.mouseClicked(event, doubleClick);
            }
        }
        if (isMouseOver(event.x(), event.y())) {
            toggleExpand();
            return true;
        }
        return false;
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        // Background
        int bgColor = getBackgroundColor();
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, TASK_ITEM_BACKGROUND, getX(), getY(), getWidth(), COLLAPSED_HEIGHT, bgColor);

        // Title
        Component title = task.title();
        int maxTitleWidth = getWidth() - 26; // Leave room for timer and button
        if (font.width(title) > maxTitleWidth) {
            title = Component.literal(font.plainSubstrByWidth(title.getString(), maxTitleWidth - 6) + "...");
        }
        GuiGraphicsExtractor.text(font, title, getX() + 4, getY() + (COLLAPSED_HEIGHT - font.lineHeight) / 2, 0xFFFFFFFF, true);

        if (locateButton != null) {
            locateButton.setPosition(getX() + getWidth() - 12, getY() + 2);
            locateButton.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        }

        // Timer (for non-unique accepted tasks)
        if (!menu.isTaskNotAccepted(taskInstance) && !taskInstance.isUnique(menu.getRegistry())) {
            Component timer = getTimerComponent();
            int timerX = getX() + getWidth() - 26;
            int timerY = getY() + 13;
            // Scale down the timer
            GuiGraphicsExtractor.pose().pushMatrix();
            GuiGraphicsExtractor.pose().translate(timerX, timerY);
            GuiGraphicsExtractor.pose().scale(0.75f, 0.75f);
            GuiGraphicsExtractor.text(font, timer, 0, 0, 0xFFFFFFFF, false);
            GuiGraphicsExtractor.pose().popMatrix();
        }

        // Expanded details
        if (expanded) {
            renderExpandedDetails(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        }

        // Tooltip on hover (only when collapsed and not over button)
        if (isHovered && !expanded && !actionButton.isHovered() && (locateButton == null || !locateButton.isHovered())) {
            renderTaskTooltip(GuiGraphicsExtractor, mouseX, mouseY);
        }
    }

    private void renderExpandedDetails(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        int detailY = getY() + COLLAPSED_HEIGHT;
        int detailHeight = getHeight() - COLLAPSED_HEIGHT;

        // Detail background
        GuiGraphicsExtractor.blitSprite(RenderPipelines.GUI_TEXTURED, TASK_DETAIL_BACKGROUND,
                getX() + 2, detailY, getWidth() - 4, detailHeight);

        int iconX = getX() + 6;
        int iconY = detailY;
        for (TaskIconWidget widget : requirementWidgets) {
            widget.setPosition(iconX, iconY);
            widget.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
            iconX += 20;
        }

        // Reward section
        int rewardLabelX = getX() + getWidth() - 18 - 14 -5;

        if (rewardWidget != null) {
            rewardWidget.setPosition(rewardLabelX, detailY);
            rewardWidget.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
        }

        // Action button at the end of expanded entry (right side)
        actionButton.setPosition(getX() + getWidth() - 18, detailY + (detailHeight - 13) / 2);
        actionButton.setTooltip(Tooltip.create(Component.translatable(menu.buttonAction(taskInstance).getTranslationKey())));
        actionButton.extractRenderState(GuiGraphicsExtractor, mouseX, mouseY, partialTick);
    }

    private void renderTaskTooltip(GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(task.title().plainCopy().withStyle(style -> style.withColor(menu.getFactionColor())));

        task.description().ifPresent(desc -> {
            tooltip.add(desc);
            tooltip.add(Component.empty());
        });

        if (menu.isTaskNotAccepted(taskInstance)) {
            tooltip.add(Component.translatable("gui.factionapi.taskmaster.not_accepted").withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY));
        } else {
            for (var entry : task.requirements().requirements().entrySet()) {
                TaskRequirement.Type type = entry.getKey();
                boolean typeCompleted = menu.areRequirementsCompleted(taskInstance, type);
                tooltip.add(Component.translatable(type.getTranslationKey()).append(":")
                        .withStyle(x -> x.withStrikethrough(typeCompleted))
                        .withStyle(typeCompleted ? ChatFormatting.GREEN : ChatFormatting.WHITE));

                for (TaskRequirement.Requirement<?> req : entry.getValue()) {
                    int current = menu.getRequirementStatus(taskInstance, req);
                    int needed = req.getAmount(factionPlayer);
                    boolean reqCompleted = menu.isRequirementCompleted(taskInstance, req);
                    tooltip.add(Component.literal("  ").append(req.description())
                            .append(" " + current + "/" + needed)
                            .withStyle(s -> s.withStrikethrough(reqCompleted))
                            .withStyle(reqCompleted ? ChatFormatting.GREEN : ChatFormatting.GRAY));
                }
            }
        }

        GuiGraphicsExtractor.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
    }

    private Component getTimerComponent() {
        @SuppressWarnings("DataFlowIssue")
        long remainingTime = taskInstance.getTaskTimeStamp() - Minecraft.getInstance().level.getGameTime();
        if (remainingTime >= 0) {
            remainingTime = remainingTime / 20;
            long hours = remainingTime / 3600;
            long minutes = (remainingTime % 3600) / 60;
            long seconds = remainingTime % 60;
            String time = String.format("%d:%02d:%02d", hours, minutes, seconds);
            ChatFormatting color = remainingTime < taskInstance.getTaskDuration() / 20f * 0.1F
                    ? ChatFormatting.YELLOW : ChatFormatting.WHITE;
            return Component.literal(time).withStyle(color);
        } else {
            return Component.translatable("gui.factionapi.taskmaster.task_failed").withStyle(ChatFormatting.BLACK);
        }
    }

    private int getBackgroundColor() {
        if (menu.isCompleted(taskInstance)) {
            return ARGB.colorFromFloat(1, 0.4f, 0.4f, 0.4f);
        }

        boolean isUnique = taskInstance.isUnique(menu.getRegistry());
        @SuppressWarnings("DataFlowIssue")
        boolean remainsTime = taskInstance.getTaskTimeStamp() - Minecraft.getInstance().level.getGameTime() > 0;

        if (menu.canCompleteTask(taskInstance)) {
            return isUnique
                    ? ARGB.colorFromFloat(1, 1f, 0.855859375f, 0)  // Gold for unique
                    : ARGB.colorFromFloat(1, 0, 0.9f, 0);          // Green for normal
        } else if (menu.isTaskNotAccepted(taskInstance)) {
            return isUnique
                    ? ARGB.colorFromFloat(1, 0.64f, 0.57f, 0.5f)   // Tan for unique not accepted
                    : 0xFFFFFFFF;                                   // White for normal not accepted
        } else if (!isUnique && !remainsTime) {
            return ARGB.colorFromFloat(1, 1f, 85 / 255f, 85 / 255f); // Red for expired
        } else {
            return isUnique
                    ? ARGB.colorFromFloat(1, 1f, 0.9f, 0.6f)       // Light gold for unique in progress
                    : 0xFFFFFFFF;                                   // White for normal in progress
        }
    }


    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, task.title());
    }
}