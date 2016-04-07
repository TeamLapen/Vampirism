package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static SoundEvent entity_vampire_ambient;
    public static SoundEvent player_bite;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerSounds();
                break;
        }

    }

    private static void registerSounds() {
        entity_vampire_ambient = registerSound("entity.vampire.scream");
        player_bite = registerSound("player.bite");
    }

    private static SoundEvent registerSound(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        SoundEvent event = new SoundEvent(resourcelocation);
        GameRegistry.register(event);
        return event;
    }
}
