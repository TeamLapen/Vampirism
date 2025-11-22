package de.teamlapen.vampirism.api.util;

import de.teamlapen.vampirism.api.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.api.event.*;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

@ApiStatus.Internal
public class VampirismEventFactory {



    public static @NotNull BloodDrinkEvent.PlayerDrinkBloodEvent fireVampirePlayerDrinkBloodEvent(@NotNull IVampirePlayer vampirePlayer, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.PlayerDrinkBloodEvent event = new BloodDrinkEvent.PlayerDrinkBloodEvent(vampirePlayer, amount, saturationAmount, useRemaining, bloodSource);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static @NotNull BloodDrinkEvent.EntityDrinkBloodEvent fireVampireDrinkBlood(@NotNull IVampire vampire, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.EntityDrinkBloodEvent event = new BloodDrinkEvent.EntityDrinkBloodEvent(vampire, amount, saturationAmount, useRemaining, bloodSource);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }



    public static float fireVampireFogEvent(float fogDistanceMultiplier) {
        VampireFogEvent event = new VampireFogEvent(fogDistanceMultiplier);
        NeoForge.EVENT_BUS.post(event);
        return event.getFogDistanceMultiplier();
    }



}