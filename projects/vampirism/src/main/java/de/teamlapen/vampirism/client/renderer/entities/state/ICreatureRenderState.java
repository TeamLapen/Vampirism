package de.teamlapen.vampirism.client.renderer.entities.state;

public interface ICreatureRenderState {

    int vampirism$blood();

    void vampirism$blood(int blood);

    boolean vampirism$poisonousBlood();

    void vampirism$poisonousBlood(boolean poisonous);
}
