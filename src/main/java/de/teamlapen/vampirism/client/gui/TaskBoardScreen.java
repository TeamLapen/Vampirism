package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.GuiUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TaskBoardScreen extends ContainerScreen<TaskBoardContainer> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private static final ITextComponent SUBMIT = new TranslationTextComponent("gui.vampirism.taskmaster.complete_task");
    private static final ITextComponent ACCEPT = new TranslationTextComponent("gui.vampirism.taskmaster.accept_task");
    private static final ITextComponent ABORT = new TranslationTextComponent("gui.vampirism.taskmaster.abort_task");
    private static final ITextComponent REQUIREMENT = new TranslationTextComponent("gui.vampirism.taskmaster.requirement").mergeStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent REQUIREMENT_STRIKE = REQUIREMENT.copyRaw().mergeStyle(TextFormatting.STRIKETHROUGH);
    private static final ITextComponent REWARD = new TranslationTextComponent("gui.vampirism.taskmaster.reward").mergeStyle(TextFormatting.UNDERLINE);
    private static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);
    private static final ItemStack PAPER = new ItemStack(Items.PAPER);
    private final TaskActionButton[] buttons = new TaskActionButton[7];
    private final Map<Task, List<ITextComponent>> toolTips = Maps.newHashMap();
    private final String messageKey;
    private final IFactionPlayer<?> factionPlayer;
    private int scrolledTask;
    private int openedTask;
    @SuppressWarnings({"ConstantConditions", "RedundantCast"})
    private final Task dummy = new Task(Task.Variant.UNIQUE, null, (TaskRequirement) null, (TaskReward) null, (TaskUnlocker[]) null, false) {
        @Nonnull
        @Override
        public TaskReward getReward() {
            return TaskBoardScreen.this.container.getTask(TaskBoardScreen.this.openedTask).getReward();
        }

        @Nonnull
        @Override
        public TaskRequirement getRequirement() {
            return TaskBoardScreen.this.container.getTask(TaskBoardScreen.this.openedTask).getRequirement();
        }

        @Nullable
        @Override
        public IPlayableFaction<?> getFaction() {
            return TaskBoardScreen.this.container.getTask(TaskBoardScreen.this.openedTask).getFaction();
        }

        @Nonnull
        @Override
        public String getTranslationKey() {
            return TaskBoardScreen.this.container.getTask(TaskBoardScreen.this.openedTask).getTranslationKey();
        }
    };
    private boolean isOpen;
    private boolean mouseOnScroller;

    public TaskBoardScreen(TaskBoardContainer container, PlayerInventory playerInventory, ITextComponent containerName) {
        super(container, playerInventory, containerName);
        this.xSize = 176;
        this.ySize = 181;
        //noinspection OptionalGetWithoutIsPresent
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
        this.messageKey = "gui.vampirism.taskmaster.text." + container.getFaction().getID().toString().replace(':', '_');
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; ++l) {
            this.buttons[l] = this.addButton(new TaskActionButton(i + 5 + 132 - 2, k + 3, l, (button) -> {
                if (button instanceof TaskActionButton) {
                    Task task = this.container.getTask(((TaskActionButton) button).getChosenItem() + this.scrolledTask - 1);
                    if (this.container.canCompleteTask(task)) {
                        this.container.completeTask(task);
                    } else if (this.container.isTaskNotAccepted(task)) {
                        this.container.acceptTask(task);
                    } else {
                        this.container.abortTask(task);
                    }
                    this.generateTaskToolTip(task, this.toolTips.get(task));
                }
            }));
            k += 21;
        }
    }


    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.func_243248_b(mStack, this.title, (float) (this.xSize / 2 - this.font.getStringPropertyWidth(this.title) / 2), 5.0F, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(mStack, i, j, this.getBlitOffset(), 0, 0, this.xSize, this.ySize, 256, 256);
    }

    private static IFormattableTextComponent getRequirementTypeTitle(TaskRequirement.Type type) {
        IFormattableTextComponent title;

        switch (type) {
            case ITEMS:
                title = new TranslationTextComponent("gui.vampirism.taskmaster.item_req").appendString(":");
                break;
            case STATS:
                title = new TranslationTextComponent("gui.vampirism.taskmaster.stat_req").appendString(":");
                break;
            case ENTITY:
                title = new TranslationTextComponent("gui.vampirism.taskmaster.entity_req").appendString(":");
                break;
            case ENTITY_TAG:
                title = new TranslationTextComponent("gui.vampirism.taskmaster.entity_tag_req").appendString(":");
                break;
            default:
                title = new TranslationTextComponent("gui.vampirism.taskmaster.bool_req").appendString(":");
                break;
        }
        return title;
    }

    private void renderTaskToolTip(MatrixStack mStack, Task task, int x, int y, int mouseX, int mouseY) {
        //tooltips for the dummy task
        if (task == dummy) {
            if (mouseX >= x + 3 + 113 - 21 && mouseX < x + 3 + 113 - 21 + 16 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                TaskReward reward = task.getReward();
                if (reward instanceof ItemReward) {
                    this.renderItemTooltip(mStack, ((ItemReward) reward).getReward(), mouseX, mouseY, REWARD, false, null);
                } else {
                    this.renderItemTooltip(mStack, task, mouseX, mouseY);
                }
            }
            List<TaskRequirement.Requirement<?>> requirements = task.getRequirement().getAll();
            for (int i = 0; i < requirements.size(); i++) {
                if (mouseX >= x + 3 + 3 + i * 20 && mouseX < x + 3 + 16 + 3 + i * 20 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                    this.renderRequirementTool(mStack, this.container.getTask(this.openedTask), requirements.get(i), mouseX, mouseY);
                }
            }
        } else {
            //default task tooltips
            List<ITextComponent> toolTips = this.toolTips.getOrDefault(task, Lists.newArrayList());
            if (toolTips.isEmpty()) {
                generateTaskToolTip(task, toolTips);
            }
            this.func_243308_b(mStack,toolTips, mouseX, mouseY);
        }
    }



    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        renderMessage(mStack);
        Collection<Task> tasks = this.container.getVisibleTasks();
        if (!tasks.isEmpty()) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = j + 16 + 3 + 2;
            mStack.push();
            RenderSystem.enableRescaleNormal();
            RenderSystem.enableColorMaterial();
            RenderSystem.enableLighting();
            this.renderScroller(mStack,i + 60, j - 1, tasks);
            int i1 = 0;

            for (Task task : tasks) {
                if (tasks.size() <= 7 || (i1 >= this.scrolledTask && i1 < 7 + this.scrolledTask)) {
                    this.renderTask(mStack, task, i - 1 + 17, k - 4);
                    k += 21;
                }
                ++i1;
            }
            for (TaskActionButton button : this.buttons) {
                button.visible = button.getChosenItem() < this.container.size() && this.container.getTask(button.getChosenItem() + this.scrolledTask) == dummy && !this.container.isCompleted(this.container.getTask(button.getChosenItem() + this.scrolledTask - 1));
            }
            for (TaskActionButton button : this.buttons) {
                button.render(mStack,mouseX, mouseY, partialTicks);
            }
            for (TaskActionButton button : this.buttons) {
                button.renderToolTip(mStack, mouseX, mouseY);
            }
            i1 = 0;
            k = j + 16 + 3 + 2;
            for (Task task : tasks) {
                if (tasks.size() <= 7 || (i1 >= this.scrolledTask && i1 < 7 + this.scrolledTask)) {
                    if (mouseX > i - 1 + 17 && mouseX < i - 1 + 139 + 17 && mouseY > k - 4 && mouseY < k + 21 - 4) {
                        this.renderTaskToolTip(mStack, task, i - 1 + 17, k - 4, mouseX, mouseY);
                    }
                    k += 21;
                }
                ++i1;
            }
            mStack.pop();
        }
    }

    private void generateTaskToolTip(Task task, List<ITextComponent> toolTips) {
        toolTips.clear();
        toolTips.add(task.getTranslation().copyRaw().mergeStyle(this.container.getFactionColor()));
        if (task.useDescription()) {
            toolTips.add(task.getDescription());
            toolTips.add(new StringTextComponent(" "));
        }
        if (this.container.isTaskNotAccepted(task)) {
            toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.not_accepted"));
        } else {
            for (List<TaskRequirement.Requirement<?>> requirements : task.getRequirement().requirements().values()) {
                if (requirements == null) continue;
                TaskRequirement.Type type = requirements.get(0).getType();
                boolean completed = this.container.areRequirementsCompleted(task, type);
                IFormattableTextComponent title = getRequirementTypeTitle(type);

                if (completed) {
                    title.mergeStyle(TextFormatting.STRIKETHROUGH);
                }
                toolTips.add(title);
                for (TaskRequirement.Requirement<?> requirement : requirements) {
                    IFormattableTextComponent desc;
                    int completedAmount = this.container.getRequirementStatus(task, requirement);
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
                    if (completed || this.container.isRequirementCompleted(task, requirement)) {
                        desc.mergeStyle(TextFormatting.STRIKETHROUGH);
                    }
                    toolTips.add(new StringTextComponent("  ").append(desc));
                }
            }
        }
        this.toolTips.put(task, toolTips);
    }

    private void renderTask(MatrixStack mStack, Task task, int x, int y) {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        boolean dummy = task == this.dummy;
        int offset = dummy ? 21 : 0;
        if (dummy && this.isOpen) {
            task = this.container.getTask(this.openedTask);
        }
        if (this.container.isCompleted(task)) {
            RenderSystem.color4f(0.4f, 0.4f, 0.4f, 1);
        } else if (!dummy) {
            if (this.container.canCompleteTask(task)) {
                if (task.isUnique()) {
                    RenderSystem.color4f(1f, 0.855859375f, 0, 1);
                } else {
                    RenderSystem.color4f(0, 0.9f, 0, 1);
                }
            } else if (this.container.isTaskNotAccepted(task)) {
                if (task.isUnique()) {
                    RenderSystem.color4f(0.64f, 0.57f, 0.5f, 1);
                } else {
                    RenderSystem.color4f(0.55f, 0.55f, 0.55f, 1);
                }
            } else {
                if (task.isUnique()) {
                    RenderSystem.color4f(1f, 0.9f, 0.6f, 1f);
                } else {
                    RenderSystem.color4f(0.85f, 1f, 0.85f, 1f);
                }
            }
        }
        blit(mStack, x, y, this.getBlitOffset(), 16, 187 + offset, 137, 21, 256, 256);
        RenderSystem.color4f(1, 1, 1, 1);

        if (!dummy) {
            Optional<IReorderingProcessor> text = Optional.ofNullable(this.font.trimStringToWidth(task.getTranslation(), 131).get(0));
            text.ifPresent(t->this.font.func_238422_b_(mStack,t , x + 4, y + 7, 3419941));//(6839882 & 16711422) >> 1 //8453920 //4226832
        } else {
            TaskReward reward = task.getReward();
            if (reward instanceof ItemReward) {
                ItemStack stack = ((ItemReward) reward).getReward();
                this.itemRenderer.renderItemAndEffectIntoGUI(stack, x + 3 + 113 - 21, y + 2);
                this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x + 3 + 113 - 21, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
            } else {
                this.itemRenderer.renderItemAndEffectIntoGUI(PAPER, x + 3 + 113 - 21, y + 2);
            }
            List<TaskRequirement.Requirement<?>> requirements = task.getRequirement().getAll();
            for (int i = 0; i < requirements.size(); i++) {
                TaskRequirement.Requirement<?> requirement = requirements.get(i);
                switch (requirement.getType()) {
                    case ITEMS:
                        ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x + 3 + 3 + i * 20, y + 2);
                        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x + 3 + 3 + i * 20, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
                        break;
                    case ENTITY:
                    case ENTITY_TAG:
                        this.itemRenderer.renderItemAndEffectIntoGUI(SKULL_ITEM, x + 3 + 3 + i * 20, y + 2);
                        this.itemRenderer.renderItemOverlayIntoGUI(this.font, SKULL_ITEM, x + 3 + 3 + i * 20, y + 2, "" + requirement.getAmount(factionPlayer));
                        break;
                    default:
                        this.itemRenderer.renderItemAndEffectIntoGUI(PAPER, x + 3 + 3 + i * 20, y + 2);
                        break;
                }
            }
        }
    }

    private void renderGenericRequirementTooltip(MatrixStack mStack, TaskRequirement.Type type, int x, int y, IFormattableTextComponent text, boolean strikeThrough) {
        List<ITextComponent> tooltips = Lists.newArrayList();
        IFormattableTextComponent title = getRequirementTypeTitle(type);
        if (strikeThrough) {
            text.mergeStyle(TextFormatting.STRIKETHROUGH);
            title.mergeStyle(TextFormatting.STRIKETHROUGH);
        }
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
        tooltips.add(title.mergeStyle(TextFormatting.ITALIC));
        tooltips.add(new StringTextComponent("  ").append(text));
        this.renderWrappedToolTip(mStack, tooltips, x, y, this.font);
    }


    private void renderDefaultRequirementToolTip(MatrixStack mStack, Task task, TaskRequirement.Requirement<?> requirement, int x, int y, boolean strikeThrough) {
        List<ITextComponent> tooltips = Lists.newArrayList();
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE : REQUIREMENT));
        IFormattableTextComponent text = new TranslationTextComponent(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':', '.'));
        if (strikeThrough) {
            text.mergeStyle(TextFormatting.STRIKETHROUGH);
        }
        tooltips.add(text);
        this.func_243308_b(mStack, tooltips, x, y);
    }

    private void renderRequirementTool(MatrixStack mStack, Task task, TaskRequirement.Requirement<?> requirement, int x, int y) {
        boolean notAccepted = this.container.isTaskNotAccepted(task);
        boolean completed = this.container.isRequirementCompleted(task, requirement);
        int completedAmount = this.container.getRequirementStatus(task, requirement);
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


    private void renderItemTooltip(MatrixStack mStack, Task task, int x, int y) {
        List<ITextComponent> tooltips = Lists.newArrayList(REWARD);
        tooltips.add(new TranslationTextComponent(task.getTranslationKey() + ".reward"));
        this.func_243308_b(mStack, tooltips, x, y);
    }

    private void renderItemTooltip(MatrixStack mStack, ItemStack stack, int x, int y, ITextComponent text, boolean strikeThrough, @Nullable String bonus) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        GuiUtils.preItemToolTip(stack);
        List<ITextComponent> tooltips = getTooltipFromItem2(stack, strikeThrough, bonus);
        tooltips.add(0, text);
        this.renderWrappedToolTip(mStack, tooltips, x, y, (font == null ? this.font : font));
        GuiUtils.postItemToolTip();
    }

    public List<ITextComponent> getTooltipFromItem2(ItemStack itemStack, boolean strikeThough, @Nullable String bonus) {
        assert this.minecraft != null;
        List<ITextComponent> list = itemStack.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        List<ITextComponent> list1 = Lists.newArrayList();
        for (int i = 0; i < list.size(); i++) {
            if(i==0){
                IFormattableTextComponent t=list.get(0).deepCopy().appendString(" " + (bonus != null ? bonus : "") + itemStack.getCount());
                if(strikeThough)t.mergeStyle(TextFormatting.STRIKETHROUGH);
                list1.add(t);
            }
            else{
                list1.add(strikeThough?list.get(i).deepCopy().mergeStyle(TextFormatting.STRIKETHROUGH):list.get(i));
            }
        }
        return list1;
    }

    private void renderMessage(MatrixStack mStack) {
        RenderSystem.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableLighting();
        RenderSystem.disableDepthTest();

        int width = (this.width - this.xSize) / 2 - 20;

        List<IReorderingProcessor> lines = this.font.trimStringToWidth(new TranslationTextComponent(messageKey), width);
        int x = 10;
        int y = (this.height - this.ySize) / 2 + 5;
        int height = 8;
        if (lines.size() > 1) {
            height += 2 + (lines.size() - 1) * 10;
        }

        this.setBlitOffset(300);
        this.itemRenderer.zLevel = 300.0F;
        int colorCenter = -267386864;
        this.fillGradient(mStack, x - 3, y - 4, x + width + 3, y - 3, colorCenter, colorCenter);
        this.fillGradient(mStack, x - 3, y + height + 3, x + width + 3, y + height + 4, colorCenter, colorCenter);
        this.fillGradient(mStack, x - 3, y - 3, x + width + 3, y + height + 3, colorCenter, colorCenter);
        this.fillGradient(mStack, x - 4, y - 3, x - 3, y + height + 3, colorCenter, colorCenter);
        this.fillGradient(mStack, x + width + 3, y - 3, x + width + 4, y + height + 3, colorCenter, colorCenter);
        int colorOuter1 = 1347420415;
        int colorOuter2 = 1344798847;
        this.fillGradient(mStack, x - 3, y - 3 + 1, x - 3 + 1, y + height + 3 - 1, colorOuter1, colorOuter2);
        this.fillGradient(mStack, x + width + 2, y - 3 + 1, x + width + 3, y + height + 3 - 1, colorOuter1, colorOuter2);
        this.fillGradient(mStack, x - 3, y - 3, x + width + 3, y - 3 + 1, colorOuter1, colorOuter1);
        this.fillGradient(mStack, x - 3, y + height + 2, x + width + 3, y + height + 3, colorOuter2, colorOuter2);

        for (IReorderingProcessor line : lines) {
            this.font.func_238407_a_(mStack, line, (float) x, (float) y, -1);
            y += 10;
        }

        this.setBlitOffset(0);;
        this.itemRenderer.zLevel = 0.0F;
        RenderSystem.enableLighting();
        RenderSystem.enableDepthTest();
        RenderHelper.enableStandardItemLighting();
        RenderSystem.enableRescaleNormal();
    }

    private void renderScroller(MatrixStack mStack, int x, int y, Collection<Task> tasks) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        int i = tasks.size() - 7;
        if (i >= 1) {
            int k = 144 - (2 + (i - 1) * 144 / i);
            int i1 = Math.min(120, this.scrolledTask * k);
            if (this.scrolledTask >= i + 4) {
                i1 = 120;
            }
            blit(mStack, x + 94, y + 18 + i1, this.getBlitOffset(), 0F, 181.0F, 6, 27, 256, 256);
        } else {
            blit(mStack,x + 94, y + 18, this.getBlitOffset(), 6F, 181.0F, 6, 27, 256, 256);
        }

    }

    private boolean isTaskListTooLong(int size) {
        return size > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int i = this.container.size();
        if (this.isTaskListTooLong(i)) {
            int j = i - 7;
            this.scrolledTask = (int) ((double) this.scrolledTask - scrollAmount);
            this.scrolledTask = MathHelper.clamp(this.scrolledTask, 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int buttonId, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        int i = this.container.size();
        if (this.mouseOnScroller) {
            int j = this.guiTop + 18;
            int k = j + 144;
            int l = i - 7;
            float f = ((float) p_mouseDragged_3_ - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.scrolledTask = MathHelper.clamp((int) f, 0, l);
            return true;
        } else {
            return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, buttonId, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        this.mouseOnScroller = false;
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (this.isTaskListTooLong(this.container.size()) && mouseX > (double) (i + 154) && mouseX < (double) (i + 160) && mouseY > (double) (j + 17) && mouseY <= (double) (j + 166)) {
            this.mouseOnScroller = true;
        }
        if (mouseX > i + 17 && mouseX < i + 136 + 17 && mouseY > j - 4 + 21 && mouseY < j + 168 - 4) {
            int num = ((int) (mouseY - (j - 4 + 21)) / 21) + this.scrolledTask;
            if (this.isOpen) {
                if (this.openedTask + 1 != num) {
                    this.container.getVisibleTasks().remove(this.openedTask + 1);
                    if (num > this.openedTask) {
                        num--;
                    }
                    if (num < this.container.size() && num != this.openedTask) {
                        this.openedTask = num;
                        this.container.getVisibleTasks().add(this.openedTask + 1, dummy);
                    } else {
                        this.isOpen = false;
                        if (this.container.size() < this.scrolledTask + 7 && this.scrolledTask != 0) {
                            this.scrolledTask--;
                        }
                    }
                }
            } else {
                if (num < this.container.size()) {
                    this.isOpen = true;
                    this.openedTask = num;
                    this.container.getVisibleTasks().add(this.openedTask + 1, dummy);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, buttonId);
    }

    private class TaskActionButton extends ImageButton {
        final int chosenItem;

        public TaskActionButton(int xPos, int yPos, int chosenItem, IPressable onPress) {
            super(xPos, yPos, 14, 13, 0, 0, 0, TASKMASTER_GUI_TEXTURE, 0, 0, onPress, new StringTextComponent(""));
            this.chosenItem = chosenItem;
            this.visible = false;
        }

        public int getChosenItem() {
            return chosenItem;
        }

        @Override
        public void renderToolTip(MatrixStack mStack, int mouseX, int mouseY) {
            if (this.isHovered && this.visible && TaskBoardScreen.this.container.size() > this.chosenItem + TaskBoardScreen.this.scrolledTask) {
                TaskBoardContainer.TaskAction action = getAction();
                TaskBoardScreen.this.renderTooltip(mStack, (action == TaskBoardContainer.TaskAction.ACCEPT ? ACCEPT : action == TaskBoardContainer.TaskAction.ABORT ? ABORT : SUBMIT), mouseX, mouseY);
            }
        }

        @Override
        public void renderButton(MatrixStack mStack, int mouseX, int mouseY, float p_renderButton_3_) {
            TaskBoardContainer.TaskAction action = getAction();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
            GlStateManager.disableDepthTest();
            int i = 0;
            if (this.isHovered()) {
                i += 13;
            }
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

            blit(mStack, this.x, this.y, (float) j, (float) i, this.width, this.height, 256, 256);
            GlStateManager.enableDepthTest();
        }

        private TaskBoardContainer.TaskAction getAction() {
            Task task = TaskBoardScreen.this.container.getTask(this.chosenItem + TaskBoardScreen.this.scrolledTask - 1);
            if (TaskBoardScreen.this.container.canCompleteTask(task)) {
                return TaskBoardContainer.TaskAction.COMPLETE;
            } else if (TaskBoardScreen.this.container.isTaskNotAccepted(task)) {
                return TaskBoardContainer.TaskAction.ACCEPT;
            } else {
                return TaskBoardContainer.TaskAction.ABORT;
            }
        }
    }
}
