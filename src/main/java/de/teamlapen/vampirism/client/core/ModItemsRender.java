package de.teamlapen.vampirism.client.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.lib.lib.util.InventoryRenderHelper;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.entity.player.hunter.HunterLevelingConf;
import de.teamlapen.vampirism.items.ItemBloodBottle;
import de.teamlapen.vampirism.items.ItemInjection;
import de.teamlapen.vampirism.items.ItemPureBlood;
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
        renderHelper.registerRenderAllMeta(ModItems.pureBlood, ItemPureBlood.COUNT);
        renderHelper.registerRenderAllMeta(ModItems.hunterIntel, HunterLevelingConf.instance().HUNTER_INTEL_COUNT, "normal");
        renderHelper.registerRender(ModItems.itemGarlic, "normal");
        renderHelper.registerRenderAllMeta(ModItems.injection, ItemInjection.META_COUNT);
        renderHelper.registerRender(ModItems.itemMedChair, "normal");
        renderHelper.registerRender(ModItems.pitchfork, "normal");
        renderHelper.registerRender(ModItems.basicCrossbow, "normal");
        renderHelper.registerRender(ModItems.crossbowArrow, "normal");
        renderHelper.registerRender(ModItems.basicDoubleCrossbow, "normal");
        renderHelper.registerRender(ModItems.enhancedCrossbow, "normal");
        renderHelper.registerRender(ModItems.enhancedDoubleCrossbow, "normal");
        renderHelper.registerRender(ModItems.stake, "normal");
        renderHelper.registerRender(ModItems.vampireBlood, "normal");
        renderHelper.registerRender(ModItems.bloodPotion, "normal");
    }
}
