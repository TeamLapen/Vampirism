package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillageBuilder;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BannerPatterns;
import org.jetbrains.annotations.NotNull;

public class HunterVillage {

    public static @NotNull ItemStack createBanner(HolderLookup.Provider provider) {
        HolderLookup.RegistryLookup<BannerPattern> bannerPattern = provider.lookupOrThrow(Registries.BANNER_PATTERN);
        ItemStack itemStack = new ItemStack(Items.BLUE_BANNER);
        itemStack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        itemStack.set(DataComponents.CUSTOM_NAME, Component.translatable("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        BannerPatternLayers.Builder builder = new BannerPatternLayers.Builder();
        builder.add(bannerPattern.getOrThrow(BannerPatterns.STRIPE_SMALL), DyeColor.BLACK)
                .add(bannerPattern.getOrThrow(BannerPatterns.STRIPE_CENTER), DyeColor.BLACK)
                .add(bannerPattern.getOrThrow(BannerPatterns.BORDER), DyeColor.WHITE)
                .add(bannerPattern.getOrThrow(BannerPatterns.STRIPE_MIDDLE), DyeColor.BLACK)
                .add(bannerPattern.getOrThrow(BannerPatterns.CURLY_BORDER), DyeColor.BLACK)
                .add(bannerPattern.getOrThrow(BannerPatterns.STRAIGHT_CROSS), DyeColor.WHITE);
        itemStack.set(DataComponents.BANNER_PATTERNS, builder.build());
        return itemStack;
    }

    public static void hunterVillage(@NotNull IFactionVillageBuilder builder) {
        builder.badOmenEffect(ModEffects.BAD_OMEN_HUNTER)
                .captureEntities(Lists.newArrayList(new CaptureEntityEntry<>(ModEntities.HUNTER, 10)))
                .factionVillagerProfession(ModVillage.HUNTER_EXPERT)
                .guardSuperClass(HunterBaseEntity.class)
                .taskMaster(ModEntities.TASK_MASTER_HUNTER)
                .banner(HunterVillage::createBanner)
                .totem(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER, ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED);
    }
}
