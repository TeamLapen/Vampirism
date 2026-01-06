package de.teamlapen.vampirism.common.world;

import de.teamlapen.faction.api.event.FactionVillageEvent;
import de.teamlapen.faction.api.factions.IFaction;
import de.teamlapen.faction.api.world.ITotem;
import de.teamlapen.faction.api.world.entities.ICaptureIgnore;
import de.teamlapen.faction.common.util.SpawnUtil;
import de.teamlapen.vampirism.common.core.ModEffects;
import de.teamlapen.vampirism.common.core.ModEntities;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.world.attachments.LevelFog;
import de.teamlapen.vampirism.common.world.effects.ModEffectInstanceHelper;
import de.teamlapen.vampirism.common.world.effects.SanguinareMobEffect;
import de.teamlapen.vampirism.common.world.entity.ExtendedCreature;
import de.teamlapen.vampirism.common.world.entity.VampirismEntity;
import de.teamlapen.vampirism.common.world.entity.converted.ConvertedVillagerEntity;
import de.teamlapen.vampirism.common.world.entity.hunter.AggressiveVillagerEntity;
import de.teamlapen.vampirism.common.world.entity.hunter.DummyHunterTrainerEntity;
import de.teamlapen.vampirism.common.world.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.common.world.entity.hunter.HunterTrainerEntity;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaseEntity;
import net.minecraft.util.Mth;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VillageEventHandler {

    @SubscribeEvent
    public void onVillageAreaChanged(FactionVillageEvent.AreaChangedEvent event) {
        ITotem totem = event.getTotem();
        if (totem.getTileLevel() instanceof Level level) {
            LevelFog levelFog = LevelFog.get(level);

            levelFog.updateArtificialFogBoundingBox(totem.position(), IFaction.is(totem.getControllingFaction(), ModFactions.VAMPIRE) ? event.getArea() : null);
            if (totem.isRaidTriggeredByBadOmen() && IFaction.is(totem.getCapturingFaction(), ModFactions.VAMPIRE)) {
                levelFog.updateTemporaryArtificialFog(totem.position(), event.getArea());
            }
        }
    }

    @SubscribeEvent
    public void onVillageAreaChanged(FactionVillageEvent.RemovedEvent event) {
        ITotem totem = event.getTotem();
        if (totem.getTileLevel() instanceof Level level) {
            LevelFog levelFog = LevelFog.get(level);

            levelFog.updateArtificialFogBoundingBox(totem.position(), null);
        }
    }

    @SubscribeEvent
    public void onBreakCapture(FactionVillageEvent.BreakCaptureEvent event) {
        ITotem totem = event.getTotem();
        if (totem.getTileLevel() instanceof Level level) {
            LevelFog levelFog = LevelFog.get(level);

            levelFog.updateTemporaryArtificialFog(totem.position(), null);
        }
    }

    @SubscribeEvent
    public void onSpawnVillager(FactionVillageEvent.SpawnNewVillager event) {
        if (IFaction.is(event.getFaction(), ModFactions.VAMPIRE)) {
            if (event.getNewVillager().getRandom().nextBoolean()) {
                var newVillager = event.setNewVillager(ModEntities.VILLAGER_CONVERTED.get().create(event.getLevel(), EntitySpawnReason.EVENT));
                if (event.getOldEntity() instanceof Villager oldVillager) {
                    newVillager.setHomeTo(oldVillager.getHomePosition(), oldVillager.getHomeRadius());
                }
            }
        } else if (IFaction.is(event.getFaction(), ModFactions.HUNTER)) {
            ExtendedCreature.getSafe(event.getNewVillager()).ifPresent(x -> x.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION));
            if (event.getOldEntity() instanceof Villager oldVillager) {
                event.getNewVillager().setHomeTo(oldVillager.getHomePosition(), oldVillager.getHomeRadius());
            }
        }
    }

    @SubscribeEvent
    public void onUpdateCreaturesOnCapture(FactionVillageEvent.UpdateCreaturesOnCaptureFinishEvent event) {
        List<Villager> villagerEntities = event.getLevel().getEntitiesOfClass(Villager.class, event.getVillageArea());

        if (IFaction.is(event.getCapturingFaction(), ModFactions.HUNTER)) {
            List<HunterBaseEntity> hunters = event.getLevel().getEntitiesOfClass(HunterBaseEntity.class, event.getVillageArea());
            int i = Math.max(2, hunters.size() / 2);
            for (HunterBaseEntity hunter : hunters) {
                if (hunter instanceof ICaptureIgnore){
                    continue;
                }

                if (i-- > 0) {
                    event.requestReplacement(hunter);
                }
            }

            villagerEntities.forEach(x -> ExtendedCreature.getSafe(x).ifPresent(y -> y.setPoisonousBlood(ExtendedCreature.POISONOUS_BLOOD_DOSE_DURATION)));
            this.updateTrainer(event.getTotem(), false);
        } else if (IFaction.is(event.getControllingFaction(), ModFactions.HUNTER)) {
            villagerEntities.forEach(x -> ExtendedCreature.getSafe(x).ifPresent(y -> y.setPoisonousBlood(0)));

            if (event.isForced()) {
                event.getLevel().getEntitiesOfClass(HunterBaseEntity.class, event.getVillageArea(), EntitySelector.NO_SPECTATORS.and(x -> !(x instanceof ICaptureIgnore))).forEach(event::requestKill);
            }
            this.updateTrainer(event.getTotem(), true);
        } else {
            this.updateTrainer(event.getTotem(), true);
        }

        if (IFaction.is(event.getCapturingFaction(), ModFactions.VAMPIRE)) {
            for (Villager villager : villagerEntities) {
                if (event.isForced()) {
                    villager.addEffect(ModEffectInstanceHelper.createSanguinare(11));
                } else if (villager.getRandom().nextBoolean()) {
                    ExtendedCreature.getSafe(villager).ifPresent(x -> x.setPoisonousBlood(0));
                    SanguinareMobEffect.addRandom(villager, false);
                }
            }
        } else if (IFaction.is(event.getControllingFaction(), ModFactions.VAMPIRE)) {
            for (Villager villagerEntity : villagerEntities) {
                if (villagerEntity.hasEffect(ModEffects.SANGUINARE)) {
                    villagerEntity.removeEffect(ModEffects.SANGUINARE);
                }
                if (event.isForced() && villagerEntity instanceof ConvertedVillagerEntity) {
                    event.requestReplacement(villagerEntity);
                }
            }

            if (event.isForced()) {
                event.getLevel().getEntitiesOfClass(VampireBaseEntity.class, event.getVillageArea(), EntitySelector.NO_SPECTATORS.and(x -> !(x instanceof ICaptureIgnore))).forEach(event::requestKill);
            }
        }
    }

    @SubscribeEvent
    public void onSpawnCaptureEntity(FactionVillageEvent.SpawnCaptureEntityEvent event) {
        if (IFaction.is(event.getFaction(), ModFactions.HUNTER)) {
            WeightedList.of(new Weighted<>(ModEntities.HUNTER.get(), 10), new Weighted<>(ModEntities.ADVANCED_HUNTER.get(), 2))
                    .getRandom(event.getLevel().random)
                    .ifPresent(type -> {
                        event.setEntity(type.create(event.getLevel(), EntitySpawnReason.EVENT));
                    });
        } else if (IFaction.is(event.getFaction(), ModFactions.VAMPIRE)) {
            WeightedList.of(new Weighted<>(ModEntities.VAMPIRE.get(), 10), new Weighted<>(ModEntities.ADVANCED_VAMPIRE.get(), 2))
                    .getRandom(event.getLevel().random)
                    .ifPresent(type -> {
                        var entity = event.setEntity(type.create(event.getLevel(), EntitySpawnReason.EVENT));
                        entity.setSpawnRestriction(VampireBaseEntity.SpawnRestriction.SIMPLE);
                    });
        }
    }

    @SubscribeEvent
    public void onMakeAggressive(FactionVillageEvent.MakeAggressive event){
        if (event.getVillager().getAge() >= 0
                && IFaction.is(event.getCapturingFaction(), ModFactions.VAMPIRE)
                && (IFaction.is(event.getControllingFaction(), ModFactions.HUNTER) || IFaction.isNeutral(event.getControllingFaction()))
                && event.getVillager().getRandom().nextInt(3) == 0
        ) {
            makeAggressive(event.getVillager());
        }
    }

    public static void makeAggressive(@NotNull Villager villager) {
        AggressiveVillagerEntity hunter = AggressiveVillagerEntity.makeHunter(villager);
        SpawnUtil.replaceEntity(villager, hunter);
    }

    public void updateTrainer(ITotem totem, boolean toDummy) {
        List<? extends VampirismEntity> trainer;
        EntityType<? extends VampirismEntity> entityType;
        if (toDummy) {
            trainer = totem.getTileLevel().getEntitiesOfClass(HunterTrainerEntity.class, totem.getVillageArea());
            entityType = ModEntities.HUNTER_TRAINER_DUMMY.get();
        } else {
            trainer = totem.getTileLevel().getEntitiesOfClass(DummyHunterTrainerEntity.class, totem.getVillageArea());
            entityType = ModEntities.HUNTER_TRAINER.get();
        }
        for (VampirismEntity oldEntity : trainer) {
            VampirismEntity newEntity = entityType.create(totem.getTileLevel(), EntitySpawnReason.EVENT);
            if (newEntity == null) continue;
            newEntity.restoreFrom(oldEntity);
            newEntity.setUUID(Mth.createInsecureUUID());
            newEntity.setInvulnerable(true);
            SpawnUtil.replaceEntity(oldEntity, newEntity);
        }
    }
}
