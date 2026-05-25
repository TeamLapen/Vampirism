package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.util.FIdentifier;
import de.teamlapen.faction.api.util.REFERENCE;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionSounds {

    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(Registries.SOUND_EVENT, REFERENCE.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> CHOIR_SHORT = create("choir_short");
    public static final DeferredHolder<SoundEvent, SoundEvent> UNLOCK_SKILLS = create("fx.unlock_skills");
    public static final DeferredHolder<SoundEvent, SoundEvent> TASK_COMPLETE = create("task_complete");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAID_FAILED = create("event.raid_failed");
    public static final DeferredHolder<SoundEvent, SoundEvent> RAID_WON = create("event.raid_won");

    private static DeferredHolder<SoundEvent, SoundEvent> create(String soundNameIn) {
        return SOUND_EVENTS.register(soundNameIn, () -> SoundEvent.createVariableRangeEvent(FIdentifier.mod(soundNameIn)));
    }

    static void register(IEventBus bus) {
        SOUND_EVENTS.register(bus);
    }

    public static void playLevelUpSoundServer(ServerPlayer serverPlayer) {
        serverPlayer.connection.send(new ClientboundSoundPacket(FactionSounds.CHOIR_SHORT, SoundSource.PLAYERS, serverPlayer.getX(), serverPlayer.getY(), serverPlayer.getZ(), 0.5f, 1.0f + (serverPlayer.getRandom().nextFloat() - 0.5f) / 5.0f, serverPlayer.getRandom().nextLong()));
    }
}
