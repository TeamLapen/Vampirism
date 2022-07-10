package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    private static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, REFERENCE.MODID);

    public static final RegistryObject<SoundEvent> ENTITY_VAMPIRE_SCREAM = create("entity.vampire.scream");
    public static final RegistryObject<SoundEvent> PLAYER_BITE = create("player.bite");
    public static final RegistryObject<SoundEvent> AMBIENT_CASTLE = create("ambient.castle");
    public static final RegistryObject<SoundEvent> COFFIN_LID = create("coffin_lid");
    public static final RegistryObject<SoundEvent> CROSSBOW = create("crossbow");
    public static final RegistryObject<SoundEvent> BAT_SWARM = create("bat_swarm");
    public static final RegistryObject<SoundEvent> BOILING = create("boiling");
    public static final RegistryObject<SoundEvent> GRINDER = create("grinder");
    public static final RegistryObject<SoundEvent> TASK_COMPLETE = create("task_complete");
    public static final RegistryObject<SoundEvent> PLAYER_FEEDING = create("player.feeding");
    public static final RegistryObject<SoundEvent> BLESSING_MUSIC = create("blessing_music");

    static void registerSounds(IEventBus bus) {
        SOUNDS.register(bus);
    }

    private static RegistryObject<SoundEvent> create(String soundNameIn) {
        final ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        return SOUNDS.register(soundNameIn.replaceAll("\\.", "_"), () -> new SoundEvent(resourcelocation));
    }
}
