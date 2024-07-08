package de.teamlapen.vampirism.data.provider;

import de.teamlapen.vampirism.api.VReference;
import de.teamlapen.vampirism.core.ModSounds;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.data.SoundDefinitionsProvider;

import static de.teamlapen.vampirism.api.util.VResourceLocation.mc;
import static de.teamlapen.vampirism.api.util.VResourceLocation.mod;

public class SoundDefinitionProvider extends SoundDefinitionsProvider {

    protected SoundDefinitionProvider(PackOutput output, ExistingFileHelper helper) {
        super(output, VReference.MODID, helper);
    }

    @Override
    public void registerSounds() {
        this.add(ModSounds.ENTITY_VAMPIRE_SCREAM, definition().subtitle("subtitles.vampirism.entity.vampire_scream").with(sound(mod("cc/entity/vampire_scream"))));
        this.add(ModSounds.VAMPIRE_BITE, definition().subtitle("subtitles.vampirism.player.bite").with(sound(mod("cc/entity/vampire_bite")), sound(mod("cc/entity/vampire_bite_long"))));
        this.add(ModSounds.VAMPIRE_FEEDING, definition().subtitle("subtitles.vampirism.player.feeding").with(sound(mod("cc/entity/vampire_feeding"))));
        this.add(ModSounds.AMBIENT_BLOOD_DRIPPING, definition().with(sound(mod("cc/ambient/blood_dripping")).stream(), sound(mod("cc/ambient/blood_dripping2")).stream()));
        this.add(ModSounds.VAMPIRE_FOREST_AMBIENT, definition().with(sound(mod("reserved/ambient/vampire_forest")).stream()));
        this.add(ModSounds.COFFIN_LID, definition().subtitle("subtitles.vampirism.coffin_lid").with(sound(mod("cc/block/coffin_lid_slide"))));
        this.add(ModSounds.BAT_SWARM, definition().subtitle("subtitles.vampirism.bat_swarm").with(sound(mod("cc/fx/bat_swarm"))));
        this.add(ModSounds.BOILING, definition().subtitle("subtitles.vampirism.boiling").with(sound(mod("cc/block/boiling"))));
        this.add(ModSounds.GRINDER, definition().subtitle("subtitles.vampirism.grinder").with(sound(mod("cc/block/grinder"))));
        this.add(ModSounds.TASK_COMPLETE, definition().subtitle("subtitles.vampirism.task_complete").with(sound(mod("cc/fx/task_complete"))));
        this.add(ModSounds.BLESSING_MUSIC, definition().subtitle("subtitles.vampirism.blessing_music").with(sound(mod("cc/block/church_organ")).stream()));
        this.add(ModSounds.BLOOD_PROJECTILE_HIT, definition().subtitle("subtitles.vampirism.blood_projectile_hit").with(sound(mod("reserved/fx/blood_projectile_hit"))));
        this.add(ModSounds.WEAPON_TABLE_CRAFTING, definition().with(sound(mod("reserved/block/weapon_table_crafting")).stream()));
        this.add(ModSounds.STAKE, definition().with(sound(mod("reserved/fx/stake"))));
        this.add(ModSounds.TELEPORT_AWAY, definition().subtitle("subtitles.vampirism.teleport_away").with(sound(mod("reserved/fx/teleport1"))));
        this.add(ModSounds.TELEPORT_HERE, definition().subtitle("subtitles.vampirism.teleport_here").with(sound(mod("reserved/fx/teleport2"))));
        this.add(ModSounds.FREEZE, definition().subtitle("subtitles.vampirism.freeze").with(sound(mod("reserved/fx/freeze"))));
        this.add(ModSounds.POTION_TABLE_CRAFTING, definition().with(sound(mod("reserved/block/potion_table_crafting")).stream()));
        this.add(ModSounds.MOTHER_DEATH, definition().with(sound(mod("reserved/fx/mother_death"))));
        this.add(ModSounds.MOTHER_AMBIENT, definition().with(sound(mod("reserved/ambient/mother")).stream()));
        this.add(ModSounds.GHOST_AMBIENT, definition().subtitle("subtitles.vampirism.ghost.ambient").with(sound(mc("mob/vex/idle1")), sound(mc("mob/vex/idle2")), sound(mc("mob/vex/idle3")), sound(mc("mob/vex/idle4"))));
        this.add(ModSounds.GHOST_DEATH, definition().subtitle("subtitles.vampirism.ghost.death").with(sound(mc("mob/vex/death1")), sound(mc("mob/vex/death2"))));
        this.add(ModSounds.GHOST_HURT, definition().subtitle("subtitles.vampirism.ghost.hurt").with(sound(mc("mob/allay/item_given1")).volume(0.1), sound(mc("mob/allay/item_given2")).volume(0.1), sound(mc("mob/allay/item_given3")).volume(0.1), sound(mc("mob/allay/item_given4")).volume(0.1)));
        this.add(ModSounds.REMAINS_DEFENDER_AMBIENT, definition().subtitle("subtitles.vampirism.remains_defender.ambient").with(sound(mc("block/amethyst/shimmer"))));
        this.add(ModSounds.REMAINS_DEFENDER_DEATH, definition().subtitle("subtitles.vampirism.remains_defender.death").with(sound(mc("block/amethyst_cluster/break1")).pitch(0.8).volume(0.8), sound(mc("block/amethyst_cluster/break2")).pitch(0.8).volume(0.8), sound(mc("block/amethyst_cluster/break3")).pitch(0.8).volume(0.8), sound(mc("block/amethyst_cluster/break4")).pitch(0.8).volume(0.8)));
        this.add(ModSounds.REMAINS_DEFENDER_HURT, definition().subtitle("subtitles.vampirism.remains_defender.hit").with(sound(mc("block/amethyst/step1")), sound(mc("block/amethyst/step2")), sound(mc("block/amethyst/step3")), sound(mc("block/amethyst/step4")), sound(mc("block/amethyst/step5")), sound(mc("block/amethyst/step6")), sound(mc("block/amethyst/step7")), sound(mc("block/amethyst/step8")), sound(mc("block/amethyst/step9")), sound(mc("block/amethyst/step10")), sound(mc("block/amethyst/step11")), sound(mc("block/amethyst/step12"))));
        this.add(ModSounds.REMAINS_DEATH, definition().subtitle("subtitles.vampirism.remains.death").with(sound(mod("reserved/block/remains_destroyed"))));
        this.add(ModSounds.REMAINS_HURT, definition().subtitle("subtitles.vampirism.remains.hurt").with(sound(mc("block/rooted_dirt/step1")).volume(0.8), sound(mc("block/rooted_dirt/step2")).volume(0.8), sound(mc("block/rooted_dirt/step3")).volume(0.8), sound(mc("block/rooted_dirt/step4")).volume(0.8), sound(mc("block/rooted_dirt/step5")).volume(0.8), sound(mc("block/rooted_dirt/step6")).volume(0.8)));
        this.add(ModSounds.RAID_WON, definition().with(sound(mod("reserved/event/raid_won"))));
        this.add(ModSounds.RAID_FAILED, definition().with(sound(mod("reserved/event/raid_failed"))));
        this.add(ModSounds.LEVEL_UP, definition().with(sound(mod("reserved/fx/level_up"))));
        this.add(ModSounds.BUBBLES, definition().with(sound(mod("reserved/block/bubbles_1")), sound(mod("reserved/block/bubbles_2")), sound(mod("reserved/block/bubbles_3"))));
        this.add(ModSounds.UNLOCK_SKILLS, definition().with(sound(mod("reserved/fx/unlock_skills_1")), sound(mod("reserved/fx/unlock_skills_2")), sound(mod("reserved/fx/unlock_skills_3"))));
    }
}
