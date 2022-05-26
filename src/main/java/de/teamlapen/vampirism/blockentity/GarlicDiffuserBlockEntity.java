package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.GarlicDiffuserBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.DamageHandler;
import de.teamlapen.vampirism.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.player.vampire.VampirePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class GarlicDiffuserBlockEntity extends BlockEntity {
    private static final int FUEL_DURATION = 20 * 60 * 2;
    private int id;
    private EnumStrength strength = EnumStrength.MEDIUM;
    private EnumStrength defaultStrength = EnumStrength.MEDIUM;
    private int r = 1;
    private boolean registered = false;
    private int fueled = 0;
    private int bootTimer;
    private int maxBootTimer;
    private boolean initiateBootTimer = false;


    public GarlicDiffuserBlockEntity(BlockPos pos, BlockState state) {
        super(ModTiles.garlic_diffuser.get(), pos, state);
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
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    public boolean isActive() {
        return bootTimer == 0;
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        return this.saveWithoutMetadata();
    }

    /**
     * @return If inside effective distance
     */
    public boolean isInRange(BlockPos pos) {
        return new ChunkPos(this.getBlockPos()).getChessboardDistance(new ChunkPos(pos)) <= r;
    }

    @Override
    public void load(@Nonnull CompoundTag compound) {
        super.load(compound);
        r = compound.getInt("radius");
        defaultStrength = EnumStrength.getFromStrength(compound.getInt("strength"));
        bootTimer = compound.getInt("boot_timer");
        setFueledTime(compound.getInt("fueled"));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (hasLevel()) {
            CompoundTag nbt = pkt.getTag();
            handleUpdateTag(nbt);
            if (isActive()) {
                register(); //Register in case we weren't active before. Shouldn't have an effect when already registered
            }
        }
    }

    public void onTouched(Player player) {
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
    public void saveAdditional(@Nonnull CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("radius", r);
        compound.putInt("strength", defaultStrength.getStrength());
        compound.putInt("fueled", fueled);
        if (bootTimer != 0) {
            compound.putInt("boot_timer", bootTimer);
        }
    }

    public void setNewBootDelay(int delayTicks) {
        this.bootTimer = delayTicks;
        this.maxBootTimer = delayTicks;
    }

    public void initiateBootTimer() {
        this.initiateBootTimer = true;
    }

    public void setType(GarlicDiffuserBlock.Type type) {
        switch (type) {
            case WEAK -> {
                r = VampirismConfig.BALANCE.hsGarlicDiffuserWeakDist.get();
                defaultStrength = EnumStrength.WEAK;
            }
            case NORMAL -> {
                r = VampirismConfig.BALANCE.hsGarlicDiffuserNormalDist.get();
                defaultStrength = EnumStrength.MEDIUM;
            }
            case IMPROVED -> {
                defaultStrength = EnumStrength.MEDIUM;
                r = VampirismConfig.BALANCE.hsGarlicDiffuserEnhancedDist.get();
            }
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


    public static void tick(Level level, BlockPos pos, BlockState state, GarlicDiffuserBlockEntity blockEntity) {
        if (blockEntity.initiateBootTimer) {
            blockEntity.initiateBootTimer = false;
            int bootTime = VampirismConfig.BALANCE.garlicDiffuserStartupTime.get() * 20;
            if (level instanceof ServerLevel) {
                if (((ServerLevel) level).players().size() <= 1) {
                    bootTime >>= 2; // /4
                }
            }
            blockEntity.bootTimer = bootTime;
            blockEntity.maxBootTimer = bootTime;

        }
        if (blockEntity.bootTimer > 0) {
            if (--blockEntity.bootTimer == 0) {
                blockEntity.setChanged();
                blockEntity.register();
            }
        } else if (blockEntity.fueled > 0) {
            if (blockEntity.fueled == 1) {
                blockEntity.setFueledTime(0);
                blockEntity.setChanged();
            } else {
                blockEntity.fueled--;
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
        for (int x = -r; x <= r; x++) {
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
