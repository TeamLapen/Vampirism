package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;


public class VampirismItemWeapon extends SwordItem {

    protected final String regName;
    private final float attackDamage;
    private final float attackSpeed;
    private String translation_key;



    public VampirismItemWeapon(String regName, IItemTier material, int attackDamageIn, float attackSpeedIn, Properties builder) {
        super(material, attackDamageIn, attackSpeedIn, builder);
        this.attackDamage = attackDamageIn;
        this.attackSpeed = attackSpeedIn;
        this.regName = regName;
        setRegistryName(REFERENCE.MODID, regName);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        if (flagIn.isAdvanced()) {
            tooltip.add(new StringTextComponent("ModDamage: " + getAttackDamage(stack)));
            tooltip.add(new StringTextComponent("ModSpeed: " + getAttackSpeed(stack)));
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EquipmentSlotType.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", getAttackDamage(stack), AttributeModifier.Operation.ADDITION));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", getAttackSpeed(stack), AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    protected float getAttackDamage(ItemStack stack) {
        return attackDamage;
    }

    /**
     * This is effectively a attack slowness. The more negative the value, the lower the attack speed
     */
    protected float getAttackSpeed(ItemStack stack) {
        return attackSpeed;
    }

    @Override
    protected String getDefaultTranslationKey() {
        if (this.translation_key == null) {
            this.translation_key = Util.makeTranslationKey("item", ForgeRegistries.ITEMS.getKey(this));
        }

        return this.translation_key;
    }

    /**
     * Set a custom translation key
     */
    protected void setTranslation_key(String name) {
        this.translation_key = Util.makeTranslationKey("item", new ResourceLocation(REFERENCE.MODID, name));
    }
}
