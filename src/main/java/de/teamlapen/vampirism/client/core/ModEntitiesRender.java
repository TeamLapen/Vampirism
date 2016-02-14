package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.client.render.RenderConvertedCreature;
import de.teamlapen.vampirism.client.render.RenderGhost;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityGhost;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles entity render registration
 */
public class ModEntitiesRender{

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                preInit((FMLPreInitializationEvent) event);
                break;
            case INIT:
                init((FMLInitializationEvent) event);
                break;
        }

    }

    private static void init(FMLInitializationEvent event) {

    }

    private static void preInit(FMLPreInitializationEvent event){
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
    }
}
