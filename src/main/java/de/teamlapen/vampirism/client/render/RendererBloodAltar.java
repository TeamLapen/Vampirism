package de.teamlapen.vampirism.client.render;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelBloodAltar;
import de.teamlapen.vampirism.util.REFERENCE;

public class RendererBloodAltar extends TileEntitySpecialRenderer {
	
        //The model of your block
        private final ModelBloodAltar model;
        
        public RendererBloodAltar() {
                this.model = new ModelBloodAltar();
        }
        
        private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
                int meta = world.getBlockMetadata(x, y, z);
                GL11.glPushMatrix();
                GL11.glRotatef(meta * (-90), 0.0F, 0.0F, 1.0F);
                GL11.glPopMatrix();
        }
        
        @Override
        public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
        //The PushMatrix tells the renderer to "start" doing something.
                GL11.glPushMatrix();
        //This is setting the initial location.
                GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
        //This is the texture of your block. It's pathed to be the same place as your other blocks here.
                //Outdated bindTextureByName("/mods/roads/textures/blocks/TrafficLightPoleRed.png");
       //Use in 1.6.2  this
                ResourceLocation textures = (new ResourceLocation(REFERENCE.MODID + ":textures/block/bloodAltar.png")); 
        //the ':' is very important
        //binding the textures
                Minecraft.getMinecraft().renderEngine.bindTexture(textures);

        //This rotation part is very important! Without it, your model will render upside-down! And for some reason you DO need PushMatrix again!                       
                GL11.glPushMatrix();
                GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
        //A reference to your Model file. Again, very important.
                this.model.render((Entity)null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        //Tell it to stop rendering for both the PushMatrix's
                GL11.glPopMatrix();
                GL11.glPopMatrix();
        }

        //Set the lighting stuff, so it changes it's brightness properly.       
        private void adjustLightFixture(World world, int i, int j, int k, Block block) {
                Tessellator tess = Tessellator.instance;
                //float brightness = block.getBlockBrightness(world, i, j, k);
                //As of MC 1.7+ block.getBlockBrightness() has become block.getLightValue():
                float brightness = block.getLightValue(world, i, j, k);
                int skyLight = world.getLightBrightnessForSkyBlocks(i, j, k, 0);
                int modulousModifier = skyLight % 65536;
                int divModifier = skyLight / 65536;
                tess.setColorOpaque_F(brightness, brightness, brightness);
                OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit,  modulousModifier,  divModifier);
        }
}
