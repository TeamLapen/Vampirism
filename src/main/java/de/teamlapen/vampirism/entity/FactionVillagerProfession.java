package de.teamlapen.vampirism.entity;

import com.google.common.collect.ImmutableSet;
import de.teamlapen.vampirism.api.entity.factions.IFaction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import javax.annotation.Nullable;

public abstract class FactionVillagerProfession extends VillagerProfession {

    public FactionVillagerProfession(String nameIn, PoiType pointOfInterestIn, ImmutableSet<Item> specificItemsIn, ImmutableSet<Block> relatedWorldBlocksIn, @Nullable SoundEvent soundEvent) {
        super(nameIn, pointOfInterestIn, specificItemsIn, relatedWorldBlocksIn, soundEvent);
    }

    public abstract IFaction getFaction();
}
