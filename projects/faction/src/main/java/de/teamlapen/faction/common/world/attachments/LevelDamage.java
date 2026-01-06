package de.teamlapen.faction.common.world.attachments;

import de.teamlapen.faction.common.core.FactionAttachments;
import de.teamlapen.faction.common.world.ModDamageSources;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;

public record LevelDamage(ModDamageSources modDamageSources) {

    public static ModDamageSources get(Level level) {
        return level.getData(FactionAttachments.LEVEL_DAMAGE).modDamageSources();
    }


    public LevelDamage(Level level) {
        this(new ModDamageSources(level.registryAccess()));
    }

    public static class Factory implements Function<IAttachmentHolder, LevelDamage> {

        @Override
        public LevelDamage apply(IAttachmentHolder holder) {
            if (holder instanceof Level level) {
                return new LevelDamage(level);
            }
            throw new IllegalArgumentException("Cannot create level damage for holder " + holder.getClass() + ". Expected Level");
        }
    }
}
