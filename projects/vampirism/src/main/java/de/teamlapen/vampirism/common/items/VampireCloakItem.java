package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.common.core.ModArmorMaterials;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.factions.common.components.FactionRestriction;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.Equippable;

public class VampireCloakItem extends Item {

    public VampireCloakItem(DyeColor color, Properties properties) {
        super(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(properties.stacksTo(1)).component(DataComponents.EQUIPPABLE, Equippable.builder(EquipmentSlot.CHEST).setEquipSound(SoundEvents.ARMOR_EQUIP_LEATHER).setAsset(ModArmorMaterials.Asset.VAMPIRE_CLOAKS.get(color)).build()));
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return super.canEquip(stack, armorType, entity) && FactionRestriction.canUse(entity, stack, true);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot equipmentSlot) {
        if (entity instanceof LivingEntity living && equipmentSlot == EquipmentSlot.CHEST) {
            if (living.tickCount % 16 == 8) {
                if (!Helper.isVampire(living)) {
                    living.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                }
            }
        }
    }
}
