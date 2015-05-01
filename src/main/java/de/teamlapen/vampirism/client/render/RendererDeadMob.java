package de.teamlapen.vampirism.client.render;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.teamlapen.vampirism.client.model.ModelPileOfBones;
import de.teamlapen.vampirism.entity.EntityDeadMob;
import de.teamlapen.vampirism.util.REFERENCE;

@SideOnly(Side.CLIENT)
public class RendererDeadMob extends Render {

	protected final ModelBase model;
	private final ResourceLocation texture_zombie = new ResourceLocation(REFERENCE.MODID + ":textures/entity/deadZombie.png");
	private final ResourceLocation texture_skeleton = new ResourceLocation(REFERENCE.MODID + ":textures/entity/deadSkeleton.png");

	RenderBlocks render = new RenderBlocks();

	public RendererDeadMob() {
		model = new ModelPileOfBones();
	}

	@Override
	public void doRender(Entity p_76986_1_, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		this.doRender((EntityDeadMob) p_76986_1_, p_76986_2_, p_76986_4_, p_76986_6_, p_76986_8_, p_76986_9_);

	}

	public void doRender(EntityDeadMob mob, double p_76986_2_, double p_76986_4_, double p_76986_6_, float p_76986_8_, float p_76986_9_) {
		GL11.glPushMatrix();
		GL11.glTranslatef((float) p_76986_2_, (float) p_76986_4_, (float) p_76986_6_);

		this.bindTexture(this.getEntityTexture(mob));
		model.render(mob, 0, 0, 0, 0, 0, 0.0625F);
		GL11.glPopMatrix();

	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return this.getEntityTexture((EntityDeadMob) entity);
	}

	protected ResourceLocation getEntityTexture(EntityDeadMob mob) {
		if ("Skeleton".equals(mob.getDeadMob())) {
			return this.texture_skeleton;
		}
		return this.texture_zombie;
	}

	@Override
	public boolean isStaticEntity() {
		return false;
	}

}
