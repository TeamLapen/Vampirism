package de.teamlapen.vampirism.api.util;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.IActionSkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.callback.AddCallback;
import net.neoforged.neoforge.registries.callback.ClearCallback;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SkillCallbacks implements AddCallback<ISkill<?>>, ClearCallback<ISkill<?>> {


    private static final Map<IAction<?>, ISkill<?>> ACTION_TO_SKILL_MAP = new HashMap<>();
    private static final Map<IAction<?>, ISkill<?>> ACTION_TO_SKILL_MAP_READ_ONLY = Collections.unmodifiableMap(ACTION_TO_SKILL_MAP);

    @Override
    public void onAdd(Registry<ISkill<?>> registry, int id, ResourceKey<ISkill<?>> key, ISkill<?> value) {
        if (value instanceof IActionSkill<?> actionSkill) {
            ACTION_TO_SKILL_MAP.put(actionSkill.getAction(), actionSkill);
        }
    }

    @Override
    public void onClear(Registry<ISkill<?>> registry, boolean full) {
        if (full) {
            ACTION_TO_SKILL_MAP.clear();
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Map<IAction<T>, ISkill<T>> getActionSkillMap() {
        return (Map<IAction<T>, ISkill<T>>) (Object) ACTION_TO_SKILL_MAP_READ_ONLY;
    }
}
