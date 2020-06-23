package de.teamlapen.vampirism.api.entity.factions;

import com.google.common.collect.ImmutableList;
import de.teamlapen.vampirism.api.entity.CaptureEntityEntry;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.merchant.villager.VillagerProfession;

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
            return ImmutableList.of();
        }
    };

    Class<? extends MobEntity> getGuardSuperClass();

    VillagerProfession getFactionVillageProfession();

    List<CaptureEntityEntry> getCaptureEntries();
}
