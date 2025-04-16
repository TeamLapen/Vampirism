package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.ItemPropertiesExtension;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModFactions;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all hunter only armor tileInventory
 */
public class HunterArmorItem extends ModArmorItem {

    public HunterArmorItem(@NotNull ArmorMaterial materialIn, @NotNull ArmorType type, @NotNull Properties props) {
        super(materialIn, type, FactionRestriction.builder(ModFactionTags.IS_HUNTER).apply(ItemPropertiesExtension.descriptionWithout(props, "_normal|_enhanced|_ultimate")));
    }
    public HunterArmorItem(@NotNull ArmorMaterial materialIn, @NotNull ArmorType type, @NotNull Properties props, ItemAttributeModifiers attributeModifiers) {
        super(materialIn, type, FactionRestriction.builder(ModFactionTags.IS_HUNTER).apply(ItemPropertiesExtension.descriptionWithout(props, "_normal|_enhanced|_ultimate")), attributeModifiers);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, Entity entity, int slotId, boolean isSelected) {
        if (entity.tickCount % 16 == 8 && slotId >= 36 && slotId <= 39 && entity instanceof Player player) {
            if (!ModFactions.HUNTER.match(VampirismPlayerAttributes.get(player).faction())) {
                player.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
            }
        }
    }

    @Override
    public boolean canEquip(@NotNull ItemStack stack, @NotNull EquipmentSlot armorType, @NotNull LivingEntity entity) {
        return super.canEquip(stack, armorType, entity) && FactionRestriction.canUse(entity, stack, true);
    }

    protected String getTextureLocation(String name, EquipmentSlot slot, @Nullable String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }
}
