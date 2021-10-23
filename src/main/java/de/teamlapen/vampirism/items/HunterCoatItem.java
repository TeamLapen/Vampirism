package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.util.VampirismArmorMaterials;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class HunterCoatItem extends VampirismHunterArmor implements IItemWithTier {

    private final static String baseRegName = "hunter_coat";

    /**
     * Consider using cached value instead {@link HunterPlayerSpecialAttribute#fullHunterCoat}
     * Checks if the player has this armor fully equipped
     *
     * @return if fully equipped the tier of the worst item, otherwise null
     */
    @Nullable
    public static TIER isFullyEquipped(Player player) {
        int minLevel = 1000;
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.isEmpty() || !(stack.getItem() instanceof HunterCoatItem)) {
                return null;
            } else {
                minLevel = Math.min(minLevel, ((HunterCoatItem) stack.getItem()).getVampirismTier().ordinal());
            }
        }
        return IItemWithTier.TIER.values()[minLevel];
    }

    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{3, 7, 9, 3};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{3, 6, 8, 3};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{2, 5, 6, 2};

    private final TIER tier;

    public HunterCoatItem(EquipmentSlot equipmentSlotIn, TIER tier) {
        super(baseRegName, tier.getName(), VampirismArmorMaterials.MASTERLY_IRON, equipmentSlotIn, new Properties().tab(VampirismMod.creativeTab));
        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level worldIn, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return switch (getVampirismTier()) {
            case ENHANCED -> getTextureLocation("hunter_coat_enhanced", slot, type);
            case ULTIMATE -> getTextureLocation("hunter_coat_ultimate", slot, type);
            default -> getTextureLocation("hunter_coat", slot, type);
        };

    }

    @Override
    public String getBaseRegName() {
        return baseRegName;
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        TIER tier = getVampirismTier();
        return switch (tier) {
            case ULTIMATE -> DAMAGE_REDUCTION_ULTIMATE[slot];
            case ENHANCED -> DAMAGE_REDUCTION_ENHANCED[slot];
            default -> DAMAGE_REDUCTION_NORMAL[slot];
        };
    }

    @Override
    protected double getToughness(int slot, ItemStack stack) {
        return 2;
    }
}
