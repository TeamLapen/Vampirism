package de.teamlapen.vampirism.api.util;

import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.actions.ILastingAction;
import net.minecraft.core.Holder;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class RegUtil {

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Holder<ILastingAction<T>> holder(ILastingAction<T> action) {
        return (Holder<ILastingAction<T>>) (Object) VampirismRegistries.ACTION.get().wrapAsHolder(action);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IAction<?>> Holder<T> holder(T action) {
        return (Holder<T>) VampirismRegistries.ACTION.get().wrapAsHolder(action);
    }
}
