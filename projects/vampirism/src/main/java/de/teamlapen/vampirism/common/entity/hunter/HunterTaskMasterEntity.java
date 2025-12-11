package de.teamlapen.vampirism.common.entity.hunter;

import de.teamlapen.vampirism.common.config.BalanceMobProps;
import de.teamlapen.vampirism.common.core.ModItems;
import de.teamlapen.vampirism.common.entity.IDefaultTaskMasterEntity;
import de.teamlapen.vampirism.common.entity.VampirismEntity;
import de.teamlapen.factions.common.entities.ForceLookEntityGoal;
import de.teamlapen.vampirism.common.entity.vampire.VampireBaseEntity;
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
import net.minecraft.world.entity.EquipmentSlot;
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

public class HunterTaskMasterEntity extends HunterBaseEntity implements IDefaultTaskMasterEntity {

    private static final EntityDataAccessor<VillagerData> VILLAGER_DATA = SynchedEntityData.defineId(HunterTaskMasterEntity.class, EntityDataSerializers.VILLAGER_DATA);

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, BalanceMobProps.mobProps.VAMPIRE_HUNTER_MAX_HEALTH)
                .add(Attributes.ATTACK_DAMAGE, BalanceMobProps.mobProps.VAMPIRE_HUNTER_ATTACK_DAMAGE)
                .add(Attributes.MOVEMENT_SPEED, BalanceMobProps.mobProps.VAMPIRE_HUNTER_SPEED);
    }

    @Nullable
    private Player interactor;

    public HunterTaskMasterEntity(EntityType<? extends HunterBaseEntity> type, Level world) {
        super(type, world, false);
        this.peaceful = true;
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        this.entityData.set(VILLAGER_DATA, input.read("VillagerData", VillagerData.CODEC).orElseGet(HunterTaskMasterEntity::createDefaultVillagerData));
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput output) {
        super.addAdditionalSaveData(output);
        output.store("VillagerData", VillagerData.CODEC, this.getVillageData());
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
        this.setSkinType(worldIn.registryAccess(), VillagerType.byBiome(worldIn.getBiome(this.blockPosition())));
        this.setItemSlot(EquipmentSlot.HEAD, ModItems.HUNTER_HAT_TALL.get().getDefaultInstance());
        return data;
    }

    @Override
    public @NotNull VillagerData getVillageData() {
        return this.entityData.get(VILLAGER_DATA);
    }

    protected void setSkinType(@NotNull RegistryAccess registryAccess, @NotNull ResourceKey<VillagerType> type) {
        this.entityData.set(VILLAGER_DATA, createDefaultVillagerData());
    }

    private static VillagerData createDefaultVillagerData() {
        return new VillagerData(BuiltInRegistries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS), BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE),0);
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
        builder.define(VILLAGER_DATA, new VillagerData(BuiltInRegistries.VILLAGER_TYPE.getOrThrow(VillagerType.PLAINS), BuiltInRegistries.VILLAGER_PROFESSION.getOrThrow(VillagerProfession.NONE),0));
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player playerEntity, @NotNull InteractionHand hand) {
        if (this.level().isClientSide()) {
            return Helper.isHunter(playerEntity) ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        if (Helper.isHunter(playerEntity) && interactor == null) {
            if (this.processInteraction(playerEntity, this)) {
                this.getNavigation().stop();
                this.interactor = playerEntity;
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(5, new MoveThroughVillageGoal(this, 0.7F, false, 300, () -> false));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
    }
}
