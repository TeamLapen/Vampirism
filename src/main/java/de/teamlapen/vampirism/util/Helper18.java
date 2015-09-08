package de.teamlapen.vampirism.util;

import com.google.common.base.Predicate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.pathfinding.PathNavigateGround;

/**
 * Created by Max on 08.09.2015.
 */
public class Helper18 {

    public static void setAvoidsWater(EntityLiving entity,boolean value){
        ((PathNavigateGround)entity.getNavigator()).func_179690_a(value);
    }

    public static boolean getAvoidsWater(EntityLiving entity){
        return ((PathNavigateGround)entity.getNavigator()).func_179689_e();
    }

    public static void setCanSwim(EntityLiving entity,boolean value){
        ((PathNavigateGround)entity.getNavigator()).func_179693_d(value);
    }

    public static void setBreakDoors(EntityLiving entity,boolean value){
        ((PathNavigateGround)entity.getNavigator()).func_179688_b(value);
    }

    public static Predicate getPredicateForClass(final Class clazz){
        return new Predicate() {
            @Override
            public boolean apply(Object input) {
                return clazz.isInstance(input);
            }
        };
    }


}
