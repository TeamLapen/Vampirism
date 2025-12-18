package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.factions.api.factions.LevelingChange;
import de.teamlapen.factions.common.factions.FactionPlayerHandler;
import de.teamlapen.factions.common.world.blockentity.NetworkedBlockEntity;
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

import java.util.Optional;

public class AltarInspirationBlockEntity extends NetworkedBlockEntity {

    public static final int CAPACITY = 100 * VReference.FOOD_TO_FLUID_BLOOD;
    private static final int RITUAL_TIME = 60;

    public final ControllableFluidTank fluidInventory;

    private int ritualTicksLeft = 0;
    /**
     * Only valid while ritualTicksLeft > 0
     */
    private int targetLevel;
    private Player ritualPlayer;

    public AltarInspirationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_INSPIRATION.get(), pos, state);
        this.fluidInventory = new ControllableFluidTank(CAPACITY, this::setChanged, fluid -> fluid.is(ModFluids.BLOOD), true, false);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.fluidInventory.deserialize(input.childOrEmpty("fluid"));
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        this.fluidInventory.serialize(output.child("fluid"));
    }


    public void startRitual(Player player) {
        if (ritualTicksLeft > 0 || !player.isAlive()) return;

        targetLevel = VampirePlayer.get(player).getLevel() + 1;
        Optional<AltarInspirationRequirement> requirement = VampireLeveling.getInspirationRequirement(targetLevel);
        if (requirement.isEmpty()) {
            if (player.level().isClientSide()) {
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_level_wrong"), true);
            }
            return;
        }

        int neededBlood = requirement.get().bloodAmount() * VReference.FOOD_TO_FLUID_BLOOD;

        try (var transaction = Transaction.openRoot()) {
            try (var ignored = fluidInventory.beginAccess()){
                var blood = ResourceHandlerUtil.extractFirst(fluidInventory, x -> x.is(ModFluids.BLOOD), neededBlood, transaction);

                if (blood == null || blood.amount() < neededBlood) {
                    player.displayClientMessage(Component.translatable("text.vampirism.not_enough_blood"), true);
                    return;
                }
            }
        }

        if (!player.level().isClientSide()) {
            ModParticles.spawnParticlesServer(player.level(), new FlyingBloodEntityParticleOptions(player.getId(), false), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 1, this.worldPosition.getZ() + 0.5, 40, 0.1F, 0.1f, 0.1f, 0);
        }

        setChanged();
        ritualPlayer = player;
        ritualTicksLeft = RITUAL_TIME;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, AltarInspirationBlockEntity blockEntity) {
        if (blockEntity.ritualTicksLeft <= 0 || blockEntity.ritualPlayer == null || !blockEntity.ritualPlayer.isAlive()) return;

        if (blockEntity.ritualTicksLeft == 5) {
            LightningBolt lightningBolt = EntityType.LIGHTNING_BOLT.create(level, EntitySpawnReason.EVENT);
            if (lightningBolt != null) {
                lightningBolt.moveOrInterpolateTo(Vec3.atBottomCenterOf(pos));
                lightningBolt.setVisualOnly(true);
                level.addFreshEntity(lightningBolt);
            }
            blockEntity.ritualPlayer.setHealth(blockEntity.ritualPlayer.getMaxHealth());
        }

        if (blockEntity.ritualTicksLeft == 1) {
            try (var transaction = Transaction.openRoot()) {
                Optional<AltarInspirationRequirement> requirement = VampireLeveling.getInspirationRequirement(blockEntity.targetLevel);
                int blood = requirement.map(VampireLeveling.AltarInspirationRequirement::bloodAmount).orElse(0) * VReference.FOOD_TO_FLUID_BLOOD;
                try (var access = blockEntity.fluidInventory.beginAccess()) {
                    blockEntity.fluidInventory.extract(FluidResource.of(ModFluids.BLOOD), blood, transaction);
                }
                blockEntity.ritualPlayer.addEffect(new MobEffectInstance(MobEffects.REGENERATION, blockEntity.targetLevel * 10 * 20));
                FactionPlayerHandler.get(blockEntity.ritualPlayer).setFaction(LevelingChange.builder().faction(ModFactions.VAMPIRE).level(blockEntity.targetLevel));
                VampirePlayer.get(blockEntity.ritualPlayer).drinkBlood(Integer.MAX_VALUE, 0, false, DrinkBloodContext.none());
                if (blockEntity.ritualPlayer instanceof ServerPlayer serverPlayer) {
                    ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().trigger(serverPlayer, VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INSPIRATION);
                }
                transaction.commit();
            }
        }

        blockEntity.ritualTicksLeft--;
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
}
