package de.teamlapen.vampirism.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.gui.GuiUtils;


public class CooldownButton extends Button {
    private float progress = 1f;

    public CooldownButton(int x, int y, int width, int height, ITextComponent title, IPressable pressedAction) {
        super(x, y, width, height, title, pressedAction);
    }



    public void updateState(float progress){
        if(progress==0){
            this.active = true;
        }
        else{
            this.active = false;
        }
        this.progress = progress;
    }

    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        FontRenderer fontrenderer = minecraft.fontRenderer;
        minecraft.getTextureManager().bindTexture(WIDGETS_LOCATION);
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
        int i = this.getYImage(this.isHovered());
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, x ,y, 0, 46, this.width, this.height,  200,20,3,0);
        GuiUtils.drawContinuousTexturedBox(matrixStack, WIDGETS_LOCATION, x ,y, 0, this.active && this.isHovered() ? 86 : 66, (int)((1f-progress)*this.width), this.height,  200,20,3,0);
//        this.blit(matrixStack, this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
//        this.blit(matrixStack, this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
        this.renderBg(matrixStack, minecraft, mouseX, mouseY);
        int j = getFGColor();
        drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, j | MathHelper.ceil(this.alpha * 255.0F) << 24);

        if(isHovered()){
            renderToolTip(matrixStack, mouseX, mouseY);
        }
    }


}
