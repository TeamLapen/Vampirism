package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ArmorOfSwiftnessItem;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import de.teamlapen.vampirism.items.VampireRefinementItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Handles item render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModItemsRender {

    public static void registerColors() {
        ItemColors colors = Minecraft.getInstance().getItemColors();
        // Swiftness armor
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                return 10511680;
            } else {
                return switch (((ArmorOfSwiftnessItem) stack.getItem()).getVampirismTier()) {
                    case ENHANCED -> 0x007CFF;
                    case ULTIMATE -> 0x07F8FF;
                    default -> 0xFFF100;
                };
            }
        }, ModItems.armor_of_swiftness_feet_normal, ModItems.armor_of_swiftness_chest_normal, ModItems.armor_of_swiftness_head_normal, ModItems.armor_of_swiftness_legs_normal, ModItems.armor_of_swiftness_feet_enhanced, ModItems.armor_of_swiftness_chest_enhanced, ModItems.armor_of_swiftness_head_enhanced, ModItems.armor_of_swiftness_legs_enhanced, ModItems.armor_of_swiftness_feet_ultimate, ModItems.armor_of_swiftness_chest_ultimate, ModItems.armor_of_swiftness_head_ultimate, ModItems.armor_of_swiftness_legs_ultimate);
        //Crossbow arrow
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ((CrossbowArrowItem) stack.getItem()).getType().color;
            }
            return 0xFFFFFF;
        }, ModItems.crossbow_arrow_normal, ModItems.crossbow_arrow_vampire_killer, ModItems.crossbow_arrow_spitfire);
        colors.register((state, tintIndex) -> 0x1E1F1F, ModBlocks.vampire_spruce_leaves);
        colors.register((state, tintIndex) -> 0x2e0606, ModBlocks.bloody_spruce_leaves);
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                if (stack.getItem() instanceof VampireRefinementItem) {
                    IRefinementSet set = ((VampireRefinementItem) stack.getItem()).getRefinementSet(stack);
                    if (set != null) {
                        return set.getColor();
                    }
                }
            }
            return 0xFFFFFF;
        }, ModItems.amulet, ModItems.ring, ModItems.obi_belt);
    }
}
