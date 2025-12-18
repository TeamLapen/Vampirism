package de.teamlapen.vampirism.common.world.items.recipes;

public interface ITestableRecipeInput {

    TestType testType();

    enum TestType {
        INPUT_1,
        INPUT_2,
        BOTH
    }
}
