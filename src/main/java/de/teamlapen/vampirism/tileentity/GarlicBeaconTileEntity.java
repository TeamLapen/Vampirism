package de.teamlapen.vampirism.tileentity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.GarlicBeaconBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


/**
 * TODO 1.17 refractor garlic diffusor
 */
public class GarlicBeaconTileEntity extends TileEntity implements ITickableTileEntity {
    private static final int FUEL_DURATION = 20 * 60 * 2;
    private int id;
    private EnumStrength strength = EnumStrength.MEDIUM;
    private EnumStrength defaultStrength = EnumStrength.MEDIUM;
    private int r = 1;
    private boolean registered = false;
    private int fueled = 0;
    private int bootTimer;
    private int maxBootTimer;


    public GarlicBeaconTileEntity() {
        super(ModTiles.GARLIC_BEACON.get());
    }

    public float getBootProgress() {
        return bootTimer > 0 ? (1 - (bootTimer / (float) maxBootTimer)) : 1f;
    }

    public int getFuelTime() {
        return fueled;
    }

    public float getFueledState() {
        return this.fueled / (float) FUEL_DURATION;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        if (isActive()) {
            register();
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getBlockPos(), 1, getUpdateTag());
    }

    public boolean isActive() {
        return bootTimer == 0;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.save(new CompoundNBT());
    }

    /**
     * @return If inside effective distance
     */
    public boolean isInRange(BlockPos pos) {
        return new ChunkPos(this.getBlockPos()).getChessboardDistance(new ChunkPos(pos)) <= r;
    }

    @Override
    public void load(BlockState state, CompoundNBT compound) {
        super.load(state, compound);
        r = compound.getInt("radius");
        defaultStrength = EnumStrength.getFromStrenght(compound.getInt("strength"));
        bootTimer = compound.getInt("boot_timer");
        setFueledTime(compound.getInt("fueled"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (hasLevel()) {
            CompoundNBT nbt = pkt.getTag();
            handleUpdateTag(this.level.getBlockState(pkt.getPos()), nbt);
            if (isActive()) {
                register(); //Register in case we weren't active before. Shouldn't have an effect when already registered
            }
        }
    }

    public void onTouched(PlayerEntity player) {
        if (VampirismPlayerAttributes.get(player).vampireLevel > 0) {
            VampirePlayer.getOpt(player).ifPresent(vampirePlayer -> {
                DamageHandler.affectVampireGarlicDirect(vampirePlayer, strength);
            });
        }

    }

    public void onFueled() {
        setFueledTime(FUEL_DURATION);
        this.setChanged();
    }

    @Override
    public CompoundNBT save(CompoundNBT compound) {
        super.save(compound);
        compound.putInt("radius", r);
        compound.putInt("strength", defaultStrength.getStrength());
        compound.putInt("fueled", fueled);
        if (bootTimer != 0) {
            compound.putInt("boot_timer", bootTimer);
        }
        return compound;
    }

    public void setNewBootDelay(int delayTicks) {
        this.bootTimer = delayTicks;
        this.maxBootTimer = delayTicks;
    }

    public void setType(GarlicBeaconBlock.Type type) {
        switch (type) {
            case WEAK:
                r = VampirismConfig.BALANCE.hsGarlicDiffusorWeakDist.get();
                defaultStrength = EnumStrength.WEAK;
                break;
            case NORMAL:
                r = VampirismConfig.BALANCE.hsGarlicDiffusorNormalDist.get();
                defaultStrength = EnumStrength.MEDIUM;
                break;
            case IMPROVED:
                defaultStrength = EnumStrength.MEDIUM;
                r = VampirismConfig.BALANCE.hsGarlicDiffusorEnhancedDist.get();
                break;
        }
        strength = defaultStrength;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (hasLevel()) {
            BlockState state = level.getBlockState(worldPosition);
            this.level.sendBlockUpdated(worldPosition, state, state, 3);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        unregister();

    }

    @Override
    public void tick() {
        if (bootTimer > 0) {
            if (--bootTimer == 0) {
                this.setChanged();
                register();
            }
        } else if (fueled > 0) {
            if (fueled == 1) {
                setFueledTime(0);
                this.setChanged();
            } else {
                fueled--;
            }
        }
    }

    private void register() {
        if (registered || !hasLevel()) {
            return;
        }
        int baseX = (getBlockPos().getX() >> 4);
        int baseZ = (getBlockPos().getZ() >> 4);
        ChunkPos[] chunks = new ChunkPos[(2 * r + 1) * (2 * r + 1)];
        int i = 0;
        for (int x = -r; x <= +r; x++) {
            for (int z = -r; z <= r; z++) {
                chunks[i++] = new ChunkPos(x + baseX, z + baseZ);
            }
        }
        id = VampirismAPI.getVampirismWorld(getLevel()).map(vw -> vw.registerGarlicBlock(strength, chunks)).orElse(0);
        registered = i != 0;

    }

    private void setFueledTime(int time) {
        int old = fueled;
        fueled = time;
        if (fueled > 0) {
            strength = EnumStrength.STRONG;
        } else {
            strength = defaultStrength;
        }
        if (time > 0 && old == 0 || time == 0 && old > 0) {
            if (!isRemoved()) {
                unregister();
                register();
            }
        }
    }

    private void unregister() {
        if (registered && hasLevel()) {
            VampirismAPI.getVampirismWorld(getLevel()).ifPresent(vw -> vw.removeGarlicBlock(id));
            registered = false;
        }
    }
}
