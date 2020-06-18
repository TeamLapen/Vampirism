package de.teamlapen.vampirism.entity;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.util.SoundEvent;
import net.minecraft.village.PointOfInterestType;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class FactionVillagerProfession extends VillagerProfession {

    public FactionVillagerProfession(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn, @Nullable SoundEvent soundEvent) {
        super(nameIn, pointOfInterestIn, specificItemsIn, relatedWorldBlocksIn, soundEvent);
    }

    public abstract IFaction getFaction();
}
