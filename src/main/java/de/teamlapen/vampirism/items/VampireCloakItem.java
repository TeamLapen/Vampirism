package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.api.items.ICapeItem;
import de.teamlapen.vampirism.api.util.VResourceLocation;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.tags.ModFactionTags;
import de.teamlapen.vampirism.items.component.FactionRestriction;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class VampireCloakItem extends Item implements ICapeItem {

    public VampireCloakItem(Properties properties) {
        super(FactionRestriction.builder(ModFactionTags.IS_VAMPIRE).apply(properties.stacksTo(1)).equippable(EquipmentSlot.CHEST));
    }

    @Override
    public boolean canEquip(@NotNull ItemStack stack, @NotNull EquipmentSlot armorType, @NotNull LivingEntity entity) {
        return super.canEquip(stack, armorType, entity) && FactionRestriction.canUse(entity, stack, true);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slotId, boolean isSelected) {
        if (entity instanceof LivingEntity living && slotId >= 36 && slotId <= 39) {
            if (living.tickCount % 16 == 8) {
                if (!Helper.isVampire(living)) {
                    living.addEffect(new MobEffectInstance(ModEffects.POISON, 20, 1));
                }
            }
        }
    }

    @Override
    public ResourceLocation getCapeTexture() {
        return VResourceLocation.mod("textures/entity/cloak/" + BuiltInRegistries.ITEM.getKey(this).getPath() + ".png");
    }
}
