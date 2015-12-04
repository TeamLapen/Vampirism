package de.teamlapen.vampirism.api.entity.player;

import de.teamlapen.vampirism.api.VampirismAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.FMLLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Fraction registry.
 * Register all extended properties that extend {@link IFractionPlayer} here
 * Currently only used for managing IPlayerEventListeners.
 */
public class FractionRegistry {
    private static List<Fraction> temp=new ArrayList<Fraction>();
    private static Fraction[] fractions;
    static {
        addFraction(VampirismAPI.VP_EXT_PROP_NAME,true);
        addFraction(VampirismAPI.HP_EXT_PROP_NAME,true);
    }

    /**
     *
     * @param propertiesKey ExtendedPlayerPropertiesKey The corresponding class has to implement IFractionPlayer
     * @param shouldReceivePlayerEvents Whether the extended props implement {@link IPlayerEventListener} or not.
     */
    public static void addFraction(String propertiesKey, boolean shouldReceivePlayerEvents){
        if(temp==null){
            FMLLog.severe("[VampirismApi] You have to register fractions BEFORE post init. ("+propertiesKey+")");
        }
        else{
            temp.add(new Fraction(propertiesKey,shouldReceivePlayerEvents));
        }
    }

    /**
     * Returns the fraction player in which the player has a level above zero which means he is part of that faction.
     * @param player
     * @return Can be null if the player does not belong to any faction
     */
    public static IFractionPlayer getActiveFraction(EntityPlayer player){
        for(Fraction f:fractions){
            IFractionPlayer player1=f.getProp(player);
            if(player1.getLevel()>0)return player1;
        }
        return null;
    }

    /**
     * Finishes registrations during post init.
     * FOR INTERNAL USAGE ONLY
     */
    public static void finish(){
        fractions =temp.toArray(new Fraction[temp.size()]);
        temp=null;
    }

    public static Fraction[] getFractions(){
        return fractions;
    }

    public static class Fraction{
        private final String prop;
        private final boolean implEventListener;

        /**
         * @param prop ExtendedPlayerPropertiesKey
         * @param implEventListener Whether the extended props implement {@link IPlayerEventListener} or not
         */
        public Fraction(String prop,boolean implEventListener) {
            this.prop = prop;
            this.implEventListener=implEventListener;
        }

        public IFractionPlayer getProp(EntityPlayer player){
            return (IFractionPlayer) player.getExtendedProperties(prop);
        }

        public boolean implementsEventListener(){
            return implEventListener;
        }
    }

}
