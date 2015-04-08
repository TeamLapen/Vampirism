package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelBloodAltar3;
import de.teamlapen.vampirism.util.REFERENCE;

/**
 * 
 * @author Max
 *
 */
public class RendererBloodAltar3 extends TileEntitySpecialRenderer {

	private final ModelBloodAltar3 model;
	private final ResourceLocation texture;

	public RendererBloodAltar3() {
		model = new ModelBloodAltar3();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltar3.png");
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x, (float) y + 0.1F, (float) z);
		bindTexture(texture);
		model.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.05F);
		GL11.glPopMatrix();

	}

}
