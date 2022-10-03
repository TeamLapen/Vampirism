package de.teamlapen.vampirism.entity.player.lord.skills;

import de.teamlapen.vampirism.REFERENCE;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.SkillType;
import de.teamlapen.vampirism.entity.player.lord.actions.LordActions;
import de.teamlapen.vampirism.entity.player.skills.ActionSkill;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.ApiStatus;

public class LordSkills {

    public static final DeferredRegister<ISkill<?>> SKILLS = DeferredRegister.create(VampirismRegistries.SKILLS_ID, REFERENCE.MODID);


    public static final RegistryObject<ISkill<?>> LORD_SPEED = SKILLS.register("lord_speed", () -> new ActionSkill<>(LordActions.LORD_SPEED::get, SkillType.LORD, true));
    public static final RegistryObject<ISkill<?>> LORD_ATTACK_SPEED = SKILLS.register("lord_attack_speed", () -> new ActionSkill<>(LordActions.LORD_ATTACK_SPEED::get, SkillType.LORD, true));
    public static final RegistryObject<ISkill<?>> MINION_RECOVERY = SKILLS.register("minion_recovery", () -> new SimpleLordSkill<>(true));


    @ApiStatus.Internal
    public static void register(IEventBus bus){
        SKILLS.register(bus);
    }
}
