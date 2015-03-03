package de.teamlapen.vampirism.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelTorch;


public class RendererTorch implements IItemRenderer
{
        protected ModelTorch model = new ModelTorch();

        public boolean handleRenderType(ItemStack var1, ItemRenderType var2)
        {
                switch (RenderHelper.newRender[var2.ordinal()])
                {
                        case 1:
                        return true;

                        default:
                        return false;
                }
        }

        public boolean shouldUseRenderHelper(ItemRenderType var1, ItemStack var2, ItemRendererHelper var3)
        {
                return false;
        }

        public void renderItem(ItemRenderType var1, ItemStack var2, Object ... var3)
        {
                switch (RenderHelper.newRender[var1.ordinal()])
                {
                        case 1:
                        GL11.glPushMatrix();
                        Minecraft.getMinecraft().renderEngine.bindTexture("/tutorial/textures/New3DItem.png");
                        
                        boolean var4 = false;

                        if (var3[1] != null && var3[1] instanceof EntityPlayer)
                        {
                                float var5;

                        if ((EntityPlayer)var3[1] == Minecraft.getMinecraft().renderViewEntity && Minecraft.getMinecraft().gameSettings.thirdPersonView == 0 && (!(Minecraft.getMinecraft().currentScreen instanceof GuiInventory) && !(Minecraft.getMinecraft().currentScreen instanceof GuiContainerCreative) || RenderManager.instance.playerViewY != 180.0F))
                        {
                                var4 = true;
                               var5 = 0.7F;
                               GL11.glTranslatef(1.0F, 0.7F, 0.6F);
                               GL11.glScalef(var5, var5, var5);
                               GL11.glRotatef(205.0F, 0.0F, 0.0F, 1.0F);
                               GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
                               GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
                        }
                        else
                        {
                                var5 = 0.3F;
                                GL11.glTranslatef(0.5F, 0.5F, 0.1F);
                                GL11.glScalef(var5, var5, var5);
                                GL11.glRotatef(190.0F, 0.0F, 0.0F, 1.0F);
                                GL11.glRotatef(100.0F, 0.0F, 1.0F, 0.0F);
                                GL11.glRotatef(-5.0F, 1.0F, 0.0F, 0.0F);
                        }
                }

                this.model.render((Entity)var3[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
                GL11.glPopMatrix();

                default:
                }
        }
}