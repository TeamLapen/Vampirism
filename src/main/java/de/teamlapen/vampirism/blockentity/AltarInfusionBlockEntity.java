package de.teamlapen.vampirism.blockentity;

import de.teamlapen.lib.lib.blockentity.InventoryBlockEntity;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.util.ValuedObject;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.blocks.AltarTipBlock;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.AltarInfusionContainer;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class AltarInfusionBlockEntity extends InventoryBlockEntity {

    private final static Logger LOGGER = LogManager.getLogger(AltarInfusionBlockEntity.class);
    private static final int DURATION_TICK = 450;
    private final LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.of(this::createWrapper);
    /**
     * Used to store a saved player UUID during read until world and player are available
     */
    private UUID playerToLoadUUID;
    /**
     * Only available when running ({@link #runningTick}>0)
     */
    private Player player;
    /**
     * Only available when running ({@link #runningTick}>0)
     */
    private BlockPos[] tips;
    private int runningTick;
    /**
     * The level the player will be after the levelup.
     * Only available when running ({@link #runningTick}>0)
     */
    private int targetLevel;

    public AltarInfusionBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.ALTAR_INFUSION.get(), pos, state, 3, AltarInfusionContainer.SELECTOR_INFOS);
    }

    /**
     * Checks all the requirements
     *
     * @param player        trying to execute the ritual
     * @param messagePlayer If the player should be notified on fail
     */
    public Result canActivate(Player player, boolean messagePlayer) {
        if (runningTick > 0) {
            if (messagePlayer)
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_still_running"), true);

            return Result.ISRUNNING;
        }
        this.player = null;

        targetLevel = VampirismPlayerAttributes.get(player).vampireLevel + 1;
        int requiredLevel = checkRequiredLevel();
        if (requiredLevel == -1) {
            if (messagePlayer)
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_level_wrong"), true);
            return Result.WRONGLEVEL;
        } else if (player.getCommandSenderWorld().isDay()) {
            if (messagePlayer)
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_night_only"), true);
            return Result.NIGHTONLY;
        } else if (!checkStructureLevel(requiredLevel)) {
            if (messagePlayer)
                player.displayClientMessage(Component.translatable("text.vampirism.altar_infusion.ritual_structure_wrong"), true);
            tips = null;
            return Result.STRUCTUREWRONG;
        } else if (!checkItemRequirements(player, messagePlayer)) {
            tips = null;
            return Result.INVMISSING;
        }
        return Result.OK;

    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandlerOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Returns the phase the ritual is in
     */
    public PHASE getCurrentPhase() {
        if (runningTick < 1) {
            return PHASE.NOT_RUNNING;
        }
        if (runningTick == 1) {
            return PHASE.CLEAN_UP;
        }
        if (runningTick > (DURATION_TICK - 100)) {
            return PHASE.PARTICLE_SPREAD;
        }
        if (runningTick < DURATION_TICK - 160 && runningTick >= (DURATION_TICK - 200)) {
            return PHASE.BEAM1;
        }
        if (runningTick < (DURATION_TICK - 200) && (runningTick > 50)) {
            return PHASE.BEAM2;
        }
        if (runningTick == 50) {
            return PHASE.LEVELUP;
        }
        if (runningTick < 50) {
            return PHASE.ENDING;
        }
        return PHASE.WAITING;
    }

    /**
     * Returns the affected player. If the ritual isn't running it returns null
     */
    public Player getPlayer() {
        if (this.runningTick <= 1)
            return null;
        return this.player;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public int getRunningTick() {
        return runningTick;
    }

    /**
     * Returns the position of the tips. If the ritual isn't running it returns null
     */
    public BlockPos[] getTips() {
        if (this.runningTick <= 1)
            return null;
        return this.tips;
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @NotNull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    @Override
    public void load(@NotNull CompoundTag tagCompound) {
        super.load(tagCompound);
        int tick = tagCompound.getInt("tick");
        //This is used on both client and server side and has to be prepared for the world not being available yet
        if (tick > 0 && player == null && tagCompound.hasUUID("playerUUID")) {
            UUID playerID = tagCompound.getUUID("playerUUID");
            if (!loadRitual(playerID)) {
                this.playerToLoadUUID = playerID;
            }
            this.runningTick = tick;
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (this.hasLevel()) this.load(pkt.getTag());
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("tick", runningTick);
        if (player != null) {
            compound.putUUID("playerUUID", player.getUUID());
        }
    }

    /**
     * Starts the ritual.
     * ONLY call if {@link AltarInfusionBlockEntity#canActivate(Player, boolean)} returned 1
     */
    public void startRitual(Player player) {
        if (level == null) return;
        LOGGER.debug("Starting ritual for {}", player);
        this.player = player;
        runningTick = DURATION_TICK;

        this.setChanged();
        if (!this.level.isClientSide) {
            for (BlockPos pTip : tips) {
                ModParticles.spawnParticlesServer(level, new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), 60, false, pTip.getX() + 0.5, pTip.getY() + 0.3, pTip.getZ() + 0.5), worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0);
            }
            BlockState state = this.level.getBlockState(getBlockPos());
            this.level.sendBlockUpdated(getBlockPos(), state, state, 3); //Notify client about started ritual
        }
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, DURATION_TICK, 10));
        this.setChanged();
    }

    /**
     * Called when the ritual is running
     */
    private void tickRitual() {
        if (player == null || !player.isAlive()) {
            runningTick = 1;
        } else {
            if (player.getDeltaMovement().y >= 0) {
                player.setDeltaMovement(0D, 0D, 0D);
            } else {
                player.setDeltaMovement(0D, player.getDeltaMovement().y, 0D);
                player.setDeltaMovement(player.getDeltaMovement().multiply(1D, 0.5D, 1D));
            }
        }

        PHASE phase = getCurrentPhase();
        if (this.level.isClientSide) {
            if (phase.equals(PHASE.PARTICLE_SPREAD)) {
                if (runningTick % 15 == 0) {
                    BlockPos pos = getBlockPos();
                    for (BlockPos pTip : tips) {
                        ModParticles.spawnParticlesClient(level, new FlyingBloodParticleData(ModParticles.FLYING_BLOOD.get(), 60, false, pTip.getX() + 0.5, pTip.getY() + 0.3, pTip.getZ() + 0.5), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0, 5, 0.1, RandomSource.create());
                    }
                }
            }
            if (runningTick == DURATION_TICK - 200) {
                if (getPlayer().isLocalPlayer()) {
                    VampirismMod.proxy.renderScreenFullColor(DURATION_TICK - 250, 50, 0xFF0000);
                }
            }
        }
        if (phase.equals(PHASE.CLEAN_UP)) {
            player = null;
            tips = null;
            this.setChanged();
            this.runningTick = 0;
        }
        if (phase.equals(PHASE.LEVELUP)) {
            if (!level.isClientSide) {
                assert player.isAlive();
                LazyOptional<FactionPlayerHandler> handler = FactionPlayerHandler.getOpt(player);
                if (handler.map(h->h.getCurrentLevel(VReference.VAMPIRE_FACTION) != targetLevel - 1).orElse(false)) {
                    LOGGER.warn("Player {} changed level while the ritual was running. Cannot levelup.", player);
                    return;
                }
                handler.ifPresent(h->h.setFactionLevel(VReference.VAMPIRE_FACTION, h.getCurrentLevel(VReference.VAMPIRE_FACTION) + 1));
                VampirePlayer.getOpt(player).ifPresent(v -> v.drinkBlood(Integer.MAX_VALUE, 0, false));
                if (player instanceof ServerPlayer serverPlayer) {
                    ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger(serverPlayer, VampireActionTrigger.Action.PERFORM_RITUAL_INFUSION);
                }
            } else {
                this.level.playLocalSound(player.getX(), player.getY(), player.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, true);
                this.level.addParticle(ParticleTypes.EXPLOSION, player.getX(), player.getY(), player.getZ(), 1.0D, 0.0D, 0.0D);
            }

            player.addEffect(new MobEffectInstance(ModEffects.SATURATION.get(), 400, 2));
            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 400, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 400, 2));
        }
    }

    public static void tick(Level level, BlockPos pos, BlockState state, AltarInfusionBlockEntity blockEntity) {
        if (blockEntity.playerToLoadUUID != null) { //Restore loaded ritual
            if (!blockEntity.loadRitual(blockEntity.playerToLoadUUID)) return;
            blockEntity.playerToLoadUUID = null;
            blockEntity.setChanged();
            level.sendBlockUpdated(pos, state, state, 3); //Notify client about started ritual

        }
        if (blockEntity.runningTick == DURATION_TICK && !level.isClientSide) {
            LOGGER.debug("Ritual started");
            blockEntity.consumeItems();
            blockEntity.setChanged();
        }
        if (blockEntity.runningTick > 0) {
            blockEntity.runningTick--;
            blockEntity.tickRitual();
        }
    }

    @NotNull
    @Override
    protected AbstractContainerMenu createMenu(int id, @NotNull Inventory player) {
        return new AltarInfusionContainer(id, player, this, level == null ? ContainerLevelAccess.NULL : ContainerLevelAccess.create(level, worldPosition));
    }

    @NotNull
    @Override
    protected Component getDefaultName() {
        return Component.translatable("tile.vampirism.altar_infusion");
    }

    /**
     * Checks for the requirements for the give player to level up
     *
     * @param messagePlayer If the player should be notified about missing ones
     */
    private boolean checkItemRequirements(Player player, boolean messagePlayer) {
        int newLevel = targetLevel;
        VampireLevelingConf.AltarInfusionRequirements requirements = VampireLevelingConf.getInstance().getAltarInfusionRequirements(newLevel);
        ItemStack missing = InventoryHelper.checkItems(this, new Item[] {
                PureBloodItem.getBloodItemForLevel(requirements.pureBloodLevel()), ModItems.HUMAN_HEART.get(), ModItems.VAMPIRE_BOOK.get() },
                new int[]{ requirements.blood(), requirements.heart(), requirements.vampireBook() },
                (supplied, required) -> supplied.equals(required) || (supplied instanceof PureBloodItem suppliedBlood && required instanceof PureBloodItem requiredBlood && suppliedBlood.getLevel() >= requiredBlood.getLevel()));

        if (!missing.isEmpty()) {
            if (messagePlayer) {
                Component item = missing.getItem() instanceof PureBloodItem pureBloodItem? pureBloodItem.getCustomName() : Component.translatable(missing.getDescriptionId());
                Component main = Component.translatable("text.vampirism.altar_infusion.ritual_missing_items", missing.getCount(), item);
                player.sendSystemMessage(main);
            }

            return false;
        }
        return true;

    }

    /**
     * Determines the structure required for leveling up.
     * The current implementation returns a value between 4 two high stone pillars and 6 three high gold pillars.
     */
    private int checkRequiredLevel() {
        int newLevel = targetLevel;

        if (!VampireLevelingConf.getInstance().isLevelValidForAltarInfusion(newLevel)) {
            return -1;
        }
        return VampireLevelingConf.getInstance().getRequiredStructureLevelAltarInfusion(newLevel);

    }

    /**
     * Checks if the structure around the altar is at least the required one.
     * Also determines which tips are used for that and stores them in {@link AltarInfusionBlockEntity#tips }
     * Used at max the 8 most valued pillars
     */
    private boolean checkStructureLevel(int required) {
        if (level == null) return false;
        BlockPos[] tips = findTips();
        @SuppressWarnings("unchecked")
        ValuedObject<BlockPos>[] valuedTips = new ValuedObject[tips.length];
        for (int i = 0; i < tips.length; i++) {
            BlockPos pPos = tips[i];
            int j = 0;
            AltarPillarBlock.EnumPillarType type = null;
            BlockState temp;
            while ((temp = level.getBlockState(pPos.offset(0, -j - 1, 0))).getBlock().equals(ModBlocks.ALTAR_PILLAR.get())) {
                AltarPillarBlock.EnumPillarType t = temp.getValue(AltarPillarBlock.TYPE_PROPERTY);
                if (type == null) {
                    type = t;
                    j++;
                } else if (type.equals(t)) {
                    j++;
                } else {
                    break;
                }
            }

            int value = (int) (10 * Math.min(j, 3) * (type == null ? 0 : type.getValue()));
            valuedTips[i] = new ValuedObject<>(tips[i], value);
        }
        Arrays.sort(valuedTips, ValuedObject.getInvertedComparator());
        int found = 0;
        int i = 0;
        //Valued tips are multiplied by 10, so have to multiply required with 10 as well
        while (found < required * 10 && i < valuedTips.length && i < 9) {
            int v = valuedTips[i].value;
            if (v == 0) break;
            found += v;
            i++;
        }
        valuedTips = Arrays.copyOfRange(valuedTips, 0, i);
        this.tips = ValuedObject.extract(BlockPos.class, valuedTips);

        return found >= required * 10;

    }

    /**
     * Consume the required tileInventory
     */
    private void consumeItems() {
        VampireLevelingConf.AltarInfusionRequirements requirements = VampireLevelingConf.getInstance().getAltarInfusionRequirements(targetLevel);
        InventoryHelper.removeItems(this, new int[]{requirements.blood(), requirements.heart(), requirements.vampireBook()});
    }

    /**
     * Finds all {@link AltarTipBlock}'s in the area
     */
    private BlockPos[] findTips() {
        if (level == null) return new BlockPos[0];
        List<BlockPos> list = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = getBlockPos().getX() - 4; x < getBlockPos().getX() + 5; x++) {
            for (int y = getBlockPos().getY() + 1; y < getBlockPos().getY() + 4; y++) {
                for (int z = getBlockPos().getZ() - 4; z < getBlockPos().getZ() + 5; z++) {
                    if (level.getBlockState(pos.set(x, y, z)).getBlock().equals(ModBlocks.ALTAR_TIP.get())) {
                        list.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return list.toArray(new BlockPos[0]);
    }

    private boolean loadRitual(UUID playerID) {
        if (this.level == null) return false;
        if (this.level.players().size() == 0) return false;
        this.player = this.level.getPlayerByUUID(playerID);
        if (this.player != null && player.isAlive()) {
            this.targetLevel = VampirismPlayerAttributes.get(player).vampireLevel + 1;
            checkStructureLevel(checkRequiredLevel());
        } else {
            runningTick = 0;
            this.tips = null;
            LOGGER.warn("Failed to find player {}", playerID);

        }
        return true;
    }

    public enum PHASE {
        NOT_RUNNING, PARTICLE_SPREAD, BEAM1, BEAM2, WAITING, LEVELUP, ENDING, CLEAN_UP
    }

    public enum Result {
        OK, ISRUNNING, WRONGLEVEL, NIGHTONLY, STRUCTUREWRONG, INVMISSING
    }
}
