package de.teamlapen.vampirism.items;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.items.IFactionExclusiveItem;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for all hunter only armor tileInventory
 */
public abstract class VampirismHunterArmor extends ArmorItem implements IFactionExclusiveItem {
    protected static final UUID[] VAMPIRISM_ARMOR_MODIFIER = new UUID[]{UUID.fromString("f0b9a417-0cec-4629-8623-053cd0feec3c"), UUID.fromString("e54474a9-62a0-48ee-baaf-7efddca3d711"), UUID.fromString("ac0c33f4-ebbf-44fe-9be3-a729f7633329"), UUID.fromString("8839e157-d576-4cff-bf34-0a788131fe0f")};
    private final @NotNull Multimap<Attribute, AttributeModifier> modifierMultimap;

    public VampirismHunterArmor(@NotNull ArmorMaterial materialIn, @NotNull EquipmentSlot equipmentSlotIn, Item.@NotNull Properties props) {
        this(materialIn, equipmentSlotIn, props, ImmutableMap.of());
    }

    public VampirismHunterArmor(@NotNull ArmorMaterial materialIn, @NotNull EquipmentSlot equipmentSlotIn, Item.@NotNull Properties props, @NotNull Map<Attribute, Tuple<Double, AttributeModifier.Operation>> modifiers) {
        super(materialIn, equipmentSlotIn, props);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        for (Map.Entry<Attribute, Tuple<Double, AttributeModifier.Operation>> modifier : modifiers.entrySet()) {
            builder.put(modifier.getKey(), new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Vampirism armor modifier", modifier.getValue().getA(), modifier.getValue().getB()));
        }
        if (!modifiers.containsKey(Attributes.ARMOR)) {
            builder.put(Attributes.ARMOR, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Vampirism armor modifier", materialIn.getDefenseForSlot(equipmentSlotIn), AttributeModifier.Operation.ADDITION));
        }
        if (!modifiers.containsKey(Attributes.ARMOR_TOUGHNESS) && materialIn.getToughness() > 0) {
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Vampirism armor modifier", materialIn.getToughness(), AttributeModifier.Operation.ADDITION));
        }
        if (!modifiers.containsKey(Attributes.KNOCKBACK_RESISTANCE) && materialIn.getKnockbackResistance() > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(VAMPIRISM_ARMOR_MODIFIER[slot.getIndex()], "Armor knockback resistance", materialIn.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
        }
        modifierMultimap = builder.build();
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        Player player = VampirismMod.proxy.getClientPlayer();
        addFactionPoisonousToolTip(stack, worldIn, tooltip, flagIn, player);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if(slot == this.slot){
            return modifierMultimap;
        }
        return ImmutableMultimap.of();
    }

    @Override
    @Nullable
    public IFaction<?> getExclusiveFaction(@NotNull ItemStack stack) {
        return VReference.HUNTER_FACTION;
    }

    @Override
    public void onArmorTick(ItemStack stack, Level world, @NotNull Player player) {
        if (player.tickCount % 16 == 8) {
            IFaction<?> f = VampirismPlayerAttributes.get(player).faction;
            if (f != null && !VReference.HUNTER_FACTION.equals(f)) {
                player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 20, 1));
            }
        }
    }

    protected String getTextureLocation(String name, EquipmentSlot slot, @Nullable String type) {
        return String.format(REFERENCE.MODID + ":textures/models/armor/%s_layer_%d%s.png", name, slot == EquipmentSlot.LEGS ? 2 : 1, type == null ? "" : "_overlay");
    }

    private String descriptionId;
    @NotNull
    @Override
    protected String getOrCreateDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = super.getOrCreateDescriptionId().replaceAll("_normal|_enhanced|_ultimate", "");
        }

        return this.descriptionId;
    }
}
