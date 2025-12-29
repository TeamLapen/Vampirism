package de.teamlapen.vampirism.misc.mixin.client;

import de.teamlapen.vampirism.misc.extension.client.IVampirePlayerState;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.ambient.Bat;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(AvatarRenderState.class)
public class VampireAvatarRenderStateMixin implements IVampirePlayerState {

    @Unique
    private int vampirism$vampire$eyeType;
    @Unique
    private int vampirism$vampire$fangType;
    @Unique
    private boolean vampirism$vampire$glowingEyes;
    @Unique
    private boolean vampirism$vampire$isDisguised;
    @Unique
    private int vampirism$vampire$vampireLevel;
    @Unique
    private boolean vampirism$vampire$isDbno;
    @Unique
    private boolean vampirism$vampire$sleepingInCoffin;
    @Unique
    private boolean vampirism$vampire$isInvisible;
    @Unique
    @Nullable
    private Bat vampirism$vampire$bat;


    @Override
    public int vampirism$vampire$getEyeType() {
        return this.vampirism$vampire$eyeType;
    }

    @Override
    public void vampirism$vampire$setEyeType(int type) {
        this.vampirism$vampire$eyeType = type;
    }


    @Override
    public int vampirism$vampire$getFangType() {
        return this.vampirism$vampire$fangType;
    }

    @Override
    public void vampirism$vampire$setFangType(int type) {
        this.vampirism$vampire$fangType = type;
    }

    @Override
    public boolean vampirism$vampire$getGlowingEyes() {
        return this.vampirism$vampire$glowingEyes;
    }

    @Override
    public void vampirism$vampire$setGlowingEyes(boolean glowing) {
        this.vampirism$vampire$glowingEyes = glowing;
    }

    @Override
    public boolean vampirism$vampire$isDisguised() {
        return this.vampirism$vampire$isDisguised;
    }

    @Override
    public void vampirism$vampire$setDisguised(boolean disguised) {
        this.vampirism$vampire$isDisguised = disguised;
    }

    @Override
    public int vampirism$vampire$level() {
        return this.vampirism$vampire$vampireLevel;
    }

    @Override
    public void vampirism$vampire$setVampireLevel(int level) {
        this.vampirism$vampire$vampireLevel = level;
    }

    @Override
    public boolean vampirism$vampire$isDbno() {
        return this.vampirism$vampire$isDbno;
    }

    @Override
    public void vampirism$vampire$setDbno(boolean dbno) {
        this.vampirism$vampire$isDbno = dbno;
    }

    @Override
    public boolean vampirism$vampire$sleepingInCoffin() {
        return this.vampirism$vampire$sleepingInCoffin;
    }

    @Override
    public void vampirism$vampire$setSleepingInCoffin(boolean sleepingInCoffin) {
        this.vampirism$vampire$sleepingInCoffin = sleepingInCoffin;
    }

    @Override
    public @Nullable Bat vampirism$vampire$getBat() {
        return this.vampirism$vampire$bat;
    }

    @Override
    public boolean vampirism$vampire$invisible() {
        return this.vampirism$vampire$isInvisible;
    }

    @Override
    public void vampirism$vampire$setBat(@Nullable Bat bat) {
        this.vampirism$vampire$bat = bat;
    }

    @Override
    public void vampirism$vampire$setInvisible(boolean invisible) {
        this.vampirism$vampire$isInvisible = invisible;
    }
}
