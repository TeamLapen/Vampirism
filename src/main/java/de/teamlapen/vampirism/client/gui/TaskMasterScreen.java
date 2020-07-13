package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.api.entity.player.task.TaskUnlocker;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
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
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TaskMasterScreen extends ContainerScreen<TaskMasterContainer> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private static final ITextComponent SUBMIT = new TranslationTextComponent("gui.vampirism.taskmaster.complete_task");
    private static final ITextComponent ACCEPT = new TranslationTextComponent("gui.vampirism.taskmaster.accept_task");
    private static final ITextComponent ABORT = new TranslationTextComponent("gui.vampirism.taskmaster.abort_task");
    private static final ITextComponent REQUIREMENT = new TranslationTextComponent("gui.vampirism.taskmaster.requirement").applyTextStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent REQUIREMENT_STRIKE = REQUIREMENT.shallowCopy().applyTextStyle(TextFormatting.STRIKETHROUGH);
    private static final ITextComponent REWARD = new TranslationTextComponent("gui.vampirism.taskmaster.reward").applyTextStyle(TextFormatting.UNDERLINE);
    private static final ItemStack SKULL_ITEM = new ItemStack(Blocks.SKELETON_SKULL);
    private static final ItemStack PAPER = new ItemStack(Items.PAPER);
    @SuppressWarnings({"ConstantConditions", "RedundantCast"})
    private final Task dummy = new Task(Task.Variant.UNIQUE, null, (TaskRequirement)null, (TaskReward)null, (TaskUnlocker[])null, false) {
        @Nonnull
        @Override
        public TaskReward getReward() {
            return TaskMasterScreen.this.container.getTask(TaskMasterScreen.this.openedTask).getReward();
        }

        @Nonnull
        @Override
        public TaskRequirement getRequirement() {
            return TaskMasterScreen.this.container.getTask(TaskMasterScreen.this.openedTask).getRequirement();
        }

        @Nullable
        @Override
        public IPlayableFaction<?> getFaction() {
            return TaskMasterScreen.this.container.getTask(TaskMasterScreen.this.openedTask).getFaction();
        }

        @Nonnull
        @Override
        public String getTranslationKey() {
            return TaskMasterScreen.this.container.getTask(TaskMasterScreen.this.openedTask).getTranslationKey();
        }
    };

    private final TaskActionButton[] buttons = new TaskActionButton[7];
    private final Map<Task, List<String>> toolTips = Maps.newHashMap();
    private int scrolledTask;
    private int openedTask;
    private boolean isOpen;
    private boolean mouseOnScroller;
    private final IFactionPlayer<?> factionPlayer;

    public TaskMasterScreen(TaskMasterContainer container, PlayerInventory playerInventory, ITextComponent containerName) {
        super(container, playerInventory, containerName);
        this.xSize = 176;
        this.ySize = 181;
        //noinspection OptionalGetWithoutIsPresent
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
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
                    }else if(!this.container.isTaskAccepted(task)) {
                        this.container.acceptTask(task);
                    }else {
                        this.container.abortTask(task);
                    }
                }
            }));
            k += 21;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        String s = this.title.getFormattedText();
        this.font.drawString(s, (float) (this.xSize / 2 - this.font.getStringWidth(s) / 2), 5.0F, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(i, j, this.blitOffset, 0, 0, this.xSize, this.ySize, 256, 256);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        Collection<Task> tasks = this.container.getVisibleTasks();
        if (!tasks.isEmpty()) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = j + 16 + 3 + 2;
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.renderScroller(i + 60, j - 1, tasks);
            int i1 = 0;

            for (Task task : tasks) {
                if (tasks.size() <= 7 || (i1 >= this.scrolledTask && i1 < 7 + this.scrolledTask)) {
                    this.renderTask(task, i - 1 + 17, k - 4);
                    k += 21;
                }
                ++i1;
            }
            for (TaskActionButton button : this.buttons) {
                button.visible = button.getChosenItem() < this.container.size() && this.container.getTask(button.getChosenItem() + this.scrolledTask) == dummy && !this.container.isCompleted(this.container.getTask(button.getChosenItem() + this.scrolledTask - 1));
            }
            for (TaskActionButton button : this.buttons) {
                button.render(mouseX, mouseY, partialTicks);
            }
            for (TaskActionButton button : this.buttons) {
                button.renderToolTip(mouseX, mouseY);
            }
            i1 = 0;
            k = j + 16 + 3 + 2;
            for (Task task : tasks) {
                if (tasks.size() <= 7 || (i1 >= this.scrolledTask && i1 < 7 + this.scrolledTask)) {
                    if (mouseX > i - 1 + 17 && mouseX < i - 1 + 139 + 17 && mouseY > k - 4 && mouseY < k + 21 - 4) {
                        this.renderTaskToolTip(task, i - 1 + 17, k - 4, mouseX, mouseY);
                    }
                    k += 21;
                }
                ++i1;
            }
            GlStateManager.popMatrix();
        }
    }

    private void renderTaskToolTip(Task task, int x, int y, int mouseX, int mouseY) {
        //tooltips for the dummy task
        if (task == dummy) {
            if (mouseX >= x + 3 + 113 - 21 && mouseX < x + 3 + 113 - 21 + 16 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                TaskReward reward = task.getReward();
                if (reward instanceof ItemReward) {
                    this.renderItemTooltip(((ItemReward) reward).getReward(), x + 3 + 113 - 21, y + 2, REWARD.getFormattedText(), false);
                } else {
                    this.renderItemTooltip(task,x + 3 + 113 - 21, y + 2);
                }
            }
            List<TaskRequirement.Requirement<?>> requirements = task.getRequirement().getAll();
            for (int i = 0; i < requirements.size(); i++) {
                if (mouseX >= x + 3 + 3 + i*20&& mouseX < x + 3 + 16 + 3 + i*20&& mouseY >= y + 2 && mouseY < y + 2 + 16) {
                    this.renderRequirementTool(this.container.getTask(this.openedTask), requirements.get(i), x + 3 + 3 + i*20, y + 2);
                }
            }
        } else {
            //default task tooltips
            List<String> toolTips = this.toolTips.computeIfAbsent(task, task1 -> Lists.newArrayList());
            if (toolTips.isEmpty()) {
                toolTips.add(task.getTranslation().applyTextStyle(this.container.getFactionColor()).getFormattedText());
                if (task.useDescription()) {
                    toolTips.add(task.getDescription().getFormattedText());
                    toolTips.add("");
                }
                for (List<TaskRequirement.Requirement<?>> requirements : task.getRequirement().requirements().values()) {
                    if(requirements == null)continue;
                    ITextComponent title;
                    TaskRequirement.Type type = requirements.get(0).getType();
                    boolean completed = this.container.areRequirementsCompleted(task, type);
                    switch (type){
                        case ITEMS:
                            title = new TranslationTextComponent("gui.vampirism.taskmaster.item_req").appendText(":");
                            break;
                        case STATS:
                            title = new TranslationTextComponent("gui.vampirism.taskmaster.stat_req").appendText(":");
                            break;
                        case ENTITY:
                            title = new TranslationTextComponent("gui.vampirism.taskmaster.entity_req").appendText(":");
                            break;
                        case ENTITY_TAG:
                            title = new TranslationTextComponent("gui.vampirism.taskmaster.entity_tag_req").appendText(":");
                            break;
                        default:
                            title = new TranslationTextComponent("gui.vampirism.taskmaster.bool_req").appendText(":");
                            break;
                    }
                    if(completed) {
                        title.applyTextStyle(TextFormatting.STRIKETHROUGH);
                    }
                    toolTips.add(title.getFormattedText());
                    for (TaskRequirement.Requirement<?> requirement : requirements) {
                        ITextComponent desc;
                        switch (type) {
                            case ITEMS:
                                desc = ((Item) requirement.getStat(this.factionPlayer)).getName().shallowCopy().appendText(" "+requirement.getAmount(this.factionPlayer));
                                break;
                            case STATS:
                                desc = new TranslationTextComponent("stat." + requirement.getStat(this.factionPlayer).toString().replace(':', '.')).appendText(" " + requirement.getAmount(this.factionPlayer));
                                break;
                            case ENTITY:
                                desc = (((EntityType<?>) requirement.getStat(this.factionPlayer)).getName().shallowCopy().appendText(" " + requirement.getAmount(this.factionPlayer)));
                                break;
                            case ENTITY_TAG:
                                //noinspection unchecked
                                desc = new TranslationTextComponent("tasks.vampirism." + ((Tag<EntityType<?>>)requirement.getStat(this.factionPlayer)).getId().toString()).appendText(" " + requirement.getAmount(this.factionPlayer));
                                break;
                            default:
                                desc = new TranslationTextComponent(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':','.'));
                                break;
                        }
                        if(completed || this.container.isRequirementCompleted(task,requirement)) {
                            desc.applyTextStyle(TextFormatting.STRIKETHROUGH);
                        }
                        toolTips.add(new StringTextComponent("  ").appendSibling(desc).getFormattedText());
                    }
                }
            }
            this.renderTooltip(toolTips, x, y);
        }
    }

    private void renderTask(Task task, int x, int y) {
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
            GlStateManager.color4f(0.4f,0.4f,0.4f,1);

        } else if (this.container.canCompleteTask(task)) {
            if(!dummy) {
                GlStateManager.color4f(0, 0.8f, 0, 1);
            }
        } else if (!this.container.isTaskAccepted(task)) {
            GlStateManager.color4f(0.8f,0.8f,1f,1);
        }
        blit(x, y, this.blitOffset, 16, 187 + offset, 137, Math.min(64 + 54 + 99 - (y), 21), 256, 256);
        GlStateManager.color4f(1,1,1,1);
        RenderHelper.enableGUIStandardItemLighting();

        if (!dummy) {
            this.font.drawString(this.font.trimStringToWidth(task.getTranslation().getFormattedText(), 131), x + 4, y + 7, 3419941);//(6839882 & 16711422) >> 1 //8453920 //4226832
        } else {
            TaskReward reward = task.getReward();
            if (reward instanceof ItemReward) {
                ItemStack stack = ((ItemReward) reward).getReward();
                this.itemRenderer.renderItemAndEffectIntoGUI(stack, x + 3 + 113 - 21, y + 2);
                this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x + 3 + 113 - 21, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
            }else{
                this.itemRenderer.renderItemAndEffectIntoGUI(PAPER,x + 3 + 113 - 21, y + 2);
            }
            List<TaskRequirement.Requirement<?>> requirements = task.getRequirement().getAll();
            for (int i = 0; i < requirements.size(); i++) {
                TaskRequirement.Requirement<?> requirement = requirements.get(i);
                if(this.container.isRequirementCompleted(task,requirement)) {
                    GlStateManager.color4f(0,0,0,1);
                }
                switch (requirement.getType()) {
                    case ITEMS:
                        ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                        this.itemRenderer.renderItemAndEffectIntoGUI(stack, x + 3 + 3 + i*20, y + 2);
                        this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x + 3 + 3 + i*20, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
                        break;
                    case ENTITY:
                    case ENTITY_TAG:
                        this.itemRenderer.renderItemAndEffectIntoGUI(SKULL_ITEM, x + 3 + 3 + i*20, y + 2);
                        this.itemRenderer.renderItemOverlayIntoGUI(this.font, SKULL_ITEM, x + 3 + 3 + i*20, y + 2, "" + requirement.getAmount(factionPlayer));
                        break;
                    default:
                        this.itemRenderer.renderItemAndEffectIntoGUI(PAPER,x + 3 + 3 + i*20, y + 2);
                        break;
                }

            }
        }
    }

    private void renderRequirementTool(Task task, TaskRequirement.Requirement<?> requirement, int x, int y) {
        boolean completed = this.container.isRequirementCompleted(task,requirement);
        switch (requirement.getType()) {
            case ITEMS:
                this.renderItemTooltip(((ItemRequirement) requirement).getItemStack(), x, y, (completed? REQUIREMENT_STRIKE: REQUIREMENT).getFormattedText(), completed);
                break;
            case ENTITY:
                this.renderEntitySkull(x, y, ((EntityType<?>) requirement.getStat(this.factionPlayer)).getName().shallowCopy().appendText(" " + requirement.getAmount(this.factionPlayer)), completed);
                break;
            case ENTITY_TAG:
                //noinspection unchecked
                this.renderEntitySkull(x, y, new TranslationTextComponent("tasks.vampirism." + ((Tag<EntityType<?>>) requirement.getStat(this.factionPlayer)).getId().toString()).appendText(" " + requirement.getAmount(this.factionPlayer)), completed);
                break;
            default:
                this.renderDefaultRequirementToolTip(task, requirement, x, y, completed);
        }
    }

    private void renderDefaultRequirementToolTip(Task task, TaskRequirement.Requirement<?> requirement, int x, int y, boolean strikeThrough) {
        List<String> tooltips = Lists.newArrayList();
        tooltips.add((strikeThrough ? REQUIREMENT_STRIKE:REQUIREMENT).getFormattedText());
        ITextComponent text = new TranslationTextComponent(task.getTranslationKey() + ".req." + requirement.getId().toString().replace(':','.'));
        if(strikeThrough) {
            text.applyTextStyle(TextFormatting.STRIKETHROUGH);
        }
        tooltips.add(text.getFormattedText());
        this.renderTooltip(tooltips, x, y, this.font);
    }

    private void renderEntitySkull(int x, int y, ITextComponent text, boolean strikeThrough) {
        List<String> tooltips = Lists.newArrayList();
        tooltips.add((strikeThrough? REQUIREMENT_STRIKE:REQUIREMENT).getFormattedText());
        if(strikeThrough) {
            text.applyTextStyle(TextFormatting.STRIKETHROUGH);
        }
        tooltips.add(text.getFormattedText());
        this.renderTooltip(tooltips, x, y, this.font);
    }

    private void renderItemTooltip(Task task, int x, int y) {
        List<String> tooltips = Lists.newArrayList(REWARD.getFormattedText());
        tooltips.add(UtilLib.translate(task.getTranslationKey() + ".reward"));
        this.renderTooltip(tooltips, x, y, this.font);
    }

    private void renderItemTooltip(ItemStack stack, int x, int y, String text, boolean strikeThrough) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        List<String> tooltips = getTooltipFromItem2(stack, strikeThrough);
        tooltips.add(0, text);
        this.renderTooltip(tooltips, x, y, (font == null ? this.font : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    public List<String> getTooltipFromItem2(ItemStack itemStack, boolean strikeThough) {
        assert this.minecraft != null;
        List<ITextComponent> list = itemStack.getTooltip(this.minecraft.player, this.minecraft.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);
        List<String> list1 = Lists.newArrayList();

        for(ITextComponent itextcomponent : list) {
            if(strikeThough) {
                itextcomponent.applyTextStyle(TextFormatting.STRIKETHROUGH);
            }
            list1.add(itextcomponent.getFormattedText());
        }

        return list1;
    }

    private void renderScroller(int x, int y, Collection<Task> tasks) {
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        int i = tasks.size() - 7;
        if (i >= 1) {
            int k = 144 - (2 + (i - 1) * 144 / i);
            int i1 = Math.min(121, this.scrolledTask * k);
            if (this.scrolledTask >= i + 4) {
                i1 = 121;
            }
            blit(x + 94, y + 18 + i1, this.blitOffset, 2.0F, 195.0F, 6, 27, 256, 256);
        } else {
            blit(x + 94, y + 18, this.blitOffset, 2.0F, 195.0F, 6, 27, 256, 256);
        }

        RenderHelper.enableGUIStandardItemLighting();
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
            super(xPos, yPos, 14, 13, 0, 0, 0, TASKMASTER_GUI_TEXTURE, 0, 0, onPress, "");
            this.chosenItem = chosenItem;
            this.visible = false;
        }

        public int getChosenItem() {
            return chosenItem;
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            if (this.isHovered && this.visible && TaskMasterScreen.this.container.size() > this.chosenItem + TaskMasterScreen.this.scrolledTask) {
                TaskMasterContainer.TaskAction action = getAction();
                TaskMasterScreen.this.renderTooltip((action == TaskMasterContainer.TaskAction.ACCEPT?ACCEPT:action == TaskMasterContainer.TaskAction.ABORT?ABORT:SUBMIT).getFormattedText(), mouseX, mouseY);
            }
        }

        @Override
        public void renderButton(int mouseX, int mouseY, float p_renderButton_3_) {
            TaskMasterContainer.TaskAction action = getAction();
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
            GlStateManager.disableDepthTest();
            int i = 0;
            if (this.isHovered()) {
                i += 13;
            }
            int j;
            switch (action){
                case ACCEPT:
                    j = 190;
                    break;
                case COMPLETE:
                    j = 176;
                    break;
                default:
                    j = 204;

            }

            blit(this.x, this.y, (float)j, (float)i, this.width, this.height, 256, 256);
            GlStateManager.enableDepthTest();
        }

        private TaskMasterContainer.TaskAction getAction() {
            Task task = TaskMasterScreen.this.container.getTask(this.chosenItem + TaskMasterScreen.this.scrolledTask - 1);
            if (TaskMasterScreen.this.container.canCompleteTask(task)) {
                return TaskMasterContainer.TaskAction.COMPLETE;
            } else if (!TaskMasterScreen.this.container.isTaskAccepted(task)) {
                return TaskMasterContainer.TaskAction.ACCEPT;
            } else {
                return TaskMasterContainer.TaskAction.ABORT;
            }
        }
    }
}
