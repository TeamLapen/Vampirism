package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
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


public class ItemHunterCoat extends VampirismHunterArmor implements IItemWithTier {

    private final static String baseRegName = "hunter_coat";

    public static boolean isFullyEquipped(PlayerEntity player) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ItemHunterCoat)) {
                return false;
            }
        }
        return true;
    }

    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{3, 6, 8, 3};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{2, 6, 7, 2};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{2, 5, 6, 2};

    private final TIER tier;

    public ItemHunterCoat(EquipmentSlotType equipmentSlotIn, TIER tier) {
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
                return getTextureLocation("hunter_coat_enhanced", slot, type);
            case ULTIMATE:
                return getTextureLocation("hunter_coat_ultimate", slot, type);
            default:
                return getTextureLocation("hunter_coat", slot, type);
        }

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
}
