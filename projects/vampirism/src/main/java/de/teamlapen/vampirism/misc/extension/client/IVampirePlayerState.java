package de.teamlapen.vampirism.misc.extension.client;

import net.minecraft.world.entity.ambient.Bat;
import org.jetbrains.annotations.Nullable;

public interface IVampirePlayerState {

    //<editor-fold desc="Getter">

    int vampirism$vampire$getEyeType();
    int vampirism$vampire$getFangType();
    boolean vampirism$vampire$getGlowingEyes();
    boolean vampirism$vampire$isDisguised();
    int vampirism$vampire$level();
    boolean vampirism$vampire$isDbno();
    boolean vampirism$vampire$sleepingInCoffin();
    @Nullable
    Bat vampirism$vampire$getBat();
    boolean vampirism$vampire$invisible();

    //</editor-fold>

    //<editor-fold desc="Setter">

    void vampirism$vampire$setEyeType(int type);
    void vampirism$vampire$setFangType(int type);
    void vampirism$vampire$setGlowingEyes(boolean glowing);
    void vampirism$vampire$setDisguised(boolean disguised);
    void vampirism$vampire$setVampireLevel(int level);
    void vampirism$vampire$setDbno(boolean dbno);
    void vampirism$vampire$setSleepingInCoffin(boolean sleepingInCoffin);
    void vampirism$vampire$setBat(@Nullable Bat bat);
    void vampirism$vampire$setInvisible(boolean invisible);

    //</editor-fold>
}
