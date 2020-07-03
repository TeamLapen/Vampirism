package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import de.teamlapen.vampirism.inventory.container.BloodGrinderContainer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BloodGrinderScreen extends ContainerScreen<BloodGrinderContainer> {

    private static final ResourceLocation background = new ResourceLocation(REFERENCE.MODID, "textures/gui/grinder.png");
    private final Container grinderContainer;

    public BloodGrinderScreen(BloodGrinderContainer inventorySlotsIn, PlayerInventory playerInventory, ITextComponent name) {
        super(inventorySlotsIn, playerInventory, name);
        this.grinderContainer = inventorySlotsIn;
    }

    @Override
    protected void func_230450_a_(MatrixStack stack, float var1, int var2, int var3) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(background);
        int k = (this.field_230708_k_ - this.xSize) / 2;
        int l = (this.field_230709_l_ - this.ySize) / 2;
        this.func_238474_b_(stack, k, l, 0, 0, this.xSize, this.ySize);
    }


}
