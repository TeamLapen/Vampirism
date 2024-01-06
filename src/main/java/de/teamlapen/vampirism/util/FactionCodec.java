package de.teamlapen.vampirism.util;

import com.mojang.serialization.Codec;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.core.ModRegistries;

public class FactionCodec {

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Codec<ISkill<T>> skillCodec() {
        return ModRegistries.SKILLS.byNameCodec().xmap(s -> (ISkill<T>) s, s -> s);
    }

    @SuppressWarnings("unchecked")
    public static <T extends IFactionPlayer<T>> Codec<IAction<T>> actionCodec() {
        return ModRegistries.ACTIONS.byNameCodec().xmap(s -> (IAction<T>) s, s -> s);
    }
}
