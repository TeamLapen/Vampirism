package de.teamlapen.vampirism.util;

/**
 * Created by Max on 10.09.2015.
 */
public interface IBlockRegistrable {
    String[] getVariantsToRegister();

    boolean shouldRegisterSimpleItem();
}
