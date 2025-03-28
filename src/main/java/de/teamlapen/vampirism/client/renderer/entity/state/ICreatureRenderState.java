package de.teamlapen.vampirism.client.renderer.entity.state;

public interface ICreatureRenderState {

    int vampirism$blood();

    void vampirism$blood(int blood);

    boolean vampirism$poisonousBlood();

    void vampirism$poisonousBlood(boolean poisonous);
}
