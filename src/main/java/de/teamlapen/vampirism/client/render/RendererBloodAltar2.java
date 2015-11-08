package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelBloodAltar2;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

/**
 * 
 * @author Moritz
 *
 *
 */
@SideOnly(Side.CLIENT)
public class RendererBloodAltar2 extends VampirismTileEntitySpecialRenderer {

	// The model of your block
	private final ModelBloodAltar2 model;
	private final ResourceLocation texture;

	public RendererBloodAltar2() {
		model = new ModelBloodAltar2();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltar2.png");
	}


	@Override
	public void renderTileEntity(TileEntity te, double x, double y, double z, float scale, int p6) {
		TileEntityBloodAltar2 te2 = (TileEntityBloodAltar2) te;
		model.setBloodLevel(te2==null?0:((int) StrictMath.ceil(((float) te2.getBloodAmount()) / te2.getMaxBlood() * 15)));
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		model.renderBase(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		model.renderBlood(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		model.renderSphere(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
	}

}
