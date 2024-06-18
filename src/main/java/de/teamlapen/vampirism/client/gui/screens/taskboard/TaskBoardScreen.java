package de.teamlapen.vampirism.client.gui.screens.taskboard;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.client.gui.screens.ExtendedScreen;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.inventory.TaskMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class TaskBoardScreen extends AbstractContainerScreen<TaskBoardMenu> implements ExtendedScreen {
    private static final ResourceLocation TASKMASTER_GUI_TEXTURE = VResourceLocation.mod("textures/gui/taskmaster.png");
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
    public void resize(Minecraft pMinecraft, int pWidth, int pHeight) {
        super.resize(pMinecraft, pWidth, pHeight);
        this.list.updateContent();
    }

    @Override
    public @NotNull TaskMenu getTaskContainer() {
        return this.menu;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseDragged(double pMouseX, double pMouseY, int pButton, double pDragX, double pDragY) {
        this.list.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
        return super.mouseDragged(pMouseX, pMouseY, pButton, pDragX, pDragY);
    }

    @Override
    protected void init() {
        super.init();
        this.list = this.addRenderableWidget(new TaskList(Minecraft.getInstance(), this.menu, this.factionPlayer, this.leftPos + 8, this.topPos + 16, 145 + 10 + 5, 155, this.menu::getVisibleTasks));
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        graphics.blit(TASKMASTER_GUI_TEXTURE, this.leftPos, this.topPos, 0, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    protected void renderLabels(@NotNull GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(this.font, this.title, this.imageWidth / 2 - this.font.width(this.title) / 2, 5, 4210752, false);
    }
}
