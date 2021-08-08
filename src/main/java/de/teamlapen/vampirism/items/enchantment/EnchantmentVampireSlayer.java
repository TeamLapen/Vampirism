package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.items.PitchforkItem;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.damagesource.DamageSource;

import net.minecraft.world.item.enchantment.Enchantment.Rarity;

public class EnchantmentVampireSlayer extends Enchantment {
    public EnchantmentVampireSlayer(Rarity rarityIn) {
        super(rarityIn, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        this.setRegistryName(REFERENCE.MODID, "vampireslayer");
    }

    @Override
    public boolean checkCompatibility(Enchantment ench) {
        return super.checkCompatibility(ench) && !(ench instanceof DamageEnchantment);
    }

    @Override
    public void doPostAttack(LivingEntity user, Entity target, int level) {
        super.doPostAttack(user, target, level);
        //Cannot damage players until https://github.com/MinecraftForge/MinecraftForge/pull/4052
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return stack.getItem() instanceof PitchforkItem || super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public float getDamageBonus(int level, MobType creatureType) {
        return creatureType == VReference.VAMPIRE_CREATURE_ATTRIBUTE ? level * 2.5F : 0;
    }

    @Override
    public int getDamageProtection(int level, DamageSource source) {
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

    @Override
    protected String getOrCreateDescriptionId() {
        return "enchantment.vampirism.vampire_slayer";
    }
}
