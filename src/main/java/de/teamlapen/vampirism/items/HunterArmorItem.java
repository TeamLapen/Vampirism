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
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Base class for all hunter only armor tileInventory
 */
public abstract class HunterArmorItem extends ArmorItem implements IFactionExclusiveItem {
    private static final EnumMap<Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE = Util.make(new EnumMap<>(ArmorItem.Type.class), p_266744_ -> {
        p_266744_.put(ArmorItem.Type.BOOTS, UUID.fromString("8839e157-d576-4cff-bf34-0a788131fe0f"));
        p_266744_.put(ArmorItem.Type.LEGGINGS, UUID.fromString("ac0c33f4-ebbf-44fe-9be3-a729f7633329"));
        p_266744_.put(ArmorItem.Type.CHESTPLATE, UUID.fromString("e54474a9-62a0-48ee-baaf-7efddca3d711"));
        p_266744_.put(ArmorItem.Type.HELMET, UUID.fromString("f0b9a417-0cec-4629-8623-053cd0feec3c"));
    });
    private final @NotNull Multimap<Attribute, AttributeModifier> modifierMultimap;

    public HunterArmorItem(@NotNull ArmorMaterial materialIn, @NotNull ArmorItem.Type type, Item.@NotNull Properties props) {
        this(materialIn, type, props, ImmutableMap.of());
    }

    public HunterArmorItem(@NotNull ArmorMaterial materialIn, @NotNull ArmorItem.Type type, Item.@NotNull Properties props, @NotNull Map<Attribute, Tuple<Double, AttributeModifier.Operation>> modifiers) {
        super(materialIn, type, props);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = new ImmutableMultimap.Builder<>();
        var uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(type);
        builder.putAll(getDefaultAttributeModifiers(type.getSlot()));
        for (Map.Entry<Attribute, Tuple<Double, AttributeModifier.Operation>> modifier : modifiers.entrySet()) {
            builder.put(modifier.getKey(), new AttributeModifier(uuid, "Vampirism armor modifier", modifier.getValue().getA(), modifier.getValue().getB()));
        }
        modifierMultimap = builder.build();
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level worldIn, @NotNull List<Component> tooltip, @NotNull TooltipFlag flagIn) {
        this.addFactionToolTips(stack, worldIn, tooltip, flagIn, VampirismMod.proxy.getClientPlayer());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        if (slot == this.type.getSlot()) {
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
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        if (pEntity.tickCount % 16 == 8 && pSlotId >= 36 && pSlotId <= 39 && pEntity instanceof Player player) {
            IFaction<?> f = VampirismPlayerAttributes.get(player).faction;
            if (f != null && !VReference.HUNTER_FACTION.equals(f)) {
                player.addEffect(new MobEffectInstance(ModEffects.POISON.get(), 20, 1));
            }
        }
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlot armorType, Entity entity) {
        return super.canEquip(stack, armorType, entity) && Helper.isHunter(entity);
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
