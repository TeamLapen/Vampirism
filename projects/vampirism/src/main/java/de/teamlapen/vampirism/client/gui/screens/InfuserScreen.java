package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.faction.client.gui.GuiRenderer;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.common.world.inventory.InfuserMenu;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class InfuserScreen extends AbstractContainerScreen<InfuserMenu> {

    private static final Identifier BACKGROUND = VIdentifier.mod("textures/gui/container/infuser.png");
    private static final Identifier BURN_PROGRESS_SPRITE = VIdentifier.mc("container/furnace/burn_progress");

    public InfuserScreen(InfuserMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, 176, 181);
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        GuiRenderer.blit(graphics, BACKGROUND, this.leftPos, this.topPos, this.imageWidth, this.imageHeight);

        int j = Mth.ceil(this.menu.getBurnProgress() * 24f);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BURN_PROGRESS_SPRITE, 24, 16, 0, 0, this.leftPos + 117, this.topPos + 32 + 9, j, 16);
    }
}
