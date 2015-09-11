package de.teamlapen.vampirism.util;

/**
 * Created by Max on 11.09.2015.
 */
public interface IItemRegistrable {
    String getBaseName();


    interface IItemMetaRegistrable extends IItemRegistrable{
        int getMetaCount();
        Helper.IntToString getMetaMatcher();
    }

    interface IItemFlexibleRegistrable extends IItemRegistrable{
        String[] getModelVariants();
        Helper.StackToString getModelMatcher();
    }
}
