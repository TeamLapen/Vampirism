package de.teamlapen.vampirism.blockentity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import de.teamlapen.vampirism.core.ModTags;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.inventory.VampireBeaconMenu;
import de.teamlapen.vampirism.mixin.accessor.BeaconBeamSectionyMixin;
import de.teamlapen.vampirism.util.Helper;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.LockCode;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.BeaconMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static net.minecraft.world.level.block.entity.BeaconBlockEntity.playSound;

public class VampireBeaconBlockEntity extends BlockEntity implements MenuProvider, Nameable {
    private static final int MAX_LEVELS = 3;
    public static final MobEffect[][] BEACON_EFFECTS = new MobEffect[][] {{MobEffects.MOVEMENT_SPEED}, {MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING}, {MobEffects.REGENERATION, MobEffects.SATURATION}};
    public static final int[][] BEACON_EFFECTS_AMPLIFIER = new int[][] {{0, 0}, {0, 0}, {0, 0}};
    public static final Set<MobEffect> NO_AMPLIFIER_EFFECTS = Set.of(MobEffects.NIGHT_VISION, MobEffects.WATER_BREATHING);
    private static final Set<MobEffect> VALID_EFFECTS = Arrays.stream(BEACON_EFFECTS).flatMap(Arrays::stream).collect(Collectors.toSet());
    public static final int DATA_LEVELS = 0;
    public static final int DATA_PRIMARY = 1;
    public static final int DATA_AMPLIFIER = 2;
    public static final int DATA_UPGRADED = 3;
    public static final int NUM_DATA_VALUES = 4;
    private static final int BLOCKS_CHECK_PER_TICK = 10;
    private static final Component DEFAULT_NAME = Component.translatable("container.beacon");
    private List<BeaconBlockEntity.BeaconBeamSection> beamSections = Lists.newArrayList();
    private List<BeaconBlockEntity.BeaconBeamSection> checkingBeamSections = Lists.newArrayList();
    private int levels;
    private int lastCheckY;
    @Nullable
    private MobEffect primaryPower;
    private int effectAmplifier;
    private boolean isUpgraded;
    @Nullable
    private Component name;
    private LockCode lockKey = LockCode.NO_LOCK;
    private final ContainerData dataAccess = new ContainerData() {
        public int get(int slot) {
            return switch (slot) {
                case DATA_LEVELS -> VampireBeaconBlockEntity.this.levels;
                case DATA_PRIMARY -> BeaconMenu.encodeEffect(VampireBeaconBlockEntity.this.primaryPower);
                case DATA_AMPLIFIER -> VampireBeaconBlockEntity.this.effectAmplifier;
                case DATA_UPGRADED -> VampireBeaconBlockEntity.this.isUpgraded ? 1 : 0;
                default -> 0;
            };
        }

        public void set(int slot, int value) {
            switch (slot) {
                case DATA_LEVELS:
                    VampireBeaconBlockEntity.this.levels = value;
                    break;
                case DATA_PRIMARY:
                    if (!VampireBeaconBlockEntity.this.level.isClientSide && !VampireBeaconBlockEntity.this.beamSections.isEmpty()) {
                        playSound(VampireBeaconBlockEntity.this.level, VampireBeaconBlockEntity.this.worldPosition, SoundEvents.BEACON_POWER_SELECT);
                    }
                    VampireBeaconBlockEntity.this.primaryPower = VampireBeaconBlockEntity.getValidEffectById(value);
                    break;
                case DATA_AMPLIFIER:
                    VampireBeaconBlockEntity.this.effectAmplifier = value;
                    break;
                case DATA_UPGRADED:
                    VampireBeaconBlockEntity.this.isUpgraded = value != 0;
                    break;
            }

        }

        public int getCount() {
            return NUM_DATA_VALUES;
        }
    };

