package de.teamlapen.factions.common.core;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.api.factions.skills.ISkill;
import de.teamlapen.factions.api.util.REFERENCE;
import de.teamlapen.factions.common.factions.skills.LordSkill;
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
