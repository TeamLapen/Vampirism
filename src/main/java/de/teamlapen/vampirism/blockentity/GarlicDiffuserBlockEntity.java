package de.teamlapen.vampirism.blockentity;

import de.teamlapen.vampirism.api.EnumStrength;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.blocks.GarlicDiffuserBlock;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.core.ModTiles;
import de.teamlapen.vampirism.entity.player.VampirismPlayerAttributes;
import de.teamlapen.vampirism.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.util.DamageHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class GarlicDiffuserBlockEntity extends BlockEntity {
    private static final int FUEL_DURATION = 20 * 60 * 2;
    private int id;
    private EnumStrength strength = EnumStrength.MEDIUM;
    private @NotNull EnumStrength defaultStrength = EnumStrength.MEDIUM;
    private int r = 1;
    private boolean registered = false;
    private int fueled = 0;
    private int bootTimer = -1;
    private int maxBootTimer;
    private boolean initiateBootTimer = false;


    public GarlicDiffuserBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        super(ModTiles.GARLIC_DIFFUSER.get(), pos, state);
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

    @NotNull
    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    /**
     * @return If inside effective distance
     */
    public boolean isInRange(@NotNull BlockPos pos) {
        return new ChunkPos(this.getBlockPos()).getChessboardDistance(new ChunkPos(pos)) <= r;
    }

    @Override
    public void loadAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        super.loadAdditional(compound, provider);
        r = compound.getInt("radius");
        defaultStrength = EnumStrength.getFromStrength(compound.getInt("strength"));
        bootTimer = compound.getInt("boot_timer");
        maxBootTimer = compound.contains("max_boot_timer") ? compound.getInt("max_boot_timer") : 1;
        setFueledTime(compound.getInt("fueled"));
    }

    @Override
    public void onDataPacket(Connection net, @NotNull ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider provider) {
        if (hasLevel()) {
            CompoundTag nbt = pkt.getTag();
            handleUpdateTag(nbt, provider);
            if (isActive()) {
                register(); //Register in case we weren't active before. Shouldn't have an effect when already registered
            }
        }
    }

    public void onTouched(@NotNull Player player) {
        if (VampirismPlayerAttributes.get(player).vampireLevel > 0) {
            DamageHandler.affectVampireGarlicDirect(VampirePlayer.get(player), strength);
        }

    }

    public void onFueled() {
        setFueledTime(FUEL_DURATION);
        this.updateLevel();
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag compound, HolderLookup.Provider provider) {
        super.saveAdditional(compound, provider);
        compound.putInt("radius", r);
        compound.putInt("strength", defaultStrength.getStrength());
        compound.putInt("fueled", fueled);
        if (bootTimer != 0) {
            compound.putInt("boot_timer", bootTimer);
            compound.putInt("max_boot_timer", maxBootTimer);
        }
    }

    public void setNewBootDelay(int delayTicks) {
        this.bootTimer = delayTicks;
        this.maxBootTimer = delayTicks;
    }

    public void initiateBootTimer() {
        this.initiateBootTimer = true;
    }

    public void setType(GarlicDiffuserBlock.@NotNull Type type) {
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

    public void updateLevel() {
        this.setChanged();
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


    public static void tick(Level level, BlockPos pos, BlockState state, @NotNull GarlicDiffuserBlockEntity blockEntity) {
        if (blockEntity.initiateBootTimer && !level.isClientSide) {
            blockEntity.initiateBootTimer = false;
            int bootTime = VampirismConfig.BALANCE.garlicDiffuserStartupTime.get() * 20;
            if (level instanceof ServerLevel serverLevel) {
                if (serverLevel.players().size() <= 1) {
                    bootTime >>= 2; // /4
                }
            }
            blockEntity.bootTimer = bootTime;
            blockEntity.maxBootTimer = bootTime;
            blockEntity.updateLevel();
        }
        if (blockEntity.bootTimer > 0) {
            if (--blockEntity.bootTimer == 0) {
                blockEntity.updateLevel();
                blockEntity.register();
            }
        } else if (blockEntity.fueled > 0) {
            if (blockEntity.fueled == 1) {
                blockEntity.setFueledTime(0);
                blockEntity.updateLevel();
            } else {
                blockEntity.fueled--;
                blockEntity.setChanged();
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
        id = VampirismAPI.garlicHandler(getLevel()).registerGarlicBlock(strength, chunks);
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
            VampirismAPI.garlicHandler(getLevel()).removeGarlicBlock(id);
            registered = false;
        }
    }
}
