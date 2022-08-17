package de.teamlapen.vampirism.player.tasks.req;

import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.task.TaskRequirement;
import net.minecraft.resources.ResourceLocation;

import org.jetbrains.annotations.NotNull;
import java.util.function.Function;

public class BooleanRequirement implements TaskRequirement.Requirement<Boolean> {

    @NotNull
    private final ResourceLocation id;
    @NotNull
    private final BooleanSupplier function;

    public BooleanRequirement(@NotNull ResourceLocation id, @NotNull BooleanSupplier function) {
        this.id = id;
        this.function = function;
    }

    @NotNull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @NotNull
    @Override
    public Boolean getStat(IFactionPlayer<?> player) {
        return this.function.apply(player);
    }

    @FunctionalInterface
    public interface BooleanSupplier extends Function<IFactionPlayer<?>, Boolean> {

    }
}
