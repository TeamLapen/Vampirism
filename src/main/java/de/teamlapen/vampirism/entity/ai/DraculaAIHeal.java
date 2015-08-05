package de.teamlapen.vampirism.entity.ai;

import de.teamlapen.vampirism.ModBlocks;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.entity.EntityDracula;
import de.teamlapen.vampirism.network.SpawnCustomParticlePacket;
import de.teamlapen.vampirism.tileEntity.TileEntityBloodAltar2;
import de.teamlapen.vampirism.util.BALANCE;
import de.teamlapen.vampirism.util.Logger;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ChunkCoordinates;

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
    private ChunkCoordinates currentPos;
    private List<ChunkCoordinates> positions = new ArrayList<ChunkCoordinates>();

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
                path = dracula.getNavigator().getPathToXYZ(currentPos.posX, currentPos.posY, currentPos.posZ);
                if (path != null) return true;
                currentPos = null;
                path = null;
            }
            Logger.t("No path");

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
                if (dracula.getNavigator().tryMoveToXYZ(currentPos.posX, currentPos.posY, currentPos.posZ, 0.9F))
                    return true;
            }
        }
        Logger.t("stop");
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
        if (dracula.getDistanceSq(currentPos.posX, currentPos.posY, currentPos.posZ) < 8) {
            atAltar = true;
            dracula.getNavigator().clearPathEntity();
            if (isAltarAtPos(currentPos)) {
                TileEntityBloodAltar2 altar = getAltarTile(currentPos);
                if (altar.getBloodAmount() > 0) {
                    if (dracula.ticksExisted % 5 == 0) {
                        NBTTagCompound data = new NBTTagCompound();
                        data.setInteger("player_id", dracula.getEntityId());
                        data.setBoolean("direct", true);
                        VampirismMod.modChannel.sendToAll(new SpawnCustomParticlePacket(0, currentPos.posX, currentPos.posY + 0.5, currentPos.posZ, 2, data));
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
                    if (ModBlocks.bloodAltar2.equals(dracula.worldObj.getBlock(x, y, z)) && ((TileEntityBloodAltar2) dracula.worldObj.getTileEntity(x, y, z)).getBloodAmount() > 0) {
                        if (dracula.hasHome() && !dracula.isWithinHomeDistance(x, y, z)) {
                            continue;
                        }
                        positions.add(new ChunkCoordinates(x, y, z));
                    }
                }
            }
        }
        checked = true;
        Logger.t("Found %d", positions.size());
    }

    private void updateAltars() {
        Iterator<ChunkCoordinates> iterator = positions.iterator();
        while (iterator.hasNext()) {
            ChunkCoordinates pos = iterator.next();
            if (isAltarAtPos(pos)) {
                if (getAltarTile(pos).getBloodAmount() > 0) {
                    continue;
                }
            }
            iterator.remove();
        }
        Logger.t("%d Left", positions.size());
    }

    private boolean isAltarAtPos(ChunkCoordinates pos) {
        return ModBlocks.bloodAltar2.equals(dracula.worldObj.getBlock(pos.posX, pos.posY, pos.posZ));
    }

    private TileEntityBloodAltar2 getAltarTile(ChunkCoordinates pos) {
        return ((TileEntityBloodAltar2) dracula.worldObj.getTileEntity(pos.posX, pos.posY, pos.posZ));
    }
}
