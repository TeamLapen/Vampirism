package de.teamlapen.vampirism.proxy;

import de.teamlapen.vampirism.core.*;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Abstract proxy base for both client and server.
 * Try to keep this quite empty and move larger code parts into dedicated classes.
 *
 * @author Maxanier
 */
public abstract class CommonProxy implements IProxy {
    @Override
    public void onInitStep(Step step, FMLStateEvent event) {
        ModFluids.onInitStep(step, event);
        ModPotions.onInitStep(step, event);
        ModSounds.onInitStep(step, event);
        ModBlocks.onInitStep(step, event);
        ModItems.onInitStep(step, event);
        ModBiomes.onInitStep(step, event);
        ModVillages.onInitStep(step, event);
        ModEntities.onInitStep(step, event);
    }


}
