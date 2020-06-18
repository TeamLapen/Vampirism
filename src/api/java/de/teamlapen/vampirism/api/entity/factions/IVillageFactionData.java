package de.teamlapen.vampirism.api.entity.factions;

import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;

import java.util.Collections;
import java.util.List;

public interface IVillageFactionData {

    IVillageFactionData INSTANCE = new IVillageFactionData() {
        @Override
        public List<CaptureEntityEntry> getCaptureEntries() {
            return Collections.emptyList();
        }

        @Override
        public VillagerProfession getFactionVillageProfession() {
            return VillagerProfession.NONE;
        }

        @Override
        public Class<? extends MobEntity> getGuardSuperClass() {
            return MobEntity.class;
        }
    };

    List<CaptureEntityEntry> getCaptureEntries();

    VillagerProfession getFactionVillageProfession();

    Class<? extends MobEntity> getGuardSuperClass();
}
