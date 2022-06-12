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
    
    public static final RegistryObject<SoundEvent> ENTITY_VAMPIRE_SCREAM = create("entity_vampire_scream");
    public static final RegistryObject<SoundEvent> PLAYER_BITE = create("player_bite");
    public static final RegistryObject<SoundEvent> AMBIENT_CASTLE = create("ambient_castle");
    public static final RegistryObject<SoundEvent> COFFIN_LID = create("coffin_lid");
    public static final RegistryObject<SoundEvent> CROSSBOW = create("crossbow");
    public static final RegistryObject<SoundEvent> BAT_SWARM = create("bat_swarm");
    public static final RegistryObject<SoundEvent> BOILING = create("boiling");
    public static final RegistryObject<SoundEvent> GRINDER = create("grinder");
    public static final RegistryObject<SoundEvent> TASK_COMPLETE = create("task_complete");


    static void registerSounds(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static RegistryObject<SoundEvent> create(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        return SOUND_EVENTS.register(soundNameIn, () -> new SoundEvent(resourcelocation));
    }
}
