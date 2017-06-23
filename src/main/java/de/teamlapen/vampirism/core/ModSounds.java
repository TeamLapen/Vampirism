package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static SoundEvent entity_vampire_ambient;
    public static SoundEvent player_bite;
    public static SoundEvent ambient_castle;
    public static SoundEvent block_coffin_lid;
    public static SoundEvent crossbow;
    public static SoundEvent bat_swarm;
    public static SoundEvent boiling;


    static void registerSounds() {
        entity_vampire_ambient = registerSound("entity.vampire.scream");
        player_bite = registerSound("player.bite");
        ambient_castle = registerSound("ambient.castle");
        block_coffin_lid = registerSound("coffin_lid");
        crossbow = registerSound("crossbow");
        bat_swarm = registerSound("bat_swarm");
        boiling = registerSound("boiling");
    }

    private static SoundEvent registerSound(String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        SoundEvent event = new SoundEvent(resourcelocation);
        event.setRegistryName(resourcelocation);
        GameRegistry.register(event);
        return event;
    }
}
