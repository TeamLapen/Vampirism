package de.teamlapen.vampirism.api.world;

import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.api.entity.factions.IFactionEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.village.Village;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public interface IVampirismVillage {

    @Nullable
    IFaction getControllingFaction();

    @Nonnull
    Village getVillage();

    void addOrRenewAggressor(@Nullable Entity entity);

    @Nullable
    IFactionEntity findNearestVillageAggressor(@Nonnull LivingEntity entity);
}
