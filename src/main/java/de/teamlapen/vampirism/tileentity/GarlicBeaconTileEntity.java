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
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;


/**
 * TODO 1.17 refractor garlic diffusor
 */
public class GarlicBeaconTileEntity extends TileEntity implements ITickableTileEntity {
    private int id;
    private EnumStrength strength = EnumStrength.MEDIUM;
    private EnumStrength defaultStrength = EnumStrength.MEDIUM;
    private int r = 1;
    private boolean registered = false;
    private int fueled = 0;
    private int bootTimer;
    private int maxBootTimer;
    private static final int FUEL_DURATION = 20*60*2;


    public GarlicBeaconTileEntity() {
        super(ModTiles.garlic_beacon);
    }

    public int getFuelTime() {
        return fueled;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return this.write(new CompoundNBT());
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (hasWorld()) {
            BlockState state = world.getBlockState(pos);
            this.world.notifyBlockUpdate(pos, state, state, 3);
        }
    }

    @Override
    public void read(BlockState state, CompoundNBT compound) {
        super.read(state, compound);
        r = compound.getInt("radius");
        defaultStrength = EnumStrength.getFromStrenght(compound.getInt("strength"));
        bootTimer = compound.getInt("boot_timer");
        setFueledTime(compound.getInt("fueled"));
    }

    public void onFueled() {
        setFueledTime(FUEL_DURATION);
        this.markDirty();
    }

    public float getFueledState(){
        return this.fueled/(float)FUEL_DURATION;
    }

    public void onTouched(PlayerEntity player) {
        if(VampirismPlayerAttributes.get(player).vampireLevel>0){
            VampirePlayer.getOpt(player).ifPresent(vampirePlayer -> {
                    DamageHandler.affectVampireGarlicDirect(vampirePlayer, strength);
            });
        }

    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        if (hasWorld()) {
            CompoundNBT nbt = pkt.getNbtCompound();
            handleUpdateTag(this.world.getBlockState(pkt.getPos()), nbt);
            if(isActive()){
                register(); //Register in case we weren't active before. Shouldn't have an effect when already registered
            }
        }
    }

    @Override
    public void remove() {
        super.remove();
        unregister();

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
    public void tick() {
        if(bootTimer>0){
            if(--bootTimer==0){
                this.markDirty();
                register();
            }
        }
        else if (fueled > 0) {
            if (fueled == 1) {
                setFueledTime(0);
                this.markDirty();
            } else {
                fueled--;
            }
        }
    }

    public boolean isActive(){
        return bootTimer == 0;
    }

    public float getBootProgress(){
        return bootTimer>0 ? (1-(bootTimer/(float)maxBootTimer)) : 1f;
    }

    public void setNewBootDelay(int delayTicks){
        this.bootTimer  = delayTicks;
        this.maxBootTimer = delayTicks;
    }


    @Override
    public void validate() {
        super.validate();
        if(isActive()){
            register();
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        compound.putInt("radius", r);
        compound.putInt("strength", defaultStrength.getStrength());
        compound.putInt("fueled", fueled);
        if(bootTimer!=0){
            compound.putInt("boot_timer",bootTimer);
        }
        return compound;
    }


    private void register() {
        if (registered || !hasWorld()) {
            return;
        }
        int baseX = (getPos().getX() >> 4);
        int baseZ = (getPos().getZ() >> 4);
        ChunkPos[] chunks = new ChunkPos[(2 * r + 1) * (2 * r + 1)];
        int i = 0;
        for (int x = -r; x <= +r; x++) {
            for (int z = -r; z <= r; z++) {
                chunks[i++] = new ChunkPos(x + baseX, z + baseZ);
            }
        }
        id = VampirismAPI.getVampirismWorld(getWorld()).map(vw->vw.registerGarlicBlock(strength, chunks)).orElse(0);
        registered = i!=0;

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
        if (registered && hasWorld()) {
            VampirismAPI.getVampirismWorld(getWorld()).ifPresent(vw->vw.removeGarlicBlock(id));
            registered = false;
        }
    }
}
