package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.api.entity.factions.IPlayableFaction;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import de.teamlapen.vampirism.api.entity.player.task.TaskReward;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.player.tasks.reward.ItemReward;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TaskMasterScreen extends ContainerScreen<TaskMasterContainer> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private final Task dummy = new Task(null, ImmutableList.of(), ImmutableList.of(), null, false) {
        @Nonnull
        @Override
        public ImmutableList<TaskReward> getRewards() {
            return TaskMasterScreen.this.container.getAvailableTasks().get(TaskMasterScreen.this.openedTask).getRewards();
        }

        @Nonnull
        @Override
        public ImmutableList<TaskRequirement<?>> getRequirements() {
            return TaskMasterScreen.this.container.getAvailableTasks().get(TaskMasterScreen.this.openedTask).getRequirements();
        }

        @Nullable
        @Override
        public IPlayableFaction<?> getFaction() {
            return TaskMasterScreen.this.container.getAvailableTasks().get(TaskMasterScreen.this.openedTask).getFaction();
        }

        @Nonnull
        @Override
        public String getTranslationKey() {
            return "";
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
            this.buttons[l] = this.addButton(new CompleteButton(i + 5 + 132, k + 3, l, (button) -> {
                if (button instanceof CompleteButton) {
                    Task task = this.container.getAvailableTasks().get(((CompleteButton) button).getChosenItem() + this.scrolledTask);
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
        Collection<Task> tasks = this.container.getAvailableTasks();
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

            Triple<Task, Integer, Integer> tooltips = null;
            for (Task task : tasks) {
                if (tasks.size() > 7 && (i1 < this.scrolledTask || i1 >= 7 + this.scrolledTask)) {
                    ++i1;
                } else {
                    this.renderTask(task, i - 1 + 17, k - 4);
                    if (mouseX > i - 1 && mouseX < i - 1 + 139 && mouseY > k && mouseY < k + 21) {
                        if (!(this.isOpen && ((mouseY - (j - 4 + 21)) / 21) + this.scrolledTask == this.openedTask + 1)) {
                            tooltips = Triple.of(task, i - 1, k);
                        }
                    }
                    k += 21;
                    ++i1;
                }
            }
            for (CompleteButton button : this.buttons) {
                button.visible = button.getChosenItem() < this.container.getAvailableTasks().size() && this.container.canCompleteTask(this.container.getAvailableTasks().get(button.getChosenItem() + this.scrolledTask));
            }
            for (CompleteButton button : this.buttons) {
                button.render(mouseX, mouseY, partialTicks);
            }
            if (tooltips != null) {
                renderToolTip(tooltips.getLeft(), tooltips.getMiddle(), tooltips.getRight());
            }
            GlStateManager.popMatrix();
        }
    }

    private void renderToolTip(Task task, int x, int y) {
        List<String> toolTips = this.toolTips.computeIfAbsent(task, task1 -> Lists.newArrayList());
        if (toolTips.isEmpty()) {
            ITextComponent paragraph = new StringTextComponent(" ");
            toolTips.add(task.getTranslation().applyTextStyle(this.container.getFactionColor()).getFormattedText());
            List<TaskRequirement<?>> items = task.getRequirements().stream().filter(taskRequirement -> taskRequirement.getType().equals(TaskRequirement.Type.ITEMS)).collect(Collectors.toList());
            List<TaskRequirement<?>> entities = task.getRequirements().stream().filter(taskRequirement -> taskRequirement.getType().equals(TaskRequirement.Type.ENTITY)).collect(Collectors.toList());
            List<TaskRequirement<?>> stats = task.getRequirements().stream().filter(taskRequirement -> taskRequirement.getType().equals(TaskRequirement.Type.STATS)).collect(Collectors.toList());
            if (!items.isEmpty()) {
                toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.itemreq").appendText(":").getFormattedText());
                for (TaskRequirement<?> requirement : items) {
                    toolTips.add(paragraph.shallowCopy().appendSibling(((Item) requirement.getStat()).getName()).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount())).getFormattedText());
                }
            }
            if (!entities.isEmpty()) {
                toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.entityreq").appendText(":").getFormattedText());
                for (TaskRequirement<?> requirement : entities) {
                    toolTips.add(paragraph.shallowCopy().appendSibling(((EntityType<?>) requirement.getStat()).getName()).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount())).getFormattedText());
                }
            }
            if (!stats.isEmpty()) {
                toolTips.add(new TranslationTextComponent("gui.vampirism.taskmaster.statreq").appendText(":").getFormattedText());
                for (TaskRequirement<?> requirement : stats) {
                    toolTips.add(paragraph.shallowCopy().appendSibling(new TranslationTextComponent("stat." + requirement.getStat().toString().replace(':', '.'))).appendSibling(paragraph.shallowCopy()).appendSibling(new StringTextComponent("" + requirement.getAmount())).getFormattedText());
                }
            }
            if (task.useDescription()) {
                toolTips.add(task.getDescription().getFormattedText());
            }

        }
        this.renderTooltip(toolTips, x, y);
    }

    private void renderTask(Task task, int x, int y) {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        boolean dummy = task == this.dummy;
        int offset = dummy ? 63 : 0;
        if (dummy && this.isOpen) {
            task = this.container.getAvailableTasks().get(this.openedTask);
        }
        if (this.container.isCompleted(task)) {
            blit(x, y, this.blitOffset, 16, 208 + offset, 137, 21, 325, 256);
        } else if (this.container.canCompleteTask(task)) {
            blit(x, y, this.blitOffset, 16, 229 + offset, 137, Math.min(64 + 54 + 99 - (y), 21), 325, 256);
        } else {
            blit(x, y, this.blitOffset, 16, 187 + offset, 137, Math.min(64 + 54 + 99 - (y), 21), 325, 256);
        }

        if (!dummy) {
            this.font.drawString(this.font.trimStringToWidth(task.getTranslationKey(), 105), x + 4, y + 7, 3419941);//(6839882 & 16711422) >> 1 //8453920 //4226832
        } else {
            Iterator<ItemStack> rewards = task.getRewards().stream().filter(taskReward -> taskReward instanceof ItemReward).map(taskReward -> ((ItemReward) taskReward).getReward()).iterator();
            for (int i = 0; i < 6; i++) {
                if (!rewards.hasNext()) break;
                this.itemRenderer.renderItemAndEffectIntoGUI(rewards.next(), x + 3 + i * 15, y + 2);
            }
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void renderScroller(int x, int y, Collection<Task> tasks) {
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        RenderHelper.disableStandardItemLighting();
        int i = tasks.size() - 7;
        if (i > 1) {
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
        int i = this.container.getAvailableTasks().size();
        if (this.isTaskListTooLong(i)) {
            int j = i - 7;
            this.scrolledTask = (int) ((double) this.scrolledTask - scrollAmount);
            this.scrolledTask = MathHelper.clamp(this.scrolledTask, 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int buttonId, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        int i = this.container.getAvailableTasks().size();
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
        if (this.isTaskListTooLong(this.container.getAvailableTasks().size()) && mouseX > (double) (i + 154) && mouseX < (double) (i + 160) && mouseY > (double) (j + 17) && mouseY <= (double) (j + 166)) {
            this.mouseOnScroller = true;
        }
        if (mouseX > i + 17 && mouseX < i + 136 + 17 && mouseY > j - 4 + 21 && mouseY < j + 168 - 4) {
            int num = ((int) (mouseY - (j - 4 + 21)) / 21) + this.scrolledTask;
            if (this.isOpen) {
                if (this.openedTask + 1 != num) {
                    this.container.getAvailableTasks().remove(this.openedTask + 1);
                    if (num > this.openedTask) {
                        num--;
                    }
                    if (num < this.container.getAvailableTasks().size() && num != this.openedTask) {
                        this.openedTask = num;
                        this.container.getAvailableTasks().add(this.openedTask + 1, dummy);
                    } else {
                        this.isOpen = false;
                        if (this.container.getAvailableTasks().size() < this.scrolledTask + 7) {
                            this.scrolledTask--;
                        }
                    }
                }
            } else {
                if (num < this.container.getAvailableTasks().size()) {
                    this.isOpen = true;
                    this.openedTask = num;
                    this.container.getAvailableTasks().add(this.openedTask + 1, dummy);
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
        public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
            if (this.isHovered && TaskMasterScreen.this.container.getAvailableTasks().size() > this.chosenItem + TaskMasterScreen.this.scrolledTask) {
                TaskMasterScreen.this.renderTooltip("test", p_renderToolTip_1_, p_renderToolTip_2_);
            }
        }
    }
}
