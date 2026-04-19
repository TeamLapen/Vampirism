package de.teamlapen.faction.client.gui.screens.taskboard;

import de.teamlapen.faction.api.factions.tasks.ITaskInstance;
import de.teamlapen.faction.api.factions.tasks.Task;
import de.teamlapen.faction.api.factions.tasks.TaskRequirement;
import de.teamlapen.faction.api.world.entities.player.IFactionPlayer;
import de.teamlapen.faction.common.factions.tasks.requirements.EntityRequirement;
import de.teamlapen.faction.common.factions.tasks.requirements.EntityTypeRequirement;
import de.teamlapen.faction.common.factions.tasks.requirements.ItemRequirement;
import de.teamlapen.faction.common.factions.tasks.reward.ItemReward;
import de.teamlapen.faction.common.world.inventory.ITaskMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class TaskIconWidget extends AbstractWidget {

    private static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);
    private static final ItemStack PAPER = new ItemStack(Items.PAPER);

    private final ItemStack displayStack;
    private final Component tooltipTitle;
    private final List<Component> tooltipLines;
    private final Font font;

    @Nullable
    private final String progressText;
    private final int progressColor;

    private TaskIconWidget(ItemStack displayStack, Component tooltipTitle, List<Component> tooltipLines,
                           @Nullable String progressText, int progressColor) {
        super(0, 0, 16, 16, tooltipTitle);
        this.displayStack = displayStack;
        this.tooltipTitle = tooltipTitle;
        this.tooltipLines = tooltipLines;
        this.progressText = progressText;
        this.progressColor = progressColor;
        this.font = Minecraft.getInstance().font;
    }

    /**
     * Creates a requirement icon widget.
     */
    public static TaskIconWidget createRequirement(ITaskMenu menu, IFactionPlayer<?> factionPlayer,
                                                   ITaskInstance taskInstance, TaskRequirement.Requirement<?> requirement) {
        ItemStack stack = getRequirementStack(requirement);
        Component title = requirement.description();

        List<Component> tooltipLines = new ArrayList<>();
        String progressText = null;
        int progressColor = 0xFFFFFFFF;

        int needed = requirement.getAmount(factionPlayer);

        if (!menu.isTaskNotAccepted(taskInstance)) {
            int current = menu.getRequirementStatus(taskInstance, requirement);
            boolean completed = menu.isRequirementCompleted(taskInstance, requirement);

            progressText = current + "/" + needed;
            progressColor = completed ? 0xFF00FF00 : 0xFFFFFFFF;

            tooltipLines.add(Component.literal(current + "/" + needed)
                    .withStyle(completed ? ChatFormatting.GREEN : ChatFormatting.GRAY));
        } else {
            progressText = String.valueOf(needed);
        }

        return new TaskIconWidget(stack, title, tooltipLines, progressText, progressColor);
    }

    /**
     * Creates a reward icon widget.
     */
    public static TaskIconWidget createReward(ITaskMenu menu, ITaskInstance taskInstance, Task task) {
        ItemStack stack;
        String progressText = null;
        if (taskInstance.getReward() instanceof ItemReward.Instance(ItemStackTemplate rewardStack)) {
            stack = rewardStack.create();
            progressText = String.valueOf(rewardStack.count());
        } else {
            stack = PAPER;
        }

        Component title = Component.translatable("gui.factionapi.taskmaster.reward")
                .withStyle(ChatFormatting.UNDERLINE, ChatFormatting.GOLD);

        List<Component> tooltipLines = new ArrayList<>();
        MutableComponent description = task.reward().description().plainCopy();
        if (progressText != null) {
            description.append(" ").append(progressText);
        }
        tooltipLines.add(description);

        return new TaskIconWidget(stack, title, tooltipLines, progressText, -1);
    }

    private static ItemStack getRequirementStack(TaskRequirement.Requirement<?> requirement) {
        return switch (requirement) {
            case ItemRequirement itemReq -> itemReq.getItemStack();
            case EntityRequirement ignored -> SKULL_ITEM;
            case EntityTypeRequirement ignored -> SKULL_ITEM;
            default -> PAPER;
        };
    }

    @Override
    protected void extractWidgetRenderState(@NotNull GuiGraphicsExtractor GuiGraphicsExtractor, int mouseX, int mouseY, float partialTick) {
        GuiGraphicsExtractor.item(displayStack, getX(), getY());

        // Render progress text below the item if present
        if (progressText != null) {
            // Scale down the text to fit
            GuiGraphicsExtractor.pose().pushMatrix();
            GuiGraphicsExtractor.pose().translate(getX() + (width/2f), getY() + 16);
            GuiGraphicsExtractor.pose().scale(0.7f, 0.7f);
            GuiGraphicsExtractor.text(font, progressText, 0, 0, progressColor);
            GuiGraphicsExtractor.pose().popMatrix();
        }

        // Render tooltip when hovered
        if (isHovered) {
            List<Component> tooltip = new ArrayList<>();
            tooltip.add(tooltipTitle);
            tooltip.addAll(tooltipLines);
            GuiGraphicsExtractor.setComponentTooltipForNextFrame(font, tooltip, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        narrationElementOutput.add(NarratedElementType.TITLE, tooltipTitle);
    }
}