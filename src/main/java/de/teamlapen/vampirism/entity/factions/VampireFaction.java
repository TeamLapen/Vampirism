package de.teamlapen.vampirism.entity.factions;

import de.teamlapen.vampirism.api.entity.factions.PlayableFaction;
import de.teamlapen.vampirism.api.entity.player.vampire.IVampirePlayer;
import de.teamlapen.vampirism.api.entity.vampire.IVampire;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

/**
 * Vampire Faction
 */
public class VampireFaction extends PlayableFaction<IVampirePlayer> {

    private static VampireFaction instance;

    public static VampireFaction instance() {
        if (instance == null) {
            instance = new VampireFaction();
        }
        return instance;
    }

    private VampireFaction() {
        super("Vampire", IVampire.class, "VampirePlayer");
    }

    @Override
    public EnumChatFormatting getChatColor() {
        return EnumChatFormatting.LIGHT_PURPLE;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getColor() {
        return Color.MAGENTA.getRGB();
    }

    @Override
    public int getHighestReachableLevel() {
        return REFERENCE.HIGHEST_VAMPIRE_LEVEL;
    }
}
