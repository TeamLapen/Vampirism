package de.teamlapen.vampirism.client.render.entities;

import com.mojang.blaze3d.matrix.MatrixStack;
import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.BipedRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.Stream;


public abstract class DualBipedRenderer<T extends MobEntity, M extends BipedModel<T>> extends BipedRenderer<T, M> {
    private final M modelA;
    private final M modelB;

    private ResourceLocation currentTexture;

    public DualBipedRenderer(EntityRendererManager renderManagerIn, M modelBipedInA, M modelBipedInB, float shadowSize) {
        super(renderManagerIn, modelBipedInA, shadowSize);
        this.modelA = modelBipedInA;
        this.modelB = modelBipedInB;
    }

    @Override
    public ResourceLocation getEntityTexture(T entity) {
        return currentTexture != null ? currentTexture : super.getEntityTexture(entity);
    }

    @Override
    public final void render(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Pair<ResourceLocation, Boolean> b = determineTextureAndModel(entityIn);
        this.currentTexture = b.getLeft();
        this.entityModel = b.getRight() ? modelB : modelA;
        this.renderSelected(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    protected abstract Pair<ResourceLocation, Boolean> determineTextureAndModel(T entity);

    protected void renderSelected(T entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
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
     * @param dirPath relative assets path (no namespace)
     * @param required whether to throw an illegal state exception if none found
     * @return Array of texture and slim status
     */
    protected Pair<ResourceLocation, Boolean>[] gatherTextures(String dirPath, boolean required){
        Collection<ResourceLocation> hunterTextures = new ArrayList<>(Minecraft.getInstance().getResourceManager().getAllResourceLocations(dirPath, s -> s.endsWith(".png")));
        Pair<ResourceLocation, Boolean>[] textures =  separateSlimTextures(hunterTextures.stream().filter(r -> REFERENCE.MODID.equals(r.getNamespace())));
        if (textures.length == 0) {
            throw new IllegalStateException("Must have at least one hunter texture: "+REFERENCE.MODID+":"+dirPath+"texture.png");
        }
        return textures;
    }
}
