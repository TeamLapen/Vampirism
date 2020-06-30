package de.teamlapen.vampirism.client.render.entities;

import com.google.common.base.Predicates;
import de.teamlapen.vampirism.client.model.VampireBaronModel;
import de.teamlapen.vampirism.client.render.LayerCloak;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class VampireBaronRenderer extends BipedRenderer<VampireBaronEntity, VampireBaronModel<VampireBaronEntity>> {

    private static final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID + ":textures/entity/vampire_baron.png");

    public VampireBaronRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new VampireBaronModel<>(), 0.5F);
        this.addLayer(new LayerCloak<>(this, texture, Predicates.alwaysTrue()));

    }

    @Override
    protected ResourceLocation getEntityTexture(VampireBaronEntity p_110775_1_) {
        return texture;
    }

}