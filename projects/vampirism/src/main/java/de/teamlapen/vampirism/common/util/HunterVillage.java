package de.teamlapen.vampirism.common.util;

import com.mojang.datafixers.util.Pair;
import de.teamlapen.faction.api.factions.village.IFactionVillageBuilder;
import de.teamlapen.faction.api.factions.village.VillageBanner;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.vampirism.common.core.ModBlocks;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.tags.ModEntityTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
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

import java.util.List;

public class HunterVillage {

    public static VillageBanner create() {
        return new VillageBanner(Items.BLUE_BANNER.builtInRegistryHolder(), List.of(
                new Pair<>(BannerPatterns.STRIPE_SMALL, DyeColor.BLACK),
                new Pair<>(BannerPatterns.STRIPE_CENTER, DyeColor.BLACK),
                new Pair<>(BannerPatterns.BORDER, DyeColor.WHITE),
                new Pair<>(BannerPatterns.STRIPE_MIDDLE, DyeColor.BLACK),
                new Pair<>(BannerPatterns.CURLY_BORDER, DyeColor.BLACK),
                new Pair<>(BannerPatterns.STRAIGHT_CROSS, DyeColor.WHITE)
        ));
    }
}
