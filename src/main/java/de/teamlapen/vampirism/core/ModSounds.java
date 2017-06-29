package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static final SoundEvent entity_vampire_ambient = getNull();
    public static final SoundEvent player_bite = getNull();
    public static final SoundEvent ambient_castle = getNull();
    public static final SoundEvent block_coffin_lid = getNull();
    public static final SoundEvent crossbow = getNull();
    public static final SoundEvent bat_swarm = getNull();
    public static final SoundEvent boiling = getNull();

    @SuppressWarnings("ConstantConditions")
    private static @Nonnull
    <T> T getNull() {
        return null;
    }

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
