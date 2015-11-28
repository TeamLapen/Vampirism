package de.teamlapen.lib.util;

import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;

/**
 * Simplifies a few strange method names etc.
 * Subject to be removed as soon as there are more reasonable names.
 */
public class HelperOneEightMapping {
    public static void setAvoidsWater(EntityLiving entity,boolean value){
        ((PathNavigateGround)entity.getNavigator()).func_179690_a(value);
    }

    public static boolean getAvoidsWater(EntityLiving entity){
        return ((PathNavigateGround)entity.getNavigator()).func_179689_e();
    }

    public static void setCanSwim(EntityLiving entity,boolean value){
        ((PathNavigateGround)entity.getNavigator()).func_179693_d(value);
    }

    public static void setBreakDoors(EntityLiving entity, boolean value){
        ((PathNavigateGround)entity.getNavigator()).func_179688_b(value);
    }
}
