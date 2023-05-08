package de.teamlapen.vampirism.core;

import de.teamlapen.vampirism.REFERENCE;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, REFERENCE.MODID);

    public static final RegistryObject<SoundEvent> ENTITY_VAMPIRE_SCREAM = create("entity.vampire_scream");
    public static final RegistryObject<SoundEvent> VAMPIRE_BITE = create("entity.vampire_bite");
    public static final RegistryObject<SoundEvent> AMBIENT_BLOOD_DRIPPING = create("ambient.blood_dripping");
    public static final RegistryObject<SoundEvent> COFFIN_LID = create("coffin_lid");
    public static final RegistryObject<SoundEvent> BAT_SWARM = create("entity.bat_swarm");
    public static final RegistryObject<SoundEvent> BOILING = create("block.boiling");
    public static final RegistryObject<SoundEvent> GRINDER = create("block.grinder");
    public static final RegistryObject<SoundEvent> TASK_COMPLETE = create("task_complete");
    public static final RegistryObject<SoundEvent> VAMPIRE_FEEDING = create("entity.vampire_feeding");
    public static final RegistryObject<SoundEvent> BLESSING_MUSIC = create("block.blessing_music");
    public static final RegistryObject<SoundEvent> BLOOD_PROJECTILE_HIT  = create("fx.blood_projectile_hit");
    public static final RegistryObject<SoundEvent> WEAPON_TABLE_CRAFTING = create("block.weapon_table_crafting");
    public static final RegistryObject<SoundEvent> STAKE = create("fx.stake");
    public static final RegistryObject<SoundEvent> TELEPORT_AWAY = create("fx.teleport_away");
    public static final RegistryObject<SoundEvent> TELEPORT_HERE = create("fx.teleport_here");
    public static final RegistryObject<SoundEvent> FREEZE = create("fx.freeze");
    public static final RegistryObject<SoundEvent> POTION_TABLE_CRAFTING = create("block.potion_table_crafting");

    static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static RegistryObject<SoundEvent> create(@NotNull String soundNameIn) {
        ResourceLocation resourcelocation = new ResourceLocation(REFERENCE.MODID, soundNameIn);
        return SOUND_EVENTS.register(soundNameIn, () -> SoundEvent.createVariableRangeEvent(resourcelocation));
    }
}
