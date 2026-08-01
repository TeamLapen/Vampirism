package de.teamlapen.faction.api.factions.level.change;

import com.google.gson.Gson;
import de.teamlapen.faction.api.factions.level.ChangeKey;

public interface Change<T extends Change<T>> {

    ChangeKey<T> key();

    default String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
