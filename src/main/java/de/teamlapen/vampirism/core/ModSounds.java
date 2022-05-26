package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, REFERENCE.MODID);
    
    public static final RegistryObject<SoundEvent> entity_vampire_scream = create("entity_vampire_scream");
    public static final RegistryObject<SoundEvent> player_bite = create("player_bite");
    public static final RegistryObject<SoundEvent> ambient_castle = create("ambient_castle");
    public static final RegistryObject<SoundEvent> coffin_lid = create("coffin_lid");
    public static final RegistryObject<SoundEvent> crossbow = create("crossbow");
    public static final RegistryObject<SoundEvent> bat_swarm = create("bat_swarm");
    public static final RegistryObject<SoundEvent> boiling = create("boiling");
    public static final RegistryObject<SoundEvent> grinder = create("grinder");
    public static final RegistryObject<SoundEvent> task_complete = create("task_complete");


    static void registerSounds(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static RegistryObject<SoundEvent> create(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        return SOUND_EVENTS.register(soundNameIn, () -> new SoundEvent(resourcelocation));
    }
}
