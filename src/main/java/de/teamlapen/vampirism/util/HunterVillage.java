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
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
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

public class HunterVillage {

    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLUE_BANNER);
        CompoundTag compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListTag listNBT = new BannerPattern.Builder()
                .addPattern(BannerPattern.STRIPE_SMALL, DyeColor.BLACK)
                .addPattern(BannerPattern.STRIPE_CENTER, DyeColor.BLACK)
                .addPattern(BannerPattern.BORDER, DyeColor.WHITE)
                .addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
                .addPattern(BannerPattern.CURLY_BORDER, DyeColor.BLACK)
                .addPattern(BannerPattern.STRAIGHT_CROSS, DyeColor.WHITE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        itemStack.setHoverName(new TranslatableComponent("block.minecraft.ominous_banner").withStyle(ChatFormatting.GOLD));
        return itemStack;
    }

    public static void hunterVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(() -> ModEffects.bad_omen_hunter.get())
                .captureEntities(() -> Lists.newArrayList(new CaptureEntityEntry(ModEntities.hunter, 10)))
                .factionVillagerProfession(() -> ModVillage.hunter_expert)
                .guardSuperClass(HunterBaseEntity.class)
                .taskMaster(() -> ModEntities.task_master_hunter)
                .banner(HunterVillage::createBanner)
                .totem(() -> ModBlocks.totem_top_vampirism_hunter.get(), () -> ModBlocks.totem_top_vampirism_hunter_crafted.get());
    }
}
