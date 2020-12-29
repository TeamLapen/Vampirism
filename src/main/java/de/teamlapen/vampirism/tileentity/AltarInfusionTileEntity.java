package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.lib.lib.util.ValuedObject;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.advancements.VampireActionTrigger;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.blocks.AltarPillarBlock;
import de.teamlapen.vampirism.blocks.AltarTipBlock;
import de.teamlapen.vampirism.core.*;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.inventory.container.AltarInfusionContainer;
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.particle.FlyingBloodParticleData;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class AltarInfusionTileEntity extends InventoryTileEntity implements ITickableTileEntity {

    private final static Logger LOGGER = LogManager.getLogger(AltarInfusionTileEntity.class);
    private final int DURATION_TICK = 450;


    /**
     * Used to store a saved player UUID during read until world and player are available
     */
    private UUID playerToLoadUUID;
    /**
     * Only available when running ({@link #runningTick}>0)
     */
    private PlayerEntity player;
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

    private final LazyOptional<IItemHandler> itemHandlerOptional = LazyOptional.of(this::createWrapper);

    public AltarInfusionTileEntity() {
        super(ModTiles.altar_infusion, 3, AltarInfusionContainer.SELECTOR_INFOS);
    }

    /**
     * Checks all the requirements
     *
     * @param player        trying to execute the ritual
     * @param messagePlayer If the player should be notified on fail
     */
    public Result canActivate(PlayerEntity player, boolean messagePlayer) {
        if (runningTick > 0) {
            if (messagePlayer)
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_still_running"), true);

            return Result.ISRUNNING;
        }
        this.player = null;

        targetLevel = VampirePlayer.get(player).getLevel() + 1;
        int requiredLevel = checkRequiredLevel();
        if (requiredLevel == -1) {
            if (messagePlayer)
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_level_wrong"), true);
            return Result.WRONGLEVEL;
        } else if (player.getEntityWorld().isDaytime()) {
            if (messagePlayer)
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_night_only"), true);
            return Result.NIGHTONLY;
        } else if (!checkStructureLevel(requiredLevel)) {
            if (messagePlayer)
                player.sendStatusMessage(new TranslationTextComponent("text.vampirism.altar_infusion.ritual_structure_wrong"), true);
            tips = null;
            return Result.STRUCTUREWRONG;
        } else if (!checkItemRequirements(player, messagePlayer)) {
            tips = null;
            return Result.INVMISSING;
        }
        return Result.OK;

    }

    @Nullable
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
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
     *
     * @return
     */
    public PlayerEntity getPlayer() {
        if (this.runningTick <= 1)
            return null;
        return this.player;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

    public int getRunningTick() {
        return runningTick;
    }

    /**
     * Returns the position of the tips. If the ritual isn't running it returns null
     *
     * @return
     */
    public BlockPos[] getTips() {
        if (this.runningTick <= 1)
            return null;
        return this.tips;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @Override
    public void read(BlockState state, CompoundNBT tagCompound) {
        super.read(state, tagCompound);
        int tick = tagCompound.getInt("tick");
        //This is used on both client and server side and has to be prepared for the world not being available yet
        if (tick > 0 && player == null && tagCompound.hasUniqueId("playerUUID")) {
            UUID playerID = tagCompound.getUniqueId("playerUUID");
            if (!loadRitual(playerID)) {
                this.playerToLoadUUID = playerID;
            }
            this.runningTick = tick;
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (this.hasWorld()) this.read(this.world.getBlockState(pkt.getPos()), pkt.getNbtCompound());
    }

    /**
     * Starts the ritual.
     * ONLY call if {@link AltarInfusionTileEntity#canActivate(PlayerEntity, boolean)} returned 1
     */
    public void startRitual(PlayerEntity player) {
        if (world == null) return;
        LOGGER.debug("Starting ritual for {}", player);
        this.player = player;
        runningTick = DURATION_TICK;

        this.markDirty();
        if (!this.world.isRemote) {
            for (BlockPos pTip : tips) {
                ModParticles.spawnParticlesServer(world, new FlyingBloodParticleData(ModParticles.flying_blood, 60, false, pTip.getX() + 0.5, pTip.getY() + 0.3, pTip.getZ() + 0.5), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3, 0.1, 0.1, 0.1, 0);
            }
            BlockState state = this.world.getBlockState(getPos());
            this.world.notifyBlockUpdate(getPos(), state, state, 3); //Notify client about started ritual
        }
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, DURATION_TICK, 10));
        this.markDirty();
    }

    @Override
    public void tick() {
        if (world == null) return;
        if (playerToLoadUUID != null) { //Restore loaded ritual
            if (!loadRitual(playerToLoadUUID)) return;
            playerToLoadUUID = null;
            this.markDirty();
            BlockState state = this.world.getBlockState(getPos());
            this.world.notifyBlockUpdate(getPos(), state, state, 3); //Notify client about started ritual

        }
        if (runningTick == DURATION_TICK && !world.isRemote) {
            LOGGER.debug("Ritual started");
            consumeItems();
            this.markDirty();
        }
        if (runningTick <= 0)
            return;
        runningTick--;

        if (player == null || !player.isAlive()) {
            runningTick = 1;
        } else {
            if (player.getMotion().y >= 0) {
                player.setMotion(0D, 0D, 0D);
            } else {
                player.setMotion(0D, player.getMotion().y, 0D);
                player.setMotion(player.getMotion().mul(1D, 0.5D, 1D));
            }
        }

        PHASE phase = getCurrentPhase();
        if (this.world.isRemote) {
            if (phase.equals(PHASE.PARTICLE_SPREAD)) {
                if (runningTick % 15 == 0) {
                    BlockPos pos = getPos();
                    for (BlockPos pTip : tips) {
                        ModParticles.spawnParticlesClient(world, new FlyingBloodParticleData(ModParticles.flying_blood, 60, false, pTip.getX() + 0.5, pTip.getY() + 0.3, pTip.getZ() + 0.5), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0, 5, 0.1, new Random());
                    }
                }
            }
            if (runningTick == DURATION_TICK - 200) {
                if (getPlayer().isUser()) {
                    VampirismMod.proxy.renderScreenFullColor(DURATION_TICK - 250, 50, 0xFF0000);
                }
            }
        }
        if (phase.equals(PHASE.CLEAN_UP)) {
            player = null;
            tips = null;
            this.markDirty();
            this.runningTick = 0;
        }
        if (phase.equals(PHASE.LEVELUP)) {
            if (!world.isRemote) {
                IFactionPlayerHandler handler = FactionPlayerHandler.get(player);
                if (handler.getCurrentLevel(VReference.VAMPIRE_FACTION) != targetLevel - 1) {
                    LOGGER.warn("Player {} changed level while the ritual was running. Cannot levelup.", player);
                    return;
                }
                handler.setFactionLevel(VReference.VAMPIRE_FACTION, handler.getCurrentLevel(VReference.VAMPIRE_FACTION) + 1);
                VampirePlayer.get(player).drinkBlood(Integer.MAX_VALUE, 0, false);
                if (player instanceof ServerPlayerEntity) {
                    ModAdvancements.TRIGGER_VAMPIRE_ACTION.trigger((ServerPlayerEntity) player, VampireActionTrigger.Action.PERFORM_RITUAL_INFUSION);
                }
            } else {
                this.world.playSound(player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, true);
                this.world.addParticle(ParticleTypes.EXPLOSION, player.getPosX(), player.getPosY(), player.getPosZ(), 1.0D, 0.0D, 0.0D);
            }

            player.addPotionEffect(new EffectInstance(ModEffects.saturation, 400, 2));
            player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 400, 2));
            player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 400, 2));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        nbt.putInt("tick", runningTick);
        if (player != null) {
            nbt.putUniqueId("playerUUID", player.getUniqueID());
        }
        return nbt;
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return new AltarInfusionContainer(id, player, this, world == null ? IWorldPosCallable.DUMMY : IWorldPosCallable.of(world, pos));
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("tile.vampirism.altar_infusion");
    }

    /**
     * Checks for the requirements for the give player to level up
     *
     * @param messagePlayer If the player should be notified about missing ones
     */
    private boolean checkItemRequirements(PlayerEntity player, boolean messagePlayer) {
        int newLevel = targetLevel;
        VampireLevelingConf.AltarInfusionRequirements requirements = VampireLevelingConf.getInstance().getAltarInfusionRequirements(newLevel);
        ItemStack missing = InventoryHelper.checkItems(this, new Item[]{PureBloodItem.getBloodItemForLevel(requirements.pureBloodLevel), ModItems.human_heart, ModItems.vampire_book}, new int[]{requirements.blood, requirements.heart, requirements.vampireBook}, (supplied, required) -> supplied.equals(required) || (supplied instanceof PureBloodItem && required instanceof PureBloodItem && ((PureBloodItem) supplied).getLevel() >= ((PureBloodItem) required).getLevel()));
        if (!missing.isEmpty()) {
            if (messagePlayer) {
                ITextComponent item = missing.getItem() instanceof PureBloodItem ? ((PureBloodItem) missing.getItem()).getCustomName() : new TranslationTextComponent(missing.getTranslationKey());
                ITextComponent main = new TranslationTextComponent("text.vampirism.altar_infusion.ritual_missing_items", missing.getCount(), item);
                player.sendMessage(main, Util.DUMMY_UUID);
            }

            return false;
        }
        return true;

    }

    /**
     * Determines the structure required for leveling up.
     * The current implementation returns a value between 4 two high stone pillars and 6 three high gold pillars.
     *
     * @return
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
     * Also determines which tips are used for that and stores them in {@link AltarInfusionTileEntity#tips }
     * Used at max the 8 most valued pillars
     *
     * @param required
     * @return
     */
    private boolean checkStructureLevel(int required) {
        if (world == null) return false;
        BlockPos[] tips = findTips();
        ValuedObject<BlockPos>[] valuedTips = new ValuedObject[tips.length];
        for (int i = 0; i < tips.length; i++) {
            BlockPos pPos = tips[i];
            int j = 0;
            AltarPillarBlock.EnumPillarType type = null;
            BlockState temp;
            while ((temp = world.getBlockState(pPos.add(0, -j - 1, 0))).getBlock().equals(ModBlocks.altar_pillar)) {
                AltarPillarBlock.EnumPillarType t = temp.get(AltarPillarBlock.TYPE_PROPERTY);
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
        InventoryHelper.removeItems(this, new int[]{requirements.blood, requirements.heart, requirements.vampireBook});
    }

    /**
     * Finds all {@link AltarTipBlock}'s in the area
     */
    private BlockPos[] findTips() {
        if (world == null) return new BlockPos[0];
        List<BlockPos> list = new ArrayList<>();
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int x = getPos().getX() - 4; x < getPos().getX() + 5; x++) {
            for (int y = getPos().getY() + 1; y < getPos().getY() + 4; y++) {
                for (int z = getPos().getZ() - 4; z < getPos().getZ() + 5; z++) {
                    if (world.getBlockState(pos.setPos(x, y, z)).getBlock().equals(ModBlocks.altar_tip)) {
                        list.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return list.toArray(new BlockPos[0]);
    }

    private boolean loadRitual(UUID playerID) {
        if (this.world == null) return false;
        if (this.world.getPlayers().size() == 0) return false;
        this.player = this.world.getPlayerByUuid(playerID);
        if (this.player != null && player.isAlive()) {
            this.targetLevel = VampirePlayer.get(player).getLevel() + 1;
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
