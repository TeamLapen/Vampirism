package de.teamlapen.vampirism.common.world.entity.dracula;

import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.attachment.IAttachmentHolder;

import java.util.function.Function;

public class DraculaFightData {

    private final ServerDraculaEvent event = new ServerDraculaEvent(1, FightStage.NONE, false);
    private final ServerLevel level;

    public DraculaFightData(ServerLevel level) {
        this.level = level;
    }

    public ServerDraculaEvent getEvent() {
        return this.event;
    }

    public static class Factory implements Function<IAttachmentHolder, DraculaFightData> {

        @Override
        public DraculaFightData apply(IAttachmentHolder holder) {
            if (holder instanceof ServerLevel level) {
                return new DraculaFightData(level);
            }
            throw new IllegalArgumentException("Cannot create dracula fight data for holder " + holder.getClass() + ". Expected ServerLevel");
        }
    }
}
