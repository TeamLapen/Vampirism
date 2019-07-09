package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.inventory.InventorySlot;
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
import de.teamlapen.vampirism.items.PureBloodItem;
import de.teamlapen.vampirism.player.vampire.VampireLevelingConf;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.init.Particles;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.*;

/**
 * TODO make ritual survive load/save
 */
public class TileAltarInfusion extends InventoryTileEntity implements ITickableTileEntity {

    private final static Logger LOGGER = LogManager.getLogger(TileAltarInfusion.class);
    private final int DURATION_TICK = 450;
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

    public TileAltarInfusion() {
        super(ModTiles.altar_infusion, new InventorySlot[]{new InventorySlot(PureBloodItem.class, 44, 34), new InventorySlot(ModItems.human_heart, 80, 34), new InventorySlot(ModItems.vampire_book, 116, 34)});
    }

    /**
     * Checks all the requirements
     *
     * @param player        trying to execute the ritual
     * @param messagePlayer If the player should be notified on fail
     * @return 1 if it can start, 0 if still running, -1 if wrong level, -2 if night only, -3 if structure wrong, -4 if items missing
     */
    public int canActivate(PlayerEntity player, boolean messagePlayer) {
        if (runningTick > 0) {
            if (messagePlayer)
                player.sendMessage(new TranslationTextComponent("text.vampirism.ritual_still_running"));

            return 0;
        }
        this.player = null;
        if (player.getEntityWorld().isDaytime()) {
            if (messagePlayer) player.sendMessage(new TranslationTextComponent("text.vampirism.ritual_night_only"));
            return -2;
        }
        targetLevel = VampirePlayer.get(player).getLevel() + 1;
        int requiredLevel = checkRequiredLevel();
        if (requiredLevel == -1) {
            if (messagePlayer) player.sendMessage(new TranslationTextComponent("text.vampirism.ritual_level_wrong"));
            return -1;
        } else if (!checkStructureLevel(requiredLevel)) {
            if (messagePlayer)
                player.sendMessage(new TranslationTextComponent("text.vampirism.ritual_structure_wrong"));
            tips = null;
            return -3;
        } else if (!checkItemRequirements(player, messagePlayer)) {
            tips = null;
            return -4;
        }
        return 1;

    }

    /**
     * Returns the phase the ritual is in
     *
     * @return
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

    @Override
    public ITextComponent getName() {
        return new StringTextComponent("tile.vampirism.altar_infusion.name");
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

    @Override
    public CompoundNBT getUpdateTag() {
        return write(new CompoundNBT());
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.read(pkt.getNbtCompound());
    }

    @Override
    public void read(CompoundNBT tagCompound) {
        super.read(tagCompound);
        int tick = tagCompound.getInt("tick");
        if (tick > 0 && player == null) {
                this.player = this.getWorld().getPlayerEntityByUUID(UUID.fromString(tagCompound.getString("playerUUID")));
            if (this.player != null) {
                this.runningTick = tick;
                this.targetLevel = VampirePlayer.get(player).getLevel() + 1;
            } else {
                LOGGER.warn("Failed to find player {}", tagCompound.getInt("playerUUID"));

            }

        }
        if (player == null) {
            this.runningTick = 0;
            this.tips = null;
        } else {
            checkStructureLevel(checkRequiredLevel());
        }
    }

    /**
     * Starts the ritual.
     * ONLY call if {@link TileAltarInfusion#canActivate(PlayerEntity, boolean)} returned 1
     */
    public void startRitual(PlayerEntity player) {
        LOGGER.debug("Starting ritual for {}", player);
        this.player = player;
        runningTick = DURATION_TICK;

        this.markDirty();
        if (!this.getWorld().isRemote) {
//                for (int i = 0; i < tips.length; i++) {
//                    NBTTagCompound data = new NBTTagCompound();
//                    data.setInteger("destX", tips[i].posX);
//                    data.setInteger("destY", tips[i].posY);
//                    data.setInteger("destZ", tips[i].posZ);
//                    data.setInteger("age", 100);
//                    VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(1, this.xCoord, this.yCoord, this.zCoord, 5, data));
//                }
            BlockState state = this.getWorld().getBlockState(getPos());
            this.getWorld().notifyBlockUpdate(getPos(), state, state, 3); //Notify client about started ritual
        }
        player.addPotionEffect(new EffectInstance(Effects.RESISTANCE, DURATION_TICK, 10));
        this.markDirty();
    }

