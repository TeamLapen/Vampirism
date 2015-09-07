package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.client.model.ModelBloodAltar1;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar1;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RendererBloodAltar1 extends VampirismTileEntitySpecialRenderer {

	// The model of your block
	private final ModelBloodAltar1 model;
	private final ResourceLocation texture;
	private final TileEntityBeaconRenderer fakeBeaconRenderer;

	public RendererBloodAltar1() {
		model = new ModelBloodAltar1();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltar1.png");
		fakeBeaconRenderer = new TileEntityBeaconRenderer();
	}



	@Override
	public void setRendererDispatcher(TileEntityRendererDispatcher p_147497_1_) {
		super.setRendererDispatcher(p_147497_1_);
		this.fakeBeaconRenderer.setRendererDispatcher(p_147497_1_);
	}


	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	public void renderTileEntity(TileEntity te, double x, double y, double z, float p5, int p_180535_9_) {
		TileEntityBloodAltar1 te1 = ((TileEntityBloodAltar1) te);
		model.setOccupied(te1.isOccupied());
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorld(), te.getPos());
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		Tessellator.getInstance().getWorldRenderer().setBrightness(5);
		Tessellator.getInstance().getWorldRenderer().setColorOpaque(0, 0, 0);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();

		if (te1.isActive()) {
			this.fakeBeaconRenderer.renderTileEntityAt(te1.getFakeBeacon(), x, y, z, p5,p_180535_9_);
		}
	}

}
