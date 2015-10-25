package de.teamlapen.vampirism.client.render;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelVHVillager;
import de.teamlapen.vampirism.client.model.ModelVampireHunter;
import de.teamlapen.vampirism.entity.EntityVampireHunter;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class VampireHunterRenderer extends RenderBiped {

	protected static class RendererCustomVillager extends RenderLiving {

		private static final ResourceLocation villagerTexture = new ResourceLocation("textures/entity/villager/villager.png");

		public RendererCustomVillager(ModelBase p_i1262_1_, float p_i1262_2_) {
			super(p_i1262_1_, p_i1262_2_);
		}

		@Override
		protected ResourceLocation getEntityTexture(Entity p_110775_1_) {
			return villagerTexture;
		}

		@Override
		protected void preRenderCallback(EntityLivingBase p_77041_1_, float p_77041_2_) {
			float f1 = 0.9375F;

			GL11.glScalef(f1, f1, f1);
		}

	}


	private static final ResourceLocation textureBase1 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireHunterBase1.png");
	private static final ResourceLocation textureBase4 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireHunterBase4.png");
	private static final ResourceLocation textureExtra = new ResourceLocation(REFERENCE.MODID, "textures/entity/vampireHunterExtra.png");

	private final RendererCustomVillager rendererVillager;

	public VampireHunterRenderer() {
		super(new ModelVampireHunter(), 0.5F);
		rendererVillager = new RendererCustomVillager(new ModelVHVillager(0.0F), 0.0F);
	}

	@Override
	public void doRender(EntityLiving entity, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		int level = ((EntityVampireHunter) entity).getLevel();
		if (level == 1) {
			rendererVillager.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);
			return;
		}
		super.doRender(entity, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);

	}

	@Override
	protected void renderModel(EntityLivingBase entity, float p_77036_2_, float p_77036_3_, float p_77036_4_, float p_77036_5_, float p_77036_6_, float f) {
		int level = ((EntityVampireHunter) entity).getLevel();
		boolean second = entity.getEntityId() % 2 == 1;
		if (second) {
			((ModelVampireHunter) modelBipedMain).setSkipCloakOnce();
		}
		super.renderModel(entity, p_77036_2_, p_77036_3_, p_77036_4_, p_77036_5_, p_77036_6_, f);
		bindTexture(textureExtra);
		((ModelVampireHunter) modelBipedMain).renderHat(f, second ? 3 : 1);
		if (level == 3 || level == 4) {
			((ModelVampireHunter) modelBipedMain).renderWeapons(f);
		}
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return entity.getEntityId() % 2 == 0 ? textureBase1 : textureBase4;
	}

	@Override
	public void setRenderManager(RenderManager manager) {
		super.setRenderManager(manager);
		rendererVillager.setRenderManager(manager);
	}

}
