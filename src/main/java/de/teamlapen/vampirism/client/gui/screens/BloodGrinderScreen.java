package de.teamlapen.vampirism.client.gui.screens;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.inventory.BloodGrinderMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BloodGrinderScreen extends AbstractContainerScreen<BloodGrinderMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(REFERENCE.MODID, "textures/gui/container/blood_grinder.png");

    public BloodGrinderScreen(@NotNull BloodGrinderMenu inventorySlotsIn, @NotNull Inventory playerInventory, @NotNull Component name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float var1, int var2, int var3) {
        graphics.setColor(1, 1, 1, 1);
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        graphics.blitSprite(new ResourceLocation(REFERENCE.MODID, "container/grinder/progress_background"), this.leftPos + 80, this.topPos + 55, 16,16);

        if (this.menu.hasItem()) {
            int i = Minecraft.getInstance().levelRenderer.getTicks()/10 % 4;
            graphics.blitSprite(new ResourceLocation(REFERENCE.MODID, switch (i) {
                case 0 -> "container/grinder/progress_0";
                case 1 -> "container/grinder/progress_1";
                case 2 -> "container/grinder/progress_2";
                default -> "container/grinder/progress_3";
            }), this.leftPos + 80, this.topPos + 55, 16, 16);
        }
    }
}
