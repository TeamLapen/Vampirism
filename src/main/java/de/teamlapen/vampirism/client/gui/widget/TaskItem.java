package de.teamlapen.vampirism.client.gui.widget;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.api.entity.player.task.ITaskRewardInstance;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.client.gui.ExtendedScreen;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemRewardInstance;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.gui.ScreenUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Element for {@link ScrollableListWithDummyWidget} that presents a {@link Task}
 *
 * @param <T>
 */
public class TaskItem<T extends Screen & ExtendedScreen> extends ScrollableListWithDummyWidget.ListItem<ITaskInstance> {
    protected static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    protected static final Component REWARD = Component.translatable("gui.vampirism.taskmaster.reward").withStyle(ChatFormatting.UNDERLINE);
    protected static final Component REQUIREMENT = Component.translatable("gui.vampirism.taskmaster.requirement").withStyle(ChatFormatting.UNDERLINE);
    protected static final Component REQUIREMENT_STRIKE = REQUIREMENT.plainCopy().withStyle(ChatFormatting.STRIKETHROUGH);
    protected static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);
    protected static final ItemStack PAPER = new ItemStack(Items.PAPER);

    protected final T screen;
    protected final IFactionPlayer<?> factionPlayer;
    private final @Nullable TaskActionButton taskButton;

    private final Map<ITaskInstance, List<Component>> toolTips = Maps.newHashMap();


    public TaskItem(@NotNull ITaskInstance item, @NotNull ScrollableListWithDummyWidget<ITaskInstance> list, boolean isDummy, T screen, IFactionPlayer<?> factionPlayer) {
        super(item, list, isDummy);
        this.screen = screen;
        this.factionPlayer = factionPlayer;
        this.taskButton = isDummy ? new TaskActionButton(0, 0) : null;
    }

    public @NotNull List<Component> getTooltipFromItem2(@NotNull ItemStack itemStack, boolean strikeThough, @Nullable String bonus) {
        List<Component> list = itemStack.getTooltipLines(Minecraft.getInstance().player, Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED : TooltipFlag.Default.NORMAL);
        List<Component> list1 = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                MutableComponent t = list.get(0).copy().append(" " + (bonus != null ? bonus : "") + itemStack.getCount());
                if (strikeThough) t.withStyle(ChatFormatting.STRIKETHROUGH);
                list1.add(t);
            } else {
                list1.add(strikeThough ? list.get(i).copy().withStyle(ChatFormatting.STRIKETHROUGH) : list.get(i));
            }
        }
        return list1;
    }

    @Override
    public boolean onDummyClick(double mouseX, double mouseY) {
        this.taskButton.onClick(mouseX, mouseY);
        return true;
    }

    @Override
    public void renderDummy(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
        //render background
        RenderSystem.enableDepthTest();
        ScreenUtils.blitWithBorder(matrixStack, TASKMASTER_GUI_TEXTURE, x, y, 17, 208, listWidth, itemHeight, 136, 21, 3, 3, 3, 3, zLevel);
        RenderSystem.disableDepthTest();

        //render content
        ITaskRewardInstance reward = this.item.getReward();
        if (reward instanceof ItemRewardInstance) {
            ItemStack stack = ((ItemRewardInstance) reward).getReward();
            this.screen.getItemRenderer().renderAndDecorateItem(stack, x + 3 + 113 - 21, y + 2);
            this.screen.getItemRenderer().renderGuiItemDecorations(this.screen.font, stack, x + 3 + 113 - 21, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
        } else {
            this.screen.getItemRenderer().renderAndDecorateItem(PAPER, x + 3 + 113 - 21, y + 2);
        }
        List<TaskRequirement.Requirement<?>> requirements = this.item.getTask().getRequirement().getAll();
        for (int i = 0; i < requirements.size(); i++) {
            TaskRequirement.Requirement<?> requirement = requirements.get(i);
            switch (requirement.getType()) {
                case ITEMS -> {
                    ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                    this.screen.getItemRenderer().renderAndDecorateItem(stack, x + 3 + 3 + i * 20, y + 2);
                    this.screen.getItemRenderer().renderGuiItemDecorations(this.screen.font, stack, x + 3 + 3 + i * 20, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
                }
                case ENTITY, ENTITY_TAG -> {
                    this.screen.getItemRenderer().renderAndDecorateItem(SKULL_ITEM, x + 3 + 3 + i * 20, y + 2);
                    this.screen.getItemRenderer().renderGuiItemDecorations(this.screen.font, SKULL_ITEM, x + 3 + 3 + i * 20, y + 2, "" + requirement.getAmount(factionPlayer));
                }
                default -> this.screen.getItemRenderer().renderAndDecorateItem(PAPER, x + 3 + 3 + i * 20, y + 2);
            }
        }
        //render task button
        this.taskButton.x = x + listWidth - 17;
        this.taskButton.y = y + 4;
        this.taskButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public void renderDummyToolTip(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
        if (mouseX >= x + 3 + 113 - 21 + 1 && mouseX < x + 3 + 113 - 21 + 16 + 1 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
            ITaskRewardInstance reward = this.item.getReward();
            if (reward instanceof ItemRewardInstance) {
                this.renderItemTooltip(matrixStack, ((ItemRewardInstance) reward).getReward(), mouseX, mouseY, REWARD, false, null);
            } else {
                this.renderItemTooltip(matrixStack, this.item.getTask(), mouseX, mouseY);
            }
        }
        List<TaskRequirement.Requirement<?>> requirements = this.item.getTask().getRequirement().getAll();
        for (int i = 0; i < requirements.size(); i++) {
            if (mouseX >= x + 3 + 3 + i * 20 && mouseX < x + 3 + 16 + 3 + i * 20 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                this.renderRequirementTool(matrixStack, this.item, requirements.get(i), mouseX, mouseY);
            }
        }
        this.taskButton.renderToolTip(matrixStack, mouseX, mouseY);
    }

    @Override
    public void renderItem(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
        //render background
        RenderSystem.enableDepthTest();

        TaskContainer container = this.screen.getTaskContainer();
        if (container.isCompleted(this.item)) {
            RenderSystem.setShaderColor(0.4f, 0.4f, 0.4f, 1);
        } else {
            boolean isUnique = this.item.isUnique();
            boolean remainsTime = this.item.getTaskTimeStamp() - Minecraft.getInstance().level.getGameTime() > 0;
            if (container.canCompleteTask(this.item)) {
                if (isUnique) {
                    RenderSystem.setShaderColor(1f, 0.855859375f, 0, 1);
                } else {
                    RenderSystem.setShaderColor(0, 0.9f, 0, 1);
                }
            } else if (container.isTaskNotAccepted(this.item)) {
                if (isUnique) {
                    RenderSystem.setShaderColor(0.64f, 0.57f, 0.5f, 1);
                } else {
                    RenderSystem.setShaderColor(0.55f, 0.55f, 0.55f, 1);
                }
            } else if (!isUnique && !remainsTime) {
                RenderSystem.setShaderColor(1f, 85 / 255f, 85 / 255f, 1);
            } else {
                if (isUnique) {
                    RenderSystem.setShaderColor(1f, 0.9f, 0.6f, 1f);
                } else {
                    RenderSystem.setShaderColor(0.85f, 1f, 0.85f, 1f);
                }
            }
        }

        RenderSystem.setShaderTexture(0, TASKMASTER_GUI_TEXTURE);
        GuiComponent.blit(matrixStack, x, y, this.screen.getBlitOffset(), 17, 187, 136, 21, 256, 256);
        GuiComponent.blit(matrixStack, x + 132, y, this.screen.getBlitOffset(), 17 + 133, 187, 136 - 133, 21, 256, 256);
        RenderSystem.setShaderColor(1, 1, 1, 1);

        //render name
        Optional<FormattedCharSequence> text = Optional.ofNullable(this.screen.font.split(this.item.getTask().getTranslation(), 131).get(0));
        text.ifPresent(t -> this.screen.font.draw(matrixStack, t, x + 2, y + 4, 3419941));//(6839882 & 16711422) >> 1 //8453920 //4226832

        if (!this.screen.getTaskContainer().isTaskNotAccepted(this.item) && !this.item.isUnique()) {
            long remainingTime = this.item.getTaskTimeStamp() - Minecraft.getInstance().level.getGameTime();
            Component msg;
            if (remainingTime >= 0) {
                remainingTime = remainingTime / 20;
                long hours = remainingTime / 60 / 60;
                long minutes = remainingTime / 60 % (60);
                long seconds = remainingTime % (60);
                String time = "" + hours + ":";
                if (minutes < 10) time += "0";
                time += minutes + ":";
                if (seconds < 10) time += "0";
                time += seconds;
                msg = Component.literal(time);
            } else {
                msg = Component.translatable("text.vampirism.task_failed");
            }
            int width = this.screen.font.width(msg);
            int color = 11184810;
            if (remainingTime < this.item.getTaskDuration() / 20F * 0.1F) {
                color = 16733525;
            }
            this.screen.font.drawShadow(matrixStack, msg, x + 134 - width, y + 12, color);
        }

        RenderSystem.disableDepthTest();

    }

    @Override
    public void renderItemToolTip(@NotNull PoseStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
        //default task tooltips
        List<Component> toolTips = this.toolTips.getOrDefault(this.item, Lists.newArrayList());
        if (toolTips.isEmpty()) {
            generateTaskToolTip(this.item, toolTips);
        }
        this.screen.renderComponentTooltip(matrixStack, toolTips, mouseX, mouseY);
    }

    private void clickButton(Button id) {
        this.screen.getTaskContainer().pressButton(this.item);
    }

    private void generateTaskToolTip(@NotNull ITaskInstance taskInfo, @NotNull List<Component> toolTips) {
        Task task = taskInfo.getTask();
        toolTips.clear();
        toolTips.add(task.getTranslation().plainCopy().withStyle(style -> style.withColor(this.screen.getTaskContainer().getFactionColor())));
        if (task.useDescription()) {
            toolTips.add(task.getDescription());
            toolTips.add(Component.literal(" "));
        }
        if (this.screen.getTaskContainer().isTaskNotAccepted(taskInfo)) {
            toolTips.add(Component.translatable("gui.vampirism.taskmaster.not_accepted"));
        } else {
            for (List<TaskRequirement.Requirement<?>> requirements : task.getRequirement().requirements().values()) {
                if (requirements == null) continue;
                TaskRequirement.Type type = requirements.get(0).getType();
                boolean completed = this.screen.getTaskContainer().areRequirementsCompleted(taskInfo, type);
                MutableComponent title = Component.translatable(type.getTranslationKey()).append(":");

                if (completed) {
                    title.withStyle(ChatFormatting.STRIKETHROUGH);
                }
                toolTips.add(title);
                for (TaskRequirement.Requirement<?> requirement : requirements) {
                    MutableComponent desc;
                    int completedAmount = this.screen.getTaskContainer().getRequirementStatus(taskInfo, requirement);
                    desc = switch (type) {
                        case ITEMS -> ((Item) requirement.getStat(this.factionPlayer)).getDescription().plainCopy().append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                        case STATS -> Component.translatable("stat." + requirement.getStat(this.factionPlayer).toString().replace(':', '.')).append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                        case ENTITY -> (((EntityType<?>) requirement.getStat(this.factionPlayer)).getDescription().plainCopy().append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer)));
                        case ENTITY_TAG ->
                            //noinspection unchecked
                                Component.translatable("tasks.vampirism." + ((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer)).location()).append(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                        default -> Component.translatable(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
                    };
                    if (completed || this.screen.getTaskContainer().isRequirementCompleted(taskInfo, requirement)) {
                        desc.withStyle(ChatFormatting.STRIKETHROUGH);
                    }
                    toolTips.add(Component.literal("  ").append(desc));
                }
            }
        }
        this.toolTips.put(taskInfo, toolTips);
    }

    private void renderDefaultRequirementToolTip(@NotNull PoseStack mStack, @NotNull ITaskInstance task, TaskRequirement.@NotNull Requirement<?> requirement, int x, int y, boolean strikeThrough) {
        List<Component> tooltips = Lists.newArrayList();
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
        MutableComponent text = Component.translatable(task.getTask().getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
        if (strikeThrough) {
            text.withStyle(ChatFormatting.STRIKETHROUGH);
        }
        tooltips.add(text);
        this.screen.renderComponentTooltip(mStack, tooltips, x, y);
    }

    private void renderGenericRequirementTooltip(@NotNull PoseStack mStack, TaskRequirement.@NotNull Type type, int x, int y, @NotNull MutableComponent text, boolean strikeThrough) {
        List<Component> tooltips = Lists.newArrayList();
        MutableComponent title = Component.translatable(type.getTranslationKey()).append(":");
        if (strikeThrough) {
            text.withStyle(ChatFormatting.STRIKETHROUGH);
            title.withStyle(ChatFormatting.STRIKETHROUGH);
        }
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
        tooltips.add(title.withStyle(ChatFormatting.ITALIC));
        tooltips.add(Component.literal("  ").append(text));
        this.screen.renderComponentTooltip(mStack, tooltips, x, y);
    }

    private void renderItemTooltip(@NotNull PoseStack mStack, @NotNull ItemStack stack, int x, int y, Component text, boolean strikeThrough, @Nullable String bonus) {
        List<Component> tooltips = getTooltipFromItem2(stack, strikeThrough, bonus);
        tooltips.add(0, text);
        this.screen.renderComponentTooltip(mStack, tooltips, x, y);
    }

    private void renderItemTooltip(@NotNull PoseStack mStack, @NotNull Task task, int x, int y) {
        List<Component> tooltips = Lists.newArrayList(REWARD);
        tooltips.add(Component.translatable(task.getTranslationKey() + ".reward"));
        this.screen.renderComponentTooltip(mStack, tooltips, x, y);
    }

    private void renderRequirementTool(@NotNull PoseStack mStack, @NotNull ITaskInstance task, TaskRequirement.@NotNull Requirement<?> requirement, int x, int y) {
        boolean notAccepted = this.screen.getTaskContainer().isTaskNotAccepted(this.item);
        boolean completed = this.screen.getTaskContainer().isRequirementCompleted(this.item, requirement);
        int completedAmount = this.screen.getTaskContainer().getRequirementStatus(this.item, requirement);
        switch (requirement.getType()) {
            case ITEMS -> this.renderItemTooltip(mStack, ((ItemRequirement) requirement).getItemStack(), x, y, (completed ? REQUIREMENT_STRIKE : REQUIREMENT), completed, notAccepted ? null : (completedAmount + "/"));
            case ENTITY ->
                    this.renderGenericRequirementTooltip(mStack, TaskRequirement.Type.ENTITY, x, y, ((EntityType<?>) requirement.getStat(this.factionPlayer)).getDescription().plainCopy().append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(this.factionPlayer)), completed);
            case ENTITY_TAG ->
                //noinspection unchecked
                    this.renderGenericRequirementTooltip(mStack, TaskRequirement.Type.ENTITY_TAG, x, y, Component.translatable("tasks.vampirism." + ((TagKey<EntityType<?>>) requirement.getStat(this.factionPlayer)).location()).append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(this.factionPlayer)), completed);
            case STATS ->
                    this.renderGenericRequirementTooltip(mStack, TaskRequirement.Type.STATS, x, y, Component.translatable("stat." + requirement.getStat(this.factionPlayer).toString().replace(':', '.')).append((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(this.factionPlayer)), completed);
            default -> this.renderDefaultRequirementToolTip(mStack, task, requirement, x, y, completed);
        }
    }

    private class TaskActionButton extends ImageButton {

        public TaskActionButton(int xPos, int yPos) {
            super(xPos, yPos, 14, 13, 0, 0, 0, TASKMASTER_GUI_TEXTURE, 0, 0, TaskItem.this::clickButton, Component.literal(""));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height) {
                super.onClick(mouseX, mouseY);
            }
        }

        @Override
        public void renderButton(@NotNull PoseStack mStack, int mouseX, int mouseY, float p_renderButton_3_) {
            TaskContainer.TaskAction action = TaskItem.this.screen.getTaskContainer().buttonAction(TaskItem.this.item);
            RenderSystem.enableDepthTest();
            RenderSystem.setShaderTexture(0, TASKMASTER_GUI_TEXTURE);
            int j = switch (action) {
                case ACCEPT -> 190;
                case COMPLETE -> 176;
                default -> 204;
            };

            blit(mStack, this.x, this.y, (float) j, (float) (this.isHovered ? 13 : 0), this.width, this.height, 256, 256);
            RenderSystem.disableDepthTest();

        }

        @Override
        public void renderToolTip(@NotNull PoseStack mStack, int mouseX, int mouseY) {
            if (this.isHovered && this.visible) {
                TaskContainer.TaskAction action = TaskItem.this.screen.getTaskContainer().buttonAction(TaskItem.this.item);
                TaskItem.this.screen.renderTooltip(mStack, Component.translatable(action.getTranslationKey()), mouseX, mouseY);
            }
        }
    }

}
