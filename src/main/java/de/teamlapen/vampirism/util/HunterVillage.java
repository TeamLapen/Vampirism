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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;

public class HunterVillage {

    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLUE_BANNER);
        CompoundTag compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListTag listNBT = new BannerPattern.Builder()
                .addPattern(BannerPatterns.STRIPE_SMALL, DyeColor.BLACK)
                .addPattern(BannerPatterns.STRIPE_CENTER, DyeColor.BLACK)
                .addPattern(BannerPatterns.BORDER, DyeColor.WHITE)
                .addPattern(BannerPatterns.STRIPE_MIDDLE, DyeColor.BLACK)
                .addPattern(BannerPatterns.CURLY_BORDER, DyeColor.BLACK)
                .addPattern(BannerPatterns.STRAIGHT_CROSS, DyeColor.WHITE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        itemStack.setHoverName(Component.translatable("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        return itemStack;
    }

    public static void hunterVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(ModEffects.BAD_OMEN_HUNTER)
                .captureEntities(() -> Lists.newArrayList(new CaptureEntityEntry(ModEntities.HUNTER.get(), 10)))
                .factionVillagerProfession(ModVillage.HUNTER_EXPERT)
                .guardSuperClass(HunterBaseEntity.class)
                .taskMaster(ModEntities.TASK_MASTER_HUNTER::get)
                .banner(HunterVillage::createBanner)
                .totem(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER::get, ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED::get);
    }
}
