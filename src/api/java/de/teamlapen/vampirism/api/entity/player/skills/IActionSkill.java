package de.teamlapen.vampirism.api.entity.player.skills;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.core.Holder;

public interface IActionSkill<T extends IFactionPlayer<T>> extends ISkill<T> {

    default IAction<T> action() {
        return actionHolder().value();
    }

    Holder<? extends IAction<T>> actionHolder();
}
