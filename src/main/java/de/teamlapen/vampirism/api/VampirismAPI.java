package de.teamlapen.vampirism.api;

import de.teamlapen.vampirism.api.entity.player.IFractionPlayer;
import de.teamlapen.vampirism.api.entity.player.IHunterPlayer;
import de.teamlapen.vampirism.api.entity.player.IVampirePlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.FMLLog;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Class for core api methods
 */
public class VampirismAPI {

    private final static HashMap<Integer,Boolean> sundamageDims=new HashMap<Integer, Boolean>();
    private final static HashMap<Integer,Boolean> sundamageConfiguredDims =new HashMap<Integer, Boolean>();
    private final static Set<Integer> noSundamageBiomes=new CopyOnWriteArraySet<Integer>();
    private static boolean defaultSundamage=false;

    static {
        sundamageDims.put(0,true);
        sundamageDims.put(-1,false);
        sundamageDims.put(1,false);
    }

    /**
     * Specifies the default value for non specified dimensions
     * FOR INTERNAL USAGE ONLY
     * @param val
     */
    public static void setDefaultDimsSundamage(boolean val){
        defaultSundamage=val;
    }

    public static void addNoSundamageBiome(int id){
        noSundamageBiomes.add(id);
    }
    /**
     * Specifies if vampires should get sundamage in this dimension
     * @param dimensionId
     * @param sundamage
     */
    public static void specifySundamageForDim(int dimensionId,boolean sundamage){
        sundamageDims.put(dimensionId,sundamage);
    }

    /**
     * Checkd if vampirs can get sundamage in that biome
     * @param id
     * @return
     */
    public static boolean getSundamageInBiome(int id){
        return !noSundamageBiomes.contains(id);
    }

    /**
     * Resets the configured sundamage dims. E.G. on configuration reload
     * FOR INTERNAL USAGE ONLY
     */
    public static void resetConfiguredSundamgeDims(){
        sundamageConfiguredDims.clear();
    }
    /**
     * Adds settings from Vampirism's config file.
     * FOR INTERNAL USAGE ONLY
     * @param dimensionId
     * @param sundamage
     */
    public static void specifyConfiguredSundamageForDim(int dimensionId, boolean sundamage){
        sundamageConfiguredDims.put(dimensionId,sundamage);
    }

    /**
     * Checks if vampires can get sundamge in that dimension
     * @param dim
     * @return
     */
    public static boolean getSundamageInDim(int dim){
        Boolean r= sundamageConfiguredDims.get(dim);
        if(r==null){
            r= sundamageDims.get(dim);
        }
        return r==null?defaultSundamage:r;
    }

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

    public static List<IFractionPlayer> getAllPlayerProperties(EntityPlayer player){
        List<IFractionPlayer> l=new LinkedList<IFractionPlayer>();
        l.add(getHunterPlayer(player));
        l.add(getVampirePlayer(player));
        return l;
    }

    /**
     * Returns the highest reachable vampire level.
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

    /**
     * Returns the highest reachable hunter level.
     * @return
     */
    public static int getHighestHunterLevel(){
        try {
            Class reference=Class.forName("de.teamlapen.vampirism.util.REFERENCE");
            Field level=reference.getField("HIGHEST_HUNTER_LEVEL");
            return level.getInt(null);
        } catch (Exception e) {
            FMLLog.severe("Failed to retrieve highest hunter level. This should be fixed.");
        }
        return 1;
    }
}
