package de.teamlapen.vampirism.common.world.entity.hunter;

import com.mojang.serialization.Codec;
import de.teamlapen.faction.api.Factions;
import de.teamlapen.faction.api.factions.IFactionPredicate;
import de.teamlapen.faction.api.world.entities.ICaptureIgnore;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.entities.ForceLookEntityGoal;
import de.teamlapen.vampirism.common.world.entity.VampirismEntity;
import de.teamlapen.vampirism.common.world.entity.ai.goals.HunterHurtByTargetGoal;
import de.teamlapen.vampirism.common.world.entity.ai.goals.OpenGateGoal;
import de.teamlapen.vampirism.common.world.entity.ai.navigation.HunterPathNavigation;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterLeveling;
import de.teamlapen.vampirism.common.world.entity.player.hunter.HunterPlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.VampireBaseEntity;
import de.teamlapen.vampirism.common.world.inventory.HunterTrainerMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Hunter Trainer which allows Hunter players to level up
 */
public class HunterTrainerEntity extends HunterBaseEntity implements ForceLookEntityGoal.TaskOwner, ICaptureIgnore {
    private static final Component name = Component.translatable("container.vampirism.hunter_trainer");
    private static final int MOVE_TO_RESTRICT_PRIO = 3;

    public static AttributeSupplier.@NotNull Builder getAttributeBuilder() {
        return VampirismEntity.getAttributeBuilder()
                .add(Attributes.MAX_HEALTH, 300)
                .add(Attributes.ATTACK_DAMAGE, 19)
                .add(Attributes.MOVEMENT_SPEED, 0.17)
                .add(Attributes.FOLLOW_RANGE, 5);
    }

    private @Nullable Player trainee;
    private boolean shouldCreateHome;

    public HunterTrainerEntity(EntityType<? extends HunterTrainerEntity> type, Level world) {
        super(type, world);
        saveHome = true;
        hasArms = true;
        this.getNavigation().setCanOpenDoors(true);
        this.peaceful = true;
        this.setDontDropEquipment();
    }

    @Override
    public void addAdditionalSaveData(@NotNull ValueOutput nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.store("createHome", Codec.BOOL, this.shouldCreateHome);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (trainee != null && !(trainee.containerMenu instanceof HunterTrainerMenu)) {
            this.trainee = null;
        }
    }

    /**
     * @return The player which has the trainings gui open.
     */
    @NotNull
    @Override
    public Optional<Player> getForceLookTarget() {
        return Optional.ofNullable(trainee);
    }

    @Override
    public void readAdditionalSaveData(@NotNull ValueInput input) {
        super.readAdditionalSaveData(input);
        input.read("createHome", Codec.BOOL).ifPresent(createHome -> {
            this.shouldCreateHome = createHome;
            if (!this.hasHome()) {
                this.setHomeTo(this.blockPosition(), 5);
            }
        });
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, EntitySpawnReason pReason, @Nullable SpawnGroupData pSpawnData) {
        this.setItemSlot(EquipmentSlot.HEAD, HatType.TALL.getHeadItem());
        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData);
    }

    @Override
    public boolean removeWhenFarAway(double distanceToClosestPlayer) {
        return super.removeWhenFarAway(distanceToClosestPlayer) && getHome() == null;
    }

    @Override
    public void setHome(AABB box) {
        super.setHome(box);
        this.setMoveTowardsRestriction(MOVE_TO_RESTRICT_PRIO, true);
    }

    @Override
    public boolean shouldShowName() {
        return true;
    }

    @NotNull
    @Override
    protected InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (tryCureSanguinare(player)) return InteractionResult.SUCCESS;
        ItemStack stack = player.getItemInHand(hand);
        boolean flag = !stack.isEmpty() && stack.getItem() instanceof SpawnEggItem;

        if (!flag && this.isAlive() && !player.isShiftKeyDown() && hand == InteractionHand.MAIN_HAND) {
            int lvl = HunterPlayer.get(player).getLevel();
            if (!this.level().isClientSide()) {
                if (lvl > 0) {
                    if (HunterLeveling.getTrainerRequirement(lvl + 1).isPresent()) {
                        if (trainee == null) {
                            player.openMenu(new SimpleMenuProvider((id, playerInventory, playerEntity) -> new HunterTrainerMenu(id, playerInventory, this), name));
                            this.trainee = player;
                            this.getNavigation().stop();
                        } else {
                            player.sendSystemMessage(Component.translatable("dialogue.vampirism.hunter.occupied"));
                        }
                    } else {
                        player.sendSystemMessage(Component.translatable("dialogue.vampirism.hunter_trainer.wrong_level"));
                    }
                } else if (FactionPlayerHandler.get(player).isInFaction(Factions.NEUTRAL)) {
                    player.sendSystemMessage(Component.translatable("dialogue.vampirism.hunter_trainer.no_hunter"));
                }

            }

            return InteractionResult.SUCCESS;
        }


        return super.mobInteract(player, hand);
    }

    @Override
    protected @NotNull PathNavigation createNavigation(@NotNull Level level) {
        return new HunterPathNavigation(this, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new OpenDoorGoal(this, true));
        this.goalSelector.addGoal(1, new OpenGateGoal(this, true));
        this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
        this.goalSelector.addGoal(5, new ForceLookEntityGoal<>(this));
        this.goalSelector.addGoal(6, new RandomStrollGoal(this, 0.7));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 13F));
        this.goalSelector.addGoal(9, new LookAtPlayerGoal(this, VampireBaseEntity.class, 17F));
        this.goalSelector.addGoal(10, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HunterHurtByTargetGoal(this));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 5, true, false, IFactionPredicate.builder(getFaction()).onlyPlayer().notNeutral().build()));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, PathfinderMob.class, 5, true, false, IFactionPredicate.builder(getFaction()).onlyNonPlayers().notNeutral().build()));
    }
}
