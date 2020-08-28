package de.teamlapen.vampirism.entity.vampire;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.config.BalanceMobProps;
import de.teamlapen.vampirism.entity.IDefaultTaskMasterEntity;
import de.teamlapen.vampirism.entity.goals.FleeSunVampireGoal;
import de.teamlapen.vampirism.entity.goals.ForceLookEntityGoal;
import de.teamlapen.vampirism.entity.goals.LookAtClosestVisibleGoal;
import de.teamlapen.vampirism.entity.goals.RestrictSunVampireGoal;
import de.teamlapen.vampirism.entity.hunter.HunterBaseEntity;
import de.teamlapen.vampirism.inventory.container.TaskBoardContainer;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;

public class VampireTaskMasterEntity extends VampireBaseEntity implements IDefaultTaskMasterEntity {

    @Nullable
    private PlayerEntity interactor;
    private static final DataParameter<String> BIOME_TYPE = EntityDataManager.createKey(VampireTaskMasterEntity.class, DataSerializers.STRING);

    public VampireTaskMasterEntity(EntityType<? extends VampireBaseEntity> type, World world) {
        super(type, world, false);
    }

    @Override
    public boolean canDespawn(double distanceToClosestPlayer) {
        return false;
    }

    @Override
    protected boolean processInteract(@Nonnull PlayerEntity playerEntity, @Nonnull Hand hand) {
        if (this.world.isRemote) return true;
        if (Helper.isVampire(playerEntity) && interactor == null) {
            if (this.processInteraction(playerEntity, this)) {
                this.getNavigator().clearPath();
                this.interactor = playerEntity;
            }
        }
        return true;
    }

    @Override
    public void livingTick() {
        super.livingTick();
        if (interactor != null && !(interactor.isAlive() && interactor.openContainer instanceof TaskBoardContainer)) {
            this.interactor = null;
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean getAlwaysRenderNameTagForRender() {
        return Helper.isVampire(Minecraft.getInstance().player);
    }


    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(2, new AvoidEntityGoal<>(this, CreatureEntity.class, 10, 1.0, 1.1, VampirismAPI.factionRegistry().getPredicate(getFaction(), false, true, false, false, VReference.HUNTER_FACTION)));
        this.goalSelector.addGoal(2, new RestrictSunVampireGoal<>(this));
        this.goalSelector.addGoal(2, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(3, new FleeSunVampireGoal<>(this, 0.9, false));
        this.goalSelector.addGoal(8, new MoveThroughVillageGoal(this, 0.6, true, 600, () -> false));
        this.goalSelector.addGoal(9, new RandomWalkingGoal(this, 0.7));
        this.goalSelector.addGoal(10, new LookAtClosestVisibleGoal(this, PlayerEntity.class, 20F, 0.6F));
        this.goalSelector.addGoal(10, new LookAtGoal(this, HunterBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new LookRandomlyGoal(this));

        this.targetSelector.addGoal(3, new HurtByTargetGoal(this));
    }

    @Nonnull
    @Override
    public Optional<PlayerEntity> getForceLookTarget() {
        return Optional.ofNullable(this.interactor);
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(BalanceMobProps.mobProps.VAMPIRE_SPEED);

    }

    @Override
    public IVillagerType getBiomeType() {
        String key = this.dataManager.get(BIOME_TYPE);
        ResourceLocation id = new ResourceLocation(key);
        return Registry.VILLAGER_TYPE.getOrDefault(id);
    }

    protected void setBiomeType(IVillagerType type) {
        this.dataManager.set(BIOME_TYPE, Registry.VILLAGER_TYPE.getKey(type).toString());
    }

    @Nullable
    @Override
    public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason, @Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
        ILivingEntityData data = super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
        this.setBiomeType(IVillagerType.byBiome(worldIn.getBiome(new BlockPos(this))));

        return data;
    }

    @Override
    protected void registerData() {
        super.registerData();
        this.dataManager.register(BIOME_TYPE, Registry.VILLAGER_TYPE.getDefaultKey().toString());
    }
}
