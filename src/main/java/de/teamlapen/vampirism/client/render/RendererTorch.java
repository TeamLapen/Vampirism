package de.teamlapen.vampirism.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.opengl.GL11;

import de.teamlapen.vampirism.client.model.ModelTorch;
import de.teamlapen.vampirism.util.REFERENCE;

public class RendererTorch implements IItemRenderer {
	private ModelTorch model;
	private ResourceLocation texture;

	public RendererTorch() {
		model = new ModelTorch();
		texture = new ResourceLocation(REFERENCE.MODID + ":textures/items/itemTorch.png");
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		switch (type) {
		case EQUIPPED:
		case EQUIPPED_FIRST_PERSON:
			return true;
		default:
			return false;
		}
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		GL11.glPushMatrix();
		Minecraft.getMinecraft().renderEngine.bindTexture(texture);

		switch (type) {
		case EQUIPPED:
			GL11.glRotatef(140.0F, 0.0F, 0.0F, -1.0F);
			GL11.glTranslatef(-0.9F, 0.3F, -0.07F);
			GL11.glScalef(2.0F, 2.0F, 2.0F);
			model.render((Entity) data[1], 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0625F);
			break;
		}
		GL11.glPopMatrix();
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType var1, ItemStack var2, ItemRendererHelper var3) {
		return false;
	}

}