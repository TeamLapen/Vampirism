package de.teamlapen.factions.common.factions;

import de.teamlapen.factions.api.entities.ITaskMasterEntity;
import de.teamlapen.factions.api.factions.village.IFactionVillage;
import de.teamlapen.factions.api.factions.village.IFactionVillageBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.random.Weighted;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class FactionVillageBuilder implements IFactionVillageBuilder {

    Holder<MobEffect> badOmenEffect = null;
    Function<HolderLookup.Provider, ItemStack> bannerStack = (provider) -> new ItemStack(Items.WHITE_BANNER);
    List<Weighted<Supplier<EntityType<? extends Mob>>>> captureEntities = Collections.emptyList();
    ResourceKey<VillagerProfession> factionVillageProfession = VillagerProfession.NONE;
    Class<? extends Mob> guardSuperClass = Mob.class;
    Supplier<EntityType<? extends ITaskMasterEntity>> taskMasterEntity = () -> null;
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
    public FactionVillageBuilder captureEntities(List<Weighted<Supplier<EntityType<? extends Mob>>>> captureEntities) {
        this.captureEntities = captureEntities;
        return this;
    }

    @Override
    public FactionVillageBuilder factionVillagerProfession(ResourceKey<VillagerProfession> profession) {
        this.factionVillageProfession = profession;
        return this;
    }

    @Override
    public FactionVillageBuilder guardSuperClass(Class<? extends Mob> clazz) {
        this.guardSuperClass = clazz;
        return this;
    }

    @Override
    public <Z extends Entity & ITaskMasterEntity> @NotNull FactionVillageBuilder taskMaster(Supplier<EntityType<Z>> taskmaster) {
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
