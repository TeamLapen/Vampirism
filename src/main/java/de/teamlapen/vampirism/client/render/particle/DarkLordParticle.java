package de.teamlapen.vampirism.client.render.particle;

import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

/**
 * Created by Max on 09.07.2015.
 */
public class DarkLordParticle extends EntityFX {

	private double radius;
	private double phy;
	private float alpha;
	private Entity entity;
	private double yoff;

	public DarkLordParticle(World world, Entity entity,boolean thePlayer) {
		super(world, entity.posX, entity.posY, entity.posZ);
		radius=0.7*(this.rand.nextFloat()+0.6F);
		phy=this.rand.nextFloat()*6;
		alpha=1*(this.rand.nextFloat()+0.5F);
		this.entity=entity;
		this.particleMaxAge=(int)(40*(this.rand.nextFloat()+0.5F));
		this.noClip=true;
		this.particleScale = (this.rand.nextFloat() * 0.5F + 0.5F);
		this.yoff=-entity.height*rand.nextFloat();
		if(thePlayer){
			yoff-=0.3F;
		}
		else{
			yoff+=entity.height;
		}
		Entity renderentity = FMLClientHandler.instance().getClient().getRenderViewEntity();
		int visibleDistance = 64;
		if (!FMLClientHandler.instance().getClient().gameSettings.fancyGraphics)
			visibleDistance = 32;
		if (renderentity.getDistance(this.posX, this.posY, this.posZ) > visibleDistance)
			this.phy=Math.PI*2D;
	}

	@Override
	public void func_180434_a(WorldRenderer worldRenderer, Entity player, float partT, float rotX, float rotXZ, float rotZ, float rotYZ, float rotXY) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, alpha);
		float baseX=0,baseY=0;
		float sx=32/(float)256,sy=32/(float)256;

		float x = (float)(this.prevPosX + (this.posX - this.prevPosX) * (double)partT - interpPosX);
		float y = (float)(this.prevPosY + (this.posY - this.prevPosY) * (double)partT - interpPosY);
		float z = (float)(this.prevPosZ + (this.posZ - this.prevPosZ) * (double)partT - interpPosZ);

		float s=this.particleScale*0.1F;
		worldRenderer.addVertexWithUV((double) (x - rotX *s - rotYZ*s), (double) (y - rotXZ *s), (double) (z - rotZ*s - rotXY *s), (double) baseX + sx, (double) baseY + sy);
		worldRenderer.addVertexWithUV((double) (x - rotX *s + rotYZ *s), (double) (y + rotXZ*s ), (double) (z - rotZ *s+ rotXY*s), (double) baseX + sx, (double) baseY);
		worldRenderer.addVertexWithUV((double) (x + rotX *s + rotYZ*s), (double) (y + rotXZ *s), (double) (z + rotZ *s+ rotXY *s), (double) baseX, (double) baseY);
		worldRenderer.addVertexWithUV((double) (x + rotX - rotYZ * s), (double) (y - rotXZ * s), (double) (z + rotZ * s - rotXY * s), (double) baseX, (double) baseY + sy);
	}

	public void onUpdate()
	{

		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		phy+=Math.PI*2D/(double)this.particleMaxAge;
		radius+=0.5D/(double)this.particleMaxAge;
		alpha*=0.95;
		if (this.particleAge++>particleMaxAge||entity.isDead)
		{
			this.setDead();
		}

		this.setPosition(entity.posX+Math.cos(phy)*radius,entity.posY+yoff,entity.posZ+Math.sin(phy)*radius);
	}
}
