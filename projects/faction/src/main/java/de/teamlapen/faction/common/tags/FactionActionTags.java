package de.teamlapen.faction.common.tags;

import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.actions.IAction;
import de.teamlapen.faction.api.util.FIdentifier;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;

public class FactionActionTags {

    public static final TagKey<IAction<?>> SHOW_COOLDOWN_IN_HUD = tag("show_cooldown_in_hud");
    public static final TagKey<IAction<?>> SHOW_DURATION_IN_HUD = tag("show_duration_in_hud");

    private static TagKey<IAction<?>> tag(String name) {
        return TagKey.create(FactionRegistries.Keys.ACTION, FIdentifier.mod(name));
    }

}
