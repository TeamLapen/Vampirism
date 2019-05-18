package de.teamlapen.vampirism.items;

import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;


public class ItemArmorOfSwiftness extends VampirismHunterArmor implements IItemWithTier {

    private final static String baseRegName = "armor_of_swiftness";
    private final int[] DAMAGE_REDUCTION_ULTIMATE = new int[]{2, 5, 6, 2};
    private final int[] DAMAGE_REDUCTION_ENHANCED = new int[]{2, 3, 4, 2};
    private final int[] DAMAGE_REDUCTION_NORMAL = new int[]{1, 2, 3, 1};

    private final TIER tier;

    public ItemArmorOfSwiftness(EntityEquipmentSlot equipmentSlotIn, TIER tier) {
        super(baseRegName + "_" + tier, ArmorMaterial.LEATHER, equipmentSlotIn, new Item.Properties());
        this.tier = tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        addTierInformation(tooltip);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        if (type == null) {
            return getTextureLocationLeather(slot);
        }
        switch (getTier()) {
            case ENHANCED:
                return getTextureLocation("swiftness_enhanced", slot, type);
            case ULTIMATE:
                return getTextureLocation("swiftness_ultimate", slot, type);
            default:
                return getTextureLocation("swiftness", slot, type);
        }
    }

    @Override
    public TIER getTier() {
        return tier;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> multimap = super.getAttributeModifiers(equipmentSlot, stack);

        if (equipmentSlot == this.armorType) {
            multimap.put(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[equipmentSlot.getIndex()], "Armor Swiftness", getSpeedBoost(tier), 2));
        }

        return multimap;
    }

    @Override
    public void onArmorTick(ItemStack itemStack, World world, EntityPlayer player) {
        super.onArmorTick(itemStack, world, player);
        if (player.ticksExisted % 45 == 3) {
            if (this.armorType == EntityEquipmentSlot.CHEST) {
                boolean flag = true;
                int boost = Integer.MAX_VALUE;
                for (ItemStack stack : player.inventory.armorInventory) {
                    if (!stack.isEmpty() && stack.getItem() instanceof ItemArmorOfSwiftness) {
                        int b = getJumpBoost(getTier());
                        if (b < boost) {
                            boost = b;
                        }
                    } else {
                        flag = false;
                        break;
                    }
                }
                if (flag && boost > -1) {
                    player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 50, boost, false, false));
                }
            }
        }
    }

    @Override
    protected int getDamageReduction(int slot, ItemStack stack) {
        switch (tier) {
            case ULTIMATE:
                return DAMAGE_REDUCTION_ULTIMATE[slot];
            case ENHANCED:
                return DAMAGE_REDUCTION_ENHANCED[slot];
            default:
                return DAMAGE_REDUCTION_NORMAL[slot];
        }
    }

    /**
     * Applied if complete armor is worn
     *
     * @return -1 if none
     */
    private int getJumpBoost(TIER tier) {
        switch (tier) {
            case ULTIMATE:
                return 1;
            case ENHANCED:
                return 0;
            default:
                return -1;
        }
    }

    /**
     * Applied per piece
     */
    private double getSpeedBoost(TIER tier) {
        switch (tier) {
            case ULTIMATE:
                return 0.1;
            case ENHANCED:
                return 0.075;
            default:
                return 0.035;
        }
    }

    private String getTextureLocationLeather(EntityEquipmentSlot slot) {
        return String.format("minecraft:textures/models/armor/leather_layer_%d.png", slot == EntityEquipmentSlot.LEGS ? 2 : 1);
    }


}
