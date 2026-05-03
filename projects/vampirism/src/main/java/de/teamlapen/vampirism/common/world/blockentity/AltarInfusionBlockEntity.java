package de.teamlapen.vampirism.common.world.blockentity;

import de.teamlapen.faction.api.factions.LevelingChange;
import de.teamlapen.faction.common.factions.FactionPlayerHandler;
import de.teamlapen.faction.common.world.blockentity.NetworkedContainerBlockEntity;
import de.teamlapen.faction.common.world.inventory.InventoryHelper;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.client.VampirismModClient;
import de.teamlapen.vampirism.common.advancements.critereon.VampireActionCriterionTrigger;
import de.teamlapen.vampirism.common.core.*;
import de.teamlapen.vampirism.common.particles.BloodShredParticleOptions;
import de.teamlapen.vampirism.common.world.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.common.world.blocks.AltarTipBlock;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampireLeveling;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.vampire.DrinkBloodContext;
import de.teamlapen.vampirism.common.world.inventory.AltarInfusionMenu;
import de.teamlapen.vampirism.common.world.items.PureBloodItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

import java.util.*;

public class AltarInfusionBlockEntity extends NetworkedContainerBlockEntity {

    public static final String KEY_PLAYER_UUID = "PlayerUUID";
    public static final String KEY_RUN_TIME = "RunTime";

    public static final Identifier ID_MOVEMENT_SLOWDOWN = VIdentifier.mod("altar_infusion_slowdown");

    public static final int DURATION_TICK = 450;
    public static final int MAX_PILLARS = 9;
    public static final int RISING_TICKS = 60;
    public static final float MAX_SPHERE_RITUAL_HEIGHT = 2.25F;

    private NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
    private @Nullable SphereSoundInstance sphereSoundInstance;
    private @Nullable Player player;
    private @Nullable UUID playerToLoadUUID;
    private List<BlockPos> tips = List.of();
    private int runTime;
    private int targetLevel;
    public int animationTime;
    public float rotation = 0.0F;
    public float prevRotation = 0.0F;
    public float targetRotation = 0.0F;
    public float verticalOffset = 0.0F;
    public int runningTicks = 0;
    public int stoppingTicks = 0;
    public float startHeight = 0.05F;

