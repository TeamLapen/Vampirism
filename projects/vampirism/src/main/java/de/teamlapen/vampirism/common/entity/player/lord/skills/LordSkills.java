package de.teamlapen.vampirism.common.entity.player.lord.skills;

import de.teamlapen.factions.api.FactionRegistries;
import de.teamlapen.factions.common.tags.FactionSkillTreeTags;
import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.factions.api.skills.ISkill;
import de.teamlapen.vampirism.common.entity.player.lord.actions.LordActions;
import de.teamlapen.vampirism.common.entity.player.skills.ActionSkill;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;

public class LordSkills {

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(FactionRegistries.Keys.SKILL, REFERENCE.MODID);


    public static final DeferredHolder<ISkill<?>, ISkill<?>> LORD_SPEED = SKILLS.register("lord_speed", () -> new ActionSkill(LordActions.LORD_SPEED, FactionSkillTreeTags.LORD, 1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<?>> LORD_ATTACK_SPEED = SKILLS.register("lord_attack_speed", () -> new ActionSkill(LordActions.LORD_ATTACK_SPEED, FactionSkillTreeTags.LORD, 1, true));
    public static final DeferredHolder<ISkill<?>, ISkill<?>> MINION_RECOVERY = SKILLS.register("minion_recovery", () -> new SimpleLordSkill<>(2, true));


    @ApiStatus.Internal
    public static void register(IEventBus bus) {
        SKILLS.register(bus);
    }
}
