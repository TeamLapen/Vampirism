package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IFactionVillage;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FactionVillage implements IFactionVillage {

    private final Holder<MobEffect> badOmenEffect;
    private final Function<HolderLookup.Provider, ItemStack> bannerStack;
    private final @NotNull ImmutableList<CaptureEntityEntry<?>> captureEntities;
    private final Supplier<VillagerProfession> factionVillageProfession;
    private final Class<? extends Mob> guardSuperClass;
    private final Supplier<EntityType<? extends ITaskMasterEntity>> taskMasterEntity;
    private final Supplier<? extends Block> fragileTotem;
    private final Supplier<? extends Block> craftedTotem;

    public FactionVillage(@NotNull FactionVillageBuilder builder) {
        this.badOmenEffect = builder.badOmenEffect;
        this.bannerStack = builder.bannerStack;
        this.captureEntities = ImmutableList.copyOf(builder.captureEntities);
        this.factionVillageProfession = builder.factionVillageProfession;
        this.guardSuperClass = builder.guardSuperClass;
        this.taskMasterEntity = builder.taskMasterEntity;
        this.fragileTotem = builder.fragileTotem;
        this.craftedTotem = builder.craftedTotem;
    }

    @Override
    public @Nullable Holder<MobEffect> badOmenEffect() {
        return this.badOmenEffect;
    }

    @NotNull
    @Override
    public ItemStack createBanner(HolderLookup.Provider provider) {
        return this.bannerStack.apply(provider).copy();
    }

    @Override
    public List<CaptureEntityEntry<?>> getCaptureEntries() {
        return this.captureEntities;
    }

    @NotNull
    @Override
    public VillagerProfession getFactionVillageProfession() {
        return this.factionVillageProfession.get();
    }

    @NotNull
    @Override
    public Class<? extends Mob> getGuardSuperClass() {
        return this.guardSuperClass;
    }

    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity.get();
    }

    @NotNull
    @Override
    public Block getTotemTopBlock(boolean crafted) {
        return crafted ? this.craftedTotem.get() : this.fragileTotem.get();
    }

}
