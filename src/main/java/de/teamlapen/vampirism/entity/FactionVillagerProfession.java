package de.teamlapen.vampirism.entity;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.village.PointOfInterestType;

public abstract class FactionVillagerProfession extends VillagerProfession {

    public FactionVillagerProfession(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn) {
        super(nameIn, pointOfInterestIn, specificItemsIn, relatedWorldBlocksIn);
    }

    public abstract IFaction getFaction();
}
