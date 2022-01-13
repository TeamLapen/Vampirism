package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.client.render.RenderAreaParticleCloud;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.client.render.entities.*;
import de.teamlapen.vampirism.client.render.layers.VampireEntityLayer;
import de.teamlapen.vampirism.core.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.BatRenderer;
import net.minecraft.client.renderer.entity.HorseRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IReloadableResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import org.apache.logging.log4j.LogManager;

import java.util.function.Supplier;

/**
 * Handles entity render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModEntitiesRender {

    public static void registerEntityRenderer(Supplier<Minecraft> minecraftSupplier) {
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.blinding_bat, safeFactory(BatRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_creature_imob, safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_creature, safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_horse, renderingManager -> {
            HorseRenderer renderer = new HorseRenderer(renderingManager);
            renderer.addLayer(new VampireEntityLayer<>(renderer, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png"), false));
            return renderer;
        });
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_donkey, safeFactory(ConvertedChestedHorseRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_mule, safeFactory(ConvertedChestedHorseRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_sheep, safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.converted_cow, safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter, safeFactory(BasicHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_imob, safeFactory(BasicHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire, safeFactory(BasicVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire_imob, safeFactory(BasicVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_trainer, e -> new HunterTrainerRenderer(e, true));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire_baron, safeFactory(VampireBaronRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_hunter, safeFactory(AdvancedHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_hunter_imob, safeFactory(AdvancedHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_vampire, safeFactory(AdvancedVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.advanced_vampire_imob, safeFactory(AdvancedVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.villager_converted, safeFactory(ConvertedVillagerRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.villager_angry, (renderManager) -> new HunterVillagerRenderer(renderManager, (IReloadableResourceManager) minecraftSupplier.get().getResourceManager()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.crossbow_arrow, safeFactory(CrossbowArrowRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.particle_cloud, safeFactory(RenderAreaParticleCloud::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.throwable_item, manager -> new ThrowableItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.dark_blood_projectile, safeFactory(DarkBloodProjectileRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.soul_orb, manager -> new SoulOrbRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_trainer_dummy, e -> new HunterTrainerRenderer(e, false));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.dummy_creature, safeFactory(DummyRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.vampire_minion, safeFactory(VampireMinionRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.hunter_minion, safeFactory(HunterMinionRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.task_master_vampire, safeFactory(VampireTaskMasterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.task_master_hunter, safeFactory(HunterTaskMasterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.dummy_sit_entity, safeFactory(DummyRenderer::new));
    }

    private static <T extends Entity> IRenderFactory<? super T> safeFactory(IRenderFactory<? super T> f) {
        return (IRenderFactory<T>) manager -> {
            try {
                return f.createRenderFor(manager);
            } catch (Exception e) {
                LogManager.getLogger().error("Failed to instantiate entity renderer", e);
                System.exit(0);
                throw e;
            }
        };
    }
}