    @Override
    public void tick() {
        if (runningTick == DURATION_TICK && !world.isRemote) {
            LOGGER.debug("Ritual started");
            consumeItems();
            this.markDirty();
        }
        runningTick--;
        if (runningTick <= 0)
            return;
        if (player == null || !player.isAlive()) {
            runningTick = 1;
        } else {
            player.motionX = 0;
            if (player.motionY >= 0) {
                player.motionY = 0;
            } else {
                player.motionY = player.motionY / 2;
            }
            player.motionZ = 0;
        }

        PHASE phase = getCurrentPhase();
        if (this.world.isRemote) {
            if (phase.equals(PHASE.PARTICLE_SPREAD)) {
                if (runningTick % 15 == 0) {
                    BlockPos pos = getPos();
                    for (BlockPos pTip : tips) {
                        VampLib.proxy.getParticleHandler().spawnParticles(world, ModParticles.FLYING_BLOOD, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.1, new Random(), pTip.getX() + 0.5, pTip.getY() + 0.3, pTip.getZ() + 0.5, 60);
                    }
                }
            }
            if (runningTick == DURATION_TICK - 200) {
                if (VampirismMod.proxy.isPlayerThePlayer(getPlayer())) {
                    VampirismMod.proxy.renderScreenFullColor(DURATION_TICK - 250, 50, 0xFF0000);

                }
            }
        }
        if (phase.equals(PHASE.CLEAN_UP)) {
            player = null;
            tips = null;
            this.markDirty();
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
                this.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, true);
                this.world.addParticle(Particles.EXPLOSION, player.posX, player.posY, player.posZ, 1.0D, 0.0D, 0.0D);//TODO was Explosion_huge
            }

            player.addPotionEffect(new EffectInstance(ModPotions.saturation, 400, 2));
            player.addPotionEffect(new EffectInstance(Effects.REGENERATION, 400, 2));
            player.addPotionEffect(new EffectInstance(Effects.STRENGTH, 400, 2));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        CompoundNBT nbt = super.write(compound);
        nbt.putInt("tick", runningTick);
        if (player != null) {
            nbt.putString("playerUUID", player.getUniqueID().toString());
        }
        return nbt;
    }

    /**
     * Checks for the requirements for the give player to level up
     *
     * @param messagePlayer If the player should be notified about missing ones
     */
    private boolean checkItemRequirements(PlayerEntity player, boolean messagePlayer) {
        int newLevel = targetLevel;
        VampireLevelingConf.AltarInfusionRequirements requirements = VampireLevelingConf.getInstance().getAltarInfusionRequirements(newLevel);
        ItemStack missing = InventoryHelper.checkItems(this, new Item[]{
                PureBloodItem.getBloodItemForLevel(requirements.pureBloodLevel), ModItems.human_heart, ModItems.vampire_book
        }, new int[]{requirements.blood, requirements.heart, requirements.vampireBook});
        if (!missing.isEmpty()) {
            if (messagePlayer) {
                ITextComponent item = missing.getItem().equals(ModItems.pure_blood_0) ? ModItems.pure_blood_0.getDisplayName(missing) : new TranslationTextComponent(missing.getTranslationKey() + ".name");
                ITextComponent main = new TranslationTextComponent("text.vampirism.ritual_missing_items", missing.getCount(), item);
                player.sendMessage(main);
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
     * Also determines which tips are used for that and stores them in {@link TileAltarInfusion#tips }
     * Used at max the 8 most valued pillars
     *
     * @param required
     * @return
     */
    private boolean checkStructureLevel(int required) {
        BlockPos[] tips = findTips();
        ValuedObject<BlockPos>[] valuedTips = new ValuedObject[tips.length];
        for (int i = 0; i < tips.length; i++) {
            BlockPos pPos = tips[i];
            int j = 0;
            AltarPillarBlock.EnumPillarType type = null;
            BlockState temp;
            while ((temp = getWorld().getBlockState(pPos.add(0, -j - 1, 0))).getBlock().equals(ModBlocks.altar_pillar)) {
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
     * Consume the required items
     */
    private void consumeItems() {
        VampireLevelingConf.AltarInfusionRequirements requirements = VampireLevelingConf.getInstance().getAltarInfusionRequirements(targetLevel);
        InventoryHelper.removeItems(this, new int[]{requirements.blood, requirements.heart, requirements.vampireBook});
    }

    /**
     * Finds all {@link AltarTipBlock}'s in the area
     *
     * @return
     */
    private BlockPos[] findTips() {
        List<BlockPos> list = new ArrayList<>();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int x = getPos().getX() - 4; x < getPos().getX() + 5; x++) {
            for (int y = getPos().getY() + 1; y < getPos().getY() + 4; y++) {
                for (int z = getPos().getZ() - 4; z < getPos().getZ() + 5; z++) {
                    if (getWorld().getBlockState(pos.setPos(x, y, z)).getBlock().equals(ModBlocks.altar_tip)) {
                        list.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return list.toArray(new BlockPos[list.size()]);
    }

    public enum PHASE {
        NOT_RUNNING, PARTICLE_SPREAD, BEAM1, BEAM2, WAITING, LEVELUP, ENDING, CLEAN_UP
    }
}
