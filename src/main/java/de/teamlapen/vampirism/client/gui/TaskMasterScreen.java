package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.api.entity.player.task.Task;
import de.teamlapen.vampirism.inventory.container.TaskMasterContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Collection;

public class TaskMasterScreen extends ContainerScreen<TaskMasterContainer> {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");

    private int scrolledTask;
    private boolean mouseOnScroller;

    public TaskMasterScreen(TaskMasterContainer container, PlayerInventory playerInventory, ITextComponent containerName) {
        super(container, playerInventory, containerName);
        this.xSize = 176;
        this.ySize = 179;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
        int j = this.ySize - 82;
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
        this.renderHoveredToolTip(mouseX, mouseY);
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
                    this.renderCheck(task, i - 1, j1);
                    if (mouseX > i - 1 && mouseX < i - 1 + 139 && mouseY > j1 && mouseY < j1 + 21) {
                        tooltips = Triple.of(task, i - 1, j1);
                    }
                    k += 21;
                    ++i1;
                }
            }
            if (tooltips != null) {
                renderToolTip(tooltips.getLeft(), tooltips.getMiddle(), tooltips.getRight());
            }
            GlStateManager.popMatrix();
        }
    }

    private void renderToolTip(Task task, int x, int y) {
        String string = UtilLib.translate(task.getDescriptionKey());
        this.renderTooltip(string, x, y);
    }

    private void renderTask(Task task, int x, int y) {
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableBlend();
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        if (this.container.isCompleted(task)) {
            blit(x + 17, y - 4, 16, 208, 137, 21);
        } else if (this.container.canCompleteTask(task)) {
            blit(x + 17, y - 4, 16, 229, 137, Math.min(64 + 54 - (y - 4), 21));
        } else {
            blit(x + 17, y - 4, 16, 187, 137, Math.min(64 + 54 - (y - 4), 21));
        }

        RenderHelper.enableGUIStandardItemLighting();
    }

    private void renderCheck(Task task, int x, int y) {
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        if (this.container.canCompleteTask(task)) {
            this.blit(x + 138, y, 0, 222, 14, 12);
        } else if (!this.container.isCompleted(task)) {
            this.blit(x + 138, y, 0, 234, 14, 12);
        }
    }

    private void renderScroller(int x, int y, Collection<Task> tasks) {
        RenderHelper.disableStandardItemLighting();
        int i = tasks.size() - 7;
        if (i > 1) {
//            int j = 62 - ((i - 1) * 62 / i);//TODO for task.size() > 15 the scroller doesn't work correctly when scrolling down
//            int k = 1 + j / i + 62 / i * 2;
            int k = 62 - (2 + (i - 1) * 62 / i);
            int l = 65;
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
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
        int i = this.container.getAvailableTasks().size();
        if (this.isTaskListTooLong(i)) {
            int j = i - 3;
            this.scrolledTask = (int) ((double) this.scrolledTask - p_mouseScrolled_5_);
            this.scrolledTask = MathHelper.clamp(this.scrolledTask, 0, j);
        }

        return true;
    }

    @Override
    public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
        int i = this.container.getAvailableTasks().size();
        if (this.mouseOnScroller) {
            int j = this.guiTop + 18;
            int k = j + 144;
            int l = i - 3;
            float f = ((float) p_mouseDragged_3_ - (float) j - 13.5F) / ((float) (k - j) - 27.0F);
            f = f * (float) l + 0.5F;
            this.scrolledTask = MathHelper.clamp((int) f, 0, l);
            return true;
        } else {
            return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        this.mouseOnScroller = false;
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        if (this.isTaskListTooLong(this.container.getAvailableTasks().size()) && mouseX > (double) (i + 154) && mouseX < (double) (i + 154 + 6) && mouseY > (double) (j - 1) && mouseY <= (double) (j - 1 + 144 + 1)) {
            this.mouseOnScroller = true;
        }
        return super.mouseClicked(mouseX, mouseY, buttonId);
    }
}
