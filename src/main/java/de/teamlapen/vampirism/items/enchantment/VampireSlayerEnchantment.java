package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.core.ModTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

public class VampireSlayerEnchantment extends DamageEnchantment {

    public VampireSlayerEnchantment() {
        super(Enchantment.definition(
                        ItemTags.WEAPON_ENCHANTABLE,
                        ModTags.Items.VAMPIRE_SLAYER_ITEMS,
                        5,
                        5,
                        Enchantment.dynamicCost(3, 10),
                        Enchantment.dynamicCost(20, 10),
                        2,
                        EquipmentSlot.MAINHAND),
                Optional.of(ModTags.Entities.VAMPIRE));
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity user, @NotNull Entity target, int level) {
        super.doPostAttack(user, target, level);
        //TODO check if mixin is no longer required
        //Cannot damage players until https://github.com/MinecraftForge/MinecraftForge/pull/4052
    }

    @Override
    public float getDamageBonus(int p_44635_, @Nullable EntityType<?> p_320019_) {
        if (this.targets.isEmpty()) {
            return 1.0F + (float)Math.max(0, p_44635_ - 1) * 0.5F;
        } else {
            return p_320019_ != null && p_320019_.is(this.targets.get()) ? 2f + (float)p_44635_ * 1F : 0.0F;
        }
    }
}
