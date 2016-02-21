package de.teamlapen.vampirism.api.entity.factions;

import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Represents a entity faction (e.g. Vampires)
 */
public abstract class Faction {

    public final String name;
    /**
     * Stores the interface each entity (or for playable factions the IExtendedEntityProperties) implements
     */
    protected final Class<? extends IFactionEntity> entityInterface;

    /**
     * @param name  Name
     * @param iface @param iface Interface each entity (or for playable factions the IExtendedEntityProperties) implements
     */
    protected Faction(String name, Class<? extends IFactionEntity> iface) {
        this.name = name;
        this.entityInterface = iface;
    }

    /**
     * @return Color for name in chat
     */
    public abstract EnumChatFormatting getChatColor();

    /**
     * Used for some rendering, e.g. for displaying the level
     *
     * @return
     */
    @SideOnly(Side.CLIENT)
    public abstract int getColor();
}
