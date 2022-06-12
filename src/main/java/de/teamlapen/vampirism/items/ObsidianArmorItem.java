package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObsidianArmorItem extends VampirismHunterArmor implements IItemWithTier {

    public static boolean isFullyEquipped(Player player) {
        for (ItemStack stack : player.getInventory().armor) {
            if (stack.isEmpty() || !(stack.getItem() instanceof ObsidianArmorItem)) {
                return false;
            }
        }
        return true;
    }

    private final TIER tier;
    private static final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{5, 8, 10, 5};
    private static final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{4, 7, 9, 4};
    private static final int[] DAMAGE_REDUCTION_NORMAL = new int[]{3, 6, 8, 3};
    private static final float[] SPEED_REDUCTION = new float[]{-0.025F, -0.1F, -0.05F, -0.025F};

    private static Map<Attribute, Tuple<Double, AttributeModifier.Operation>> getModifiers(EquipmentSlot slot, TIER tier) {
        HashMap<Attribute, Tuple<Double, AttributeModifier.Operation>> map = new HashMap<>();
        int slot1 = slot.getIndex();
        int damageReduction = switch (tier) {
            case ULTIMATE -> DAMAGE_REDUCTION_ULTIMATE[slot1];
            case ENHANCED -> DAMAGE_REDUCTION_ENHANCED[slot1];
            default -> DAMAGE_REDUCTION_NORMAL[slot1];
        };
        float speedReduction = SPEED_REDUCTION[slot.getIndex()];
        map.put(Attributes.ARMOR, new Tuple<>((double) damageReduction, AttributeModifier.Operation.ADDITION));
        map.put(Attributes.MOVEMENT_SPEED, new Tuple<>((double) speedReduction, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }

    public ObsidianArmorItem(EquipmentSlot equipmentSlotIn, TIER tier) {
        super(VampirismArmorMaterials.OBSIDIAN, equipmentSlotIn, new Properties().tab(VampirismMod.creativeTab).fireResistant(), getModifiers(equipmentSlotIn, tier));
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
            case ENHANCED -> getTextureLocation("obsidian_armor_of_hell_enhanced", slot, type);
            case ULTIMATE -> getTextureLocation("obsidian_armor_of_hell_ultimate", slot, type);
            default -> getTextureLocation("obsidian_armor_of_hell", slot, type);
        };

    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }
}
