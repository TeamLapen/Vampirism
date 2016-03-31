package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.client.model.ModelVampireBaron;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderVampireBaron extends RenderBiped<EntityVampireBaron> {

    private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampireBaron.png");

    public RenderVampireBaron(RenderManager renderManagerIn) {
        super(renderManagerIn, new ModelVampireBaron(), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityVampireBaron p_110775_1_) {
        return texture;
    }

}