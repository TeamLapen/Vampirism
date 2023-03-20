package de.teamlapen.vampirism.client.gui.screens.taskboard;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.client.gui.screens.ExtendedScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TaskBoardScreen extends AbstractContainerScreen<TaskBoardMenu> implements ExtendedScreen {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = new ResourceLocation(REFERENCE.MODID, "textures/gui/taskmaster.png");
    private final IFactionPlayer<?> factionPlayer;

    private TaskList list;


    public TaskBoardScreen(@NotNull TaskBoardMenu container, @NotNull Inventory playerInventory, @NotNull Component containerName) {
        super(container, playerInventory, containerName);
        this.imageWidth = 176;
        this.imageHeight = 181;
        this.factionPlayer = FactionPlayerHandler.getCurrentFactionPlayer(playerInventory.player).orElseThrow(() -> new IllegalStateException("Can't open container without faction"));
        this.menu.setReloadListener(() -> this.list.updateContent());
    }

    @Override
    public @NotNull ItemRenderer getItemRenderer() {
        return this.itemRenderer;
    }

    @Override
    public @NotNull TaskMenu getTaskContainer() {
        return this.menu;
    }

    @Override
    public void render(@NotNull PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        this.list.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected void init() {
        super.init();
        this.list = this.addRenderableWidget(new TaskList(Minecraft.getInstance(), this.menu, this.factionPlayer, this.leftPos + 8, this.topPos + 16, 145 + 10 + 5, 155, 21, this.menu::getVisibleTasks));
    }

    @Override
    protected void renderBg(@NotNull PoseStack mStack, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(mStack);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TASKMASTER_GUI_TEXTURE);
        blit(mStack, this.leftPos, this.topPos, 0, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(@NotNull PoseStack mStack, int mouseX, int mouseY) {
        this.font.draw(mStack, this.title, (float) (this.imageWidth / 2 - this.font.width(this.title) / 2), 5.0F, 4210752);
    }
}
