package de.teamlapen.vampirism.common.entity.vampire;

import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModFactions;
import de.teamlapen.vampirism.common.entity.IDefaultTaskMasterEntity;
import de.teamlapen.vampirism.common.entity.ai.goals.FleeSunVampireGoal;
import de.teamlapen.factions.common.entities.ForceLookEntityGoal;
import de.teamlapen.factions.common.entities.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.common.entity.ai.goals.RestrictSunVampireGoal;
import de.teamlapen.vampirism.common.entity.hunter.HunterBaseEntity;
import de.teamlapen.factions.common.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.common.util.Helper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class VampireTaskMasterEntity extends VampireBaseEntity implements IDefaultTaskMasterEntity {

    private static final EntityDataAccessor<VillagerData> VILLAGER_DATA = SynchedEntityData.defineId(VampireTaskMasterEntity.class, EntityDataSerializers.VILLAGER_DATA);

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampireBaseEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_SPEED);
    }

    @Nullable
    private Player interactor;

    public VampireTaskMasterEntity(EntityType<? extends VampireBaseEntity> type, Level world) {
        super(type, world);
        this.peaceful = true;
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("VillagerData", VillagerData.CODEC, this.getVillageData());
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(VILLAGER_DATA, input.read("VillagerData", VillagerData.CODEC).orElseGet(VampireTaskMasterEntity::createDefaultVillagerData));
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (interactor != null && !(interactor.isAlive() && interactor.containerMenu instanceof TaskBoardMenu)) {
            this.interactor = null;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull EntitySpawnReason reason, @Nullable SpawnGroupData spawnDataIn) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn);
        this.setBiomeType(worldIn.registryAccess(), VillagerType.byBiome(worldIn.getBiome(this.blockPosition())));
        return data;
    }

    @Override
    public @NotNull VillagerData getVillageData() {
        return this.entityData.get(VILLAGER_DATA);
    }

    protected void setBiomeType(@NotNull RegistryAccess registryAccess, @NotNull ResourceKey<VillagerType> type) {
        this.entityData.set(VILLAGER_DATA, new VillagerData(registryAccess.getOrThrow(type), registryAccess.getOrThrow(VillagerProfession.NONE),0));
    }

    @NotNull
    @Override
    public Optional<Player> getForceLookTarget() {
        return Optional.ofNullable(this.interactor);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VILLAGER_DATA, createDefaultVillagerData());
    }

    private static VillagerData createDefaultVillagerData() {
        return new VillagerData(BuiltInRegistries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS), BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE), 0);
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player playerEntity, @NotNull InteractionHand hand) {
        if (this.level().isClientSide()) {
            return Helper.isVampire(playerEntity) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (Helper.isVampire(playerEntity) && interactor == null) {
            if (this.processInteraction(playerEntity, this)) {
                this.getNavigation().stop();
                this.interactor = playerEntity;
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, PathfinderMob.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, ModFactions.HUNTER)));
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(2, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.6, true, 600, () -> false));
        this.goalSelector.addGoal(9, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, Player.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }
}
