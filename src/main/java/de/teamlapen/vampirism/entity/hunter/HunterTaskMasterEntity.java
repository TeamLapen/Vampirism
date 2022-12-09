package de.teamlapen.vampirism.entity.hunter;

import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.IDefaultTaskMasterEntity;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.ai.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.inventory.TaskBoardMenu;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class HunterTaskMasterEntity extends HunterBaseEntity implements IDefaultTaskMasterEntity {

    private static final EntityDataAccessor<String> BIOME_TYPE = SynchedEntityData.defineId(HunterTaskMasterEntity.class, EntityDataSerializers.STRING);

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
    public void aiStep() {
        super.aiStep();
        if (interactor != null && !(interactor.isAlive() && interactor.containerMenu instanceof TaskBoardMenu)) {
            this.interactor = null;
        }
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor worldIn, @NotNull DifficultyInstance difficultyIn, @NotNull MobSpawnType reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        SpawnGroupData data = super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setBiomeType(VillagerType.byBiome(worldIn.getBiome(this.blockPosition())));
        return data;
    }

    @Override
    public @NotNull VillagerType getBiomeType() {
        String key = this.entityData.get(BIOME_TYPE);
        ResourceLocation id = new ResourceLocation(key);
        return BuiltInRegistries.VILLAGER_TYPE.get(id);
    }

    protected void setBiomeType(@NotNull VillagerType type) {
        this.entityData.set(BIOME_TYPE, BuiltInRegistries.VILLAGER_TYPE.getKey(type).toString());
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

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldShowName() {
        return Helper.isHunter(Minecraft.getInstance().player);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BIOME_TYPE, BuiltInRegistries.VILLAGER_TYPE.getDefaultKey().toString());
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player playerEntity, @NotNull InteractionHand hand) {
        if (this.level.isClientSide) {
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
