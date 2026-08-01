package de.teamlapen.vampirism.client.core;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.config.ClientConfig;
import de.teamlapen.vampirism.client.renderer.blockentity.VelmorraPortalRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.*;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
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
        event.registerPipeline(BLOOD_SIPHON_PIPELINE);
        event.registerPipeline(MIST_LOW);
        event.registerPipeline(MIST_MEDIUM);
        event.registerPipeline(MIST_HIGH);
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

    /** Procedural blood streak beam between Dracula and his blood siphon victims. */
    public static final RenderPipeline BLOOD_SIPHON_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
            .withVertexShader(VIdentifier.mod("core/rendertype_blood_siphon"))
            .withFragmentShader(VIdentifier.mod("core/rendertype_blood_siphon"))
            .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.LESS_THAN_OR_EQUAL, false))
            .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
            .withCull(false)
            .withLocation(VIdentifier.mod("pipeline/blood_siphon"))
            .build();
    public static final RenderType BLOOD_SIPHON_RENDER_TYPE = RenderType.create(
            VIdentifier.modString("blood_siphon"),
            RenderSetup.builder(BLOOD_SIPHON_PIPELINE)
                    .sortOnUpload()
                    .createRenderSetup()
    );

    /**
     * Volumetric cloud raymarched on a camera-facing billboard while an entity is in mist form.
     * <p>
     * Unlike every other pipeline here this one is not wrapped in a {@link RenderType}: it is bound directly on a
     * custom render pass by {@link de.teamlapen.vampirism.client.renderer.MistRenderer}, because the shader needs
     * the scene depth texture bound as a sampler and {@link RenderSetup} can only bind textures registered with
     * the texture manager. That pass binds no depth attachment, hence the disabled depth test here - occlusion is
     * resolved in the shader against {@code DepthSampler} instead, which is what gives the soft edge.
     * <p>
     * The raymarch step count is a compile-time define, so each quality level gets its own pipeline and
     * {@link #mist(ClientConfig.MistQuality)} picks between them at draw time.
     */
    public static final RenderPipeline MIST_LOW = mistPipeline(ClientConfig.MistQuality.LOW);
    public static final RenderPipeline MIST_MEDIUM = mistPipeline(ClientConfig.MistQuality.MEDIUM);
    public static final RenderPipeline MIST_HIGH = mistPipeline(ClientConfig.MistQuality.HIGH);

    private static RenderPipeline mistPipeline(ClientConfig.MistQuality quality) {
        return RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
                .withVertexShader(VIdentifier.mod("core/rendertype_mist"))
                .withFragmentShader(VIdentifier.mod("core/rendertype_mist"))
                .withSampler("DepthSampler")
                .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
                // Deliberately no depth stencil state: RenderPipeline#wantsDepthTexture is simply "has a depth
                // stencil state", so setting one - even a never-testing, never-writing one - makes the command
                // encoder warn about the missing depth attachment this pass intentionally does not bind.
                .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                .withCull(false)
                .withShaderDefine("STEPS", quality.steps())
                .withLocation(VIdentifier.mod("pipeline/mist_" + quality.name().toLowerCase(Locale.ROOT)))
                .build();
    }

    /**
     * @return the mist pipeline for the given quality, or null when mist rendering is disabled.
     */
    public static @Nullable RenderPipeline mist(ClientConfig.MistQuality quality) {
        return switch (quality) {
            case OFF -> null;
            case LOW -> MIST_LOW;
            case MEDIUM -> MIST_MEDIUM;
            case HIGH -> MIST_HIGH;
        };
    }

}
