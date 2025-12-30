package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.factions.skills.LordSkill;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FactionSkills {

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(FactionRegistries.Keys.SKILL, REFERENCE.MOD_ID);


    public static final DeferredHolder<ISkill<?>, ISkill<?>> MINION_RECOVERY = SKILLS.register("minion_recovery", () -> new LordSkill<>(2));


    static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
