package de.teamlapen.vampirism.common.world.attributes;

import de.teamlapen.vampirism.common.core.ModDimensions;
import de.teamlapen.vampirism.common.core.ModEnvironmentAttributes;
import net.minecraft.world.attribute.EnvironmentAttributeSystem;
import net.minecraft.world.attribute.EnvironmentAttributes;
import net.minecraft.world.level.Level;

public class EnvironmentLevelAttributeModificator {

    public static void addLayers(EnvironmentAttributeSystem.Builder builder, Level level) {
        if (level.dimensionType().hasSkyLight()) {
            builder.addPositionalLayer(ModEnvironmentAttributes.SUN_DAMAGE.get(), new SunDamageAttributeLayer(level));
        }
    }
}
