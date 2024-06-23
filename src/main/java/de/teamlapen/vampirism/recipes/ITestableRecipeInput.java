package de.teamlapen.vampirism.recipes;

public interface ITestableRecipeInput {

    TestType testType();

    enum TestType {
        INPUT_1,
        INPUT_2,
        BOTH
    }
}
