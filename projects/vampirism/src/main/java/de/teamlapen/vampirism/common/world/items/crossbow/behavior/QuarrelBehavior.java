package de.teamlapen.vampirism.common.world.items.crossbow.behavior;

import de.teamlapen.vampirism.api.world.items.IVampirismQuarrel;
import de.teamlapen.vampirism.api.world.items.QuarrelProperties;

public class QuarrelBehavior implements IVampirismQuarrel.IQuarrelBehavior {

    private final QuarrelProperties properties;

    public QuarrelBehavior(QuarrelProperties properties) {
        this.properties = properties;
    }

    @Override
    public QuarrelProperties properties() {
        return properties;
    }
}
