package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWidget;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.ITaskInstance;
import de.teamlapen.vampirism.client.gui.widget.TaskItem;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import java.util.Collection;

@OnlyIn(Dist.CLIENT)
public class TaskBoardScreen extends AbstractContainerScreen<TaskBoardContainer> implements ExtendedScreen {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private final IFactionPlayer<?> factionPlayer;

    private ScrollableListWidget<ITaskInstance> list;


    public TaskBoardScreen(TaskBoardContainer container, Inventory playerInventory, Component containerName) {
        super(container, playerInventory, containerName);
        this.imageWidth = 176;
        this.imageHeight = 181;
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Can't open container without faction"));
        this.menu.setReloadListener(() -> this.list.refresh());
    }

    @Override
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    @Override
    public TaskContainer getTaskContainer() {
        return this.menu;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int buttonId, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, buttonId, dragX, dragY);
        if (!this.isQuickCrafting) {
            this.list.mouseDragged(mouseX, mouseY, buttonId, dragX, dragY);
        }
        return true;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.list.renderToolTip(matrixStack, mouseX, mouseY);
    }

    public Collection<ITaskInstance> taskSupplier() {
        return this.menu.getVisibleTasks();
    }

    @Override
    protected void init() {
        super.init();
        this.addRenderableWidget(list = new ScrollableListWithDummyWidget<>(this.leftPos + 16, this.topPos + 16, 145, 149, 21, this::taskSupplier, (item, list1, isDummy) -> new TaskItem<>(item, list1, isDummy, this, this.factionPlayer)));
    }

    @Override
    protected void renderBg(@NotNull PoseStack mStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(mStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TASKMASTER_GUI_TEXTURE);
        blit(mStack, this.leftPos, this.topPos, this.getBlitOffset(), 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack mStack, int mouseX, int mouseY) {
        this.font.draw(mStack, this.title, (float) (this.imageWidth / 2 - this.font.width(this.title) / 2), 5.0F, 4210752);
    }


}
