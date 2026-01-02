package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all hunter only armor tileInventory
 */
public class HunterArmorItem extends ModArmorItem {

    public HunterArmorItem(ArmorMaterial materialIn, ArmorType type, Properties props) {
        super(materialIn, type, props.factions$restrictFaction(ModFactionTags.IS_HUNTER).factions$descriptionWithout("_normal|_enhanced|_ultimate"));
    }
    public HunterArmorItem(ArmorMaterial materialIn, ArmorType type, Properties props, ItemAttributeModifiers attributeModifiers) {
        super(materialIn, type, props.factions$restrictFaction(ModFactionTags.IS_HUNTER).factions$descriptionWithout("_normal|_enhanced|_ultimate"), attributeModifiers);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity.tickCount % 16 == 8 && slot != null && slot.isArmor() && entity instanceof Player player && !FactionPlayerHandler.get(player).isInFaction(ModFactions.HUNTER)) {
            player.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return super.canEquip(stack, armorType, entity) && FactionRestriction.canUse(entity, stack, true);
    }

    protected String getTextureLocation(String name, EquipmentSlot slot, @Nullable String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }
}
