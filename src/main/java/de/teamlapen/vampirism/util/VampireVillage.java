package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillageBuilder;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatterns;

public class VampireVillage {

    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLACK_BANNER);
        CompoundTag compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListTag listNBT = new BannerPattern.Builder()
                .addPattern(BannerPatterns.TRIANGLES_BOTTOM, DyeColor.RED)
                .addPattern(BannerPatterns.TRIANGLES_TOP, DyeColor.RED)
                .addPattern(BannerPatterns.BORDER, DyeColor.PURPLE)
                .addPattern(BannerPatterns.RHOMBUS_MIDDLE, DyeColor.RED)
                .addPattern(BannerPatterns.STRAIGHT_CROSS, DyeColor.RED)
                .addPattern(BannerPatterns.CIRCLE_MIDDLE, DyeColor.PURPLE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        itemStack.setHoverName(Component.translatable("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        return itemStack;
    }

    public static void vampireVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(ModEffects.BAD_OMEN_VAMPIRE)
                .captureEntities(() -> Lists.newArrayList(new CaptureEntityEntry(ModEntities.VAMPIRE.get(), 10), new CaptureEntityEntry(ModEntities.ADVANCED_VAMPIRE.get(), 2)))
                .factionVillagerProfession(ModVillage.VAMPIRE_EXPERT)
                .guardSuperClass(VampireBaseEntity.class)
                .taskMaster(ModEntities.TASK_MASTER_VAMPIRE::get)
                .banner(VampireVillage::createBanner)
                .totem(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE::get, ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED::get);
    }
}
