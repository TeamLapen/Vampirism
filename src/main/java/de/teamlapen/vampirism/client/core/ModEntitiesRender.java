package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.client.render.RenderAreaParticleCloud;
import de.teamlapen.vampirism.client.render.entities.*;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

import java.util.function.Supplier;

/**
 * Handles entity render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModEntitiesRender {

    public static void registerEntityRenderer(Supplier<Minecraft> minecraftSupplier) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.blinding_bat, BatRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_creature_imob, ConvertedCreatureRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_creature, ConvertedCreatureRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_horse, ConvertedHorseRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_sheep, ConvertedCreatureRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter, BasicHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_imob, BasicHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire, BasicVampireRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire_imob, BasicVampireRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_trainer, e -> new HunterTrainerRenderer(e,true));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire_baron, VampireBaronRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_hunter, AdvancedHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_hunter_imob, AdvancedHunterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_vampire, AdvancedVampireRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_vampire_imob, AdvancedVampireRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.villager_converted, ConvertedVillagerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.villager_angry, (renderManager) -> new HunterVillagerRenderer(renderManager, (IReloadableResourceManager) minecraftSupplier.get().getResourceManager()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.crossbow_arrow, CrossbowArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.particle_cloud, RenderAreaParticleCloud::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.throwable_item, manager -> new ThrowableItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.dark_blood_projectile, DarkBloodProjectileRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.soul_orb, manager -> new SoulOrbRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_trainer_dummy, e-> new HunterTrainerRenderer(e,false));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.dummy_creature, DummyRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire_minion, VampireMinionRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_minion, HunterMinionRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.task_master_vampire, VampireTaskMasterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.task_master_hunter, HunterTaskMasterRenderer::new);
    }
}
