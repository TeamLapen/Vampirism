package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import de.teamlapen.vampirism.api.items.IItemWithTier.TIER;

public class ArmorOfSwiftnessItem extends VampirismHunterArmor implements IItemWithTier, DyeableLeatherItem {

    private final static String baseRegName = "armor_of_swiftness";
    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{3, 6, 8, 3};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{2, 5, 6, 2};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{1, 2, 3, 1};

    private final TIER tier;

    public ArmorOfSwiftnessItem(EquipmentSlot equipmentSlotIn, TIER tier) {
        super(baseRegName, tier.getSerializedName(), ArmorMaterials.LEATHER, equipmentSlotIn, new Item.Properties().tab(VampirismMod.creativeTab));
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
        if (type == null) {
            return getTextureLocationLeather(slot);
        }
        return switch (getVampirismTier()) {
            case ENHANCED -> getTextureLocation("swiftness_enhanced", slot, type);
            case ULTIMATE -> getTextureLocation("swiftness_ultimate", slot, type);
            default -> getTextureLocation("swiftness", slot, type);
        };
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == this.getSlot()) {
            multimap.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[equipmentSlot.getIndex()], "Armor Swiftness", getSpeedBoost(tier), AttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        return multimap;
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
    public void onArmorTick(ItemStack itemStack, Level world, Player player) {
        super.onArmorTick(itemStack, world, player);
        if (player.tickCount % 45 == 3) {
            if (this.getSlot() == EquipmentSlot.CHEST) {
                boolean flag = true;
                int boost = Integer.MAX_VALUE;
                for (ItemStack stack : player.getInventory().armor) {
                    if (!stack.isEmpty() && stack.getItem() instanceof ArmorOfSwiftnessItem) {
                        int b = getJumpBoost(getVampirismTier());
                        if (b < boost) {
                            boost = b;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (flag && boost > -1) {
                    player.addEffect(new MobEffectInstance(MobEffects.JUMP, 50, boost, false, false));
                }
            }
        }
    }

    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        return switch (tier) {
            case ULTIMATE -> DAMAGE_REDUCTION_ULTIMATE[slot];
            case ENHANCED -> DAMAGE_REDUCTION_ENHANCED[slot];
            default -> DAMAGE_REDUCTION_NORMAL[slot];
        };
    }

    /**
     * Applied if complete armor is worn
     *
     * @return -1 if none
     */
    private int getJumpBoost(TIER tier) {
        return switch (tier) {
            case ULTIMATE -> 1;
            case ENHANCED -> 0;
            default -> -1;
        };
    }

    /**
     * Applied per piece
     */
    private double getSpeedBoost(TIER tier) {
        return switch (tier) {
            case ULTIMATE -> 0.1;
            case ENHANCED -> 0.075;
            default -> 0.035;
        };
    }

    private String getTextureLocationLeather(EquipmentSlot slot) {
        return String.format("minecraft:textures/models/armor/leather_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1);
    }


}
