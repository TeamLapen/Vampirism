package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.common.core.FactionSounds;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.blockentity.NetworkedBlockEntity;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.common.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.particles.FlyingBloodEntityParticleOptions;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling.AltarInspirationRequirement;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.common.world.fluids.ControllableFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class AltarInspirationBlockEntity extends NetworkedBlockEntity {

    public static final String KEY_FLUID = "Fluid";

    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    private static final int RITUAL_TIME = 60;
    private static final int LIGHTNING_TICK = 5;
    private static final int LEVELUP_TICK = 1;

    public final ControllableFluidTank fluidInventory;

    private int ritualTicksLeft = 0;
    // Valid only while ritualTicksLeft > 0
    private int targetLevel;
    private @Nullable Player ritualPlayer;

    public AltarInspirationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_INSPIRATION.get(), pos, state);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, this::setChanged, fluid -> fluid.is(ModFluids.BLOOD), true, false);
    }

    public void startRitual(Player player) {
        if (isRunning() || !player.isAlive()) return;

        this.targetLevel = VampirePlayer.get(player).getLevel() + 1;
        Optional<AltarInspirationRequirement> requirement = VampireLeveling.getInspirationRequirement(this.targetLevel);
        if (requirement.isEmpty()) {
            if (player.level().isClientSide()) {
                player.sendOverlayMessage(Component.translatable("message.vampirism.altar_infusion.ritual.level_wrong"));
            }
            return;
        }

        if (!hasEnoughBlood(player, requirement.get())) return;

        if (!player.level().isClientSide()) {
            ModParticles.spawnParticlesServer(player.level(), new FlyingBloodEntityParticleOptions(player.getId(), false), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, 40, 0.1F, 0.1f, 0.1f, 0);
        }

        this.ritualPlayer = player;
        this.ritualTicksLeft = RITUAL_TIME;
        setChanged();
    }

    private boolean hasEnoughBlood(Player player, AltarInspirationRequirement requirement) {
        int neededBlood = requirement.bloodAmount() * VReference.FOOD_TO_FLUID_BLOOD;

        try (var transaction = Transaction.openRoot()) {
            try (var ignored = this.fluidInventory.beginAccess()) {
                var blood = ResourceHandlerUtil.extractFirst(this.fluidInventory, fluid -> fluid.is(ModFluids.BLOOD), neededBlood, transaction);
                if (blood == null || blood.amount() < neededBlood) {
                    player.sendOverlayMessage(Component.translatable("message.vampirism.altar_inspiration.not_enough_blood"));
                    return false;
                }
            }
        }
        return true;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarInspirationBlockEntity blockEntity) {
        if (!blockEntity.isRunning() || blockEntity.ritualPlayer == null || !blockEntity.ritualPlayer.isAlive()) return;

        if (blockEntity.ritualTicksLeft == LIGHTNING_TICK) {
            blockEntity.strikeLightning(level, pos);
        }
        if (blockEntity.ritualTicksLeft == LEVELUP_TICK) {
            blockEntity.applyLevelUp();
        }

        blockEntity.ritualTicksLeft--;
    }

    private void strikeLightning(Level level, BlockPos pos) {
        if (this.ritualPlayer == null) return;

        LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
        if (lightningBolt != null) {
            lightningBolt.moveOrInterpolateTo(Vec3.atBottomCenterOf(pos));
            lightningBolt.setVisualOnly(true);
            level.addFreshEntity(lightningBolt);
        }
        this.ritualPlayer.setHealth(this.ritualPlayer.getMaxHealth());
    }

    private void applyLevelUp() {
        if (this.ritualPlayer == null) return;

        try (var transaction = Transaction.openRoot()) {
            int blood = VampireLeveling.getInspirationRequirement(this.targetLevel).map(AltarInspirationRequirement::bloodAmount).orElse(0) * VReference.FOOD_TO_FLUID_BLOOD;
            try (var ignored = this.fluidInventory.beginAccess()) {
                this.fluidInventory.extract(FluidResource.of(ModFluids.BLOOD), blood, transaction);
            }
            this.ritualPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, this.targetLevel * 10 * 20));
            FactionPlayerHandler.get(this.ritualPlayer).setFaction(LevelingChange.builder().faction(ModFactions.VAMPIRE).level(this.targetLevel));
            VampirePlayer.get(this.ritualPlayer).drinkBlood(Integer.MAX_VALUE, 0, false, DrinkBloodContext.none());
            if (this.ritualPlayer instanceof ServerPlayer serverPlayer) {
                ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().trigger(serverPlayer, VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INSPIRATION);
                FactionSounds.playLevelUpSoundServer(serverPlayer);
            }
            transaction.commit();
        }
    }

    private boolean isRunning() {
        return this.ritualTicksLeft > 0;
    }

    public FluidStack getFluid() {
        FluidResource resource = this.fluidInventory.getResource();
        return resource.toStack(this.fluidInventory.getAmount());
    }

    public void setFluid(FluidStack fluid) {
        try (var transaction = Transaction.openRoot()) {
            try (var ignored = this.fluidInventory.beginAccess()) {
                FluidResource resource = this.fluidInventory.getResource();
                if (!resource.isEmpty()) {
                    int amount = this.fluidInventory.getAmount();
                    this.fluidInventory.extract(resource, amount, transaction);
                }
                if (!fluid.isEmpty()) {
                    this.fluidInventory.insert(FluidResource.of(fluid), fluidInventory.getAmount(), transaction);
                }
            }
            transaction.commit();
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidInventory.deserialize(input.childOrEmpty(KEY_FLUID));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.fluidInventory.serialize(output.child(KEY_FLUID));
    }
}