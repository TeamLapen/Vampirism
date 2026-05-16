package de.teamlapen.vampirism.client.resources.sounds;

import de.teamlapen.vampirism.common.core.ModSounds;
import de.teamlapen.vampirism.common.world.blockentity.AltarInfusionBlockEntity;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class SphereSoundInstance extends AbstractTickableSoundInstance {

    private final AltarInfusionBlockEntity blockEntity;

    public SphereSoundInstance(AltarInfusionBlockEntity blockEntity) {
        super(ModSounds.SPHERE_SPINNING.get(), SoundSource.BLOCKS, blockEntity.getLevel().getRandom());
        this.blockEntity = blockEntity;
        this.looping = false;
        this.delay = 0;
        this.volume = 0.75f;
        this.pitch = 1.0f;

        updatePosition();
    }

    @Override
    public boolean canPlaySound() {
        return !isStopped();
    }

    @Override
    public void tick() {
        if (blockEntity.isRemoved()) {
            stop();
            return;
        }

        AltarInfusionBlockEntity.Phase phase = blockEntity.getCurrentPhase();
        if (phase == AltarInfusionBlockEntity.Phase.NOT_RUNNING) {
            stop();
            return;
        }

        updatePosition();
    }

    private void updatePosition() {
        this.x = blockEntity.getBlockPos().getX() + 0.5;
        this.y = blockEntity.getBlockPos().getY() + 0.75 + blockEntity.verticalOffset;
        this.z = blockEntity.getBlockPos().getZ() + 0.5;
    }
}