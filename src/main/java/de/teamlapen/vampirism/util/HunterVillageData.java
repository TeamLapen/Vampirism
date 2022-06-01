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
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
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

public class HunterVillageData implements IVillageFactionData { // TODO 1.17 only keep static methods
    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLUE_BANNER);
        CompoundNBT compoundNBT = itemStack.getOrCreateTagElement("BlockEntityTag");
        ListNBT listNBT = new BannerPattern.Builder()
                .addPattern(BannerPattern.STRIPE_SMALL, DyeColor.BLACK)
                .addPattern(BannerPattern.STRIPE_CENTER, DyeColor.BLACK)
                .addPattern(BannerPattern.BORDER, DyeColor.WHITE)
                .addPattern(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
                .addPattern(BannerPattern.CURLY_BORDER, DyeColor.BLACK)
                .addPattern(BannerPattern.STRAIGHT_CROSS, DyeColor.WHITE)
                .toListTag();
        compoundNBT.put("Patterns", listNBT);
        itemStack.hideTooltipPart(ItemStack.TooltipDisplayFlags.ADDITIONAL);
        itemStack.setHoverName(new TranslationTextComponent("block.minecraft.ominous_banner").withStyle(TextFormatting.GOLD));
        return itemStack;
    }

    public static void hunterVillage(IFactionVillageBuilder builder) {
        builder.badOmenEffect(ModEffects.BAD_OMEN_HUNTER::get)
                .captureEntities(() -> Lists.newArrayList(new CaptureEntityEntry(ModEntities.HUNTER.get(), 10)))
                .factionVillagerProfession(ModVillage.HUNTER_EXPERT::get)
                .guardSuperClass(HunterBaseEntity.class)
                .taskMaster(ModEntities.TASK_MASTER_HUNTER::get)
                .banner(HunterVillageData::createBanner)
                .totem(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER::get, ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED::get);
    }

    private final ItemStack banner = createBanner();
    private List<CaptureEntityEntry> captureEntityEntries;

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return this.banner.copy();
    }

    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        if (this.captureEntityEntries == null) {
            this.captureEntityEntries = Lists.newArrayList(new CaptureEntityEntry(ModEntities.HUNTER.get(), 10)/*, new CaptureEntityEntry(ModEntities.ADVANCED_HUNTER.get(), 2)*/);
        }
        return this.captureEntityEntries;
    }

    @Nonnull
    @Override
    public VillagerProfession getFactionVillageProfession() {
        return ModVillage.HUNTER_EXPERT.get();
    }

    @Nonnull
    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return HunterBaseEntity.class;
    }

    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return ModEntities.TASK_MASTER_HUNTER.get();
    }

    @Nonnull
    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER.get(), ModBlocks.TOTEM_TOP_VAMPIRISM_HUNTER_CRAFTED.get());
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.matches(this.banner, stack);
    }
}
