package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillageBuilder;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class FactionVillageBuilder implements IFactionVillageBuilder, IFactionVillage {

    private Supplier<MobEffect> badOmenEffect = () -> null;
    private Supplier<ItemStack> bannerStack = () -> new ItemStack(Items.WHITE_BANNER);
    private Supplier<List<CaptureEntityEntry>> captureEntities = Collections::emptyList;
    private Supplier<VillagerProfession> factionVillageProfession = () -> VillagerProfession.NONE;
    private Class<? extends Mob> guardSuperClass = Mob.class;
    private Supplier<EntityType<? extends ITaskMasterEntity>> taskMasterEntity = () -> null;
    private Supplier<Block> fragileTotem = () -> Blocks.AIR;
    private Supplier<Block> craftedTotem = () -> Blocks.AIR;

    @Override
    public IFactionVillageBuilder badOmenEffect(Supplier<MobEffect> badOmenEffect) {
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
    public IFactionVillageBuilder guardSuperClass(Class<? extends Mob> clazz) {
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
    public FactionVillage build() {
        return new FactionVillage(this.badOmenEffect.get(), this.bannerStack.get(), ImmutableList.copyOf(this.captureEntities.get()), this.factionVillageProfession.get(), guardSuperClass, this.taskMasterEntity.get(), this.fragileTotem.get(), this.craftedTotem.get());
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
    public Class<? extends Mob> getGuardSuperClass() {
        return this.guardSuperClass;
    }

    @Deprecated
    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity.get();
    }

    @Nullable
    @Override
    public MobEffect getBadOmenEffect() {
        return badOmenEffect.get();
    }

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return bannerStack.get();
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.matches(stack, bannerStack.get());
    }

    @Nonnull
    @Override
    public Block getTotemTopBlock(boolean crafted) {
        return crafted ? this.craftedTotem.get() : this.fragileTotem.get();
    }
}
