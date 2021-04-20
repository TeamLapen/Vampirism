package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWidget;
import de.teamlapen.lib.lib.client.gui.widget.ScrollableListWithDummyWidget;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.client.gui.widget.TaskItem;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.inventory.container.TaskContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collection;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class TaskBoardScreen extends ContainerScreen<TaskBoardContainer> implements ExtendedScreen {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private final IFactionPlayer<?> factionPlayer;

    private ScrollableListWidget<TaskContainer.TaskInfo> list;


    public TaskBoardScreen(TaskBoardContainer container, PlayerInventory playerInventory, ITextComponent containerName) {
        super(container, playerInventory, containerName);
        this.xSize = 176;
        this.ySize = 181;
        //noinspection OptionalGetWithoutIsPresent
        this.factionPlayer = FactionPlayerHandler.get(playerInventory.player).getCurrentFactionPlayer().get();
        this.container.setReloadListener(() -> this.list.refresh());
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(list = new ScrollableListWithDummyWidget<>(this.guiLeft + 16, this.guiTop + 16, 145, 149, 21, this::taskSupplier, (item, list1, isDummy) -> new TaskItem<>(item, list1, isDummy, this, this.factionPlayer)));
    }

    public Collection<TaskContainer.TaskInfo> taskSupplier() {
        return this.container.getVisibleTasks().stream().map(a -> new TaskContainer.TaskInfo(a, this.container.getTaskBoardId())).collect(Collectors.toList());
    }

    @Override
    public ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    @Override
    public TaskContainer getTaskContainer() {
        return this.container;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.list.renderToolTip(matrixStack, mouseX, mouseY);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int buttonId, double dragX, double dragY) {
        super.mouseDragged(mouseX, mouseY, buttonId, dragX, dragY);
        if (!this.dragSplitting) {
            this.list.mouseDragged(mouseX, mouseY, buttonId, dragX, dragY);
        }
        return true;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(mStack);
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(TASKMASTER_GUI_TEXTURE);
        blit(mStack, this.guiLeft, this.guiTop, this.getBlitOffset(), 0, 0, this.xSize, this.ySize, 256, 256);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        this.font.func_243248_b(mStack, this.title, (float) (this.xSize / 2 - this.font.getStringPropertyWidth(this.title) / 2), 5.0F, 4210752);
    }


}
