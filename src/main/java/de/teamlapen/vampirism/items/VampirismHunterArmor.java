package de.teamlapen.vampirism.items;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
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

    public VampirismHunterArmor(String baseRegName, @Nullable String suffix, ArmorMaterial materialIn, EquipmentSlot equipmentSlotIn, Item.Properties props) {
        super(materialIn, equipmentSlotIn, props);
        String regName = baseRegName + "_" + equipmentSlotIn.getName();
        if (suffix != null) regName += "_" + suffix;
        setRegistryName(REFERENCE.MODID, regName);
        translation_key = Util.makeDescriptionId("item", new ResourceLocation(REFERENCE.MODID, baseRegName + "_" + equipmentSlotIn.getName()));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Player player = VampirismMod.proxy.getClientPlayer();
        addFactionPoisonousToolTip(stack, worldIn, tooltip, flagIn, player);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> map = HashMultimap.create(); //TODO 1.17 build in constructor
        if (slot == this.getSlot()) {
            map.put(Attributes.ARMOR, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor modifier", this.getDamageReduction(slot.getIndex(), stack), AttributeModifier.Operation.ADDITION));
            map.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor toughness", this.getToughness(slot.getIndex(), stack), AttributeModifier.Operation.ADDITION));
            if (this.knockbackResistance > 0)
                map.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor knockback resistance", this.knockbackResistance, AttributeModifier.Operation.ADDITION));
        }
        return map;
    }

    @Override
    @Nonnull
    public IFaction<?> getExclusiveFaction() {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, Player player) {
        if (player.tickCount % 16 == 8) {
            IFaction<?> f = VampirismPlayerAttributes.get(player).faction;
            if (f != null && !VReference.HUNTER_FACTION.equals(f)) {
                player.addEffect(new MobEffectInstance(ModEffects.poison, 20, 1));
            }
        }
    }

    /**
     * @param stack Armor stack
     * @return The damage reduction the given stack gives
     */
    protected abstract int getDamageReduction(int slot, ItemStack stack);

    @Override
    protected String getOrCreateDescriptionId() {
        return translation_key;
    }

    protected String getTextureLocation(String name, EquipmentSlot slot, String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }

    /**
     * @return The toughness of the given stack
     */
    protected double getToughness(int slot, ItemStack stack) {
        return this.getToughness();
    }
}
