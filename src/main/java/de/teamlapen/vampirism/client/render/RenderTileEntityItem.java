package de.teamlapen.vampirism.client.render;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderTileEntityItem implements IItemRenderer {
	TileEntitySpecialRenderer render;

	private TileEntity entity;
	private float rotation = 0;
	private float scale = 1;

	public RenderTileEntityItem(TileEntitySpecialRenderer render, TileEntity entity) {
		this.entity = entity;
		this.render = render;
	}

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if (type == IItemRenderer.ItemRenderType.ENTITY) {
			GL11.glTranslatef(-0.5F, 0.0F, -0.5F);
		} else {
			GL11.glScalef(scale, scale, scale);
		}
		GL11.glRotatef(rotation, 0, 1, 0);
		this.render.renderTileEntityAt(this.entity, 0.0D, 0.0D, 0.0D, 0.0F);
	}

	/**
	 * Rotates the item when rendering
	 *
	 * @param rot
	 * @return
	 */
	public RenderTileEntityItem setRotation(float rot) {
		rotation = rot;
		return this;
	}

	/**
	 * Scales the item if it rendered
	 *
	 * @param scale
	 * @return
	 */
	public RenderTileEntityItem setScale(float scale) {
		this.scale = scale;
		return this;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

}