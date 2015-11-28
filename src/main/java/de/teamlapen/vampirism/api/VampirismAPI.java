package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.player.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLLog;

import java.lang.reflect.Field;

/**
 * Class for core api methods
 */
public class VampirismAPI {
    /**
     * Extended entity properties key for vampire player
     */
    public final static String VP_EXT_PROP_NAME="VampirePlayer";

    /**
     * Extended entity properties key for hunter player
     */
    public final static String HP_EXT_PROP_NAME="HunterPlayer";

    /**
     * Don't call before the construction event of the player entity is finished
     * @param player
     * @return
     */
    public static IVampirePlayer getVampirePlayer(EntityPlayer player){
        return (IVampirePlayer) player.getExtendedProperties(VP_EXT_PROP_NAME);
    }

    /**
     * Don't call before the construction event of the player entity is finished
     * @param player
     * @return
     */
    public static IHunterPlayer getHunterPlayer(EntityPlayer player){
        return (IHunterPlayer) player.getExtendedProperties(HP_EXT_PROP_NAME);
    }

    /**
     * Returns the highest reachable vampire level.
     * TODO check if this works and implement at least one call into vanilla vampirism
     * @return
     */
    public static int getHighestVampireLevel(){
        try {
            Class reference=Class.forName("de.teamlapen.vampirism.util.REFERENCE");
            Field level=reference.getField("HIGHEST_VAMPIRE_LEVEL");
            return level.getInt(null);
        } catch (Exception e) {
            FMLLog.severe("Failed to retrieve highest vampire level. This should be fixed.");
        }
        return 1;
    }
}
