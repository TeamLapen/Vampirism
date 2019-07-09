package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class ItemObsidianArmor extends VampirismHunterArmor implements IItemWithTier {

    private final static String baseRegName = "obsidian_armor";

    private final TIER tier;

    public static boolean isFullyEquipped(PlayerEntity player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemObsidianArmor)) {
                return false;
            }
        }
        return true;
    }

    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{4, 7, 9, 4};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{3, 7, 8, 3};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{3, 6, 7, 3};

    private final float[] SPEED_REDUCTION = new float[]{-0.025F, -0.1F, -0.05F, -0.025F};

    public ItemObsidianArmor(EquipmentSlotType equipmentSlotIn, TIER tier) {
        super(baseRegName, tier.getName(), ArmorMaterial.IRON, equipmentSlotIn, new Properties().group(VampirismMod.creativeTab));
        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlotType slot, String type) {
        switch (getVampirismTier()) {
            case ENHANCED:
                return getTextureLocation("obsidian_armor_of_hell_enhanced", slot, type);
            case ULTIMATE:
                return getTextureLocation("obsidian_armor_of_hell_ultimate", slot, type);
            default:
                return getTextureLocation("obsidian_armor_of_hell", slot, type);
        }

    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = super.getAttributeModifiers(slot, stack);
        if (slot == this.getEquipmentSlot()) {
            map.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Speed modifier", this.getSpeedReduction(slot.getIndex()), AttributeModifier.Operation.MULTIPLY_TOTAL));
        }
        return map;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        TIER tier = getVampirismTier();
        switch (tier) {
            case ULTIMATE:
                return DAMAGE_REDUCTION_ULTIMATE[slot];
            case ENHANCED:
                return DAMAGE_REDUCTION_ENHANCED[slot];
            default:
                return DAMAGE_REDUCTION_NORMAL[slot];
        }
    }

    private float getSpeedReduction(int slot) {
        return SPEED_REDUCTION[slot];
    }
}
