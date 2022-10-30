package de.teamlapen.vampirism.items;

import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.util.ArmorMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class ArmorOfSwiftnessItem extends VampirismHunterArmorItem implements IItemWithTier, DyeableLeatherItem {

    public static final SwiftnessArmorMaterial NORMAL = new SwiftnessArmorMaterial("vampirism:armor_of_swiftness_normal", TIER.NORMAL,15, new int[]{1, 2, 3, 1}, 12, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Tags.Items.LEATHER), 0.035f);
    public static final SwiftnessArmorMaterial ENHANCED = new SwiftnessArmorMaterial("vampirism:armor_of_swiftness_enhanced", TIER.ENHANCED,20, new int[]{2, 5, 6, 2}, 12, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Tags.Items.LEATHER),0.075f);
    public static final SwiftnessArmorMaterial ULTIMATE = new SwiftnessArmorMaterial("vampirism:armor_of_swiftness_ultimate", TIER.ULTIMATE,25, new int[]{3, 6, 8, 3}, 12, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, 0.0F, () -> Ingredient.of(Tags.Items.LEATHER), 0.1f);

    private final @NotNull TIER tier;

    private static @NotNull Map<Attribute, Tuple<Double, AttributeModifier.Operation>> getModifiers(@NotNull EquipmentSlot slot, @NotNull SwiftnessArmorMaterial tier) {
        HashMap<Attribute, Tuple<Double, AttributeModifier.Operation>> map = new HashMap<>();
        map.put(Attributes.MOVEMENT_SPEED, new Tuple<>(tier.getSpeedReduction(), AttributeModifier.Operation.MULTIPLY_TOTAL));
        return map;
    }

    public ArmorOfSwiftnessItem(@NotNull EquipmentSlot equipmentSlotIn, @NotNull SwiftnessArmorMaterial material) {
        super(material, equipmentSlotIn, new Item.Properties().tab(VampirismMod.creativeTab), getModifiers(equipmentSlotIn, material));
        this.tier = material.getTier();
    }

    @Override
    public int getColor(ItemStack stack) {
        CompoundTag compoundtag = stack.getTagElement("display");
        return compoundtag != null && compoundtag.contains("color", 99) ? compoundtag.getInt("color") : -1;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, @Nullable String type) {
        if (type == null) {
            return getTextureLocationLeather(slot);
        }
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @Override
    public void onArmorTick(ItemStack itemStack, Level world, @NotNull Player player) {
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
    private int getJumpBoost(@NotNull TIER tier) {
        return switch (tier) {
            case ULTIMATE -> 1;
            case ENHANCED -> 0;
            default -> -1;
        };
    }


    private String getTextureLocationLeather(EquipmentSlot slot) {
        return String.format("minecraft:textures/models/armor/leather_layer_%d.png", slot == EquipmentSlot.LEGS ? 2 : 1);
    }

    public static class SwiftnessArmorMaterial extends ArmorMaterial.Tiered {

        private final double speedReduction;

        public SwiftnessArmorMaterial(String name, @NotNull TIER tier, int maxDamageFactor, int[] damageReduction, int enchantability, SoundEvent soundEvent, float toughness, float knockbackResistance, Supplier<Ingredient> repairMaterial, double speedReduction) {
            super(name, tier, maxDamageFactor, damageReduction, enchantability, soundEvent, toughness, knockbackResistance, repairMaterial);
            this.speedReduction = speedReduction;
        }

        public double getSpeedReduction() {
            return speedReduction;
        }
    }
}
