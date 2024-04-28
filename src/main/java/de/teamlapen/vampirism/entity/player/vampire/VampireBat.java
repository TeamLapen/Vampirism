package de.teamlapen.vampirism.entity.player.vampire;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ambient.Bat;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;

public class VampireBat {

    public static class Factory implements Function<IAttachmentHolder, Bat> {

        @Override
        public Bat apply(IAttachmentHolder holder) {
            if (holder instanceof Entity entity) {
                var bat = EntityType.BAT.create(((Entity) holder).getCommandSenderWorld());
                if (bat != null) {
                    bat.restAnimationState.stop();
                    bat.flyAnimationState.startIfStopped(entity.tickCount);
                    return bat;
                }
                throw new IllegalArgumentException("Cannot create vampire bat attachment for holder " + holder.getClass() + ". Bat entity could not be created");
            }
            throw new IllegalArgumentException("Cannot create vampire bat attachment for holder " + holder.getClass() + ". Expected Entity");
        }
    }
}
