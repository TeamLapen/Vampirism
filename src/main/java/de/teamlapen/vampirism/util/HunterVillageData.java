package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.core.ModBlocks;
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

public class HunterVillageData implements IVillageFactionData {
    private final ItemStack banner = createBanner();
    private List<CaptureEntityEntry> captureEntityEntries;

    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return HunterBaseEntity.class;
    }

    @Override
    public VillagerProfession getFactionVillageProfession() {
        return ModVillage.hunter_expert;
    }

    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        if(this.captureEntityEntries == null) {
            this.captureEntityEntries = Lists.newArrayList(new CaptureEntityEntry(ModEntities.hunter, 10)/*, new CaptureEntityEntry(ModEntities.advanced_hunter, 2)*/);
        }
        return this.captureEntityEntries;
    }

    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(ModBlocks.totem_top_vampirism_hunter, ModBlocks.totem_top_vampirism_hunter_crafted);
    }

    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return ModEntities.task_master_hunter;
    }

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return banner.copy();
    }

    public static ItemStack createBanner() {
        ItemStack itemStack = new ItemStack(Items.BLUE_BANNER);
        CompoundNBT compoundNBT = itemStack.getOrCreateChildTag("BlockEntityTag");
        ListNBT listNBT = new BannerPattern.Builder()
                .setPatternWithColor(BannerPattern.STRIPE_SMALL, DyeColor.BLACK)
                .setPatternWithColor(BannerPattern.STRIPE_CENTER, DyeColor.BLACK)
                .setPatternWithColor(BannerPattern.BORDER, DyeColor.WHITE)
                .setPatternWithColor(BannerPattern.STRIPE_MIDDLE, DyeColor.BLACK)
                .setPatternWithColor(BannerPattern.CURLY_BORDER, DyeColor.BLACK)
                .setPatternWithColor(BannerPattern.STRAIGHT_CROSS, DyeColor.WHITE)
                .buildNBT();
        compoundNBT.put("Patterns", listNBT);
        itemStack.func_242395_a(ItemStack.TooltipDisplayFlags.ADDITIONAL);
        itemStack.setDisplayName(new TranslationTextComponent("block.minecraft.ominous_banner").mergeStyle(TextFormatting.GOLD));
        return itemStack;
    }
}
