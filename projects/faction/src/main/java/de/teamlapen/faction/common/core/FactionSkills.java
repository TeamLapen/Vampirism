package de.teamlapen.faction.common.core;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkillRegister;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.api.util.REFERENCE;
import de.teamlapen.faction.common.factions.skills.Skill;
import net.neoforged.bus.api.IEventBus;

public class FactionSkills {

    public static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(REFERENCE.MOD_ID);


    public static final DeferredSkill<?, ISkill<?>> MINION_RECOVERY = SKILLS.registerGenericSkill("minion_recovery", props -> new Skill<>(props.cost(2).tree(FactionSkillTreeTags.LORD).factions(FactionTags.HAS_LORD_SKILLS)));


    static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
