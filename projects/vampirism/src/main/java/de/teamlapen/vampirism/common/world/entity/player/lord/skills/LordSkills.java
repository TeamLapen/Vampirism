package de.teamlapen.vampirism.common.world.entity.player.lord.skills;

import de.teamlapen.faction.api.factions.skills.ISkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkill;
import de.teamlapen.faction.api.registries.skills.DeferredSkillRegister;
import de.teamlapen.faction.api.tags.FactionSkillTreeTags;
import de.teamlapen.faction.api.tags.FactionTags;
import de.teamlapen.faction.common.factions.skills.Skill;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.common.world.entity.player.lord.actions.LordActions;
import net.minecraft.core.Holder;
import net.neoforged.bus.api.IEventBus;
import org.jetbrains.annotations.ApiStatus;

public class LordSkills {

    public static final DeferredSkillRegister SKILLS = DeferredSkillRegister.create(REFERENCE.MODID);


    public static final DeferredSkill<?, ISkill<?>> LORD_SPEED = SKILLS.registerGenericSkill("lord_speed", props -> new Skill<>(props.cost(1).withDescription().actionSkill((Holder) LordActions.LORD_SPEED).tree(FactionSkillTreeTags.LORD).factions(FactionTags.HAS_LORD_SKILLS)));
    public static final DeferredSkill<?, ISkill<?>> LORD_ATTACK_SPEED = SKILLS.registerGenericSkill("lord_attack_speed", props -> new Skill<>(props.cost(1).withDescription().actionSkill((Holder) LordActions.LORD_ATTACK_SPEED).tree(FactionSkillTreeTags.LORD).factions(FactionTags.HAS_LORD_SKILLS)));


    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
