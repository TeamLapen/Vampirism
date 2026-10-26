package de.teamlapen.faction.common.factions.minions.management;

import de.teamlapen.faction.api.world.entities.minion.tasks.IMinionTaskCategory;
import de.teamlapen.faction.common.util.RegUtil;
import net.minecraft.util.Util;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MinionTaskCategory implements IMinionTaskCategory {

    @Nullable
    private String descriptionId;

    @Override
    public @NotNull String getDescriptionId() {
        if (this.descriptionId == null) {
            this.descriptionId = Util.makeDescriptionId("minion_task_category", RegUtil.id(this));
        }
        return this.descriptionId;
    }
}
