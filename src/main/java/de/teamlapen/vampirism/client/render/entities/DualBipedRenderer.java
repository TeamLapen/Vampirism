package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Mob;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.Stream;


public abstract class DualBipedRenderer<T extends Mob, M extends HumanoidModel<T>> extends HumanoidMobRenderer<T, M> {
    private final M modelA;
    private final M modelB;

    private ResourceLocation currentTexture;

    public DualBipedRenderer(EntityRendererProvider.Context context, M modelBipedInA, M modelBipedInB, float shadowSize) {
        super(context, modelBipedInA, shadowSize);
        this.modelA = modelBipedInA;
        this.modelB = modelBipedInB;
    }

    @Nonnull
    @Override
    public ResourceLocation getTextureLocation(@Nonnull T entity) {
        return currentTexture != null ? currentTexture : super.getTextureLocation(entity);
    }

    @Override
    public final void render(@Nonnull T entityIn, float entityYaw, float partialTicks, @Nonnull PoseStack matrixStackIn, @Nonnull MultiBufferSource bufferIn, int packedLightIn) {
        Pair<ResourceLocation, Boolean> b = determineTextureAndModel(entityIn);
        this.currentTexture = b.getLeft();
        this.model = b.getRight() ? modelB : modelA;
        this.renderSelected(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * @return Sets of texture resource location and model selecting boolean (true->b, false ->a)
     */
    protected abstract Pair<ResourceLocation, Boolean> determineTextureAndModel(T entity);

    protected void renderSelected(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    /**
     * @return Array of texture and slim status
     */
    protected Pair<ResourceLocation, Boolean>[] separateSlimTextures(Stream<ResourceLocation> set) {
        return set.map(r -> {
            boolean b = r.getPath().endsWith("slim.png");
            return Pair.of(r, b);
        }).toArray((IntFunction<Pair<ResourceLocation, Boolean>[]>) Pair[]::new);
    }

    /**
     * Gather all available textures (.png) in the given directory and in MODID namespace
     *
     * @param dirPath  relative assets' path (no namespace)
     * @param required whether to throw an illegal state exception if none found
     * @return Array of texture and slim status
     */
    protected Pair<ResourceLocation, Boolean>[] gatherTextures(String dirPath, boolean required) {
        Collection<ResourceLocation> hunterTextures = new ArrayList<>(Minecraft.getInstance().getResourceManager().listResources(dirPath, s -> s.getPath().endsWith(".png")).keySet());
        Pair<ResourceLocation, Boolean>[] textures = separateSlimTextures(hunterTextures.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())));
        if (textures.length == 0 && required) {
            throw new IllegalStateException("Must have at least one hunter texture: " + REFERENCE.MODID + ":" + dirPath + "/texture.png");
        }
        return textures;
    }
}
