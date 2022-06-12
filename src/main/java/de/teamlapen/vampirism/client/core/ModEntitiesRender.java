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
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.BLINDING_BAT.get(), safeFactory(BatRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_CREATURE_IMOB.get(), safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_CREATURE.get(), safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_HORSE.get(), renderingManager -> {
            HorseRenderer renderer = new HorseRenderer(renderingManager);
            renderer.addLayer(new VampireEntityLayer<>(renderer, new ResourceLocation(REFERENCE.MODID, "textures/entity/vanilla/horse_overlay.png"), false));
            return renderer;
        });
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_DONKEY.get(), safeFactory(ConvertedChestedHorseRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_MULE.get(), safeFactory(ConvertedChestedHorseRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_SHEEP.get(), safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CONVERTED_COW.get(), safeFactory(ConvertedCreatureRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.HUNTER.get(), safeFactory(BasicHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.HUNTER_IMOB.get(), safeFactory(BasicHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAMPIRE.get(), safeFactory(BasicVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAMPIRE_IMOB.get(), safeFactory(BasicVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.HUNTER_TRAINER.get(), e -> new HunterTrainerRenderer(e, true));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAMPIRE_BARON.get(), safeFactory(VampireBaronRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ADVANCED_HUNTER.get(), safeFactory(AdvancedHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ADVANCED_HUNTER_IMOB.get(), safeFactory(AdvancedHunterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ADVANCED_VAMPIRE.get(), safeFactory(AdvancedVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.ADVANCED_VAMPIRE_IMOB.get(), safeFactory(AdvancedVampireRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VILLAGER_CONVERTED.get(), safeFactory(ConvertedVillagerRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VILLAGER_ANGRY.get(), (renderManager) -> new HunterVillagerRenderer(renderManager, (IReloadableResourceManager) minecraftSupplier.get().getResourceManager()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.CROSSBOW_ARROW.get(), safeFactory(CrossbowArrowRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.PARTICLE_CLOUD.get(), safeFactory(RenderAreaParticleCloud::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.THROWABLE_ITEM.get(), manager -> new ThrowableItemRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.DARK_BLOOD_PROJECTILE.get(), safeFactory(DarkBloodProjectileRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.SOUL_ORB.get(), manager -> new SoulOrbRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.HUNTER_TRAINER_DUMMY.get(), e -> new HunterTrainerRenderer(e, false));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.DUMMY_CREATURE.get(), safeFactory(DummyRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.VAMPIRE_MINION.get(), safeFactory(VampireMinionRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.HUNTER_MINION.get(), safeFactory(HunterMinionRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.TASK_MASTER_VAMPIRE.get(), safeFactory(VampireTaskMasterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.TASK_MASTER_HUNTER.get(), safeFactory(HunterTaskMasterRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.DUMMY_SIT_ENTITY.get(), safeFactory(DummyRenderer::new));
        RenderingRegistry.registerEntityRenderingHandler(ModEntities.BOAT.get(), safeFactory(VampirismBoatRenderer::new));
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
