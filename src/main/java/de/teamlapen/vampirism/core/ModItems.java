package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.items.*;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handles all item registrations and reference.
 */
public class ModItems {

    public static ItemVampireFang vampireFang;
    public static ItemHumanHeart humanHeart;
    public static ItemHumanHeartWeak humanHeartWeak;
    public static ItemBloodBottle bloodBottle;
    public static ItemTent itemTent;
    public static ItemBattleAxe battleAxe;
    public static ItemCoffin itemCoffin;
    public static ItemPureBlood pureBlood;
    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerItems();
                break;
        }

    }

    private static void registerItems() {
        VampirismMod.log.d("ModItems", "Registering Items");
        vampireFang = registerItem(new ItemVampireFang());
        humanHeart = registerItem(new ItemHumanHeart());
        humanHeartWeak = registerItem(new ItemHumanHeartWeak());
        bloodBottle = registerItem(new ItemBloodBottle());
        itemTent = registerItem(new ItemTent());
        battleAxe = registerItem(new ItemBattleAxe());
        itemCoffin = registerItem(new ItemCoffin());
        pureBlood = registerItem(new ItemPureBlood());
    }

    private static <T extends Item> T registerItem(T item) {
        if (item.getRegistryName() == null) {
            throw new IllegalArgumentException("Missing registry name for " + item);
        }
        GameRegistry.registerItem(item);
        return item;
    }
}
