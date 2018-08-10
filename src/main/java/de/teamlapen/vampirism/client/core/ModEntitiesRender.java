package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.client.render.RenderAreaParticleCloud;
import de.teamlapen.vampirism.client.render.entities.*;
import de.teamlapen.vampirism.entity.*;
import de.teamlapen.vampirism.entity.converted.EntityConvertedCreature;
import de.teamlapen.vampirism.entity.converted.EntityConvertedVillager;
import de.teamlapen.vampirism.entity.hunter.EntityAdvancedHunter;
import de.teamlapen.vampirism.entity.hunter.EntityAggressiveVillager;
import de.teamlapen.vampirism.entity.hunter.EntityBasicHunter;
import de.teamlapen.vampirism.entity.hunter.EntityHunterTrainer;
import de.teamlapen.vampirism.entity.minions.vampire.EntityVampireMinionBase;
import de.teamlapen.vampirism.entity.special.EntityDraculaHalloween;
import de.teamlapen.vampirism.entity.vampire.EntityAdvancedVampire;
import de.teamlapen.vampirism.entity.vampire.EntityBasicVampire;
import de.teamlapen.vampirism.entity.vampire.EntityVampireBaron;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderBat;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Handles entity render registration
 */
@SideOnly(Side.CLIENT)
public class ModEntitiesRender {


    public static void registerEntityRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityBlindingBat.class, RenderBat::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityGhost.class, RenderGhost::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityConvertedCreature.class, RenderConvertedCreature::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBasicHunter.class, RenderBasicHunter::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityBasicVampire.class, RenderBasicVampire::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityHunterTrainer.class, RenderHunterTrainer::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityVampireBaron.class, RenderVampireBaron::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityVampireMinionBase.class, RenderVampireMinion::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedHunter.class, RenderAdvancedHunter::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityAdvancedVampire.class, RenderAdvancedVampire::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityConvertedVillager.class, RenderConvertedVillager::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityAggressiveVillager.class, RenderHunterVillager::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCrossbowArrow.class, RenderCrossbowArrow::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityAreaParticleCloud.class, RenderAreaParticleCloud::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityThrowableItem.class, manager -> new RenderThrowableItem(manager, Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(EntityDraculaHalloween.class, RenderSpecialDraculaHalloween::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityDarkBloodProjectile.class, RenderDarkBloodProjectile::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySoulOrb.class, manager -> new RenderSoulOrb(manager, Minecraft.getMinecraft().getRenderItem()));
    }
}
