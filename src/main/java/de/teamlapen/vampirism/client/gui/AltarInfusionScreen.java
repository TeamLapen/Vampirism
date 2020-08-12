package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.inventory.container.AltarInfusionContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AltarInfusionScreen extends ContainerScreen<AltarInfusionContainer> {

    private static final ResourceLocation altarGuiTextures = new ResourceLocation(REFERENCE.MODID, "textures/gui/altar4.png");

    public AltarInfusionScreen(AltarInfusionContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        super(inventorySlotsIn, playerInventory, name);
    }

    @Override
    public void render
            (MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        super.render
                (stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack stack, float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(altarGuiTextures);
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize) / 2;
        this.blit(stack, k, l, 0, 0, this.xSize, this.ySize);
    }

}