package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ItemCrossbowArrow;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemArmor;
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
				return ((ItemArmor) stack.getItem()).getColor(stack);
			} else {
				switch (ModItems.armor_of_swiftness_feet.getVampirismTier(stack)) {
					case ENHANCED:
						return 0x007CFF;
					case ULTIMATE:
						return 0x07F8FF;
					default:
						return 0xFFF100;
				}
			}
		}, ModItems.armor_of_swiftness_feet, ModItems.armor_of_swiftness_chest, ModItems.armor_of_swiftness_head, ModItems.armor_of_swiftness_legs);
        Minecraft.getInstance().getItemColors().register((stack, tintIndex) -> {
			if (tintIndex == 1) {
				return ItemCrossbowArrow.getType(stack).color;
			}
			return 0xFFFFFF;
		}, ModItems.crossbow_arrow);
	}
}
