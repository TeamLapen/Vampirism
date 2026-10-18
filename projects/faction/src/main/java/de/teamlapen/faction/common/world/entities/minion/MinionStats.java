package de.teamlapen.faction.common.world.entities.minion;

import de.teamlapen.faction.api.world.entities.minion.IMinionData;
import de.teamlapen.faction.api.world.entities.minion.IMinionEntity;
import de.teamlapen.faction.api.world.entities.minion.MinionStat;
import de.teamlapen.faction.common.core.FactionAttributes;
import de.teamlapen.faction.common.core.FactionDataComponents;
import de.teamlapen.faction.common.world.entities.EntityConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.jetbrains.annotations.Nullable;

public class MinionStats {

    public static final MinionStat HEALTH_STATS = new MinionStat.AttributeBased(FactionDataComponents.MINION_HEALTH_LEVEL, Attributes.MAX_HEALTH, 3, () -> EntityConstants.MINION_MAX_HEALTH_PL);
    public static final MinionStat ATTACK_DAMAGE_STATS = new MinionStat.AttributeBased(FactionDataComponents.MINION_ATTACK_DAMAGE_LEVEL, Attributes.ATTACK_DAMAGE, 3, () -> EntityConstants.MINION_ATTACK_DAMAGE_PL);
    public static final MinionStat SPEED_STATS = new MinionStat.AttributeBased(FactionDataComponents.MINION_SPEED_LEVEL, Attributes.MOVEMENT_SPEED, 2, () -> EntityConstants.MINION_SPEED_PL);
    public static final MinionStat RESOURCES_STATS = new MinionStat.AttributeBased(FactionDataComponents.MINION_RESOURCES_LEVEL, FactionAttributes.RESOURCE_EFFICIENCY, 2, () -> EntityConstants.MINION_RESOURCE_EFFICIENCY_PL);
    public static final MinionStat INVENTORY_STATS = new MinionStat(FactionDataComponents.MINION_INVENTORY_LEVEL, 2, Component.translatable("gui.factionapi.minion_stats.inventory_level")) {
        @Override
        public void apply(int level, @Nullable IMinionEntity minion, IMinionData data) {
            super.apply(level, minion, data);
            switch (level) {
                case 1,2,3 -> data.set(FactionDataComponents.MINION_INVENTORY_SLOTS, level * 3);
                default -> data.remove(FactionDataComponents.MINION_INVENTORY_SLOTS.get());
            }
            data.getInventory().updateFromData(data);
        }

        @Override
        public String currentValue(IMinionEntity minion, IMinionData data) {
            return String.valueOf(data.getInventory().getContainerSize());
        }
    };
}
