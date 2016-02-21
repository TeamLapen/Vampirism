package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.IHunter;
import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.hunter.IHunterPlayer;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Hunter Faction
 */
public class HunterFaction extends PlayableFaction<IHunterPlayer> {

    private static HunterFaction instance;

    public static HunterFaction instance() {
        if (instance == null) {
            instance = new HunterFaction();
        }
        return instance;
    }

    private HunterFaction() {
        super("Hunter", IHunter.class, "HunterPlayer");
    }

    @Override
    public EnumChatFormatting getChatColor() {
        return EnumChatFormatting.BLUE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColor() {
        return Color.BLUE.getRGB();
    }

    @Override
    public int getHighestReachableLevel() {
        return REFERENCE.HIGHEST_HUNTER_LEVEL;
    }
}
