package de.teamlapen.faction.data.provider;

import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.core.FactionSounds;
import net.minecraft.data.PackOutput;

import static de.teamlapen.faction.api.util.FIdentifier.mod;

public class SoundDefinitionsProvider extends net.neoforged.neoforge.common.data.SoundDefinitionsProvider {

    protected SoundDefinitionsProvider(PackOutput output) {
        super(output, REFERENCE.MOD_ID);
    }

    @Override
    public void registerSounds() {
        this.add(FactionSounds.UNLOCK_SKILLS, definition().with(sound(mod("reserved/fx/unlock_skills_1")), sound(mod("reserved/fx/unlock_skills_2")), sound(mod("reserved/fx/unlock_skills_3"))));
        this.add(FactionSounds.TASK_COMPLETE, definition().subtitle("subtitles.factionapi.task_complete").with(sound(mod("cc/fx/task_complete"))));
        this.add(FactionSounds.RAID_WON, definition().with(sound(mod("reserved/event/raid_won"))));
        this.add(FactionSounds.RAID_FAILED, definition().with(sound(mod("reserved/event/raid_failed"))));
        this.add(FactionSounds.CHOIR_SHORT, definition().with(sound(mod("cc/fx/choir_short"))));
    }
}
