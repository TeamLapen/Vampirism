package de.teamlapen.vampirism.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.world.entities.player.FactionPlayerConsumer;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirismVampireVisions;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModConsumer {

    public static final DeferredRegister<FactionPlayerConsumer> CONSUMER = DeferredRegister.create(FactionRegistries.Keys.FACTION_PLAYER_CONSUMER, REFERENCE.MODID);

    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> ENABLE_VAMPIRE_ADVANCED_BITER = CONSUMER.register("enable_vampire_advanced_biter", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.getSkillProperties().advanced_biter = true;
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> DISABLE_VAMPIRE_ADVANCED_BITER = CONSUMER.register("disable_vampire_advanced_biter", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.getSkillProperties().advanced_biter = false;
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> ENABLE_AND_ACTIVATE_VAMPIRE_NIGHT_VISION = CONSUMER.register("enable_vampire_night_vision", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.unlockVision(VampirismVampireVisions.NIGHT_VISION.getKey());
            vampire.activateVision(VampirismVampireVisions.NIGHT_VISION.getKey());
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> DISABLE_VAMPIRE_NIGHT_VISION = CONSUMER.register("disable_vampire_night_vision", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.unUnlockVision(VampirismVampireVisions.NIGHT_VISION.getKey());
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> ENABLE_VAMPIRE_BLOOD_VISION = CONSUMER.register("enable_vampire_blood_vision", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.unlockVision(VampirismVampireVisions.BLOOD_VISION.getKey());
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> DISABLE_VAMPIRE_BLOOD_VISION = CONSUMER.register("disable_vampire_blood_vision", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.unUnlockVision(VampirismVampireVisions.BLOOD_VISION.getKey());
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> ENABLE_VAMPIRE_GARLIC_VISION = CONSUMER.register("enable_vampire_garlic_vision", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.getSkillProperties().blood_vision_garlic = true;
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> DISABLE_VAMPIRE_GARLIC_VISION = CONSUMER.register("disable_vampire_garlic_vision", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.getSkillProperties().blood_vision_garlic = false;
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> ENABLE_VAMPIRE_WATER_RESISTANCE = CONSUMER.register("enable_vampire_water_resistance", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.getSkillProperties().waterResistance = true;
        }
    });
    public static final DeferredHolder<FactionPlayerConsumer, FactionPlayerConsumer> DISABLE_VAMPIRE_WATER_RESISTANCE = CONSUMER.register("disable_vampire_water_resistance", () -> factionPlayer ->  {
        if (factionPlayer instanceof VampirePlayer vampire) {
            vampire.getSkillProperties().waterResistance = false;
        }
    });

    static void register(IEventBus bus) {
        CONSUMER.register(bus);
    }
}
