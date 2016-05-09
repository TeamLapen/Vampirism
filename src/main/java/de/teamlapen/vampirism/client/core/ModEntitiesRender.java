package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.client.render.entities.*;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import de.teamlapen.vampirism.entity.hunter.EntityHunterVillager;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionBase;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles entity render registration
 */
public class ModEntitiesRender {

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerEntityRenderer();
                break;
            case INIT:

                break;
        }

    }


    private static void registerEntityRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBlindingBat.class, new IRenderFactory<EntityBlindingBat>() {
            @Override
            public Render<? super EntityBlindingBat> createRenderFor(RenderManager manager) {
                return new RenderBat(manager);
            }
        });//RenderBat::new
        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, new IRenderFactory<EntityGhost>() {
            @Override
            public Render<? super EntityGhost> createRenderFor(RenderManager manager) {
                return new RenderGhost(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityConvertedCreature.class, new IRenderFactory<EntityConvertedCreature>() {
            @Override
            public Render<? super EntityConvertedCreature> createRenderFor(RenderManager manager) {
                return new RenderConvertedCreature(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityBasicHunter.class, new IRenderFactory<EntityBasicHunter>() {
            @Override
            public Render<? super EntityBasicHunter> createRenderFor(RenderManager manager) {
                return new RenderBasicHunter(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityBasicVampire.class, new IRenderFactory<EntityBasicVampire>() {
            @Override
            public Render<? super EntityBasicVampire> createRenderFor(RenderManager manager) {
                return new RenderBasicVampire(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityHunterTrainer.class, new IRenderFactory<EntityHunterTrainer>() {
            @Override
            public Render<? super EntityHunterTrainer> createRenderFor(RenderManager manager) {
                return new RenderHunterTrainer(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityVampireBaron.class, new IRenderFactory<EntityVampireBaron>() {
            @Override
            public Render<? super EntityVampireBaron> createRenderFor(RenderManager manager) {
                return new RenderVampireBaron(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityVampireMinionBase.class, new IRenderFactory<EntityVampireMinionBase>() {
            @Override
            public Render<? super EntityVampireMinionBase> createRenderFor(RenderManager manager) {
                return new RenderVampireMinion(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedHunter.class, new IRenderFactory<EntityAdvancedHunter>() {
            @Override
            public Render<? super EntityAdvancedHunter> createRenderFor(RenderManager manager) {
                return new RenderAdvancedHunter(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedVampire.class, new IRenderFactory<EntityAdvancedVampire>() {
            @Override
            public Render<? super EntityAdvancedVampire> createRenderFor(RenderManager manager) {
                return new RenderAdvancedVampire(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityConvertedVillager.class, new IRenderFactory<EntityConvertedVillager>() {
            @Override
            public Render<? super EntityConvertedVillager> createRenderFor(RenderManager manager) {
                return new RenderConvertedVillager(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler(EntityHunterVillager.class, new IRenderFactory<EntityHunterVillager>() {
            @Override
            public Render<? super EntityHunterVillager> createRenderFor(RenderManager manager) {
                return new RenderHunterVillager(manager);
            }
        });
    }
}
