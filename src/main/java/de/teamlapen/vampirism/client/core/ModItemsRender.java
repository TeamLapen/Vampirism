package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemArmorOfSwiftness;
import de.teamlapen.vampirism.items.ItemCrossbowArrow;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Handles item render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModItemsRender {

	static void registerColors() {

		// Swiftness armor
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 0) {
                return -1;//TODO test if its right
			} else {
                switch (((ItemArmorOfSwiftness) stack.getItem()).getVampirismTier()) {
					case ENHANCED:
						return 0x007CFF;
					case ULTIMATE:
						return 0x07F8FF;
					default:
						return 0xFFF100;
				}
			}
        }, ModItems.armor_of_swiftness_feet_normal, ModItems.armor_of_swiftness_chest_normal, ModItems.armor_of_swiftness_head_normal, ModItems.armor_of_swiftness_legs_normal, ModItems.armor_of_swiftness_feet_enhanced, ModItems.armor_of_swiftness_chest_enhanced, ModItems.armor_of_swiftness_head_enhanced, ModItems.armor_of_swiftness_legs_enhanced, ModItems.armor_of_swiftness_feet_ultimate, ModItems.armor_of_swiftness_chest_ultimate, ModItems.armor_of_swiftness_head_ultimate, ModItems.armor_of_swiftness_legs_ultimate);
        //Crossbow arrow
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 1) {
                return ((ItemCrossbowArrow) stack.getItem()).getType().color;
			}
			return 0xFFFFFF;
        }, ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_vampire_killer, ModItems.crossbow_arrow_spitfire);
	}
}
