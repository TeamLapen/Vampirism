package de.teamlapen.vampirism.misc.injection.client;

import de.teamlapen.vampirism.misc.extension.client.IModelPart;
import net.minecraft.client.model.geom.ModelPart;

import java.util.List;

@Deprecated
public interface IModelPartVampirismMock extends IModelPart {

    @Override
    default List<ModelPart.Cube> vampirism$cubes() {
        throw new IllegalStateException("This class is only supported as injection class");
    }
}
