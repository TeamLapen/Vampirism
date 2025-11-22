package de.teamlapen.vampirism.common.recipes;

public interface ITestableRecipeInput {

    TestType testType();

    enum TestType {
        INPUT_1,
        INPUT_2,
        BOTH
    }
}
