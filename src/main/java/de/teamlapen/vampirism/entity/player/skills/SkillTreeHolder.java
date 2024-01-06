package de.teamlapen.vampirism.entity.player.skills;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record SkillTreeHolder(@NotNull ResourceLocation id, @NotNull SkillTreeConfiguration configuration) {


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SkillTreeHolder that = (SkillTreeHolder) o;

        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    @Override
    public String toString() {
        return this.id.toString();
    }
}
