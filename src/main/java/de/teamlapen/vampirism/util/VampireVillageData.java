package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillageBuilder;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEffects;
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

public class VampireVillageData implements IVillageFactionData {// TODO 1.17 only keep static methods
    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLACK_BANNER);
        CompoundNBT compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListNBT listNBT = new BannerPattern.Builder()
                .addPattern(BannerPattern.TRIANGLES_BOTTOM, DyeColor.RED)
                .addPattern(BannerPattern.TRIANGLES_TOP, DyeColor.RED)
                .addPattern(BannerPattern.BORDER, DyeColor.PURPLE)
                .addPattern(BannerPattern.RHOMBUS_MIDDLE, DyeColor.RED)
                .addPattern(BannerPattern.STRAIGHT_CROSS, DyeColor.RED)
                .addPattern(BannerPattern.CIRCLE_MIDDLE, DyeColor.PURPLE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ADDITIONAL);
        itemStack.setHoverName(new TranslationTextComponent("block.minecraft.ominous_banner").withStyle(TextFormatting.GOLD));
        return itemStack;
    }

    public static void vampireVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(() -> ModEffects.BAD_OMEN_VAMPIRE.get())
                .captureEntities(() -> Lists.newArrayList(new CaptureEntityEntry(ModEntities.VAMPIRE.get(), 10), new CaptureEntityEntry(ModEntities.ADVANCED_VAMPIRE.get(), 2)))
                .factionVillagerProfession(() -> ModVillage.VAMPIRE_EXPERT.get())
                .guardSuperClass(VampireBaseEntity.class)
                .taskMaster(() -> ModEntities.TASK_MASTER_VAMPIRE.get())
                .banner(VampireVillageData::createBanner)
                .totem(() -> ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), () -> ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get());
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
            this.captureEntityEntries = Lists.newArrayList(new CaptureEntityEntry(ModEntities.VAMPIRE.get(), 10), new CaptureEntityEntry(ModEntities.ADVANCED_VAMPIRE.get(), 2));
        }
        return this.captureEntityEntries;
    }

    @Nonnull
    @Override
    public VillagerProfession getFactionVillageProfession() {
        return ModVillage.VAMPIRE_EXPERT.get();
    }

    @Nonnull
    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return VampireBaseEntity.class;
    }

    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return ModEntities.TASK_MASTER_VAMPIRE.get();
    }

    @Nonnull
    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_VAMPIRE_CRAFTED.get());
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.matches(this.banner, stack);
    }
}
