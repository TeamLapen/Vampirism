package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effect;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FactionVillageData implements IVillageFactionData {

    private final Effect badOmenEffect;
    private final ItemStack bannerStack;
    private final ImmutableList<CaptureEntityEntry> captureEntities;
    private final VillagerProfession factionVillageProfession;
    private final Class<? extends MobEntity> guardSuperClass;
    private final EntityType<? extends ITaskMasterEntity> taskMasterEntity;
    private final Block fragileTotem;
    private final Block craftedTotem;
    private final Pair<Block, Block> totemPair;

    public FactionVillageData(@Nullable Effect badOmenEffect, @Nonnull ItemStack bannerStack, @Nonnull ImmutableList<CaptureEntityEntry> captureEntities, @Nonnull VillagerProfession factionVillageProfession, @Nonnull Class<? extends MobEntity> guardSuperClass, @Nullable EntityType<? extends ITaskMasterEntity> taskMasterEntity, @Nonnull Block fragileTotem, @Nonnull Block craftedTotem) {
        this.badOmenEffect = badOmenEffect;
        this.bannerStack = bannerStack;
        this.captureEntities = captureEntities;
        this.factionVillageProfession = factionVillageProfession;
        this.guardSuperClass = guardSuperClass;
        this.taskMasterEntity = taskMasterEntity;
        this.fragileTotem = fragileTotem;
        this.craftedTotem = craftedTotem;
        this.totemPair = Pair.of(fragileTotem, craftedTotem);
    }

    @Nullable
    @Override
    public Effect getBadOmenEffect() {
        return this.badOmenEffect;
    }

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return this.bannerStack.copy();
    }

    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        return this.captureEntities;
    }

    @Nonnull
    @Override
    public VillagerProfession getFactionVillageProfession() {
        return this.factionVillageProfession;
    }

    @Nonnull
    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return this.guardSuperClass;
    }

    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity;
    }

    @Nonnull
    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return this.totemPair;
    }

    @Nonnull
    @Override
    public Block getTotemTopBlock(boolean crafted) {
        return crafted ? this.craftedTotem : this.fragileTotem;
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.matches(this.bannerStack, stack);
    }
}
