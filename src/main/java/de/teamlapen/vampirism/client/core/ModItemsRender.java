package de.teamlapen.vampirism.client.core;

import com.mojang.blaze3d.vertex.PoseStack;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.entity.player.refinement.IRefinementSet;
import de.teamlapen.vampirism.api.items.IArrowContainer;
import de.teamlapen.vampirism.api.items.IRefinementItem;
import de.teamlapen.vampirism.api.items.IVampirismCrossbow;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.items.CrossbowArrowItem;
import de.teamlapen.vampirism.items.component.OilContent;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import org.jetbrains.annotations.NotNull;

import java.util.stream.Stream;

/**
 * Handles item render registration
 */
public class ModItemsRender {

    public static void registerItemModelPropertyUnsafe() {
        Stream.of(ModItems.BASIC_CROSSBOW.get(),ModItems.BASIC_DOUBLE_CROSSBOW.get(),ModItems.ENHANCED_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(),ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get()).forEach(item -> {
            ItemProperties.register(item, new ResourceLocation(REFERENCE.MODID, "charged"), (stack, world, entity, tint) -> {
                return CrossbowItem.isCharged(stack) ? 0.0f : 1.0f;
            });
        });
        ItemProperties.register(ModItems.ARROW_CLIP.get(), new ResourceLocation(REFERENCE.MODID, "filled"), (stack, world, entity, tint) -> {
            return (float)((IArrowContainer) stack.getItem()).getArrows(stack).size()/(float)((IArrowContainer) stack.getItem()).getMaxArrows(stack);
        });
    }

    static void registerColors(RegisterColorHandlersEvent.@NotNull Item event) {
        // Swiftness armor
        event.register((stack, tintIndex) -> {
            return tintIndex > 0 ? -1 : DyedItemColor.getOrDefault(stack, -1);
        }, ModItems.ARMOR_OF_SWIFTNESS_FEET_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_NORMAL.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ENHANCED.get(), ModItems.ARMOR_OF_SWIFTNESS_FEET_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_CHEST_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_HEAD_ULTIMATE.get(), ModItems.ARMOR_OF_SWIFTNESS_LEGS_ULTIMATE.get());
        //Crossbow arrow
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return ((CrossbowArrowItem) stack.getItem()).getType().color;
            }
            return -1;
        }, ModItems.CROSSBOW_ARROW_NORMAL.get(), ModItems.CROSSBOW_ARROW_VAMPIRE_KILLER.get(), ModItems.CROSSBOW_ARROW_SPITFIRE.get(), ModItems.CROSSBOW_ARROW_TELEPORT.get());
        event.register((state, tintIndex) -> {
            return 0x1E1F1F;
        }, ModBlocks.DARK_SPRUCE_LEAVES.get());
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                if (stack.getItem() instanceof IRefinementItem) {
                    IRefinementSet set = ((IRefinementItem) stack.getItem()).getRefinementSet(stack);
                    if (set != null) {
                        return set.getColor() | 0xFF000000;
                    }
                }
            }
            return -1;
        }, ModItems.AMULET.get(), ModItems.RING.get(), ModItems.OBI_BELT.get());
        event.register((stack, tintIndex) -> {
            if (tintIndex == 1) {
                return OilContent.getOil(stack).value().getColor() | 0xFF000000;
            }
            return -1;
        }, ModItems.OIL_BOTTLE.get());
    }

    public static void registerItemDecorator(RegisterItemDecorationsEvent event) {
        Stream.of(ModItems.BASIC_CROSSBOW, ModItems.ENHANCED_CROSSBOW, ModItems.BASIC_DOUBLE_CROSSBOW, ModItems.ENHANCED_DOUBLE_CROSSBOW).forEach(item -> {
            event.register(item.get(), (graphics, font, stack, xOffset, yOffset) -> {
                ((IVampirismCrossbow) stack.getItem()).getAmmunition(stack).ifPresent(ammo -> {
                    PoseStack posestack = graphics.pose();
                    posestack.pushPose();
                    posestack.translate(xOffset, yOffset + 8, 0);
                    posestack.scale(0.5f, 0.5f, 0.5f);
                    graphics.renderItem(ammo.getDefaultInstance(), 0, 0);
                    posestack.popPose();
                });
                return false;
            });
        });
    }
}
