package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.util.IInitListener;
import de.teamlapen.vampirism.client.render.RenderGhost;
import de.teamlapen.vampirism.entity.EntityBlindingBat;
import de.teamlapen.vampirism.entity.EntityGhost;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * Handles entity render registration
 */
public class ModEntitiesRender{
    public static void preInit(FMLPreInitializationEvent event) {

    }

    public static void init(FMLInitializationEvent event) {
        RenderManager manager= Minecraft.getMinecraft().getRenderManager();
        RenderingRegistry.registerEntityRenderingHandler(EntityBlindingBat.class,new RenderBat(manager));
        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class,new RenderGhost(manager));
    }
}
