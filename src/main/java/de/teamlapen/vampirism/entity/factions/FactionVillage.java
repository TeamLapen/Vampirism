package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class FactionVillage implements IFactionVillage {

    private final Supplier<MobEffect> badOmenEffect;
    private final Supplier<ItemStack> bannerStack;
    private final ImmutableList<CaptureEntityEntry<?>> captureEntities;
    private final Supplier<VillagerProfession> factionVillageProfession;
    private final Class<? extends Mob> guardSuperClass;
    private final Supplier<EntityType<? extends ITaskMasterEntity>> taskMasterEntity;
    private final Supplier<? extends Block> fragileTotem;
    private final Supplier<? extends Block> craftedTotem;

    public FactionVillage(FactionVillageBuilder builder) {
        this.badOmenEffect = builder.badOmenEffect;
        this.bannerStack = builder.bannerStack;
        this.captureEntities = ImmutableList.copyOf(builder.captureEntities);
        this.factionVillageProfession = builder.factionVillageProfession;
        this.guardSuperClass = builder.guardSuperClass;
        this.taskMasterEntity = builder.taskMasterEntity;
        this.fragileTotem = builder.fragileTotem;
        this.craftedTotem = builder.craftedTotem;
    }

    @Nullable
    @Override
    public MobEffect getBadOmenEffect() {
        return this.badOmenEffect.get();
    }

    @Nonnull
    @Override
    public ItemStack getBanner() {
        return this.bannerStack.get().copy();
    }

    @Override
    public List<CaptureEntityEntry<?>> getCaptureEntries() {
        return this.captureEntities;
    }

    @Nonnull
    @Override
    public VillagerProfession getFactionVillageProfession() {
        return this.factionVillageProfession.get();
    }

    @Nonnull
    @Override
    public Class<? extends Mob> getGuardSuperClass() {
        return this.guardSuperClass;
    }

    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity.get();
    }

    @Nonnull
    @Override
    public Block getTotemTopBlock(boolean crafted) {
        return crafted ? this.craftedTotem.get() : this.fragileTotem.get();
    }

    @Override
    public boolean isBanner(@Nonnull ItemStack stack) {
        return ItemStack.matches(this.bannerStack.get(), stack);
    }
}
