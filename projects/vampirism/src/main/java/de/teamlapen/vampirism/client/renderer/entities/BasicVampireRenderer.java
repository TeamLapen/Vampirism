package de.teamlapen.vampirism.client.renderer.entities;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.models.entities.ClothedModel;
import de.teamlapen.vampirism.common.world.entity.vampire.BasicVampireEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public class BasicVampireRenderer extends HumanoidMobRenderer<BasicVampireEntity, BasicVampireEntity.BasicVampireRenderState, HumanoidModel<BasicVampireEntity.BasicVampireRenderState>> {

    private final Identifier @NotNull [] textures;

    public BasicVampireRenderer(EntityRendererProvider.@NotNull Context context) {
        super(context, new ClothedModel<>(context.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        textures = Minecraft.getInstance().getResourceManager().listResources("textures/entity/vampire", s -> s.getPath().endsWith(".png")).keySet().stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())).toArray(Identifier[]::new);
    }

    @NotNull
    @Override
    public Identifier getTextureLocation(@NotNull BasicVampireEntity.BasicVampireRenderState entity) {
        return entity.texture;
    }

    public Identifier getVampireTexture(int entityId) {
        return textures[entityId % textures.length];
    }

    @Override
    public @NotNull BasicVampireEntity.BasicVampireRenderState createRenderState() {
        return new BasicVampireEntity.BasicVampireRenderState();
    }

    @Override
    public void extractRenderState(BasicVampireEntity entity, BasicVampireEntity.BasicVampireRenderState state, float p_363123_) {
        super.extractRenderState(entity, state, p_363123_);
        state.texture = getVampireTexture(entity.getEntityTextureType());
    }
}
