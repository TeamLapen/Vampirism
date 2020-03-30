package de.teamlapen.vampirism.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

public class TaskMasterScreen extends ContainerScreen<TaskMasterContainer> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");

    private final CompleteButton[] buttons = new CompleteButton[7];
    private int scrolledTask;
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
        blit(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        Collection<Task> tasks = this.container.getAvailableTasks();
        if (!tasks.isEmpty()) {
            int i = (this.width - this.xSize) / 2;
            int j = (this.height - this.ySize) / 2;
            int k = j + 16 + 3;
            int l = i + 5 + 5;
            GlStateManager.pushMatrix();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            GlStateManager.enableColorMaterial();
            GlStateManager.enableLighting();
            this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
            this.renderScroller(i + 60, j - 1, tasks);
            int i1 = 0;

            Triple<Task, Integer, Integer> tooltips = null;
            for (Task task : tasks) {
                if (tasks.size() > 7 && (i1 < this.scrolledTask || i1 >= 7 + this.scrolledTask)) {
                    ++i1;
                } else {
                    int j1 = k + 2;
                    this.renderTask(task, i - 1, j1);
                    this.font.drawString(this.font.trimStringToWidth(task.getTranslationKey(), 110), i - 1 + 21, k + 5, 3419941);//(6839882 & 16711422) >> 1 //8453920 //4226832
                    if (mouseX > i - 1 && mouseX < i - 1 + 139 && mouseY > j1 && mouseY < j1 + 21) {
                        tooltips = Triple.of(task, i - 1, j1);
                    }
                    k += 21;
                    ++i1;
                }
            }
            for (CompleteButton button : this.buttons) { //TODO
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
        ITextComponent title = task.getTranslation().applyTextStyle(this.container.getFactionColor());
        this.renderTooltip(Lists.newArrayList(title.getFormattedText(), task.getDescription().getFormattedText()), x, y);
    }

    private void renderTask(Task task, int x, int y) {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        if (this.container.isCompleted(task)) {
            blit(x + 17, y - 4, 16, 208, 137, 21);
        } else if (this.container.canCompleteTask(task)) {
            blit(x + 17, y - 4, 16, 229, 137, Math.min(64 + 54 + 99 - (y - 4), 21));
        } else {
            blit(x + 17, y - 4, 16, 187, 137, Math.min(64 + 54 + 99 - (y - 4), 21));
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void renderScroller(int x, int y, Collection<Task> tasks) {
        RenderHelper.disableStandardItemLighting();
        int i = tasks.size() - 7;
        if (i > 1) {
            int k = 144 - (2 + (i - 1) * 144 / i);
            int i1 = Math.min(125, this.scrolledTask * k);
            if (this.scrolledTask >= i + 4) {
                i1 = 125;
            }
            blit(x + 94, y + 18 + i1, this.blitOffset, 7.0F, 199.0F, 6, 27, 256, 512);
        } else {
            blit(x + 94, y + 18, this.blitOffset, 7.0F, 199.0F, 6, 27, 256, 512);
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
        return super.mouseClicked(mouseX, mouseY, buttonId);
    }

    private static class CompleteButton extends ImageButton {
        final int chosenItem;

        public CompleteButton(int xPos, int yPos, int chosenItem, IPressable onPress) {
            super(xPos, yPos, 14, 12, 0, 222, 12, TASKMASTER_GUI_TEXTURE, 256, 256, onPress, "");
            this.chosenItem = chosenItem;
            this.visible = false;
        }

        public int getChosenItem() {
            return chosenItem;
        }
    }
}
