package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.player.tasks.req.ItemRequirement;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderHelper;
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
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TaskMasterScreen extends ContainerScreen<TaskMasterContainer> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private static final ITextComponent SUBMIT = new TranslationTextComponent("gui.vampirism.taskmaster.complete_task");
    private static final ITextComponent REQUIREMENT = new TranslationTextComponent("gui.vampirism.taskmaster.requirement").applyTextStyle(TextFormatting.UNDERLINE);
    private static final ITextComponent REWARD = new TranslationTextComponent("gui.vampirism.taskmaster.reward").applyTextStyle(TextFormatting.UNDERLINE);
    private static final ItemStack SKULLITEM = new ItemStack(Blocks.SKELETON_SKULL);
    private static final ItemStack PAPER = new ItemStack(Items.PAPER);
    @SuppressWarnings("ConstantConditions")
    private final Task dummy = new Task(Task.Variant.UNIQUE, null, null, null, null, false) {
        @Nonnull
        @Override
        public TaskReward getReward() {
            return TaskMasterScreen.this.container.getUnlockedTasks().get(TaskMasterScreen.this.openedTask).getReward();
        }

        @Nonnull
        @Override
        public TaskRequirement<?> getRequirement() {
            return TaskMasterScreen.this.container.getUnlockedTasks().get(TaskMasterScreen.this.openedTask).getRequirement();
        }

        @Nullable
        @Override
        public IPlayableFaction<?> getFaction() {
            return TaskMasterScreen.this.container.getUnlockedTasks().get(TaskMasterScreen.this.openedTask).getFaction();
        }

        @Nonnull
        @Override
        public String getTranslationKey() {
            return TaskMasterScreen.this.container.getUnlockedTasks().get(TaskMasterScreen.this.openedTask).getTranslationKey();
        }
    };

    private final CompleteButton[] buttons = new CompleteButton[7];
    private final Map<Task, List<String>> toolTips = Maps.newHashMap();
    private int scrolledTask;
    private int openedTask;
    private boolean isOpen;
    private boolean mouseOnScroller;

    public TaskMasterScreen(TaskMasterContainer container, PlayerInventory playerInventory, ITextComponent containerName) {
        super(container, playerInventory, containerName);
        this.xSize = 176;
        this.ySize = 181;
    }

    @Override
    protected void init() {
        super.init();
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        int k = j + 16 + 2;

        for (int l = 0; l < 7; ++l) {
            this.buttons[l] = this.addButton(new CompleteButton(i + 5 + 132 - 2, k + 3, l, (button) -> {
                if (button instanceof CompleteButton) {
                    Task task = this.container.getUnlockedTasks().get(((CompleteButton) button).getChosenItem() + this.scrolledTask - 1);
                    if (this.container.canCompleteTask(task)) {
                        this.container.completeTask(task);
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
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        blit(i, j, this.blitOffset, 0, 0, this.xSize, this.ySize, 325, 256);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        Collection<Task> tasks = this.container.getUnlockedTasks();
        if (!tasks.isEmpty()) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = j + 16 + 3 + 2;
            int l = i + 5 + 5;
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.renderScroller(i + 60, j - 1, tasks);
            int i1 = 0;

            for (Task task : tasks) {
                if (tasks.size() > 7 && (i1 < this.scrolledTask || i1 >= 7 + this.scrolledTask)) {
                    ++i1;
                } else {
                    this.renderTask(task, i - 1 + 17, k - 4, mouseX, mouseY);
                    k += 21;
                    ++i1;
                }
            }
            for (CompleteButton button : this.buttons) {
                try {
                    button.visible = button.getChosenItem() < this.container.getUnlockedTasks().size() && this.container.getUnlockedTasks().get(button.getChosenItem() + this.scrolledTask) == dummy && this.container.canCompleteTask(this.container.getUnlockedTasks().get(button.getChosenItem() + this.scrolledTask - 1)) && !this.container.isCompleted(this.container.getUnlockedTasks().get(button.getChosenItem() + this.scrolledTask - 1));
                }catch (ArrayIndexOutOfBoundsException e) {
                    button.visible = false;
                    try {
                        LogManager.getLogger().info(this.container.getUnlockedTasks().get(button.getChosenItem() + this.scrolledTask) == dummy);
                    }catch (ArrayIndexOutOfBoundsException e1){
                        LogManager.getLogger().info(button.getChosenItem() + this.scrolledTask);
                    }
                }
            }
            for (CompleteButton button : this.buttons) {
                button.render(mouseX, mouseY, partialTicks);
            }
            for (CompleteButton button : this.buttons) {
                button.renderToolTip(mouseX, mouseY);
            }
            i1 = 0;
            k = j + 16 + 3 + 2;
            for (Task task : tasks) {
                if (tasks.size() > 7 && (i1 < this.scrolledTask || i1 >= 7 + this.scrolledTask)) {
                    ++i1;
                } else {
                    if (mouseX > i - 1 + 17 && mouseX < i - 1 + 139 + 17 && mouseY > k - 4 && mouseY < k + 21 - 4) {
                        this.renderTaskToolTip(task, i - 1 + 17, k - 4, mouseX, mouseY);
                    }
                    k += 21;
                    ++i1;
                }
            }
            GlStateManager.popMatrix();
        }
    }

    private void renderTaskToolTip(Task task, int x, int y, int mouseX, int mouseY) {
        if (task == dummy) {
            if (mouseX >= x + 3 + 113 - 21 && mouseX < x + 3 + 113 - 21 + 16 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                TaskReward reward = task.getReward();
                if (reward instanceof ItemReward) {
                    this.renderRewardTooltip(((ItemReward) reward).getReward(), x + 3 + 113 - 21, y + 2, REWARD.getFormattedText());
                } else {
                    this.renderRewardTooltip(task,x + 3 + 113 - 21, y + 2);
                }
            }
            if (mouseX >= x + 3 + 3 && mouseX < x + 3 + 16 + 3 && mouseY >= y + 2 && mouseY < y + 2 + 16) {
                this.renderRequirementTool(task, x + 3 + 3, y + 2);
            }
        } else {
            List<String> toolTips = this.toolTips.computeIfAbsent(task, task1 -> Lists.newArrayList());
            if (toolTips.isEmpty()) {
                ITextComponent paragraph = new StringTextComponent(" ");
                toolTips.add(task.getTranslation().applyTextStyle(this.container.getFactionColor()).getFormattedText());
                TaskRequirement<?> requirement = task.getRequirement();
                switch (requirement.getType()) {
                    case ITEMS:
                        toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.item_req").appendText(":").getFormattedText());
                        toolTips.add(paragraph.shallowCopy().appendSibling(((Item) requirement.getStat()).getName()).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount())).getFormattedText());
                        break;
                    case STATS:
                        toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.stat_req").appendText(":").getFormattedText());
                        toolTips.add(paragraph.shallowCopy().appendSibling(new TranslationTextComponent("stat." + requirement.getStat().toString().replace(':', '.')).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount()))).getFormattedText());
                        break;
                    case ENTITY:
                        toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.entity_req").appendText(":").getFormattedText());
                        toolTips.add(paragraph.shallowCopy().appendSibling(((EntityType<?>) requirement.getStat()).getName()).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount())).getFormattedText());
                        break;
                    case ENTITY_TYPE:
                        toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.entitytype_req").appendText(":").getFormattedText());
                        toolTips.add(paragraph.shallowCopy().appendSibling(new TranslationTextComponent(((Tag<EntityType<?>>)requirement.getStat()).getId().toString())).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount())).getFormattedText());
                }
                if (task.useDescription()) {
                    toolTips.add(task.getDescription().getFormattedText());
                }

            }
            this.renderTooltip(toolTips, x, y);
        }
    }

    private void renderTask(Task task, int x, int y, int mouseX, int mouseY) {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        boolean dummy = task == this.dummy;
        int offset = dummy ? 63 : 0;
        if (dummy && this.isOpen) {
            task = this.container.getUnlockedTasks().get(this.openedTask);
        }
        if (this.container.isCompleted(task)) {
            blit(x, y, this.blitOffset, 16, 208 + offset, 137, 21, 325, 256);
        } else if (this.container.canCompleteTask(task)) {
            blit(x, y, this.blitOffset, 16, 229 + offset, 137, Math.min(64 + 54 + 99 - (y), 21), 325, 256);
        } else {
            blit(x, y, this.blitOffset, 16, 187 + offset, 137, Math.min(64 + 54 + 99 - (y), 21), 325, 256);
        }
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
            TaskRequirement<?> requirement = task.getRequirement();
            switch (task.getRequirement().getType()) {
                case ITEMS:
                    ItemStack stack = ((ItemRequirement) requirement).getItemStack();
                    this.itemRenderer.renderItemAndEffectIntoGUI(stack, x + 3 + 3, y + 2);
                    this.itemRenderer.renderItemOverlayIntoGUI(this.font, stack, x + 3 + 3, y + 2, "" + Math.min(stack.getCount(), stack.getMaxStackSize()));
                    break;
                case ENTITY:
                case ENTITY_TYPE:
                    this.itemRenderer.renderItemAndEffectIntoGUI(SKULLITEM, x + 3 + 3, y + 2);
                    break;
                default:
                    this.itemRenderer.renderItemAndEffectIntoGUI(PAPER,x + 3 + 3, y + 2);
                    break;
            }
        }
    }

    private void renderRequirementTool(Task task, int x, int y) {
        TaskRequirement<?> requirement = task.getRequirement();
        if (requirement.getType().equals(TaskRequirement.Type.ITEMS)) {
            this.renderRewardTooltip(((ItemRequirement) requirement).getItemStack(), x,y, REQUIREMENT.getFormattedText());
        }else if(requirement.getType().equals(TaskRequirement.Type.ENTITY)) {
            this.renderEntitySkull(x,y, (EntityType<?>)requirement.getStat(), requirement.getAmount());
        }else {
            this.renderDefaultRequirementToolTip(task, x,y);
        }
    }

    private void renderDefaultRequirementToolTip(Task task, int x, int y) {
        List<String> tooltips = Lists.newArrayList();
        tooltips.add(REQUIREMENT.getFormattedText());
        tooltips.add(UtilLib.translate(task.getTranslationKey() + ".requirement"));
        this.renderTooltip(tooltips, x, y, this.font);
    }

    private void renderEntitySkull(int x, int y, EntityType<?> entity, int amount) {
        List<String> tooltips = Lists.newArrayList();
        tooltips.add(REQUIREMENT.getFormattedText());
        tooltips.add(entity.getName().getFormattedText() + " " + amount);
        this.renderTooltip(tooltips, x, y, this.font);
    }

    private void renderRewardTooltip(Task task, int x, int y) {
        List<String> tooltips = Lists.newArrayList(REWARD.getFormattedText());
        tooltips.add(UtilLib.translate(task.getTranslationKey() + ".reward"));
        this.renderTooltip(tooltips, x, y, this.font);
    }

    private void renderRewardTooltip(ItemStack stack, int x, int y, String text) {
        FontRenderer font = stack.getItem().getFontRenderer(stack);
        net.minecraftforge.fml.client.config.GuiUtils.preItemToolTip(stack);
        List<String> tooltips = this.getTooltipFromItem(stack);
        tooltips.add(0, text);
        this.renderTooltip(tooltips, x, y, (font == null ? this.font : font));
        net.minecraftforge.fml.client.config.GuiUtils.postItemToolTip();
    }

    private void renderScroller(int x, int y, Collection<Task> tasks) {
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        int i = tasks.size() - 7;
        if (i >= 1) {
            int k = 144 - (2 + (i - 1) * 144 / i);
            int i1 = Math.min(121, this.scrolledTask * k);
            if (this.scrolledTask >= i + 4) {
                i1 = 121;
            }
            blit(x + 94, y + 18 + i1, this.blitOffset, 2.0F, 195.0F, 6, 27, 325, 256);
        } else {
            blit(x + 94, y + 18, this.blitOffset, 2.0F, 195.0F, 6, 27, 325, 256);
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private boolean isTaskListTooLong(int size) {
        return size > 7;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollAmount) {
        int i = this.container.getUnlockedTasks().size();
        if (this.isTaskListTooLong(i)) {
            int j = i - 7;
            this.scrolledTask = (int) ((double) this.scrolledTask - scrollAmount);
            this.scrolledTask = MathHelper.clamp(this.scrolledTask, 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int buttonId, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        int i = this.container.getUnlockedTasks().size();
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
        if (this.isTaskListTooLong(this.container.getUnlockedTasks().size()) && mouseX > (double) (i + 154) && mouseX < (double) (i + 160) && mouseY > (double) (j + 17) && mouseY <= (double) (j + 166)) {
            this.mouseOnScroller = true;
        }
        if (mouseX > i + 17 && mouseX < i + 136 + 17 && mouseY > j - 4 + 21 && mouseY < j + 168 - 4) {
            int num = ((int) (mouseY - (j - 4 + 21)) / 21) + this.scrolledTask;
            if (this.isOpen) {
                if (this.openedTask + 1 != num) {
                    this.container.getUnlockedTasks().remove(this.openedTask + 1);
                    if (num > this.openedTask) {
                        num--;
                    }
                    if (num < this.container.getUnlockedTasks().size() && num != this.openedTask) {
                        this.openedTask = num;
                        this.container.getUnlockedTasks().add(this.openedTask + 1, dummy);
                    } else {
                        this.isOpen = false;
                        if (this.container.getUnlockedTasks().size() < this.scrolledTask + 7 && this.scrolledTask != 0) {
                            this.scrolledTask--;
                        }
                    }
                }
            } else {
                if (num < this.container.getUnlockedTasks().size()) {
                    this.isOpen = true;
                    this.openedTask = num;
                    this.container.getUnlockedTasks().add(this.openedTask + 1, dummy);
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, buttonId);
    }

    private class CompleteButton extends ImageButton {
        final int chosenItem;

        public CompleteButton(int xPos, int yPos, int chosenItem, IPressable onPress) {
            super(xPos, yPos, 14, 12, 0, 222, 12, TASKMASTER_GUI_TEXTURE, 256, 325, onPress, "");
            this.chosenItem = chosenItem;
            this.visible = false;
        }

        public int getChosenItem() {
            return chosenItem;
        }

        @Override
        public void renderToolTip(int mouseX, int mouseY) {
            if (this.isHovered && TaskMasterScreen.this.container.getUnlockedTasks().size() > this.chosenItem + TaskMasterScreen.this.scrolledTask) {
                TaskMasterScreen.this.renderTooltip(SUBMIT.getFormattedText(), mouseX, mouseY);
            }
        }
    }
}
