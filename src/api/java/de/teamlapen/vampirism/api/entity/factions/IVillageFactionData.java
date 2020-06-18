package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;

import java.util.Collections;
import java.util.List;

public interface IVillageFactionData {

    IVillageFactionData INSTANCE = new IVillageFactionData() {
        @Override
        public Class<? extends MobEntity> getGuardSuperClass() {
            return MobEntity.class;
        }

        @Override
        public VillagerProfession getFactionVillageProfession() {
            return VillagerProfession.NONE;
        }

        @Override
        public List<CaptureEntityEntry> getCaptureEntries() {
            return Collections.emptyList();
        }
    };

    Class<? extends MobEntity> getGuardSuperClass();

    VillagerProfession getFactionVillageProfession();

    List<CaptureEntityEntry> getCaptureEntries();
}
