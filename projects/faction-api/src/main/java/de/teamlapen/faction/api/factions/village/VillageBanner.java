package de.teamlapen.faction.api.factions.village;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.codecs.PairCodec;
import de.teamlapen.faction.api.FactionDataComponents;
import de.teamlapen.faction.api.util.ModStreamCodecs;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;

import java.util.List;

public record VillageBanner(Holder<Item> base, List<Pair<ResourceKey<BannerPattern>,DyeColor>> layers) {

    public static final StreamCodec<RegistryFriendlyByteBuf, VillageBanner> STREAM_CODEC = StreamCodec.composite(
            Item.STREAM_CODEC, VillageBanner::base,
            ModStreamCodecs.pair(ResourceKey.streamCodec(Registries.BANNER_PATTERN), DyeColor.STREAM_CODEC).apply(ByteBufCodecs.list()), VillageBanner::layers,
            VillageBanner::new
    );

    public ItemStack createBanner(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<BannerPattern> bannerPattern = provider.lookupOrThrow(Registries.BANNER_PATTERN);
        ItemStack itemStack = new ItemStack(base);
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        BannerPatternLayers.Builder builder = new BannerPatternLayers.Builder();
        for (Pair<ResourceKey<BannerPattern>, DyeColor> layer : layers) {
            bannerPattern.get(layer.getFirst()).ifPresent(x -> builder.add(x, layer.getSecond()));
        }
        itemStack.set(DataComponents.BANNER_PATTERNS, builder.build());
        itemStack.set(FactionDataComponents.IS_FACTION_BANNER, Unit.INSTANCE);
        return itemStack;
    }
}
