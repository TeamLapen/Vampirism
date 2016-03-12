package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.common.event.FMLStateEvent;

/**
 * Handles item render registration
 */
public class ModItemsRender {

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerRenderers();
                break;
        }

    }

    private static void registerRenderers() {
        VampirismMod.log.d("ModItemsRender", "Registering renderer");
        InventoryRenderHelper renderHelper = new InventoryRenderHelper(REFERENCE.MODID);
        renderHelper.registerRender(ModItems.vampireFang, "normal");
        renderHelper.registerRender(ModItems.humanHeart, "normal");
        renderHelper.registerRender(ModItems.humanHeartWeak, "normal");
        renderHelper.registerRender(ModItems.itemTent, "normal");
        renderHelper.registerRenderAllMeta(ModItems.bloodBottle, ItemBloodBottle.AMOUNT + 1);
        renderHelper.registerRender(ModItems.battleAxe, "normal");
        renderHelper.registerRender(ModItems.itemCoffin, "normal");

    }
}
