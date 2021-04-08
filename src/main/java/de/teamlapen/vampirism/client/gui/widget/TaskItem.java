package de.teamlapen.vampirism.client.gui.widget;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.client.gui.ExtendedScreen;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Element for {@link ScrollableListWithDummyWidget} that presents a {@link Task}
 *
 * @param <T>
 */
public class TaskItem<T extends Screen & ExtendedScreen> extends ScrollableListWithDummyWidget.ListItem<TaskContainer.TaskInfo> {
    protected static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    protected static final ITextComponent REWARD = new TranslationTextComponent("gui.vampirism.taskmaster.reward").mergeStyle(TextFormatting.UNDERLINE);
    protected static final ITextComponent REQUIREMENT = new TranslationTextComponent("gui.vampirism.taskmaster.requirement").mergeStyle(TextFormatting.UNDERLINE);
    protected static final ITextComponent REQUIREMENT_STRIKE = REQUIREMENT.copyRaw().mergeStyle(TextFormatting.STRIKETHROUGH);
    protected static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);
    protected static final ItemStack PAPER = new ItemStack(Items.PAPER);
    protected static final ITextComponent SUBMIT = new TranslationTextComponent("gui.vampirism.taskmaster.complete_task");
    protected static final ITextComponent ACCEPT = new TranslationTextComponent("gui.vampirism.taskmaster.accept_task");
    protected static final ITextComponent ABORT = new TranslationTextComponent("gui.vampirism.taskmaster.abort_task");

    protected final T screen;
    protected final IFactionPlayer<?> factionPlayer;
    private final TaskActionButton taskButton;

    private final Map<TaskContainer.TaskInfo, List<ITextComponent>> toolTips = Maps.newHashMap();


    public TaskItem(TaskContainer.TaskInfo item, ScrollableListWithDummyWidget<TaskContainer.TaskInfo> list, boolean isDummy, T screen, IFactionPlayer<?> factionPlayer) {
        super(item, list, isDummy);
        this.screen = screen;
        this.factionPlayer = factionPlayer;
        this.taskButton = isDummy ? new TaskActionButton(0, 0) : null;
    }

    @Override
    public void renderItem(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
        //render background
        RenderSystem.enableDepthTest();

        this.colorTask();
        Minecraft.getInstance().textureManager.bindTexture(TASKMASTER_GUI_TEXTURE);
        AbstractGui.blit(matrixStack, x, y, this.screen.getBlitOffset(), 17, 187, 136, 21, 256, 256);
        AbstractGui.blit(matrixStack, x + 132, y, this.screen.getBlitOffset(), 17 + 133, 187, 136 - 133, 21, 256, 256);
        RenderSystem.color4f(1, 1, 1, 1);

        //render name
        Optional<IReorderingProcessor> text = Optional.ofNullable(this.screen.font.trimStringToWidth(this.item.task.getTranslation(), 131).get(0));
        text.ifPresent(t -> this.screen.font.func_238422_b_(matrixStack, t, x + 4, y + 7, 3419941));//(6839882 & 16711422) >> 1 //8453920 //4226832

        RenderSystem.disableDepthTest();

    }

    @Override
    public void renderDummy(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float partialTicks, float zLevel) {
        //render background
        RenderSystem.enableDepthTest();
        GuiUtils.drawContinuousTexturedBox(matrixStack, TASKMASTER_GUI_TEXTURE, x, y, 17, 208, listWidth, itemHeight, 136, 21, 3, 3, 3, 3, zLevel);
        RenderSystem.disableDepthTest();

        //render content
        TaskReward reward = this.item.task.getReward();
        if (reward instanceof ItemReward) {
            ItemStack stack = ((ItemReward) reward).getReward();
            this.screen.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 3 + 113 - 21, y + 2);
            this.screen.getItemRenderer().renderItemOverlayIntoGUI(this.screen.font, stack, x + 3 + 113 - 21, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
        } else {
            this.screen.getItemRenderer().renderItemAndEffectIntoGUI(PAPER, x + 3 + 113 - 21, y + 2);
        }
        List<TaskRequirement.Requirement<?>> requirements = this.item.task.getRequirement().getAll();
        for (int i = 0; i < requirements.size(); i++) {
            TaskRequirement.Requirement<?> requirement = requirements.get(i);
            switch (requirement.getType()) {
                case ITEMS:
                    ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                    this.screen.getItemRenderer().renderItemAndEffectIntoGUI(stack, x + 3 + 3 + i * 20, y + 2);
                    this.screen.getItemRenderer().renderItemOverlayIntoGUI(this.screen.font, stack, x + 3 + 3 + i * 20, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
                    break;
                case ENTITY:
                case ENTITY_TAG:
                    this.screen.getItemRenderer().renderItemAndEffectIntoGUI(SKULL_ITEM, x + 3 + 3 + i * 20, y + 2);
                    this.screen.getItemRenderer().renderItemOverlayIntoGUI(this.screen.font, SKULL_ITEM, x + 3 + 3 + i * 20, y + 2, "" + requirement.getAmount(factionPlayer));
                    break;
                default:
                    this.screen.getItemRenderer().renderItemAndEffectIntoGUI(PAPER, x + 3 + 3 + i * 20, y + 2);
                    break;
            }
        }
        //render task button
        this.taskButton.x = x + listWidth - 17;
        this.taskButton.y = y + 4;
        this.taskButton.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    /**
     * sets OpenGL colors based on task and completion
     */
    private void colorTask() {
        TaskContainer container = TaskItem.this.screen.getTaskContainer();
        if (container.isCompleted(this.item)) {
            RenderSystem.color4f(0.4f, 0.4f, 0.4f, 1);
        } else {
            boolean isUnique = this.item.task.isUnique();
            if (container.canCompleteTask(this.item)) {
                if (isUnique) {
                    RenderSystem.color4f(1f, 0.855859375f, 0, 1);
                } else {
                    RenderSystem.color4f(0, 0.9f, 0, 1);
                }
            } else if (container.isTaskNotAccepted(this.item)) {
                if (isUnique) {
                    RenderSystem.color4f(0.64f, 0.57f, 0.5f, 1);
                } else {
                    RenderSystem.color4f(0.55f, 0.55f, 0.55f, 1);
                }
            } else {
                if (isUnique) {
                    RenderSystem.color4f(1f, 0.9f, 0.6f, 1f);
                } else {
                    RenderSystem.color4f(0.85f, 1f, 0.85f, 1f);
                }
            }
        }
    }

    @Override
    public boolean onDummyClick(double mouseX, double mouseY) {
        this.taskButton.onClick(mouseX, mouseY);
        return true;
    }

    private void clickButton(Button id) {
        this.screen.getTaskContainer().pressButton(this.item);
    }

    @Override
    public void renderItemToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
        //default task tooltips
        List<ITextComponent> toolTips = this.toolTips.getOrDefault(this.item, Lists.newArrayList());
        if (toolTips.isEmpty()) {
            generateTaskToolTip(this.item, toolTips);
        }
        this.screen.func_243308_b(matrixStack, toolTips, mouseX, mouseY);
    }

    @Override
    public void renderDummyToolTip(MatrixStack matrixStack, int x, int y, int listWidth, int listHeight, int itemHeight, int mouseX, int mouseY, float zLevel) {
        if (mouseX >= x + 3 + 113 - 21 + 1 && mouseX < x + 3 + 113 - 21 + 16 + 1 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
            TaskReward reward = this.item.task.getReward();
            if (reward instanceof ItemReward) {
                this.renderItemTooltip(matrixStack, ((ItemReward) reward).getReward(), mouseX, mouseY, REWARD, false, null);
            } else {
                this.renderItemTooltip(matrixStack, this.item.task, mouseX, mouseY);
            }
        }
        List<TaskRequirement.Requirement<?>> requirements = this.item.task.getRequirement().getAll();
        for (int i = 0; i < requirements.size(); i++) {
            if (mouseX >= x + 3 + 3 + i * 20 && mouseX < x + 3 + 16 + 3 + i * 20 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                this.renderRequirementTool(matrixStack, this.item.task, requirements.get(i), mouseX, mouseY);
            }
        }
        this.taskButton.renderToolTip(matrixStack, mouseX, mouseY);
    }

    private void generateTaskToolTip(TaskContainer.TaskInfo taskInfo, List<ITextComponent> toolTips) {
        Task task = taskInfo.task;
        toolTips.clear();
        toolTips.add(task.getTranslation().copyRaw().mergeStyle(this.screen.getTaskContainer().getFactionColor()));
        if (task.useDescription()) {
            toolTips.add(task.getDescription());
            toolTips.add(new StringTextComponent(" "));
        }
        if (this.screen.getTaskContainer().isTaskNotAccepted(taskInfo)) {
            toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.not_accepted"));
        } else {
            for (List<TaskRequirement.Requirement<?>> requirements : task.getRequirement().requirements().values()) {
                if (requirements == null) continue;
                TaskRequirement.Type type = requirements.get(0).getType();
                boolean completed = this.screen.getTaskContainer().areRequirementsCompleted(taskInfo, type);
                IFormattableTextComponent title = new TranslationTextComponent(type.getTranslationKey()).appendString(":");

                if (completed) {
                    title.mergeStyle(TextFormatting.STRIKETHROUGH);
                }
                toolTips.add(title);
                for (TaskRequirement.Requirement<?> requirement : requirements) {
                    IFormattableTextComponent desc;
                    int completedAmount = this.screen.getTaskContainer().getRequirementStatus(taskInfo, requirement);
                    switch (type) {
                        case ITEMS:
                            desc = ((Item) requirement.getStat(this.factionPlayer)).getName().copyRaw().appendString(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                            break;
                        case STATS:
                            desc = new TranslationTextComponent("stat." + requirement.getStat(this.factionPlayer).toString().replace(':', '.')).appendString(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                            break;
                        case ENTITY:
                            desc = (((EntityType<?>) requirement.getStat(this.factionPlayer)).getName().copyRaw().appendString(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer)));
                            break;
                        case ENTITY_TAG:
                            //noinspection unchecked
                            desc = new TranslationTextComponent("tasks.vampirism." + ((ITag.INamedTag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getName().toString()).appendString(" " + completedAmount + "/" + requirement.getAmount(this.factionPlayer));
                            break;
                        default:
                            desc = new TranslationTextComponent(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
                            break;
                    }
                    if (completed || this.screen.getTaskContainer().isRequirementCompleted(taskInfo, requirement)) {
                        desc.mergeStyle(TextFormatting.STRIKETHROUGH);
                    }
                    toolTips.add(new StringTextComponent("  ").append(desc));
                }
            }
        }
        this.toolTips.put(taskInfo, toolTips);
    }

    private void renderItemTooltip(MatrixStack mStack, ItemStack stack, int x, int y, ITextComponent text, boolean strikeThrough, @Nullable String bonus) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        GuiUtils.preItemToolTip(stack);
        List<ITextComponent> tooltips = getTooltipFromItem2(stack, strikeThrough, bonus);
        tooltips.add(0, text);
        this.screen.renderWrappedToolTip(mStack, tooltips, x, y, (font == null ? this.screen.font : font));
        GuiUtils.postItemToolTip();
    }

    private void renderItemTooltip(MatrixStack mStack, Task task, int x, int y) {
        List<ITextComponent> tooltips = Lists.newArrayList(REWARD);
        tooltips.add(new TranslationTextComponent(task.getTranslationKey() + ".reward"));
        this.screen.func_243308_b(mStack, tooltips, x, y);
    }

    public List<ITextComponent> getTooltipFromItem2(ItemStack itemStack, boolean strikeThough, @Nullable String bonus) {
        List<ITextComponent> list = itemStack.getTooltip(Minecraft.getInstance().player, Minecraft.getInstance().gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        List<ITextComponent> list1 = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                IFormattableTextComponent t = list.get(0).deepCopy().appendString(" " + (bonus != null ? bonus : "") + itemStack.getCount());
                if (strikeThough) t.mergeStyle(TextFormatting.STRIKETHROUGH);
                list1.add(t);
            } else {
                list1.add(strikeThough ? list.get(i).deepCopy().mergeStyle(TextFormatting.STRIKETHROUGH) : list.get(i));
            }
        }
        return list1;
    }

    private void renderRequirementTool(MatrixStack mStack, Task task, TaskRequirement.Requirement<?> requirement, int x, int y) {
        boolean notAccepted = this.screen.getTaskContainer().isTaskNotAccepted(this.item);
        boolean completed = this.screen.getTaskContainer().isRequirementCompleted(this.item, requirement);
        int completedAmount = this.screen.getTaskContainer().getRequirementStatus(this.item, requirement);
        switch (requirement.getType()) {
            case ITEMS:
                this.renderItemTooltip(mStack, ((ItemRequirement) requirement).getItemStack(), x, y, (completed ? REQUIREMENT_STRIKE : REQUIREMENT), completed, notAccepted ? null : (completedAmount + "/"));
                break;
            case ENTITY:
                this.renderGenericRequirementTooltip(mStack, TaskRequirement.Type.ENTITY, x, y, ((EntityType<?>) requirement.getStat(this.factionPlayer)).getName().copyRaw().appendString((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(this.factionPlayer)), completed);
                break;
            case ENTITY_TAG:
                //noinspection unchecked
                this.renderGenericRequirementTooltip(mStack, TaskRequirement.Type.ENTITY_TAG, x, y, new TranslationTextComponent("tasks.vampirism." + ((ITag.INamedTag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getName().toString()).appendString((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(this.factionPlayer)), completed);
                break;
            case STATS:
                this.renderGenericRequirementTooltip(mStack, TaskRequirement.Type.STATS, x, y, new TranslationTextComponent("stat." + requirement.getStat(this.factionPlayer).toString().replace(':', '.')).appendString((notAccepted ? " " : (" " + (completedAmount + "/"))) + requirement.getAmount(this.factionPlayer)), completed);
                break;
            default:
                this.renderDefaultRequirementToolTip(mStack, task, requirement, x, y, completed);
        }
    }

    private void renderGenericRequirementTooltip(MatrixStack mStack, TaskRequirement.Type type, int x, int y, IFormattableTextComponent text, boolean strikeThrough) {
        List<ITextComponent> tooltips = Lists.newArrayList();
        IFormattableTextComponent title = new TranslationTextComponent(type.getTranslationKey()).appendString(":");
        if (strikeThrough) {
            text.mergeStyle(TextFormatting.STRIKETHROUGH);
            title.mergeStyle(TextFormatting.STRIKETHROUGH);
        }
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
        tooltips.add(title.mergeStyle(TextFormatting.ITALIC));
        tooltips.add(new StringTextComponent("  ").append(text));
        this.screen.renderWrappedToolTip(mStack, tooltips, x, y, this.screen.font);
    }

    private void renderDefaultRequirementToolTip(MatrixStack mStack, Task task, TaskRequirement.Requirement<?> requirement, int x, int y, boolean strikeThrough) {
        List<ITextComponent> tooltips = Lists.newArrayList();
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
        IFormattableTextComponent text = new TranslationTextComponent(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
        if (strikeThrough) {
            text.mergeStyle(TextFormatting.STRIKETHROUGH);
        }
        tooltips.add(text);
        this.screen.func_243308_b(mStack, tooltips, x, y);
    }

    private class TaskActionButton extends ImageButton {

        public TaskActionButton(int xPos, int yPos) {
            super(xPos, yPos, 14, 13, 0, 0, 0, TASKMASTER_GUI_TEXTURE, 0, 0, TaskItem.this::clickButton, new StringTextComponent(""));
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            if (mouseX > this.x && mouseX < this.x + this.width && mouseY > this.y && mouseY < this.y + this.height) {
                super.onClick(mouseX, mouseY);
            }
        }

        @Override
        public void renderToolTip(@Nonnull MatrixStack mStack, int mouseX, int mouseY) {
            if (this.isHovered && this.visible) {
                TaskContainer.TaskAction action = TaskItem.this.screen.getTaskContainer().buttonAction(TaskItem.this.item);
                TaskItem.this.screen.renderTooltip(mStack, (action == TaskContainer.TaskAction.ACCEPT ? ACCEPT : action == TaskContainer.TaskAction.ABORT ? ABORT : SUBMIT), mouseX, mouseY);
            }
        }

        @Override
        public void renderButton(@Nonnull MatrixStack mStack, int mouseX, int mouseY, float p_renderButton_3_) {
            TaskContainer.TaskAction action = TaskItem.this.screen.getTaskContainer().buttonAction(TaskItem.this.item);
            RenderSystem.enableDepthTest();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
            int j;
            switch (action) {
                case ACCEPT:
                    j = 190;
                    break;
                case COMPLETE:
                    j = 176;
                    break;
                default:
                    j = 204;

            }

            blit(mStack, this.x, this.y, (float) j, (float) (this.isHovered ? 13 : 0), this.width, this.height, 256, 256);
            RenderSystem.disableDepthTest();

        }
    }

}
