package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class VampireVillageData implements IVillageFactionData {
    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLACK_BANNER);
        CompoundNBT compoundNBT = itemStack.getOrCreateChildTag("BlockEntityTag");
        ListNBT listNBT = new BannerPattern.Builder()
                .setPatternWithColor(BannerPattern.TRIANGLES_BOTTOM, DyeColor.RED)
                .setPatternWithColor(BannerPattern.TRIANGLES_TOP, DyeColor.RED)
                .setPatternWithColor(BannerPattern.BORDER, DyeColor.PURPLE)
                .setPatternWithColor(BannerPattern.RHOMBUS_MIDDLE, DyeColor.RED)
                .setPatternWithColor(BannerPattern.STRAIGHT_CROSS, DyeColor.RED)
                .setPatternWithColor(BannerPattern.CIRCLE_MIDDLE, DyeColor.PURPLE)
                .buildNBT();
        compoundNBT.put("Patterns", listNBT);
        itemStack.func_242395_a(ItemStack.TooltipDisplayFlags.ADDITIONAL);
        itemStack.setDisplayName(new TranslationTextComponent("block.minecraft.ominous_banner").mergeStyle(TextFormatting.GOLD));
        return itemStack;
    }
    private final ItemStack banner = createBanner();
    private List<CaptureEntityEntry> captureEntityEntries;

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return banner.copy();
    }

    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        if (this.captureEntityEntries == null) {
            this.captureEntityEntries = Lists.newArrayList(new CaptureEntityEntry(ModEntities.vampire, 10), new CaptureEntityEntry(ModEntities.advanced_vampire, 2));
        }
        return this.captureEntityEntries;
    }

    @Override
    public VillagerProfession getFactionVillageProfession() {
        return ModVillage.vampire_expert;
    }

    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return VampireBaseEntity.class;
    }

    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return ModEntities.task_master_vampire;
    }

    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(ModBlocks.totem_top_vampirism_vampire, ModBlocks.totem_top_vampirism_vampire_crafted);
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.areItemStacksEqual(this.banner, stack);
    }
}
