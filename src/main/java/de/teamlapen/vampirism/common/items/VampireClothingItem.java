package de.teamlapen.vampirism.common.items;

import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.items.component.FactionRestriction;
import de.teamlapen.vampirism.common.tags.ModFactionTags;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class VampireClothingItem extends Item {

    public VampireClothingItem(ArmorType type, ArmorMaterial material, Properties properties) {
        super(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(properties).humanoidArmor(material, type));
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, LivingEntity entity) {
        return super.canEquip(stack, armorType, entity) && FactionRestriction.canUse(entity, stack, true);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof LivingEntity living && slot != null && slot.isArmor()) {
            if (living.tickCount % 16 == 8) {
                if (!Helper.isVampire(living)) {
                    living.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                }
            }
        }
    }
}
