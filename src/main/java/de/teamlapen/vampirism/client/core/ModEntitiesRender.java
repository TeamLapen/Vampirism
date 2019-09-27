package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.client.render.RenderAreaParticleCloud;
import de.teamlapen.vampirism.client.render.entities.*;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.ConvertedCreatureEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedHorseEntity;
import de.teamlapen.vampirism.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.entity.hunter.*;
import de.teamlapen.vampirism.entity.special.DraculaHalloweenEntity;
import de.teamlapen.vampirism.entity.vampire.AdvancedVampireEntity;
import de.teamlapen.vampirism.entity.vampire.BasicVampireEntity;
import de.teamlapen.vampirism.entity.vampire.VampireBaronEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

/**
 * Handles entity render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModEntitiesRender {

    public static void registerEntityRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(BlindingBatEntity.class, BatRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GhostEntity.class, GhostRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ConvertedCreatureEntity.class, ConvertedCreatureRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ConvertedHorseEntity.class, ConvertedHorseRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BasicHunterEntity.class, BasicHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BasicVampireEntity.class, BasicVampireRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HunterTrainerEntity.class, HunterTrainerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(VampireBaronEntity.class, VampireBaronRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AdvancedHunterEntity.class, AdvancedHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AdvancedVampireEntity.class, AdvancedVampireRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ConvertedVillagerEntity.class, ConvertedVillagerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AggressiveVillagerEntity.class, HunterVillagerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CrossbowArrowEntity.class, CrossbowArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AreaParticleCloudEntity.class, RenderAreaParticleCloud::new);
        RenderingRegistry.registerEntityRenderingHandler(ThrowableItemEntity.class, manager -> new ThrowableItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(DraculaHalloweenEntity.class, DraculaHalloweenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DarkBloodProjectileEntity.class, DarkBloodProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SoulOrbEntity.class, manager -> new SoulOrbRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(DummyHunterTrainerEntity.class, DummyHunterTrainerRenderer::new);
    }
}
