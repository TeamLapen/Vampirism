package de.teamlapen.vampirism.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import net.minecraft.item.Item;
import net.minecraft.village.PointOfInterestType;

import java.util.Set;

public class FactionVillagerProfession extends VillagerProfession {
    private static final Set<VillagerProfession> professions = Sets.newHashSet();

    public FactionVillagerProfession(String nameIn, PointOfInterestType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn) {
        super(nameIn, pointOfInterestIn, specificItemsIn, relatedWorldBlocksIn);
        professions.add(this);
    }

    public static Set<VillagerProfession> getProfessions() {
        return professions;
    }
}
