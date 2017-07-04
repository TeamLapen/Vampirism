package de.teamlapen.vampirism.tileentity;

import de.teamlapen.lib.VampLib;
import de.teamlapen.lib.lib.inventory.InventoryHelper;
import de.teamlapen.lib.lib.inventory.InventorySlot;
import de.teamlapen.lib.lib.tile.InventoryTileEntity;
import de.teamlapen.lib.lib.util.ItemStackUtil;
import de.teamlapen.lib.lib.util.ValuedObject;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.api.entity.factions.IFactionPlayerHandler;
import de.teamlapen.vampirism.blocks.BlockAltarPillar;
import de.teamlapen.vampirism.core.ModBlocks;
import de.teamlapen.vampirism.core.ModItems;
import de.teamlapen.vampirism.core.ModParticles;
import de.teamlapen.vampirism.core.ModPotions;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * TODO make ritual survive load/save
 */
public class TileAltarInfusion extends InventoryTileEntity implements ITickable {

    private final static String TAG = "TEAltarInfusion";
    private static final Item[] items = new Item[]{
            ModItems.pure_blood, ModItems.human_heart, ModItems.vampire_book
    };
    private final int DURATION_TICK = 450;
    /**
     * Only available when running ({@link #runningTick}>0)
     */
    private EntityPlayer player;
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
        super(new InventorySlot[]{new InventorySlot(items[0], 44, 34), new InventorySlot(items[1], 80, 34), new InventorySlot(items[2], 116, 34)});
    }

    /**
     * Checks all the requirements
     *
     * @param player        trying to execute the ritual
     * @param messagePlayer If the player should be notified on fail
     * @return 1 if it can start, 0 if still running, -1 if wrong level, -2 if night only, -3 if structure wrong, -4 if items missing
     */
    public int canActivate(EntityPlayer player, boolean messagePlayer) {
        if (runningTick > 0) {
            if (messagePlayer)
                player.sendMessage(new TextComponentTranslation("text.vampirism.ritual_still_running"));

            return 0;
        }
        this.player = null;
        if (player.getEntityWorld().isDaytime()) {
            if (messagePlayer) player.sendMessage(new TextComponentTranslation("text.vampirism.ritual_night_only"));
            return -2;
        }
        targetLevel = VampirePlayer.get(player).getLevel() + 1;
        int requiredLevel = checkRequiredLevel();
        if (requiredLevel == -1) {
            if (messagePlayer) player.sendMessage(new TextComponentTranslation("text.vampirism.ritual_level_wrong"));
            return -1;
        } else if (!checkStructureLevel(requiredLevel)) {
            if (messagePlayer)
                player.sendMessage(new TextComponentTranslation("text.vampirism.ritual_structure_wrong"));
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
    public String getName() {
        return "tile.vampirism.altar_infusion.name";
    }

    /**
     * Returns the affected player. If the ritual isn't running it returns null
     *
     * @return
     */
    public EntityPlayer getPlayer() {
        if (this.runningTick <= 1)
            return null;
        return this.player;
    }

    @SideOnly(Side.CLIENT)
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
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getUpdateTag());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToNBT(new NBTTagCompound());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void readFromNBT(NBTTagCompound tagCompound) {
        super.readFromNBT(tagCompound);
        int tick = tagCompound.getInteger("tick");
        if (tick > 0 && player == null) {
            try {
                this.player = (EntityPlayer) this.getWorld().getEntityByID(tagCompound.getInteger("playerId"));
                this.runningTick = tick;
                this.targetLevel = VampirePlayer.get(player).getLevel() + 1;
            } catch (NullPointerException e) {
                VampirismMod.log.w(TAG, "Failed to find player %d", tagCompound.getInteger("playerId"));
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
     * ONLY call if {@link TileAltarInfusion#canActivate(EntityPlayer, boolean)} returned 1
     */
    public void startRitual(EntityPlayer player) {
        this.player = player;
        runningTick = DURATION_TICK;

        if (!this.getWorld().isRemote) {
//                for (int i = 0; i < tips.length; i++) {
//                    NBTTagCompound data = new NBTTagCompound();
//                    data.setInteger("destX", tips[i].posX);
//                    data.setInteger("destY", tips[i].posY);
//                    data.setInteger("destZ", tips[i].posZ);
//                    data.setInteger("age", 100);
//                    VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(1, this.xCoord, this.yCoord, this.zCoord, 5, data));
//                }
            IBlockState state = this.getWorld().getBlockState(getPos());
            this.getWorld().notifyBlockUpdate(getPos(), state, state, 3);
        }
        player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, DURATION_TICK, 10));
        this.markDirty();
    }

    @Override
    public void update() {
        runningTick--;
        if (runningTick <= 0)
            return;
        if (player == null || player.isDead) {
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
                    VampirismMod.log.w(TAG, "Player %s changed level while the ritual was running. Cannot levelup.", player);
                    return;
                }
                handler.setFactionLevel(VReference.VAMPIRE_FACTION, handler.getCurrentLevel(VReference.VAMPIRE_FACTION) + 1);
            } else {
                this.world.playSound(player.posX, player.posY, player.posZ, SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.2F) * 0.7F, true);
                this.world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, player.posX, player.posY, player.posZ, 1.0D, 0.0D, 0.0D);
            }
            player.addPotionEffect(new PotionEffect(ModPotions.saturation, 400, 2));

            player.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 400, 2));
            player.addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 400, 2));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        NBTTagCompound nbt = super.writeToNBT(compound);
        nbt.setInteger("tick", runningTick);
        if (player != null) {
            nbt.setInteger("playerId", player.getEntityId());
        }
        return nbt;
    }

    private ItemStack checkAndRemoveItems(int bloodMeta, int blood, int heart, int par3) {
        ItemStack missing = InventoryHelper.checkItems(this, items, new int[]{blood, heart, par3}, new int[]{bloodMeta == 0 ? Integer.MIN_VALUE : -bloodMeta, Integer.MIN_VALUE, Integer.MIN_VALUE});
        if (!ItemStackUtil.isEmpty(missing)) {
            InventoryHelper.removeItems(this, new int[]{blood, heart, par3});
        }
        return missing;
    }

    /**
     * Checks for the requirements for the give player to level up
     *
     * @param messagePlayer If the player should be notified about missing ones
     */
    private boolean checkItemRequirements(EntityPlayer player, boolean messagePlayer) {
        int newLevel = targetLevel;
        ItemStack missing = ItemStackUtil.getEmptyStack();
        switch (newLevel) {
            case 5:
                missing = checkAndRemoveItems(0, 0, 5, 1);
                break;
            case 6:
                missing = checkAndRemoveItems(0, 1, 5, 1);
                break;
            case 7:
                missing = checkAndRemoveItems(0, 1, 10, 1);
                break;
            case 8:
                missing = checkAndRemoveItems(1, 1, 10, 1);
                break;
            case 9:
                missing = checkAndRemoveItems(1, 1, 10, 1);
                break;
            case 10:
                missing = checkAndRemoveItems(2, 1, 15, 1);
                break;
            case 11:
                missing = checkAndRemoveItems(2, 1, 15, 1);
                break;
            case 12:
                missing = checkAndRemoveItems(3, 1, 20, 1);
                break;
            case 13:
                missing = checkAndRemoveItems(3, 2, 20, 1);
                break;
            case 14:
                missing = checkAndRemoveItems(4, 2, 25, 1);
                break;
            default:
                VampirismMod.log.w(TAG, "Checking for level %d, but this altar cannot be used at that level", newLevel);
        }
        if (!ItemStackUtil.isEmpty(missing)) {
            if (messagePlayer) {
                ITextComponent item = missing.getItem().equals(ModItems.pure_blood) ? ModItems.pure_blood.getDisplayName(missing) : new TextComponentTranslation(missing.getUnlocalizedName() + ".name");
                ITextComponent main = new TextComponentTranslation("text.vampirism.ritual_missing_items", ItemStackUtil.getCount(missing), item);
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
        if (newLevel < 5 || newLevel > REFERENCE.HIGHEST_VAMPIRE_LEVEL) {
            return -1;
        }
        int t = (newLevel - 4) / 2;
        return (int) (8 + (54 - 8) * t / 5f);

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
            BlockAltarPillar.EnumPillarType type = null;
            IBlockState temp;
            while ((temp = getWorld().getBlockState(pPos.add(0, -j - 1, 0))).getBlock().equals(ModBlocks.altar_pillar)) {
                BlockAltarPillar.EnumPillarType t = temp.getValue(BlockAltarPillar.typeProperty);
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
     * Finds all {@link de.teamlapen.vampirism.blocks.BlockAltarTip}'s in the area
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
