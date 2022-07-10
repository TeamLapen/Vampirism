package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.util.OilUtils;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IDyeableArmorItem;
import net.minecraft.item.ItemModelsProperties;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.stream.Stream;

/**
 * Handles item render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModItemsRender {

    public static void registerItemModelPropertyUnsafe() {
        Stream.of(ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get()).forEach(item -> {
            ItemModelsProperties.register(item, new ResourceLocation(REFERENCE.MODID, "charged"), (stack, world, entity) -> {
                if (entity instanceof PlayerEntity && entity.getUseItem() != stack) {
                    float cooldown = ((PlayerEntity) entity).getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
                    if (cooldown > 0) {
                        return cooldown;
                    }
                    return VampirismItemCrossbow.hasAmmo((PlayerEntity) entity, stack) ? 0.0f : 1.0f;
                } else {
                    return 0.0f;
                }
            });
        });
    }

    public static void registerColorsUnsafe() {
        ItemColors colors = Minecraft.getInstance().getItemColors();
        // Swiftness armor
        colors.register((stack, tintIndex) -> {
            return tintIndex > 0 ? -1 : ((IDyeableArmorItem) stack.getItem()).getColor(stack);
        }, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get());
        //Crossbow arrow
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ((CrossbowArrowItem) stack.getItem()).getType().color;
            }
            return 0xFFFFFF;
        }, ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_TELEPORT.get());
        colors.register((state, tintIndex) -> {
            return 0x1E1F1F;
        }, ModBlocks.DARK_SPRUCE_LEAVES.get());
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
        }, ModItems.AMULET.get(), ModItems.RING.get(), ModItems.OBI_BELT.get());
        colors.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                IOil oil = OilUtils.getOil(stack);
                return oil.getColor();
            }
            return 0xFFFFFF;
        }, ModItems.OIL_BOTTLE.get());
    }
}
