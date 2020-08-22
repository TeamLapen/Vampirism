package de.teamlapen.vampirism.util;

import com.google.common.collect.Lists;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import de.teamlapen.vampirism.api.entity.ITaskMasterEntity;
import de.teamlapen.vampirism.api.entity.factions.IVillageFactionData;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.core.ModVillage;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class VampireVillageData implements IVillageFactionData {

    private List<CaptureEntityEntry> captureEntityEntries;

    @Override
    public Class<? extends MobEntity> getGuardSuperClass() {
        return VampireBaseEntity.class;
    }

    @Override
    public VillagerProfession getFactionVillageProfession() {
        return ModVillage.vampire_expert;
    }

    @Override
    public List<CaptureEntityEntry> getCaptureEntries() {
        if(this.captureEntityEntries == null) {
            this.captureEntityEntries = Lists.newArrayList(new CaptureEntityEntry(ModEntities.vampire, 10), new CaptureEntityEntry(ModEntities.advanced_vampire, 2));
        }
        return this.captureEntityEntries;
    }

    @Override
    public Pair<Block, Block> getTotemTopBlock() {
        return Pair.of(ModBlocks.totem_top_vampirism_vampire, ModBlocks.totem_top_vampirism_vampire_crafted);
    }

    @Override
    public EntityType<? extends ITaskMasterEntity> getTaskMasterEntity() {
        return ModEntities.task_master_vampire;
    }
}
