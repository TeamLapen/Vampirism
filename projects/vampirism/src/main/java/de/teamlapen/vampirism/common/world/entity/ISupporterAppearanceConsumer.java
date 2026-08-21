package de.teamlapen.vampirism.common.world.entity;

import de.teamlapen.vampirism.common.core.ModAttachments;
import de.teamlapen.vampirism.common.util.supporter.Supporter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public interface ISupporterAppearanceConsumer {

    default void applySupporter(LivingEntity entity, Supporter supporter) {
        entity.setData(ModAttachments.SUPPORTER, supporter);
        entity.setCustomName(supporter.name());
        Map<String, String> appearance = supporter.appearance();
        var mainHand = appearance.containsKey("main_hand") ? Identifier.tryParse(appearance.get("main_hand")) : null;
        var offHand = appearance.containsKey("off_hand") ? Identifier.tryParse(appearance.get("off_hand")) : null;
        var head = appearance.containsKey("head") ? Identifier.tryParse(appearance.get("head")) : null;
        if (mainHand != null) {
            BuiltInRegistries.ITEM.get(mainHand).ifPresent(x -> entity.setItemInHand(InteractionHand.MAIN_HAND, x.value().getDefaultInstance()));
        }
        if (offHand != null) {
            BuiltInRegistries.ITEM.get(offHand).ifPresent(x -> entity.setItemInHand(InteractionHand.OFF_HAND, x.value().getDefaultInstance()));
        }
        if (head != null) {
            BuiltInRegistries.ITEM.get(head).ifPresent(x -> entity.setItemSlot(EquipmentSlot.HEAD, x.value().getDefaultInstance()));
        }
    }
}
