package de.teamlapen.vampirism.client.core;

import com.google.common.base.Suppliers;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.DepthStencilState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.CompareOp;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.renderer.blockentity.VelmorraPortalRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.*;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.server.level.ParticleStatus;
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent;
import org.jspecify.annotations.NonNull;

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
        event.registerPipeline(ITEM_CRUMBLING_PIPELINE);
        event.registerPipeline(VELMORRA_PORTAL_PIPELINE);
        event.registerPipeline(BLOOD_SIPHON_PIPELINE);
        event.registerPipeline(MIST_LOW);
        event.registerPipeline(MIST_MEDIUM);
        event.registerPipeline(MIST_HIGH);
        event.registerPipeline(AURA_OF_DARKNESS_LOW);
        event.registerPipeline(AURA_OF_DARKNESS_MEDIUM);
        event.registerPipeline(AURA_OF_DARKNESS_HIGH);
    }

    /**
     * Vanilla's crumbling pipeline with the block breaking depth state swapped for the glint one: {@link CompareOp#EQUAL}
     * without depth only draws over the pixels the item itself has already drawn on, which masks the overlay to the
     * item shape so that it looks like the item is broken without a need to do that manually.
     */
    public static final RenderPipeline ITEM_CRUMBLING_PIPELINE = RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET)
            .withLocation(VIdentifier.mod("pipeline/item_crumbling"))
            .withVertexShader("core/rendertype_crumbling")
            .withFragmentShader("core/rendertype_crumbling")
            .withSampler("Sampler0")
            .withCull(false)
            .withColorTargetState(new ColorTargetState(new BlendFunction(SourceFactor.DST_COLOR, DestFactor.SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO)))
            .withVertexFormat(DefaultVertexFormat.BLOCK, VertexFormat.Mode.QUADS)
            .withDepthStencilState(new DepthStencilState(CompareOp.EQUAL, false))
            .build();
    private static final RenderType ITEM_CRUMBLING_RENDER_TYPE = RenderType.create(
            VIdentifier.modString("item_crumbling"),
            RenderSetup.builder(ITEM_CRUMBLING_PIPELINE)
                    .withTexture("Sampler0", ModelBakery.BREAKING_LOCATIONS.getLast())
                    .setOutline(RenderSetup.OutlineProperty.AFFECTS_OUTLINE)
                    .createRenderSetup()
    );

    public static RenderType itemCrumbling() {
        return ITEM_CRUMBLING_RENDER_TYPE;
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
     * {@link #mist(VolumetricQuality)} picks between them at draw time.
     */
    public static final RenderPipeline MIST_LOW = volumetricPipeline("mist", VolumetricQuality.LOW);
    public static final RenderPipeline MIST_MEDIUM = volumetricPipeline("mist", VolumetricQuality.MEDIUM);
    public static final RenderPipeline MIST_HIGH = volumetricPipeline("mist", VolumetricQuality.HIGH);

    /**
     * Thin raymarched shell drawn around entities carrying the aura of darkness effect. Shares everything but
     * see {@link de.teamlapen.vampirism.client.renderer.VolumetricBillboards}.
     */
    public static final RenderPipeline AURA_OF_DARKNESS_LOW = volumetricPipeline("aura_of_darkness", VolumetricQuality.LOW);
    public static final RenderPipeline AURA_OF_DARKNESS_MEDIUM = volumetricPipeline("aura_of_darkness", VolumetricQuality.MEDIUM);
    public static final RenderPipeline AURA_OF_DARKNESS_HIGH = volumetricPipeline("aura_of_darkness", VolumetricQuality.HIGH);

    /**
     * Builds a pipeline for a volume raymarched on a camera-facing billboard: a shared vertex shader expanding
     * the unit quad, the effect's own fragment shader, and the scene depth bound as a sampler.
     *
     * @param name    the effect's fragment shader, {@code core/rendertype_<name>}, and the pipeline's location
     * @param quality the raymarch step count, which is a compile-time define and so fixed per pipeline
     */
    private static RenderPipeline volumetricPipeline(String name, VolumetricQuality quality) {
        return RenderPipeline.builder(RenderPipelines.MATRICES_PROJECTION_SNIPPET, RenderPipelines.FOG_SNIPPET, RenderPipelines.GLOBALS_SNIPPET)
                .withVertexShader(VIdentifier.mod("core/volumetric_billboard"))
                .withFragmentShader(VIdentifier.mod("core/rendertype_" + name))
                .withSampler("DepthSampler")
                .withVertexFormat(DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS)
                // Deliberately no depth stencil state: RenderPipeline#wantsDepthTexture is simply "has a depth
                // stencil state", so setting one - even a never-testing, never-writing one - makes the command
                // encoder warn about the missing depth attachment this pass intentionally does not bind.
                .withColorTargetState(new ColorTargetState(BlendFunction.TRANSLUCENT))
                .withCull(false)
                .withShaderDefine("STEPS", quality.steps())
                .withLocation(VIdentifier.mod("pipeline/" + name + "_" + quality.name().toLowerCase(Locale.ROOT)))
                .build();
    }

    /**
     * @return the mist pipeline for the given quality, or null when mist rendering is disabled.
     */
    public static @NonNull RenderPipeline mist(VolumetricQuality quality) {
        return switch (quality) {
            case LOW -> MIST_LOW;
            case MEDIUM -> MIST_MEDIUM;
            case HIGH -> MIST_HIGH;
        };
    }

    /**
     * @return the aura of darkness pipeline for the given quality, or null when the aura is disabled.
     */
    public static @NonNull RenderPipeline auraOfDarkness(VolumetricQuality quality) {
        return switch (quality) {
            case LOW -> AURA_OF_DARKNESS_LOW;
            case MEDIUM -> AURA_OF_DARKNESS_MEDIUM;
            case HIGH -> AURA_OF_DARKNESS_HIGH;
        };
    }

    /**
     * Number of raymarch steps a volumetric shader takes. Each level is backed by its own pre-built pipeline,
     * since the step count is a compile-time shader define. Shared by every raymarched effect in the mod, which
     * is why the levels are named for quality rather than for any one of them.
     */
    public enum VolumetricQuality {
        LOW(12),
        MEDIUM(24),
        HIGH(40);

        private final int steps;

        VolumetricQuality(int steps) {
            this.steps = steps;
        }

        public int steps() {
            return this.steps;
        }

        public static VolumetricQuality of(ParticleStatus status) {
            return switch (status) {
                case MINIMAL ->  VolumetricQuality.LOW;
                case DECREASED ->   VolumetricQuality.MEDIUM;
                default ->  VolumetricQuality.HIGH;
            };
        }
    }

}