    public AltarInfusionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ALTAR_INFUSION.get(), pos, state);
    }

    @Override
    protected Component getDefaultName() {
        return Component.translatable("container.vampirism.altar_infusion");
    }

    @Override
    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> items) {
        this.items = items;
    }

    public Result tryActivate(Player player) {
        if (isRunning()) return Result.STILL_RUNNING;

        this.player = null;
        this.targetLevel = VampirePlayer.get(player).getLevel() + 1;

        if (checkRequiredLevel() == -1) return Result.LEVEL_WRONG;
        if (player.level().isBrightOutside()) return Result.NIGHT_ONLY;
        if (!checkStructureLevel(checkRequiredLevel())) return Result.MISSING_PILLARS;
        if (!checkItemRequirements()) return Result.MISSING_ITEMS;

        return Result.SUCCESS;
    }

    private int checkRequiredLevel() {
        return VampireLeveling.getInfusionRequirement(this.targetLevel).map(VampireLeveling.AltarInfusionRequirements::getRequiredStructurePoints).orElse(-1);
    }

    private boolean checkStructureLevel(int required) {
        if (this.level == null) return false;

        List<ValuedPos> valuedTips = Arrays.stream(findTips())
                .map(tip -> {
                    int height = 0;
                    double totalValue = 0;

                    while (height < 3) {
                        BlockState stateBelow = this.level.getBlockState(tip.below(height + 1));
                        if (!(stateBelow.getBlock() instanceof AltarPillarBlock)) break;

                        AltarPillarBlock.FillType type = stateBelow.getValue(AltarPillarBlock.PILLAR_TYPE);
                        totalValue += type.getValue();
                        height++;
                    }

                    int value = (int) (10 * totalValue);
                    return new ValuedPos(tip, value);
                })
                .sorted(Comparator.comparingInt(ValuedPos::value).reversed())
                .limit(MAX_PILLARS)
                .toList();

        int sum = valuedTips.stream().mapToInt(ValuedPos::value).sum();
        this.tips = valuedTips.stream().map(ValuedPos::pos).toList();

        return sum >= required * 10;
    }

    private BlockPos[] findTips() {
        if (this.level == null) return new BlockPos[0];

        List<BlockPos> tips = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

        BlockPos origin = this.worldPosition;
        for (int x = -5; x <= 5; x++) {
            for (int y = 1; y <= 3; y++) {
                for (int z = -5; z <= 5; z++) {
                    pos.set(origin.getX() + x, origin.getY() + y, origin.getZ() + z);
                    if (this.level.getBlockState(pos).getBlock() instanceof AltarTipBlock) {
                        tips.add(pos.immutable());
                    }
                }
            }
        }

        return tips.toArray(BlockPos[]::new);
    }

    private boolean checkItemRequirements() {
        return VampireLeveling.getInfusionRequirement(targetLevel)
                .map(requirements -> {
                    ItemStack missing = InventoryHelper.checkItems(this,
                            new Item[] {
                                    PureBloodItem.getBloodItemForLevel(requirements.pureBloodLevel()),
                                    ModItems.HUMAN_HEART.get(),
                                    ModItems.VAMPIRE_BOOK.get()
                            },
                            new int[] {
                                    requirements.pureBloodQuantity(),
                                    requirements.humanHeartQuantity(),
                                    requirements.vampireBookQuantity()
                            },
                            (supplied, required) ->
                                    supplied.equals(required)
                                            || required == Items.AIR
                                            || (supplied instanceof PureBloodItem suppliedItem && required instanceof PureBloodItem requiredItem && suppliedItem.getLevel(supplied.getDefaultInstance()) >= requiredItem.getLevel(required.getDefaultInstance()))
                    );

                    return missing.isEmpty();
                })
                .orElse(false);
    }

    public void startRitual(Player player) {
        if (this.level == null) return;

        this.player = player;
        this.runTime = DURATION_TICK;
        player.addEffect(new MobEffectInstance(MobEffects.RESISTANCE, DURATION_TICK, MobEffectInstance.MAX_AMPLIFIER, false, false));

        AttributeInstance movementSpeedAttribute = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (movementSpeedAttribute != null && !movementSpeedAttribute.hasModifier(ID_MOVEMENT_SLOWDOWN)) {
            movementSpeedAttribute.addPermanentModifier(new AttributeModifier(ID_MOVEMENT_SLOWDOWN, -1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
        }

        if (!this.tips.isEmpty()) {
            for (BlockPos tip : this.tips) {
                ModParticles.spawnParticlesServer(this.level, new BloodShredParticleOptions(tip.getCenter(), 60, false, BloodShredParticleOptions.PURE_BLOOD_COLOR), this.worldPosition.getX() + 0.5, this.worldPosition.getY() + 0.5, this.worldPosition.getZ() + 0.5, 5, 0.1, 0.1, 0.1, 0);
            }
        }

        updateClient();
        setChanged();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AltarInfusionBlockEntity blockEntity) {
        if (blockEntity.playerToLoadUUID != null && blockEntity.loadRitual(blockEntity.playerToLoadUUID)) {
            blockEntity.playerToLoadUUID = null;
            blockEntity.updateClient();
        }

        if (blockEntity.runTime == DURATION_TICK && !level.isClientSide()) {
            blockEntity.consumeItems();
            blockEntity.setChanged();
        }

        if (blockEntity.isRunning()) {
            blockEntity.runTime--;
            blockEntity.tickRitual();
            if (!blockEntity.isRunning()) {
                blockEntity.updateClient();
                blockEntity.setChanged();
            }
        }

        if (level.isClientSide()) {
            tickSphereAnimation(blockEntity);
            tickClientSound(blockEntity);
        }
    }

    private void tickRitual() {
        if (this.player == null || !this.player.isAlive()) {
            this.runTime = 1;
            return;
        }

        Phase phase = getCurrentPhase();

        if (this.level != null && this.level.isClientSide()) {
            handleClientEffects(phase);
        }
        if (phase == Phase.LEVELUP) {
            handleLevelUp();
        }
        if (phase == Phase.CLEAN_UP) {
            endRitual();
        }
    }

    private void handleClientEffects(Phase phase) {
        if (this.level == null) return;

        if (phase == Phase.PARTICLE_SPREAD && this.runTime % 15 == 0) {
            BlockPos pos = this.worldPosition;
            RandomSource random = RandomSource.create();

            for (BlockPos tip : this.tips) {
                ModParticles.spawnParticlesClient(this.level, new BloodShredParticleOptions(tip.getCenter(), 60, false, BloodShredParticleOptions.PURE_BLOOD_COLOR), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0, 8, 0.1, random);
            }
        }

        if (this.runTime == DURATION_TICK - 200 && this.player != null && this.player.isLocalPlayer()) {
            this.level.playLocalSound(this.player.getX(), this.player.getY(), this.player.getZ(), ModSounds.BEAM_ENTER_PLAYER.get(), SoundSource.PLAYERS, 1.0f, 1.0f, false);

            VampirismModClient.services().fullScreenOverlay().start(this.level, DURATION_TICK - 250, 50, 0xFF0000);
        }
    }

    private void handleLevelUp() {
        if (this.level != null && this.level.isClientSide()) {
            playLevelUpEffects();
            return;
        }

        if (this.player == null) return;

        FactionPlayerHandler handler = FactionPlayerHandler.get(this.player);
        if (handler.getCurrentLevel(ModFactions.VAMPIRE) != this.targetLevel - 1) return;

        handler.setFaction(LevelingChange.builder().faction(ModFactions.VAMPIRE).level(this.targetLevel));
        VampirePlayer.get(this.player).drinkBlood(Integer.MAX_VALUE, 0, false, DrinkBloodContext.none());

        if (this.player instanceof ServerPlayer serverPlayer) {
            ModAdvancements.TRIGGER_VAMPIRE_ACTION.get().trigger(serverPlayer, VampireActionCriterionTrigger.Action.PERFORM_RITUAL_INFUSION);
        }

        applyPostRitualEffects();
    }

    private void playLevelUpEffects() {
        if (this.level == null || this.player == null) return;

        this.player.playSound(ModSounds.CHOIR_SHORT.get(), 0.5f, 1.0f + (this.level.getRandom().nextFloat() - 0.5f) / 5.0f);
    }

    private void applyPostRitualEffects() {
        if (this.player == null) return;

        this.player.addEffect(new MobEffectInstance(ModEffects.SATURATION, 400, 2));
        this.player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
        this.player.addEffect(new MobEffectInstance(MobEffects.STRENGTH, 400, 2));
    }

    private void endRitual() {
        if (this.player != null) {
            AttributeInstance movementSpeedAttribute = this.player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (movementSpeedAttribute != null) {
                movementSpeedAttribute.removeModifier(ID_MOVEMENT_SLOWDOWN);
            }
        }

        this.player = null;
        this.tips = List.of();
        this.runTime = 0;

        updateClient();
        setChanged();
    }

    private void consumeItems() {
        VampireLeveling.getInfusionRequirement(targetLevel).ifPresent(requirements -> InventoryHelper.removeItems(this, requirements.pureBloodQuantity(), requirements.humanHeartQuantity(), requirements.vampireBookQuantity()));
    }

    private boolean loadRitual(UUID playerID) {
        if (this.level == null || this.level.players().isEmpty()) return false;

        this.player = this.level.getPlayerByUUID(playerID);
        if (this.player != null && this.player.isAlive()) {
            this.targetLevel = VampirePlayer.get(player).getLevel() + 1;
            checkStructureLevel(checkRequiredLevel());

            return true;
        }

        this.runTime = 0;
        this.tips = List.of();

        return false;
    }

    public static void tickSphereAnimation(AltarInfusionBlockEntity blockEntity) {
        blockEntity.prevRotation = blockEntity.rotation;

        boolean running = blockEntity.isRunning();

        float spinSpeed = (running || blockEntity.stoppingTicks < RISING_TICKS) ? 0.4F : 0.025F;
        blockEntity.targetRotation += spinSpeed;

        while (blockEntity.rotation >= (float) Math.PI) {
            blockEntity.rotation -= (float) (Math.PI * 2);
        }
        while (blockEntity.rotation < (float) -Math.PI) {
            blockEntity.rotation += (float) (Math.PI * 2);
        }
        while (blockEntity.targetRotation >= (float) Math.PI) {
            blockEntity.targetRotation -= (float) (Math.PI * 2);
        }
        while (blockEntity.targetRotation < (float) -Math.PI) {
            blockEntity.targetRotation += (float) (Math.PI * 2);
        }

        float rotationDifference = blockEntity.targetRotation - blockEntity.rotation;

        while (rotationDifference >= (float) Math.PI) {
            rotationDifference -= (float) (Math.PI * 2);
        }
        while (rotationDifference < (float) -Math.PI) {
            rotationDifference += (float) (Math.PI * 2);
        }

        blockEntity.rotation += rotationDifference * 0.4F;

        blockEntity.animationTime++;

        float bobbingHeight = (float) (Math.sin(blockEntity.animationTime * 0.1F) * 0.05F + 0.05F);

        if (running) {
            blockEntity.stoppingTicks = 0;

            if (blockEntity.runningTicks == 0) {
                blockEntity.startHeight = bobbingHeight;
            }

            if (blockEntity.runningTicks < RISING_TICKS) {
                blockEntity.runningTicks++;
                float progress = (float) blockEntity.runningTicks / RISING_TICKS;
                blockEntity.verticalOffset = blockEntity.startHeight + (MAX_SPHERE_RITUAL_HEIGHT - blockEntity.startHeight) * progress;
            } else {
                blockEntity.verticalOffset = MAX_SPHERE_RITUAL_HEIGHT;
            }
        } else {
            blockEntity.runningTicks = 0;

            if (blockEntity.stoppingTicks == 0) {
                blockEntity.startHeight = blockEntity.verticalOffset;
            }

            if (blockEntity.stoppingTicks < RISING_TICKS) {
                blockEntity.stoppingTicks++;
                float progress = (float) blockEntity.stoppingTicks / RISING_TICKS;
                blockEntity.verticalOffset = blockEntity.startHeight + (bobbingHeight - blockEntity.startHeight) * progress;
            } else {
                blockEntity.verticalOffset = bobbingHeight;
            }
        }
    }

    private static void tickClientSound(AltarInfusionBlockEntity blockEntity) {
        if (blockEntity.getCurrentPhase() != Phase.NOT_RUNNING && blockEntity.sphereSoundInstance == null) {
            SphereSoundInstance sound = new SphereSoundInstance(blockEntity);
            Minecraft.getInstance().getSoundManager().play(sound);
            blockEntity.sphereSoundInstance = sound;
        } else if (blockEntity.getCurrentPhase() == Phase.NOT_RUNNING) {
            blockEntity.sphereSoundInstance = null;
        }
    }

    private void updateClient() {
        if (this.level == null) return;

        BlockState state = this.level.getBlockState(this.worldPosition);
        this.level.sendBlockUpdated(this.worldPosition, state, state, Block.UPDATE_ALL);
    }

    public Phase getCurrentPhase() {
        if (this.runTime < 1) return Phase.NOT_RUNNING;
        if (this.runTime == 1) return Phase.CLEAN_UP;
        if (this.runTime > DURATION_TICK - 100) return Phase.PARTICLE_SPREAD;
        if (this.runTime >= DURATION_TICK - 200 && this.runTime < DURATION_TICK - 160) return Phase.BEAM_CONNECT;
        if (this.runTime <= DURATION_TICK - 200 && this.runTime > 50) return Phase.BEAM_PLAYER;
        if (this.runTime == 50) return Phase.LEVELUP;
        if (this.runTime < 50) return Phase.ENDING;
        return Phase.WAITING;
    }

    private boolean isRunning() {
        return this.runTime > 0;
    }

    public Optional<Player> getPlayer() {
        return Optional.ofNullable(isRunning() ? this.player : null);
    }

    public List<BlockPos> getTips() {
        return this.tips;
    }

    public int getRunTime() {
        return this.runTime;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input, this.items);
        this.runTime = input.getIntOr(KEY_RUN_TIME, 0);
        if (isRunning() && player == null) {
            input.read(KEY_PLAYER_UUID, UUIDUtil.CODEC).ifPresent(uuid -> {
                if (!loadRitual(uuid)) {
                    this.playerToLoadUUID = uuid;
                }
            });
        }
    }

    @Override
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items);
        output.putInt(KEY_RUN_TIME, this.runTime);
        if (player != null) {
            output.store(KEY_PLAYER_UUID, UUIDUtil.CODEC, player.getUUID());
        }
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return AltarInfusionMenu.SLOT_PREDICATES.get(slot).test(stack);
    }

    @Override
    protected AbstractContainerMenu createMenu(int id, Inventory inventory) {
        return new AltarInfusionMenu(id, inventory, this);
    }

    public enum Phase {
        NOT_RUNNING, PARTICLE_SPREAD, BEAM_CONNECT, BEAM_PLAYER, WAITING, LEVELUP, ENDING, CLEAN_UP
    }

    public enum Result implements StringRepresentable {
        SUCCESS("success"),
        STILL_RUNNING("still_running"),
        LEVEL_WRONG("level_wrong"),
        NIGHT_ONLY("night_only"),
        MISSING_PILLARS("missing_pillars"),
        MISSING_ITEMS("missing_items");

        public final String name;

        Result(String name) {
            this.name = name;
        }

        public Component getMessage() {
            return Component.translatable("message.vampirism.altar_infusion.ritual." + name);
        }

        @Override
        public String getSerializedName() {
            return name;
        }
    }

    public record ValuedPos(BlockPos pos, int value) {}

    public static class SphereSoundInstance extends AbstractTickableSoundInstance {

        private final AltarInfusionBlockEntity blockEntity;

        public SphereSoundInstance(AltarInfusionBlockEntity blockEntity) {
            super(ModSounds.SPHERE_SPINNING.get(), SoundSource.BLOCKS, RandomSource.create());
            this.blockEntity = blockEntity;
            this.looping = false;
            this.delay = 0;
            this.volume = 0.75f;
            this.pitch = 1.0f;

            updatePosition();
        }

        @Override
        public boolean canPlaySound() {
            return !isStopped();
        }

        @Override
        public void tick() {
            if (blockEntity.isRemoved()) {
                stop();
                return;
            }

            AltarInfusionBlockEntity.Phase phase = blockEntity.getCurrentPhase();
            if (phase == AltarInfusionBlockEntity.Phase.NOT_RUNNING) {
                stop();
                return;
            }

            updatePosition();
        }

        private void updatePosition() {
            this.x = blockEntity.getBlockPos().getX() + 0.5;
            this.y = blockEntity.getBlockPos().getY() + 0.75 + blockEntity.verticalOffset;
            this.z = blockEntity.getBlockPos().getZ() + 0.5;
        }
    }
}
