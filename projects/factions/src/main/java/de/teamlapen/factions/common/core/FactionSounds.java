package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.util.FResourceLocation;
import de.teamlapen.factions.api.util.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, REFERENCE.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> LEVEL_UP = create("fx.level_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> UNLOCK_SKILLS = create("fx.unlock_skills");
    public static final DeferredHolder<SoundEvent, SoundEvent> TASK_COMPLETE = create("task_complete");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAID_FAILED = create("event.raid_failed");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAID_WON = create("event.raid_won");

    static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    private static DeferredHolder<SoundEvent, SoundEvent> create(String soundNameIn) {
        ResourceLocation resourcelocation = FResourceLocation.mod(soundNameIn);
        return SOUND_EVENTS.register(soundNameIn, () -> SoundEvent.createVariableRangeEvent(resourcelocation));
    }
}
