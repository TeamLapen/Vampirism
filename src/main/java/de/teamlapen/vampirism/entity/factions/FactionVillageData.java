package de.teamlapen.vampirism.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class FactionVillageData implements IVillageFactionData {

    private final MobEffect badOmenEffect;
    private final ItemStack bannerStack;
    private final ImmutableList<CaptureEntityEntry> captureEntities;
    private final VillagerProfession factionVillageProfession;
    private final Class<? extends Mob> guardSuperClass;
    private final EntityType<? extends ITaskMasterEntity> taskMasterEntity;
    private final Block fragileTotem;
    private final Block craftedTotem;

    public FactionVillageData(@Nullable MobEffect badOmenEffect, @Nonnull ItemStack bannerStack, @Nonnull ImmutableList<CaptureEntityEntry> captureEntities, @Nonnull VillagerProfession factionVillageProfession, @Nonnull Class<? extends Mob> guardSuperClass, @Nullable EntityType<? extends ITaskMasterEntity> taskMasterEntity, @Nonnull Block fragileTotem, @Nonnull Block craftedTotem) {
        this.badOmenEffect = badOmenEffect;
        this.bannerStack = bannerStack;
        this.captureEntities = captureEntities;
        this.factionVillageProfession = factionVillageProfession;
        this.guardSuperClass = guardSuperClass;
        this.taskMasterEntity = taskMasterEntity;
        this.fragileTotem = fragileTotem;
        this.craftedTotem = craftedTotem;
    }

    @Nullable
    @Override
    public MobEffect getBadOmenEffect() {
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
    public Class<? extends Mob> getGuardSuperClass() {
        return this.guardSuperClass;
    }

    @Nullable
    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return this.taskMasterEntity;
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
