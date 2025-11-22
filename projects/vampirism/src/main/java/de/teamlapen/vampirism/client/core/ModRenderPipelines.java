package de.teamlapen.vampirism.client.core;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
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
            .withSampler("Sampler1")
            .withDepthTestFunction(DepthTestFunction.NO_DEPTH_TEST)
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .build();

    public static Supplier<RenderType> cutoutNoDepth() {
        return Suppliers.memoize(() -> {
            return RenderType.create(VResourceLocation.modString("cutout_no_depth"), 131072, true, false, CUTOUT_NO_DEPTH, RenderType.CompositeState.builder().setLightmapState(RenderStateShard.LIGHTMAP).setTextureState(RenderStateShard.BLOCK_SHEET).createCompositeState(true));
        });
    }

    public static Supplier<RenderType> solidTransparencyEntity() {
        return Suppliers.memoize(() -> {
            return RenderType.create(VResourceLocation.modString("solid_transparency_entity"), 256, false, true, SOLID_TRANSPARENCY_ENTITY, RenderType.CompositeState.builder().createCompositeState(true));
        });
    }

    public static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(GUI_TEXTURED_BLEND);
        event.registerPipeline(SOLID_TRANSPARENCY_ENTITY);
        event.registerPipeline(CUTOUT_NO_DEPTH);
    }

}
