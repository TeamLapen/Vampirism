package de.teamlapen.vampirism.common.util;

import de.teamlapen.faction.api.factions.village.IFactionVillageBuilder;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.tags.ModEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.jetbrains.annotations.NotNull;

public class VampireVillage {

    public static @NotNull ItemStack createBanner(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<BannerPattern> bannerPattern = provider.lookupOrThrow(Registries.BANNER_PATTERN);

        ItemStack itemStack = new ItemStack(Items.BLACK_BANNER);
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        BannerPatternLayers.Builder builder = new BannerPatternLayers.Builder();
        builder.add(bannerPattern.getOrThrow(BannerPatterns.TRIANGLES_BOTTOM), DyeColor.RED)
                .add(bannerPattern.getOrThrow(BannerPatterns.TRIANGLES_TOP), DyeColor.RED)
                .add(bannerPattern.getOrThrow(BannerPatterns.BORDER), DyeColor.PURPLE)
                .add(bannerPattern.getOrThrow(BannerPatterns.RHOMBUS_MIDDLE), DyeColor.RED)
                .add(bannerPattern.getOrThrow(BannerPatterns.STRAIGHT_CROSS), DyeColor.RED)
                .add(bannerPattern.getOrThrow(BannerPatterns.CIRCLE_MIDDLE), DyeColor.PURPLE);
        itemStack.set(DataComponents.BANNER_PATTERNS, builder.build());
        itemStack.set(FactionDataComponents.IS_FACTION_BANNER, Unit.INSTANCE);
        return itemStack;
    }

    public static void vampireVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(ModEffects.BAD_OMEN_VAMPIRE)
                .guardTypes(ModEntityTags.VAMPIRE_VILLAGE_GUARDS)
                .taskMaster(ModEntities.TASK_MASTER_VAMPIRE)
                .banner(VampireVillage::createBanner)
                .totem(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE, ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED);
    }
}
