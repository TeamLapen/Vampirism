package de.teamlapen.faction.common.factions.skills;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record SkillTreeHolder(@NotNull Identifier id, @NotNull SkillTreeConfiguration configuration) {


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
