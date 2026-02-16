package de.teamlapen.vampirism.common.core;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.util.VIdentifier;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.Music;
import net.minecraft.sounds.Musics;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Handle all sound related stuff
 */
public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, REFERENCE.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_VAMPIRE_SCREAM = create("entity.vampire_scream");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_BITE = create("entity.vampire_bite");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_DRIP = create("ambient.blood_drip");
    public static final DeferredHolder<SoundEvent, SoundEvent> COFFIN_LID = create("block.coffin_lid");
    public static final DeferredHolder<SoundEvent, SoundEvent> BAT_SWARM = create("entity.bat_swarm");
    public static final DeferredHolder<SoundEvent, SoundEvent> BOILING = create("block.boiling");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_SQUEEZE = create("block.blood_squeeze");
    public static final DeferredHolder<SoundEvent, SoundEvent> CHOIR_SHORT = create("choir_short");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_FEEDING = create("entity.vampire_feeding");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLESSING_MUSIC = create("block.blessing_music");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOOD_PROJECTILE_HIT = create("fx.blood_projectile_hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPHERE_SPINNING = create("block.sphere_spinning");
    public static final DeferredHolder<SoundEvent, SoundEvent> WEAPON_TABLE_CRAFTING = create("block.weapon_table_crafting");
    public static final DeferredHolder<SoundEvent, SoundEvent> STAKE = create("fx.stake");
    public static final DeferredHolder<SoundEvent, SoundEvent> TELEPORT_AWAY = create("fx.teleport_away");
    public static final DeferredHolder<SoundEvent, SoundEvent> TELEPORT_HERE = create("fx.teleport_here");
    public static final DeferredHolder<SoundEvent, SoundEvent> FREEZE = create("fx.freeze");
    public static final DeferredHolder<SoundEvent, SoundEvent> POTION_TABLE_CRAFTING = create("block.potion_table_crafting");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOTHER_DEATH = create("fx.mother_death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MOTHER_AMBIENT = create("ambient.mother");
    public static final DeferredHolder<SoundEvent, SoundEvent> VAMPIRE_FOREST_AMBIENT = create("ambient.forest");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOST_AMBIENT = create("entity.ghost.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOST_DEATH = create("entity.ghost.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOST_HURT = create("entity.ghost.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMAINS_DEFENDER_AMBIENT = create("entity.remains_defender.ambient");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMAINS_DEFENDER_DEATH = create("entity.remains_defender.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMAINS_DEFENDER_HURT = create("entity.remains_defender.hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMAINS_DEATH = create("entity.remains.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> REMAINS_HURT = create("entity.remains.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> BUBBLES = create("block.bubbles");


    static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> create(@NotNull String soundNameIn) {
        Identifier resourcelocation = VIdentifier.mod(soundNameIn);
        return SOUND_EVENTS.register(soundNameIn, () -> SoundEvent.createVariableRangeEvent(resourcelocation));
    }

    private static final Map<ResourceKey<SoundEvent>, Music> music = new HashMap<>();

    public static Music getMusic(ResourceKey<SoundEvent> soundEvent) {
        return music.computeIfAbsent(soundEvent, sound -> BuiltInRegistries.SOUND_EVENT.get(sound).map(event -> new Music(event, 0, 0, true)).orElse(Musics.GAME));
    }

}
