package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.util.VampirismArmorMaterials;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
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

public class ArmorOfSwiftnessItem extends VampirismHunterArmor implements IItemWithTier, DyeableLeatherItem {
    private static final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{3, 6, 8, 3};
    private static final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{2, 5, 6, 2};
    private static final int[] DAMAGE_REDUCTION_NORMAL = new int[]{1, 2, 3, 1};

    private final TIER tier;

    private static Map<Attribute, Tuple<Double, AttributeModifier.Operation>> getModifiers(EquipmentSlot slot, TIER tier) {
        HashMap<Attribute, Tuple<Double, AttributeModifier.Operation>> map = new HashMap<>();
        int slot1 = slot.getIndex();
        int damageReduction = switch (tier) {
            case ULTIMATE -> DAMAGE_REDUCTION_ULTIMATE[slot1];
            case ENHANCED -> DAMAGE_REDUCTION_ENHANCED[slot1];
            default -> DAMAGE_REDUCTION_NORMAL[slot1];
        };
        double speedReduction = switch (tier) {
            case ULTIMATE -> 0.1;
            case ENHANCED -> 0.075;
            default -> 0.035;
        };

        map.put(Attributes.ARMOR, new Tuple<>((double) damageReduction, AttributeModifier.Operation.ADDITION));
        map.put(Attributes.MOVEMENT_SPEED, new Tuple<>(speedReduction, AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }

    public ArmorOfSwiftnessItem(EquipmentSlot equipmentSlotIn, TIER tier) {
        super(VampirismArmorMaterials.MASTERLY_LEATHER, equipmentSlotIn, new Item.Properties().tab(VampirismMod.creativeTab), getModifiers(equipmentSlotIn, tier));
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


    private String getTextureLocationLeather(EquipmentSlot slot) {
        return String.format("minecraft:textures/models/armor/leather_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1);
    }


}
