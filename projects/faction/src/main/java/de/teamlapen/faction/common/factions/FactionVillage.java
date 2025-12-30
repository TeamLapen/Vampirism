package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.village.IFactionVillage;
import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class FactionVillage implements IFactionVillage {

    @Nullable
    private final Holder<MobEffect> badOmenEffect;
    private final Function<HolderLookup.Provider, ItemStack> bannerStack;
    @Nullable
    private final TagKey<EntityType<?>> villageGuardTypes;
    private final Supplier<EntityType<? extends ITaskMasterEntity>> taskMasterEntity;
    private final Supplier<? extends Block> fragileTotem;
    private final Supplier<? extends Block> craftedTotem;

    public FactionVillage(FactionVillageBuilder builder) {
        this.badOmenEffect = builder.badOmenEffect;
        this.bannerStack = builder.bannerStack;
        this.villageGuardTypes = builder.villageGuardTypes;
        this.taskMasterEntity = builder.taskMasterEntity;
        this.fragileTotem = builder.fragileTotem;
        this.craftedTotem = builder.craftedTotem;
    }

    @Override
    public @Nullable Holder<MobEffect> badOmenEffect() {
        return this.badOmenEffect;
    }

    @Override
    public ItemStack createBanner(HolderLookup.Provider provider) {
        return this.bannerStack.apply(provider).copy();
    }

    @Override
    public @Nullable TagKey<EntityType<?>> getVillageGuardTypes() {
        return this.villageGuardTypes;
    }

    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity.get();
    }

    @Override
    public Block getTotemTopBlock(boolean crafted) {
        return crafted ? this.craftedTotem.get() : this.fragileTotem.get();
    }

}
