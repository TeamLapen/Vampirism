package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.util.REFERENCE;
import de.teamlapen.vampirism.util.SharedMonsterAttributes;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

/**
 * Base class for all hunter only armor tileInventory
 */
public abstract class VampirismHunterArmor extends ArmorItem implements IFactionExclusiveItem {
    protected static final UUID[] VAMPIRISM_ARMOR_MODIFIER = new UUID[]{UUID.fromString("f0b9a417-0cec-4629-8623-053cd0feec3c"), UUID.fromString("e54474a9-62a0-48ee-baaf-7efddca3d711"), UUID.fromString("ac0c33f4-ebbf-44fe-9be3-a729f7633329"), UUID.fromString("8839e157-d576-4cff-bf34-0a788131fe0f")};


    private final String translation_key;

    public VampirismHunterArmor(String baseRegName, @Nullable String suffix, IArmorMaterial materialIn, EquipmentSlotType equipmentSlotIn, Item.Properties props) {
        super(materialIn, equipmentSlotIn, props);
        String regName = baseRegName + "_" + equipmentSlotIn.getName();
        if (suffix != null) regName += "_" + suffix;
        setRegistryName(REFERENCE.MODID, regName);
        translation_key = Util.makeTranslationKey("item", new ResourceLocation(REFERENCE.MODID, baseRegName + "_" + equipmentSlotIn.getName()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        PlayerEntity player = VampirismMod.proxy.getClientPlayer();
        addFactionPoisonousToolTip(stack,worldIn, tooltip, flagIn, player);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create(); //TODO 1.17 build in constructor
        if (slot == this.getEquipmentSlot()) {
            map.put(SharedMonsterAttributes.ARMOR, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor modifier", this.getDamageReduction(slot.getIndex(), stack), AttributeModifier.Operation.ADDITION));
            map.put(SharedMonsterAttributes.ARMOR_TOUGHNESS, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor toughness", this.getToughness(slot.getIndex(), stack), AttributeModifier.Operation.ADDITION));
            if (this.knockbackResistance > 0)
                map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor knockback resistance", (double) this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

    @Override
    public void onArmorTick(ItemStack stack, World world, PlayerEntity player) {
        if (player.ticksExisted % 16 == 8) {
            if (FactionPlayerHandler.getOpt(player).map(a -> a.getCurrentFaction() != null && !VReference.HUNTER_FACTION.equals(a.getCurrentFaction())).orElse(false)) {
                player.addPotionEffect(new EffectInstance(ModEffects.poison, 20, 1));
            }
        }
    }

    @Override
    @Nonnull
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    /**
     * @param stack Armor stack
     * @return The damage reduction the given stack gives
     */
    protected abstract int getDamageReduction(int slot, ItemStack stack);

    @Override
    protected String getDefaultTranslationKey() {
        return translation_key;
    }

    protected String getTextureLocation(String name, EquipmentSlotType slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EquipmentSlotType.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }

    /**
     * @return The toughness of the given stack
     */
    protected double getToughness(int slot, ItemStack stack) {
        return this.getToughness();
    }
}
