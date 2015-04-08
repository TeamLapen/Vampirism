package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelBloodAltar;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererBloodAltar extends TileEntitySpecialRenderer {

	// The model of your block
	private final ModelBloodAltar model;
	private final ResourceLocation texture;
	private final TileEntityBeaconRenderer fakeBeaconRenderer;

	public RendererBloodAltar() {
		model = new ModelBloodAltar();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/blocks/bloodAltar.png");
		fakeBeaconRenderer=new TileEntityBeaconRenderer();
	}

	private void adjustRotatePivotViaMeta(World world, int x, int y, int z) {
		int meta = 2;
		if (world != null)
			meta = world.getBlockMetadata(x, y, z);
		GL11.glRotatef(meta * 90, 0.0F, 1.0F, 0.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float scale) {
		TileEntityBloodAltar te1=((TileEntityBloodAltar) te);
		model.setOccupied(te1.isOccupied());
		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		bindTexture(texture);
		GL11.glPushMatrix();
		adjustRotatePivotViaMeta(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F);
		Tessellator.instance.setBrightness(5);
		Tessellator.instance.setColorOpaque(0, 0, 0);
		model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();
		GL11.glPopMatrix();
		
		if(te1.isActive()){
			this.fakeBeaconRenderer.renderTileEntityAt(te1.getFakeBeacon(), x, y, z, scale);
		}
	}
	
	@Override
    public void func_147497_a(TileEntityRendererDispatcher dispatcher)
    {
        super.func_147497_a(dispatcher);
        this.fakeBeaconRenderer.func_147497_a(dispatcher);
    }
	
    @SideOnly(Side.CLIENT)
    public double getMaxRenderDistanceSquared()
    {
        return 65536.0D;
    }
}
