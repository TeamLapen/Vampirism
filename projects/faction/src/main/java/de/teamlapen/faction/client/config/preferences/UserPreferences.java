package de.teamlapen.faction.client.config.preferences;

import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

public record UserPreferences(ActionOrder actionOrder, MinionTaskOrder minionTaskOrder, ActionBindings actionBindings) {

    @Nullable
    private static UserPreferences instance;

    public UserPreferences(RegistryAccess registryAccess) {
        this(new ActionOrder(registryAccess), new MinionTaskOrder(registryAccess), new ActionBindings(registryAccess));
    }

    public static UserPreferences get() {
        if (instance == null) {
            throw new IllegalStateException("UserPreferences is only available while the level is loaded");
        }
        return instance;
    }

    public static void init(RegistryAccess registryAccess) {
        instance = new UserPreferences(registryAccess);
    }

    public static void clear() {
        instance = null;
    }
}
