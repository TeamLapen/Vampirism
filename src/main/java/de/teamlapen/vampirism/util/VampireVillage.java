package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillageBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class VampireVillage {

    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLACK_BANNER);
        CompoundTag compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListTag listNBT = new BannerPattern.Builder()
                .addPattern(BannerPattern.TRIANGLES_BOTTOM, DyeColor.RED)
                .addPattern(BannerPattern.TRIANGLES_TOP, DyeColor.RED)
                .addPattern(BannerPattern.BORDER, DyeColor.PURPLE)
                .addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.RED)
                .addPattern(BannerPattern.STRAIGHT_CROSS, DyeColor.RED)
                .addPattern(BannerPattern.CIRCLE_MIDDLE, DyeColor.PURPLE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        itemStack.setHoverName(new TranslatableComponent("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        return itemStack;
    }

    public static void vampireVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(() -> ModEffects.bad_omen_vampire)
                .captureEntities(() -> Lists.newArrayList(new CaptureEntityEntry(ModEntities.vampire, 10), new CaptureEntityEntry(ModEntities.advanced_vampire, 2)))
                .factionVillagerProfession(() -> ModVillage.vampire_expert)
                .guardSuperClass(VampireBaseEntity.class)
                .taskMaster(() -> ModEntities.task_master_vampire)
                .banner(VampireVillage::createBanner)
                .totem(() -> ModBlocks.totem_top_vampirism_vampire.get(), () -> ModBlocks.totem_top_vampirism_vampire_crafted.get());
    }
}
