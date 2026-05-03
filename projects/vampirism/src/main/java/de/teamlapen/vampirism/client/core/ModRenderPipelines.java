package de.teamlapen.vampirism.client.core;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.renderer.blockentity.VelmorraPortalRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.*;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;

import java.util.function.Supplier;

public class ModRenderPipelines {

    public static final RenderPipeline GUI_TEXTURED_BLEND = RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
            .withLocation(VIdentifier.mod("pipeline/gui_textured"))
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .withColorTargetState(new ColorTargetState(BlendFunction.ADDITIVE))
            .build();

    public static final RenderPipeline.Snippet SOLID_TRANSPARENCY = RenderPipeline.builder(RenderPipelines.MATRICES_FOG_LIGHT_DIR_SNIPPET)
            .withVertexShader("core/entity")
            .withFragmentShader("core/entity")
            .withSampler("Sampler1")
            .withVertexFormat(DefaultVertexFormat.ENTITY, VertexFormat.Mode.QUADS)
            .buildSnippet();

    public static final RenderPipeline SOLID_TRANSPARENCY_ENTITY = RenderPipeline.builder(SOLID_TRANSPARENCY)
            .withLocation(VIdentifier.mod("pipeline/entity_solid"))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA))
            .withCull(true)
            .build();
    public static final RenderSetup SOLID = RenderSetup.builder(SOLID_TRANSPARENCY_ENTITY)
            .bufferSize(256)
            .useLightmap()
            .useOverlay()
            .sortOnUpload()
            .createRenderSetup();

    public static final RenderPipeline CUTOUT_NO_DEPTH = RenderPipeline.builder(RenderPipelines.ENTITY_SNIPPET)
            .withLocation(VIdentifier.mod("pipeline/entity_translucent"))
            .withSampler("Sampler0")
            .withSampler("Sampler2")
            .withShaderDefine("ALPHA_CUTOUT", 0.1F)
            .build();

    private static final RenderSetup CUTOUT_NO_DEPTH_SETUP = RenderTypes.createMovingBlockSetup(CUTOUT_NO_DEPTH, false);

    public static Supplier<RenderType> cutoutNoDepth() {
        return Suppliers.memoize(() -> RenderType.create(VIdentifier.modString("cutout_no_depth"), CUTOUT_NO_DEPTH_SETUP));
    }

    private static final RenderType ENTITY_TRANSPARENCY = RenderType.create(VIdentifier.modString("solid_transparency_entity"), SOLID);
    private static final RenderType BLOCK_AURA = RenderType.create(VIdentifier.modString("block_aura"), RenderSetup.builder(RenderPipelines.DEBUG_FILLED_BOX)
            .setLayeringTransform(LayeringTransform.VIEW_OFFSET_Z_LAYERING)
            .setOutputTarget(OutputTarget.OUTLINE_TARGET)
            .sortOnUpload()
            .bufferSize(256)
            .createRenderSetup());

    public static RenderType solidTransparencyEntity() {
        return ENTITY_TRANSPARENCY;
    }

    public static RenderType blockAura() {
        return BLOCK_AURA;
    }

    public static void registerRenderPipelines(RegisterRenderPipelinesEvent event) {
        event.registerPipeline(GUI_TEXTURED_BLEND);
        event.registerPipeline(SOLID_TRANSPARENCY_ENTITY);
        event.registerPipeline(CUTOUT_NO_DEPTH);
        event.registerPipeline(VELMORRA_PORTAL_PIPELINE);
    }

    public static final RenderPipeline.Snippet VELMORRA_PORTAL_SNIPPET = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withVertexShader(VIdentifier.mod("core/rendertype_velmorra_portal"))
            .withFragmentShader(VIdentifier.mod("core/rendertype_velmorra_portal"))
            .withSampler("Sampler0")
            .withVertexFormat(DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS)
            .withDepthStencilState(DepthStencilState.DEFAULT)
            .buildSnippet();
    public static final RenderPipeline VELMORRA_PORTAL_PIPELINE = RenderPipeline.builder(VELMORRA_PORTAL_SNIPPET).withLocation(VIdentifier.mod("pipeline/velmorra_portal")).build();
    public static final RenderType VELMORRA_PORTAL_RENDER_TYPE = RenderType.create(
            VIdentifier.modString("velmorra_portal"),
            RenderSetup.builder(VELMORRA_PORTAL_PIPELINE)
                    .withTexture("Sampler0", VelmorraPortalRenderer.PORTAL_LOCATION)
                    .createRenderSetup()
    );

}
