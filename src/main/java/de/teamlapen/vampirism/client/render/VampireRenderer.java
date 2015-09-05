package de.teamlapen.vampirism.client.render;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class VampireRenderer extends RenderBiped {

	private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire.png");
	private static final ResourceLocation texture1 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire1.png");
	private static final ResourceLocation texture2 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire2.png");
	private static final ResourceLocation texture3 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire3.png");
	private static final ResourceLocation texture4 = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire4.png");

	public static ResourceLocation getTexture(int i) {
		switch (i) {
		case 0:
			return texture1;
		case 1:
			return texture2;
		case 2:
			return texture3;
			case 3:
				return texture4;
		}
		return texture;
	}

	public VampireRenderer(float p_i1261_2_) {
		super(new ModelBiped(), p_i1261_2_);
	}

	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		int i = entity.getEntityId() % 5;
		return getTexture(i);
	}

}
