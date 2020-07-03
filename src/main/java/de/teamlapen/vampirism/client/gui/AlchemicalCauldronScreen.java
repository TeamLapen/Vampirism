package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.inventory.container.AlchemicalCauldronContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


@OnlyIn(Dist.CLIENT)
public class AlchemicalCauldronScreen extends ContainerScreen<AlchemicalCauldronContainer> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation("vampirism:textures/gui/alchemical_cauldron.png");

    public AlchemicalCauldronScreen(AlchemicalCauldronContainer inventorySlotsIn, PlayerInventory inventoryPlayer, ITextComponent name) {
        super(inventorySlotsIn, inventoryPlayer, name);
    }

    @Override
    public void func_230430_a_
            (MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.func_230446_a_(stack);
        super.func_230430_a_
                (stack, mouseX, mouseY, partialTicks);
        this.func_230459_a_(stack, mouseX, mouseY);
    }

    @Override
    protected void func_230450_a_(MatrixStack stack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1, 1, 1, 1);
        this.field_230706_i_.getTextureManager().bindTexture(BACKGROUND);
        int i = (this.field_230708_k_ - this.xSize) / 2;
        int j = (this.field_230709_l_ - this.ySize) / 2;
        this.func_238474_b_(stack, i, j, 0, 0, this.xSize, this.ySize);

        int k = container.getBurnLeftScaled();
        if (k > 0) this.func_238474_b_(stack, i + 56, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);

        int l = container.getCookProgressionScaled();
        this.func_238474_b_(stack, i + 79, j + 34, 176, 14, l + 1, 16);
        l = l / 24 * 30;
        this.func_238474_b_(stack, i + 142, j + 28 + 30 - l, 176, 60 - l, 12, l);
    }

    @Override
    protected void func_230451_b_(MatrixStack stack, int mouseX, int mouseY) {
        ITextComponent name = new TranslationTextComponent("tile.vampirism.alchemical_cauldron.display", UtilLib.addFormatting(field_230706_i_.player.getDisplayName(), (TextFormatting.DARK_BLUE)), ModBlocks.alchemical_cauldron.func_235333_g_());
        this.field_230712_o_.func_238422_b_(stack, name, 5, 6.0F, 0x404040);
        this.field_230712_o_.func_238422_b_(stack, this.playerInventory.getDisplayName(), (float) this.field_238744_r_, (float) this.field_238745_s_, 4210752);
    }

}
