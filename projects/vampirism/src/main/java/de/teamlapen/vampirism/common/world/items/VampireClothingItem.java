package de.teamlapen.vampirism.common.world.items;

import de.teamlapen.faction.common.components.FactionRestriction;
import de.teamlapen.vampirism.api.VampirismTags;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import org.jetbrains.annotations.Nullable;

public class VampireClothingItem extends Item {

    public static final Component MASSAGE_RESTRICTION_VAMPIRE_CLOTHING = Component.translatable("text.vampirism.restriction.vampire_clothing");

    public VampireClothingItem(ArmorType type, ArmorMaterial material, Properties properties) {
        super(FactionRestriction.builder(VampirismTags.Factions.IS_VAMPIRE).message(MASSAGE_RESTRICTION_VAMPIRE_CLOTHING).apply(properties).humanoidArmor(material, type));
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
