package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.util.VampirismArmorMaterials;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HunterCoatItem extends VampirismHunterArmorItem implements IItemWithTier {

    /**
     * Consider using cached value instead {@link HunterPlayerSpecialAttribute#fullHunterCoat}
     * Checks if the player has this armor fully equipped
     *
     * @return if fully equipped the tier of the worst item, otherwise null
     */
    @Nullable
    public static TIER isFullyEquipped(@NotNull Player player) {
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

    private static final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{3, 7, 9, 3};
    private static final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{3, 6, 8, 3};
    private static final int[] DAMAGE_REDUCTION_NORMAL = new int[]{2, 5, 6, 2};


    private static @NotNull Map<Attribute, Tuple<Double, AttributeModifier.Operation>> getModifiers(@NotNull EquipmentSlot slot, @NotNull TIER tier) {
        HashMap<Attribute, Tuple<Double, AttributeModifier.Operation>> map = new HashMap<>();
        int slot1 = slot.getIndex();
        int damageReduction = switch (tier) {
            case ULTIMATE -> DAMAGE_REDUCTION_ULTIMATE[slot1];
            case ENHANCED -> DAMAGE_REDUCTION_ENHANCED[slot1];
            default -> DAMAGE_REDUCTION_NORMAL[slot1];
        };
        map.put(Attributes.ARMOR, new Tuple<>((double) damageReduction, AttributeModifier.Operation.ADDITION));
        map.put(Attributes.ARMOR_TOUGHNESS, new Tuple<>(2.0, AttributeModifier.Operation.ADDITION));
        return map;
    }

    private final @NotNull TIER tier;

    private static int getDamageReduction(int slot, @NotNull TIER tier) {
        return switch (tier) {
            case ULTIMATE -> DAMAGE_REDUCTION_ULTIMATE[slot];
            case ENHANCED -> DAMAGE_REDUCTION_ENHANCED[slot];
            default -> DAMAGE_REDUCTION_NORMAL[slot];
        };
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
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
    public TIER getVampirismTier() {
        return tier;
    }

    public HunterCoatItem(@NotNull EquipmentSlot equipmentSlotIn, @NotNull TIER tier) {
        super(VampirismArmorMaterials.MASTERLY_IRON, equipmentSlotIn, new Properties().tab(VampirismMod.creativeTab), getModifiers(equipmentSlotIn, tier));
        this.tier = tier;
    }
}
