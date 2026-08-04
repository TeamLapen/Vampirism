package de.teamlapen.faction.common.factions.skills;

import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.factions.skills.*;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.core.ModRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.event.DefaultDataComponentsBoundEvent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SkillCallbacks {

    private static Map<IAction<?>, ISkill<?>> actionToSkillMap = Map.of();

    public static <T extends ISkillPlayer<T>> ISkill<T> getActionSkillMap(IAction<T> action) {
        //noinspection unchecked
        return (ISkill<T>) actionToSkillMap.get(action);
    }

    public static void onBound(DefaultDataComponentsBoundEvent event) {
        //noinspection unchecked
        actionToSkillMap = ModRegistries.SKILLS.listElements()
                .filter(x -> x.components().has(FactionDataComponents.SKILL_ACTIONS))
                .flatMap(x -> x.components().getOrDefault((DataComponentType<List<Holder<IAction<?>>>>) (Object) FactionDataComponents.SKILL_ACTIONS.get(), List.<Holder<IAction<?>>>of())
                        .stream().map(y -> (IAction<?>) y.value()).map(a -> Pair.of(a, x.value())))
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue, (x, a) -> x));
    }
}
