package de.teamlapen.vampirism.client.core;

import de.teamlapen.vampirism.api.world.items.IHunterCrossbow;
import de.teamlapen.vampirism.client.color.item.CrossbowArrowTint;
import de.teamlapen.vampirism.client.color.item.OilBottleTint;
import de.teamlapen.vampirism.client.extensions.ItemExtensions;
import de.teamlapen.vampirism.client.models.items.properties.BloodFilled;
import de.teamlapen.vampirism.client.models.items.properties.ClipFilled;
import de.teamlapen.vampirism.client.models.items.properties.HasName;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.util.ColorListsUtil;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterConditionalItemModelPropertyEvent;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3x2fStack;

import java.util.stream.Stream;

/**
 * Handles item render registration
 */
public class ModItemsRender {


    public static void registerColors(RegisterColorHandlersEvent.@NotNull ItemTintSources event) {
        event.register(CrossbowArrowTint.ID, CrossbowArrowTint.CODEC);
        event.register(OilBottleTint.ID, OilBottleTint.CODEC);
    }

    public static void registerRangeSelector(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(BloodFilled.ID, BloodFilled.CODEC);
        event.register(ClipFilled.ID, ClipFilled.CODEC);
    }

    public static void registerConditional(RegisterConditionalItemModelPropertyEvent event) {
        event.register(HasName.ID, HasName.CODEC);
    }

    public static void registerItemDecorator(RegisterItemDecorationsEvent event) {
        Stream.of(ModItems.BASIC_CROSSBOW, ModItems.ENHANCED_CROSSBOW, ModItems.BASIC_DOUBLE_CROSSBOW, ModItems.ENHANCED_DOUBLE_CROSSBOW).forEach(item -> {
            event.register(item.get(), (graphics, font, stack, xOffset, yOffset) -> {
                ((IHunterCrossbow) stack.getItem()).getAmmunition(stack).ifPresent(ammo -> {
                    Matrix3x2fStack posestack = graphics.pose();
                    posestack.translate(xOffset, yOffset + 8);
                    posestack.scale(0.5f);
                    graphics.renderItem(ammo.getDefaultInstance(), 0, 0);
                });
                return false;
            });
        });
    }

    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        ColorListsUtil.VAMPIRE_CLOAKS.values().forEach(cloak -> event.registerItem(ItemExtensions.VAMPIRE_CLOAK, cloak));
        event.registerItem(ItemExtensions.HUNTER_HAT, ModItems.HUNTER_HAT_TALL.get(), ModItems.HUNTER_HAT_BROAD.get());
        event.registerItem(ItemExtensions.VAMPIRE_CLOTHING, ModItems.VAMPIRE_CLOTHING_CROWN.get(), ModItems.VAMPIRE_CLOTHING_HAT.get(), ModItems.VAMPIRE_CLOTHING_LEGS.get(), ModItems.VAMPIRE_CLOTHING_BOOTS.get());
        event.registerItem(ItemExtensions.HUNTER_CROSSBOW, ModItems.BASIC_CROSSBOW.get(), ModItems.ENHANCED_CROSSBOW.get(), ModItems.BASIC_DOUBLE_CROSSBOW.get(), ModItems.ENHANCED_DOUBLE_CROSSBOW.get(), ModItems.BASIC_TECH_CROSSBOW.get(), ModItems.ENHANCED_TECH_CROSSBOW.get());
        event.registerItem(ItemExtensions.CRUCIFIX, ModItems.CRUCIFIX_NORMAL.get(), ModItems.CRUCIFIX_ENHANCED.get(), ModItems.CRUCIFIX_ULTIMATE.get());
    }
}
