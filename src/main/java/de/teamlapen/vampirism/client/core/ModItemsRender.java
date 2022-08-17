package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.items.oil.IOil;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import de.teamlapen.vampirism.items.VampirismItemCrossbow;
import de.teamlapen.vampirism.util.OilUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Handles item render registration
 */
@OnlyIn(Dist.CLIENT)
public class ModItemsRender {

    public static void registerItemModelPropertyUnsafe() {
        Stream.of(ModItems.BASIC_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get()).forEach(item -> {
            ItemProperties.register(item, new ResourceLocation(REFERENCE.MODID, "charged"), (stack, world, entity, value) -> {
                if (entity instanceof Player player && entity.getUseItem() != stack) {
                    float cooldown = player.getCooldowns().getCooldownPercent(stack.getItem(), Minecraft.getInstance().getFrameTime());
                    if (cooldown > 0) {
                        return cooldown;
                    }
                    return VampirismItemCrossbow.hasAmmo(player, stack) ? 0.0f : 1.0f;
                } else {
                    return 0.0f;
                }
            });
        });
    }

    public static void registerColors(RegisterColorHandlersEvent.@NotNull Item event) {
        // Swiftness armor
        event.register((stack, tintIndex) -> {
            return tintIndex > 0 ? -1 : ((DyeableLeatherItem)stack.getItem()).getColor(stack);
        }, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get());
        //Crossbow arrow
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ((CrossbowArrowItem) stack.getItem()).getType().color;
            }
            return 0xFFFFFF;
        }, ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_TELEPORT.get());
        event.register((state, tintIndex) -> {
            return 0x1E1F1F;
        }, ModBlocks.DARK_SPRUCE_LEAVES.get());
        event.register((stack, tintIndex) -> {
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
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                IOil oil = OilUtils.getOil(stack);
                return oil.getColor();
            }
            return 0xFFFFFF;
        }, ModItems.OIL_BOTTLE.get());
    }
}
