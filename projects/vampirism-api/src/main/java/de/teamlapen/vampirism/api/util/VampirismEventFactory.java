package de.teamlapen.vampirism.api.util;

import de.teamlapen.vampirism.api.event.BloodDrinkEvent;
import de.teamlapen.vampirism.api.event.VampireFogEvent;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDrinkBloodContext;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.world.entity.vampire.IVampire;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class VampirismEventFactory {



    public static BloodDrinkEvent.PlayerDrinkBloodEvent fireVampirePlayerDrinkBloodEvent(IVampirePlayer vampirePlayer, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
        BloodDrinkEvent.PlayerDrinkBloodEvent event = new BloodDrinkEvent.PlayerDrinkBloodEvent(vampirePlayer, amount, saturationAmount, useRemaining, bloodSource);
        NeoForge.EVENT_BUS.post(event);
        return event;
    }

    public static BloodDrinkEvent.EntityDrinkBloodEvent fireVampireDrinkBlood(IVampire vampire, int amount, float saturationAmount, boolean useRemaining, IDrinkBloodContext bloodSource) {
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