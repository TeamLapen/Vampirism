package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.items.PitchforkItem;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.jetbrains.annotations.NotNull;

public class EnchantmentVampireSlayer extends Enchantment {
    public EnchantmentVampireSlayer(@NotNull Rarity rarityIn) {
        super(rarityIn, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean checkCompatibility(@NotNull Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment);
    }

    @Override
    public void doPostAttack(@NotNull LivingEntity user, @NotNull Entity target, int level) {
        super.doPostAttack(user, target, level);
        //Cannot damage players until https://github.com/MinecraftForge/MinecraftForge/pull/4052
    }

    @Override
    public boolean canApplyAtEnchantingTable(@NotNull ItemStack stack) {
        return stack.getItem() instanceof PitchforkItem || super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public float getDamageBonus(int level, @NotNull MobType creatureType) {
        return creatureType == VReference.VAMPIRE_CREATURE_ATTRIBUTE ? 2f + Math.max(0, level - 1) * 1F : 0;
    }

    @Override
    public int getDamageProtection(int level, @NotNull DamageSource source) {
        return super.getDamageProtection(level, source);
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMaxCost(int enchantmentLevel) {
        return getMinCost(enchantmentLevel) + 20;
    }

    @Override
    public int getMinCost(int enchantmentLevel) {
        return 3 + (enchantmentLevel - 1) * 10;
    }

    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        return "enchantment.vampirism.vampire_slayer";
    }
}
