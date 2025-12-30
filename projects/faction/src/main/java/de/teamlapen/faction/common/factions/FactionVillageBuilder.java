package de.teamlapen.faction.common.factions;

import de.teamlapen.faction.api.factions.village.IFactionVillage;
import de.teamlapen.faction.api.factions.village.IFactionVillageBuilder;
import de.teamlapen.faction.api.world.entities.ITaskMasterEntity;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class FactionVillageBuilder implements IFactionVillageBuilder {

    @Nullable
    Holder<MobEffect> badOmenEffect = null;
    Function<HolderLookup.Provider, ItemStack> bannerStack = (provider) -> new ItemStack(Items.WHITE_BANNER);
    @Nullable
    TagKey<EntityType<?>> villageGuardTypes;
    Supplier<@Nullable EntityType<? extends ITaskMasterEntity>> taskMasterEntity = () -> null;
    Supplier<? extends Block> fragileTotem = () -> Blocks.AIR;
    Supplier<? extends Block> craftedTotem = () -> Blocks.AIR;

    @Override
    public FactionVillageBuilder badOmenEffect(Holder<MobEffect> badOmenEffect) {
        this.badOmenEffect = badOmenEffect;
        return this;
    }

    @Override
    public FactionVillageBuilder banner(Function<HolderLookup.Provider, ItemStack> bannerItem) {
        this.bannerStack = bannerItem;
        return this;
    }

    @Override
    public IFactionVillageBuilder guardTypes(TagKey<EntityType<?>> guards) {
        this.villageGuardTypes = guards;
        return this;
    }

    @Override
    public <Z extends Entity & ITaskMasterEntity> FactionVillageBuilder taskMaster(Supplier<EntityType<Z>> taskmaster) {
        //noinspection unchecked
        this.taskMasterEntity = (Supplier<EntityType<? extends ITaskMasterEntity>>) (Object) taskmaster;
        return this;
    }

    @Override
    public FactionVillageBuilder totem(Supplier<? extends Block> fragile, Supplier<? extends Block> crafted) {
        this.fragileTotem = fragile;
        this.craftedTotem = crafted;
        return this;
    }

    @Override
    public IFactionVillage build() {
        return new FactionVillage(this);
    }
}
