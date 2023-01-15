package de.teamlapen.vampirism.items;

import  de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.items.IItemWithTier;
import de.teamlapen.vampirism.entity.player.hunter.HunterPlayerSpecialAttribute;
import de.teamlapen.vampirism.util.ArmorMaterial;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

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

    public static final ArmorMaterial.Tiered NORMAL = new ArmorMaterial.Tiered("vampirism:hunter_coat", TIER.NORMAL, 17, new int[]{2, 5, 6, 2}, 10, SoundEvents.ARMOR_EQUIP_IRON, 2, 0, () -> Ingredient.of(Tags.Items.INGOTS_IRON));
    public static final ArmorMaterial.Tiered ENHANCED = new ArmorMaterial.Tiered("vampirism:hunter_coat_enhanced", TIER.ENHANCED, 25, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_IRON, 2, 0, () -> Ingredient.of(Tags.Items.INGOTS_IRON));
    public static final ArmorMaterial.Tiered ULTIMATE = new ArmorMaterial.Tiered("vampirism:hunter_coat_ultimate", TIER.ULTIMATE, 33, new int[]{3, 7, 9, 3}, 10, SoundEvents.ARMOR_EQUIP_IRON, 2, 0, () -> Ingredient.of(Tags.Items.GEMS_DIAMOND));

    private final @NotNull TIER tier;

    public HunterCoatItem(@NotNull EquipmentSlot equipmentSlotIn, @NotNull ArmorMaterial.Tiered material) {
        super(material, equipmentSlotIn, new Properties(), new HashMap<>());
        this.tier = material.getTier();
    }

    @Override
    public TIER getVampirismTier() {
        return tier;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        addTierInformation(tooltip);
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
}
