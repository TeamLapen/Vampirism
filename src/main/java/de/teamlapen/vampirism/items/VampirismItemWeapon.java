package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.registry.IRegistry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class VampirismItemWeapon extends ItemSword {

    private final float attackDamage;
    private final float attackSpeed;
    protected final String regName;
    private String translation_key;

    public VampirismItemWeapon(String regName, IItemTier material, Properties props) {
        this(regName, material, 0.4f, props);
    }


    public VampirismItemWeapon(String regName, IItemTier material, float attackSpeedModifier, Properties props) {
        this(regName, material, 3, attackSpeedModifier, props);
    }


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
            tooltip.add(new TextComponentString("ModDamage: " + getAttackDamage(stack)));
            tooltip.add(new TextComponentString("ModSpeed: " + getAttackSpeed(stack)));
        }
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = HashMultimap.create();
        if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
            multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) getAttackDamage(stack), 0));
            multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) getAttackSpeed(stack), 0));
        }

        return multimap;
    }

    @Override
    protected String getDefaultTranslationKey() {
        if (this.translation_key == null) {
            this.translation_key = Util.makeTranslationKey("item", IRegistry.field_212630_s.getKey(this));
        }

        return this.translation_key;
    }
    protected float getAttackDamage(ItemStack stack) {
        return attackDamage;
    }

    protected float getAttackSpeed(ItemStack stack) {
        return attackSpeed;
    }

    /**
     * Set a custom translation key
     */
    protected void setTranslation_key(String name) {
        this.translation_key = Util.makeTranslationKey("item", new ResourceLocation(REFERENCE.MODID, name));
    }
}
