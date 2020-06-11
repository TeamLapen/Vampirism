package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.inventory.container.MinionContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinionScreen extends ContainerScreen<MinionContainer> {

    private static final ResourceLocation TABLE_GUI_TEXTURES = new ResourceLocation(REFERENCE.MODID, "textures/gui/weapon_table.png");


    public MinionScreen(MinionContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
        this.xSize = 196;
        this.ySize = 191;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.minecraft.getTextureManager().bindTexture(TABLE_GUI_TEXTURES);
    }
}
