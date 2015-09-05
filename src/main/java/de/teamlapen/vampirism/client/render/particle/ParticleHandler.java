package de.teamlapen.vampirism.client.render.particle;

import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Particle Renderer for Vampirism. Similar to @{@link EffectRenderer}
 */
public class ParticleHandler {
	private static ParticleHandler instance;
	private final static String TAG="ParticleHandler";
	private List[] fxlayers={new ArrayList<EntityFX>()};
	private int dimId;
	private final ResourceLocation particleTextures=new ResourceLocation(REFERENCE.MODID+":textures/particles.png");

	private ParticleHandler(){
		dimId=Integer.MIN_VALUE;
	}

	public void addEffect(EntityFX effect){
		int dim=effect.worldObj.provider.dimensionId;
		if(dim!=dimId){
			dimId=dim;
			Logger.w(TAG,"Failed to add %s. Dimension %d is not activated",effect,dim);
			return;
		}
		int la=effect.getFXLayer();
		if(la>fxlayers.length){
			Logger.w(TAG,"Failed to add %s. There is no %d layer",effect,la);
			return;
		}
		if(fxlayers[la].size()>2000){
			fxlayers[la].remove(0);
		}
		fxlayers[la].add(effect);
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void renderEffects(RenderWorldLastEvent event){
		EntityPlayer player=Minecraft.getMinecraft().thePlayer;
		float parTicks=event.partialTicks;
		TextureManager renderer = Minecraft.getMinecraft().renderEngine;
			float f1 = ActiveRenderInfo.rotationX;
			float f2 = ActiveRenderInfo.rotationZ;
			float f3 = ActiveRenderInfo.rotationYZ;
			float f4 = ActiveRenderInfo.rotationXY;
			float f5 = ActiveRenderInfo.rotationXZ;
			EntityFX.interpPosX = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double)parTicks;
			EntityFX.interpPosY = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double)parTicks;
			EntityFX.interpPosZ = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double)parTicks;

			for (int k = 0; k < fxlayers.length; ++k)
			{

				if (!this.fxlayers[k].isEmpty())
				{
					switch (k)
					{
					case 0:
					default:
						renderer.bindTexture(particleTextures);
						break;

					}

					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glDepthMask(false);
					GL11.glEnable(GL11.GL_BLEND);
					GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
					GL11.glAlphaFunc(GL11.GL_GREATER, 0.003921569F);
					Tessellator tessellator = Tessellator.instance;
					tessellator.startDrawingQuads();

					for (int j = 0; j < this.fxlayers[k].size(); ++j)
					{
						final EntityFX entityfx = (EntityFX)this.fxlayers[k].get(j);
						if (entityfx == null) continue;
						tessellator.setBrightness(entityfx.getBrightnessForRender(parTicks));

						try
						{
							entityfx.renderParticle(tessellator, parTicks, f1, f5, f2, f3, f4);
						}
						catch (Throwable throwable)
						{
							CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Rendering Particle");
							CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being rendered");
							crashreportcategory.addCrashSectionCallable("Particle", new Callable()
							{
								private static final String __OBFID = "CL_00000918";
								public String call()
								{
									return entityfx.toString();
								}
							});
							throw new ReportedException(crashreport);
						}
					}

					tessellator.draw();
					GL11.glDisable(GL11.GL_BLEND);
					GL11.glDepthMask(true);
					GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
				}
			}
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void updateEffects(TickEvent.ClientTickEvent event){
		if(event.side== Side.SERVER||event.phase== TickEvent.Phase.END)return;
		Minecraft mc= Minecraft.getMinecraft();
		if(mc.theWorld==null)return;
		int dim=mc.theWorld.provider.dimensionId;
		if(dim!=dimId){
			dimId=dim;
			clearEffects();
		}
		for(int i=0;i<fxlayers.length;i++){
			for (int j = 0; j < this.fxlayers[i].size(); ++j)
			{
				final EntityFX entityfx = (EntityFX)this.fxlayers[i].get(j);

				try
				{
					if (entityfx != null)
					{
						entityfx.onUpdate();
					}
				}
				catch (Throwable throwable)
				{
					CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Ticking Particle");
					CrashReportCategory crashreportcategory = crashreport.makeCategory("Particle being ticked");
					crashreportcategory.addCrashSectionCallable("Particle", new Callable()
					{
						private static final String __OBFID = "CL_00000916";
						public String call()
						{
							return entityfx.toString();
						}
					});
					throw new ReportedException(crashreport);
				}

				if (entityfx == null || entityfx.isDead)
				{
					this.fxlayers[i].remove(j--);
				}
			}

		}
	}
	public static ParticleHandler instance(){
		if(instance==null){
			instance=new ParticleHandler();
		}
		return instance;
	}

	public void clearEffects(){
		for(List l:fxlayers){
			l.clear();
		}
	}
}
