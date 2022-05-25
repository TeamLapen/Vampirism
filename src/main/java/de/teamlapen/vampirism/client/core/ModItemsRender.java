package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.ArmorOfSwiftnessItem;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Stream;

/**
 * Handles item render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModItemsRender {

    public static void registerItemModelPropertyUnsafe() {
        Stream.of(ModItems.basic_crossbow.get(), ModItems.basic_double_crossbow.get(), ModItems.enhanced_crossbow.get(), ModItems.enhanced_double_crossbow.get(), ModItems.basic_tech_crossbow.get(), ModItems.enhanced_tech_crossbow.get()).forEach(item -> {
            ItemProperties.register(item, new ResourceLocation(REFERENCE.MODID, "charged"), (stack, world, entity, value) -> {
                if (entity == null) {
                    return 0.0F;
                } else {
                    return entity.getUseItem() != stack && entity instanceof Player ? ((Player) entity).getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime()) : 0.0F;
                }
            });
        });
    }

    public static void registerColorsUnsafe() {
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
        }, ModItems.armor_of_swiftness_feet_normal.get(), ModItems.armor_of_swiftness_chest_normal.get(), ModItems.armor_of_swiftness_head_normal.get(), ModItems.armor_of_swiftness_legs_normal.get(), ModItems.armor_of_swiftness_feet_enhanced.get(), ModItems.armor_of_swiftness_chest_enhanced.get(), ModItems.armor_of_swiftness_head_enhanced.get(), ModItems.armor_of_swiftness_legs_enhanced.get(), ModItems.armor_of_swiftness_feet_ultimate.get(), ModItems.armor_of_swiftness_chest_ultimate.get(), ModItems.armor_of_swiftness_head_ultimate.get(), ModItems.armor_of_swiftness_legs_ultimate.get());
        //Crossbow arrow
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ((CrossbowArrowItem) stack.getItem()).getType().color;
            }
            return 0xFFFFFF;
        }, ModItems.crossbow_arrow_normal.get(), ModItems.crossbow_arrow_vampire_killer.get(), ModItems.crossbow_arrow_spitfire.get());
        colors.register((state, tintIndex) -> 0x1E1F1F, ModBlocks.vampire_spruce_leaves.get());
        colors.register((state, tintIndex) -> 0x2e0606, ModBlocks.bloody_spruce_leaves.get());
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                if (stack.getItem() instanceof IRefinementItem) {
                    IRefinementSet set = ((IRefinementItem) stack.getItem()).getRefinementSet(stack);
                    if (set != null) {
                        return set.getColor();
                    }
                }
            }
            return 0xFFFFFF;
        }, ModItems.amulet.get(), ModItems.ring.get(), ModItems.obi_belt.get());
    }
}
