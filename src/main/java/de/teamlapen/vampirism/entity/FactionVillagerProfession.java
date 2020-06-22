package de.teamlapen.vampirism.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import de.teamlapen.vampirism.world.TotemUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.village.PointOfInterestType;

import java.util.Set;

public abstract class FactionVillagerProfession extends VillagerProfession {

    public FactionVillagerProfession(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn) {
        super(nameIn, pointOfInterestIn, specificItemsIn, relatedWorldBlocksIn);
        TotemUtils.addProfession(this);
    }

    public abstract IFaction getFaction();
}
