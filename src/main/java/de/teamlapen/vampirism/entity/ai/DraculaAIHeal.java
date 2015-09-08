package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.util.BALANCE;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Max on 03.08.2015.
 */
public class DraculaAIHeal extends EntityAIBase {

    public static final int THRESHOLD = (int) (BALANCE.MOBPROP.DRACULA_MAX_HEALTH / 3);
    protected EntityDracula dracula;
    boolean checked;
    private boolean atAltar;
    private PathEntity path;
    private BlockPos currentPos;
    private List<BlockPos> positions = new ArrayList<BlockPos>();

    public DraculaAIHeal(EntityDracula dracula) {
        this.dracula = dracula;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (dracula.worldObj == null) return false;
        if (dracula.ticksExisted % 10 != 0) return false;
        if (dracula.getHealth() < THRESHOLD) {
            if (!checked) {
                checkForAltars();
            } else {
                updateAltars();
            }
            if (positions.size() > 0) {
                currentPos = positions.get(dracula.getRNG().nextInt(positions.size()));
                path = dracula.getNavigator().getPathToXYZ(currentPos.getX(), currentPos.getY(), currentPos.getZ());
                if (path != null) return true;
                currentPos = null;
                path = null;
            }
        }
        return false;
    }

    @Override
    public boolean continueExecuting() {
        if (dracula.getHealth() < THRESHOLD + (dracula.getMaxHealth() - THRESHOLD) / 2) {
            if (currentPos != null) {
                //Logger.t("2 %s", dracula.getNavigator().getPath());
                if (atAltar || (dracula.ticksExisted % 40 != 0 && !dracula.getNavigator().noPath())) {
                    //  Logger.t("3");
                    return true;
                }
                if (dracula.getNavigator().tryMoveToXYZ(currentPos.getX(), currentPos.getY(), currentPos.getZ(), 0.9F))
                    return true;
            }
        }
        return false;
    }

    @Override
    public void startExecuting() {
        dracula.getNavigator().setPath(path, 0.9F);
        path = null;
        atAltar = false;
        dracula.freezeSkill();
    }

    @Override
    public void updateTask() {
        boolean remove = false;
        if (dracula.getDistanceSq(currentPos) < 8) {
            atAltar = true;
            dracula.getNavigator().clearPathEntity();
            if (isAltarAtPos(currentPos)) {
                TileEntityBloodAltar2 altar = getAltarTile(currentPos);
                if (altar.getBloodAmount() > 0) {
                    if (dracula.ticksExisted % 5 == 0) {
                        NBTTagCompound data = new NBTTagCompound();
                        data.setInteger("player_id", dracula.getEntityId());
                        data.setBoolean("direct", true);
                        VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(0, currentPos.getX(), currentPos.getY() + 0.5, currentPos.getZ(), 2, data));
                        altar.removeBlood(5);
                        dracula.heal(10F);
                        dracula.addPotionEffect(new PotionEffect(Potion.resistance.id, 6, 2));
                    }

                } else {
                    remove = true;
                }
            } else {
                remove = true;
            }

        } else {
            atAltar = false;
        }

        if (remove) {
            positions.remove(currentPos);
            currentPos = null;

        }
    }

    @Override
    public void resetTask() {
        currentPos = null;
    }

    private void checkForAltars() {
        for (int x = (int) (dracula.posX - 25); x < dracula.posX + 25; x++) {
            for (int y = (int) (dracula.posY - 5); y < dracula.posY + 10; y++) {
                for (int z = (int) (dracula.posZ - 25); z < dracula.posZ + 25; z++) {
                    BlockPos p=new BlockPos(x,y,z);
                    if (ModBlocks.bloodAltar2.equals(dracula.worldObj.getBlockState(p).getBlock()) && ((TileEntityBloodAltar2) dracula.worldObj.getTileEntity(p)).getBloodAmount() > 0) {
                        if (dracula.hasHome() && !dracula.isWithinHomeDistance(x, y, z)) {
                            continue;
                        }
                        positions.add(p);
                    }
                }
            }
        }
        checked = true;
    }

    private void updateAltars() {
        Iterator<BlockPos> iterator = positions.iterator();
        while (iterator.hasNext()) {
            BlockPos pos = iterator.next();
            if (isAltarAtPos(pos)) {
                if (getAltarTile(pos).getBloodAmount() > 0) {
                    continue;
                }
            }
            iterator.remove();
        }
    }

    private boolean isAltarAtPos(BlockPos pos) {
        return ModBlocks.bloodAltar2.equals(dracula.worldObj.getBlockState(pos).getBlock());
    }

    private TileEntityBloodAltar2 getAltarTile(BlockPos pos) {
        return ((TileEntityBloodAltar2) dracula.worldObj.getTileEntity(pos));
    }
}
