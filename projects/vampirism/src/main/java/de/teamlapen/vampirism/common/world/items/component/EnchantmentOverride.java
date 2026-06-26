package de.teamlapen.vampirism.common.world.items.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.teamlapen.vampirism.common.core.ModDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.Optional;
import java.util.Set;

public record EnchantmentOverride(TagKey<Enchantment> supportedEnchantments, TagKey<Enchantment> primaryEnchantments) {

    public static final Codec<EnchantmentOverride> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TagKey.codec(Registries.ENCHANTMENT).fieldOf("supported").forGetter(EnchantmentOverride::supportedEnchantments),
            TagKey.codec(Registries.ENCHANTMENT).fieldOf("primary").forGetter(EnchantmentOverride::primaryEnchantments)
    ).apply(inst, EnchantmentOverride::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentOverride> STREAM_CODEC = StreamCodec.composite(
            TagKey.streamCodec(Registries.ENCHANTMENT), EnchantmentOverride::supportedEnchantments,
            TagKey.streamCodec(Registries.ENCHANTMENT), EnchantmentOverride::primaryEnchantments,
            EnchantmentOverride::new
    );

    public EnchantmentOverride(TagKey<Enchantment> enchantments) {
        this(enchantments, enchantments);
    }

    public static boolean isPrimary(ItemStack stack, Holder<Enchantment> enchantment) {
        var override = stack.get(ModDataComponents.ENCHANTMENT_OVERRIDE);
        if (override == null) {
            return false;
        } else {
            return enchantment.is(override.supportedEnchantments) || enchantment.is(override.primaryEnchantments);
        }
    }

    public static boolean isSupported(ItemStack stack, Holder<Enchantment> enchantment) {
        var override = stack.get(ModDataComponents.ENCHANTMENT_OVERRIDE);
        if (override == null) {
            return false;
        } else {
            return enchantment.is(override.supportedEnchantments);
        }
    }
}
