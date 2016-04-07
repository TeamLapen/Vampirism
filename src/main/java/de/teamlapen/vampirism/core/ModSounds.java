package de.teamlapen.vampirism.core;

import de.teamlapen.lib.lib.util.IInitListener;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.registry.FMLControlledNamespacedRegistry;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static SoundEvent entity_vampire_ambient;

    public static void onInitStep(IInitListener.Step step, FMLStateEvent event) {
        switch (step) {
            case PRE_INIT:
                registerSounds();
                break;
        }

    }

    private static void registerSounds() {
        entity_vampire_ambient = registerSound("entity.vampire.scream");
    }

    private static SoundEvent registerSound(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        SoundEvent event = new SoundEvent(resourcelocation);

        ((FMLControlledNamespacedRegistry<SoundEvent>) SoundEvent.soundEventRegistry).register(event);
        return event;
    }
}