    public VampireBeaconBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModTiles.VAMPIRE_BEACON.get(), pPos, pBlockState);
    }

    public static void tick(Level pLevel, BlockPos pPos, BlockState pState, VampireBeaconBlockEntity pBlockEntity) {
        int i = pPos.getX();
        int j = pPos.getY();
        int k = pPos.getZ();
        BlockPos blockpos;
        if (pBlockEntity.lastCheckY < j) {
            blockpos = pPos;
            pBlockEntity.checkingBeamSections = Lists.newArrayList();
            pBlockEntity.lastCheckY = pPos.getY() - 1;
        } else {
            blockpos = new BlockPos(i, pBlockEntity.lastCheckY + 1, k);
        }

        BeaconBlockEntity.BeaconBeamSection beaconblockentity$beaconbeamsection = pBlockEntity.checkingBeamSections.isEmpty() ? null : pBlockEntity.checkingBeamSections.get(pBlockEntity.checkingBeamSections.size() - 1);
        int l = pLevel.getHeight(Heightmap.Types.WORLD_SURFACE, i, k);

        for(int i1 = 0; i1 < BLOCKS_CHECK_PER_TICK && blockpos.getY() <= l; ++i1) {
            BlockState blockstate = pLevel.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            float[] afloat = blockstate.getBeaconColorMultiplier(pLevel, blockpos, pPos);
            if (afloat != null) {
                if (pBlockEntity.checkingBeamSections.size() <= 1) {
                    beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(afloat);
                    pBlockEntity.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                } else if (beaconblockentity$beaconbeamsection != null) {
                    if (Arrays.equals(afloat, beaconblockentity$beaconbeamsection.getColor())) {
                        ((BeaconBeamSectionyMixin)beaconblockentity$beaconbeamsection).invoke_increaseHeight();
                    } else {
                        beaconblockentity$beaconbeamsection = new BeaconBlockEntity.BeaconBeamSection(new float[]{(beaconblockentity$beaconbeamsection.getColor()[0] + afloat[0]) / 2.0F, (beaconblockentity$beaconbeamsection.getColor()[1] + afloat[1]) / 2.0F, (beaconblockentity$beaconbeamsection.getColor()[2] + afloat[2]) / 2.0F});
                        pBlockEntity.checkingBeamSections.add(beaconblockentity$beaconbeamsection);
                    }
                }
            } else {
                if (beaconblockentity$beaconbeamsection == null || blockstate.getLightBlock(pLevel, blockpos) >= 15 && !blockstate.is(Blocks.BEDROCK)) {
                    pBlockEntity.checkingBeamSections.clear();
                    pBlockEntity.lastCheckY = l;
                    break;
                }

                ((BeaconBeamSectionyMixin)beaconblockentity$beaconbeamsection).invoke_increaseHeight();
            }

            blockpos = blockpos.above();
            ++pBlockEntity.lastCheckY;
        }

        int j1 = pBlockEntity.levels;
        if (pLevel.getGameTime() % 80L == 0L) {
            if (!pBlockEntity.beamSections.isEmpty()) {
                Pair<Integer, Boolean> integerBooleanPair = updateBase(pLevel, i, j, k);
                pBlockEntity.levels = integerBooleanPair.getLeft();
                pBlockEntity.isUpgraded = integerBooleanPair.getRight();
            }

            if (pBlockEntity.levels > 0 && !pBlockEntity.beamSections.isEmpty()) {
                pBlockEntity.applyEffects(pLevel, pPos, pBlockEntity.levels, pBlockEntity.primaryPower, pBlockEntity.isUpgraded ? pBlockEntity.effectAmplifier + 1 : pBlockEntity.effectAmplifier);
                playSound(pLevel, pPos, SoundEvents.BEACON_AMBIENT);
            }
        }

        if (pBlockEntity.lastCheckY >= l) {
            pBlockEntity.lastCheckY = pLevel.getMinBuildHeight() - 1;
            boolean flag = j1 > 0;
            pBlockEntity.beamSections = pBlockEntity.checkingBeamSections;
            if (!pLevel.isClientSide) {
                boolean flag1 = pBlockEntity.levels > 0;
                if (!flag && flag1) {
                    playSound(pLevel, pPos, SoundEvents.BEACON_ACTIVATE);

                    for(ServerPlayer serverplayer : pLevel.getEntitiesOfClass(ServerPlayer.class, (new AABB((double)i, (double)j, (double)k, (double)i, (double)(j - MAX_LEVELS), (double)k)).inflate(10.0D, 5.0D, 10.0D))) {
                        CriteriaTriggers.CONSTRUCT_BEACON.trigger(serverplayer, pBlockEntity.levels);
                    }
                } else if (flag && !flag1) {
                    playSound(pLevel, pPos, SoundEvents.BEACON_DEACTIVATE);
                }
            }
        }

    }

    protected void applyEffects(Level level, BlockPos pos, int levels, @Nullable MobEffect power, int effectAmplifier) {
        if (!level.isClientSide && power != null) {
            if (NO_AMPLIFIER_EFFECTS.contains(power)) {
                effectAmplifier = 0;
            }
            AABB aabb = (new AABB(pos)).inflate(levels * 10 + 10).expandTowards(0.0D, (double) level.getHeight(), 0.0D);
            int effectDuration = (9 + levels * 2) * 20;
            List<Player> list = level.getEntitiesOfClass(Player.class, aabb, Helper::isHunter);
            int finalEffectAmplifier = effectAmplifier;
            list.forEach((player) -> player.addEffect(new MobEffectInstance(power, effectDuration, finalEffectAmplifier, true, true)));
        }
    }

    private static Pair<Integer, Boolean> updateBase(Level pLevel, int pX, int pY, int pZ) {
        int i = 0;
        Optional<Boolean> upgradeFlag = Optional.empty();

        for(int j = 1; j <= MAX_LEVELS; i = j++) {
            int k = pY - j;
            if (k < pLevel.getMinBuildHeight()) {
                break;
            }

            boolean flag = true;
            boolean upgradeFlagLevel = upgradeFlag.orElse(true);

            for(int l = pX - j; l <= pX + j && flag; ++l) {
                for(int i1 = pZ - j; i1 <= pZ + j; ++i1) {
                    BlockState blockState = pLevel.getBlockState(new BlockPos(l, k, i1));
                    if (!blockState.is(ModTags.Blocks.VAMPIRE_BEACON_BASE_BLOCKS)) {
                        flag = false;
                        break;
                    } else if (upgradeFlagLevel) {
                        upgradeFlagLevel = blockState.is(ModTags.Blocks.VAMPIRE_BEACON_BASE_ENHANCED_BLOCKS);
                    }
                }
            }

            if (!flag) {
                break;
            }
            upgradeFlag = Optional.of(upgradeFlag.orElse(true) && upgradeFlagLevel);
        }

        return Pair.of(i, upgradeFlag.orElse(false));
    }

    @Override
    public void setRemoved() {
        playSound(this.level, this.worldPosition, SoundEvents.BEACON_DEACTIVATE);
        super.setRemoved();
    }

    public List<BeaconBlockEntity.BeaconBeamSection> getBeamSections() {
        return this.levels == 0 ? ImmutableList.of() : this.beamSections;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Nullable
    static MobEffect getValidEffectById(int pEffectId) {
        MobEffect mobeffect = BeaconMenu.decodeEffect(pEffectId);
        return VALID_EFFECTS.contains(mobeffect) ? mobeffect : null;
    }

    @Override
    public void load(@NotNull CompoundTag pTag) {
        super.load(pTag);
        this.primaryPower = getValidEffectById(pTag.getInt("Primary"));
        if (pTag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(pTag.getString("CustomName"));
        }
        this.effectAmplifier = pTag.getInt("Amplifier");

        this.lockKey = LockCode.fromTag(pTag);
        this.isUpgraded = pTag.getBoolean("Upgraded");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.putInt("Primary", BeaconMenu.encodeEffect(this.primaryPower));
        pTag.putInt("Levels", this.levels);
        if (this.name != null) {
            pTag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
        pTag.putInt("Amplifier", this.effectAmplifier);

        this.lockKey.addToTag(pTag);
        pTag.putBoolean("Upgraded", this.isUpgraded);
    }

    public void setCustomName(@Nullable Component pName) {
        this.name = pName;
    }

    @Override
    @Nullable
    public Component getCustomName() {
        return this.name;
    }

    @Override
    @Nullable
    public AbstractContainerMenu createMenu(int pContainerId, @NotNull Inventory pPlayerInventory, @NotNull Player pPlayer) {
        return BaseContainerBlockEntity.canUnlock(pPlayer, this.lockKey, this.getDisplayName()) ? new VampireBeaconMenu(pContainerId, pPlayerInventory, this.dataAccess, ContainerLevelAccess.create(this.level, this.getBlockPos())) : null;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return this.getName();
    }

    @Override
    public @NotNull Component getName() {
        return this.name != null ? this.name : DEFAULT_NAME;
    }

    @Override
    public void setLevel(@NotNull Level pLevel) {
        super.setLevel(pLevel);
        this.lastCheckY = pLevel.getMinBuildHeight() - 1;
    }
}
