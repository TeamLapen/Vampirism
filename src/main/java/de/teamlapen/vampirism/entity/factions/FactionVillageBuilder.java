package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillageBuilder;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FactionVillageBuilder implements IFactionVillageBuilder, IVillageFactionData {

    private Supplier<Effect> badOmenEffect = () -> null;
    private Supplier<ItemStack> bannerStack = () -> new ItemStack(Items.WHITE_BANNER);
    private Supplier<List<CaptureEntityEntry>> captureEntities = Collections::emptyList;
    private Supplier<VillagerProfession> factionVillageProfession = () -> VillagerProfession.NONE;
    private Class<? extends MobEntity> guardSuperClass = MobEntity.class;
    private Supplier<EntityType<? extends ITaskMasterEntity>> taskMasterEntity = () -> null;
    private Supplier<Block> fragileTotem = () -> Blocks.AIR;
    private Supplier<Block> craftedTotem = () -> Blocks.AIR;

    @Override
    public IFactionVillageBuilder badOmenEffect(Supplier<Effect> badOmenEffect) {
        this.badOmenEffect = badOmenEffect;
        return this;
    }

    @Override
    public IFactionVillageBuilder banner(Supplier<ItemStack> bannerItem) {
        this.bannerStack = bannerItem;
        return this;
    }

    @Override
    public IFactionVillageBuilder captureEntities(Supplier<List<CaptureEntityEntry>> captureEntities) {
        this.captureEntities = captureEntities;
        return this;
    }

    @Override
    public IFactionVillageBuilder factionVillagerProfession(Supplier<VillagerProfession> profession) {
        this.factionVillageProfession = profession;
        return this;
    }

    @Override
    public IFactionVillageBuilder guardSuperClass(Class<? extends MobEntity> clazz) {
        this.guardSuperClass = clazz;
        return this;
    }

    @Override
    public IFactionVillageBuilder taskMaster(Supplier<EntityType<? extends ITaskMasterEntity>> taskmaster) {
        this.taskMasterEntity = taskmaster;
        return this;
    }

    @Override
    public IFactionVillageBuilder totem(Supplier<Block> fragile, Supplier<Block> crafted) {
        this.fragileTotem = fragile;
        this.craftedTotem = crafted;
        return this;
    }

    @Override
    public FactionVillageData build() {
        return new FactionVillageData(this.badOmenEffect.get(), this.bannerStack.get(), ImmutableList.copyOf(this.captureEntities.get()), this.factionVillageProfession.get(), guardSuperClass, this.taskMasterEntity.get(), this.fragileTotem.get(), this.craftedTotem.get());
    }

    @Deprecated
    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        return this.captureEntities.get();
    }

    @Deprecated
    @Nonnull
    @Override
    public VillagerProfession getFactionVillageProfession() {
        return this.factionVillageProfession.get();
    }

    @Deprecated
    @Nonnull
    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return this.guardSuperClass;
    }

    @Deprecated
    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity.get();
    }

    @Deprecated
    @Nonnull
    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(this.fragileTotem.get(), this.fragileTotem.get());
    }
}
