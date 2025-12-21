package de.teamlapen.vampirism.client.core;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderSetup;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.function.Supplier;

public class ModRenderPipelines {

    public static final RenderPipeline GUI_TEXTURED_BLEND = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(VResourceLocation.mod("pipeline/gui_textured"))
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withBlend(BlendFunction.ADDITIVE)
            .build();

    public static final RenderPipeline SOLID_TRANSPARENCY_ENTITY = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(VResourceLocation.mod("pipeline/entity_solid"))
            .withSampler("Sampler1")
            .withBlend(BlendFunction.TRANSLUCENT) // TRANSLUCENT_PREMULTIPLIED_ALPHA
            .withCull(false)
            .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
            .build();

    public static final RenderPipeline CUTOUT_NO_DEPTH = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(VResourceLocation.mod("pipeline/entity_translucent"))
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .build();

    private static final RenderSetup CUTOUT_NO_DEPTH_SETUP = RenderSetup.builder(CUTOUT_NO_DEPTH)
            .useLightmap()
            .withTexture("Sampler0", TextureAtlas.LOCATION_BLOCKS, RenderTypes.MOVING_BLOCK_SAMPLER)
            .bufferSize(131071)
            .affectsCrumbling()
            .createRenderSetup();

    private static final RenderSetup SOLID_TRANSPARENCY_ENTITY_SETUP = RenderSetup.builder(SOLID_TRANSPARENCY_ENTITY)
            .bufferSize(256)
            .sortOnUpload()
            .createRenderSetup();


    public static Supplier<RenderType> cutoutNoDepth() {
        return Suppliers.memoize(() -> RenderType.create(VResourceLocation.modString("cutout_no_depth"), CUTOUT_NO_DEPTH_SETUP));
    }

    public static Supplier<RenderType> solidTransparencyEntity() {
        return Suppliers.memoize(() -> RenderType.create(VResourceLocation.modString("solid_transparency_entity"), SOLID_TRANSPARENCY_ENTITY_SETUP));
    }

    public static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(GUI_TEXTURED_BLEND);
        event.registerPipeline(SOLID_TRANSPARENCY_ENTITY);
        event.registerPipeline(CUTOUT_NO_DEPTH);
    }

}
