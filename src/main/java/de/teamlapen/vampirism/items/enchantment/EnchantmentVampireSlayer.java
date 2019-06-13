package de.teamlapen.vampirism.items.enchantment;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.items.ItemPitchfork;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentDamage;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;


public class EnchantmentVampireSlayer extends Enchantment {
    public EnchantmentVampireSlayer(Rarity rarityIn) {
        super(rarityIn, EnumEnchantmentType.WEAPON, new EntityEquipmentSlot[]{EntityEquipmentSlot.MAINHAND});
        this.setName("vampirism.vampire_slayer");
        this.setRegistryName(REFERENCE.MODID, "vampireslayer");
    }

    @Override
    public float calcDamageByCreature(int level, CreatureAttribute creatureType) {
        return creatureType == VReference.VAMPIRE_CREATURE_ATTRIBUTE ? level * 2.5F : 0;
    }

    @Override
    public int calcModifierDamage(int level, DamageSource source) {
        return super.calcModifierDamage(level, source);
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack) {
        return !stack.isEmpty() && stack.getItem() instanceof ItemPitchfork || super.canApplyAtEnchantingTable(stack);
    }

    @Override
    public boolean canApplyTogether(Enchantment ench) {
        return super.canApplyTogether(ench) && !(ench instanceof EnchantmentDamage);
    }

    @Override
    public int getMaxEnchantability(int enchantmentLevel) {
        return getMinEnchantability(enchantmentLevel) + 20;
    }

    @Override
    public int getMaxLevel() {
        return 5;
    }

    @Override
    public int getMinEnchantability(int enchantmentLevel) {
        return 3 + (enchantmentLevel - 1) * 10;
    }

    @Override
    public void onEntityDamaged(EntityLivingBase user, Entity target, int level) {
        super.onEntityDamaged(user, target, level);
        //Cannot damage players until https://github.com/MinecraftForge/MinecraftForge/pull/4052
    }
}
