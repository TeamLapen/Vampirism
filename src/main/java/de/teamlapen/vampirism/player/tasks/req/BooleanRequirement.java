package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class BooleanRequirement implements TaskRequirement.Requirement<Boolean> {

    @Nonnull
    private final ResourceLocation id;
    @Nonnull
    private final BooleanSupplier function;

    @Nonnull
    public BooleanRequirement(@Nonnull ResourceLocation id, @Nonnull BooleanSupplier function) {
        this.id = id;
        this.function = function;
    }
    @Nonnull
    @Override
    public Boolean getStat(IFactionPlayer<?> player) {
        return this.function.apply(player);
    }

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @FunctionalInterface
    public interface BooleanSupplier extends Function<IFactionPlayer<?>, Boolean> {

    }
}
