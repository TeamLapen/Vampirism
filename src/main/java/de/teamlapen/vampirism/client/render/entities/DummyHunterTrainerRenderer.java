package de.teamlapen.vampirism.client.render.entities;

import de.teamlapen.vampirism.entity.hunter.DummyHunterTrainerEntity;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DummyHunterTrainerRenderer extends BipedRenderer<DummyHunterTrainerEntity, PlayerModel<DummyHunterTrainerEntity>> {
    private final ResourceLocation texture = new ResourceLocation(REFERENCE.MODID, "textures/entity/hunter_trainer.png");

    public DummyHunterTrainerRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PlayerModel<>(0, false), 0.5F);
    }

    @Override
    protected ResourceLocation getEntityTexture(DummyHunterTrainerEntity entity) {
        return texture;
    }

    @Override
    protected void renderLivingLabel(DummyHunterTrainerEntity entityIn, String str, double x, double y, double z, int maxDistance) {
        super.renderLivingLabel(entityIn, str, x, y, z, maxDistance / 4);
    }
}
