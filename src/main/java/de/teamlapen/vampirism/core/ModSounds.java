package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import static de.teamlapen.lib.lib.util.UtilLib.getNull;

/**
 * Handle all sound related stuff
 */
@GameRegistry.ObjectHolder(REFERENCE.MODID)
public class ModSounds {
    public static final SoundEvent entity_vampire_scream = getNull();
    public static final SoundEvent player_bite = getNull();
    public static final SoundEvent ambient_castle = getNull();
    public static final SoundEvent coffin_lid = getNull();
    public static final SoundEvent crossbow = getNull();
    public static final SoundEvent bat_swarm = getNull();
    public static final SoundEvent boiling = getNull();


    static void registerSounds(IForgeRegistry<SoundEvent> registry) {
        registry.register(create("entity.vampire.scream"));
        registry.register(create("player.bite"));
        registry.register(create("ambient.castle"));
        registry.register(create("coffin_lid"));
        registry.register(create("crossbow"));
        registry.register(create("bat_swarm"));
        registry.register(create("boiling"));
    }

    private static SoundEvent create(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        SoundEvent event = new SoundEvent(resourcelocation);
        event.setRegistryName(REFERENCE.MODID, soundNameIn.replaceAll("\\.", "_"));
        return event;
    }
}
