package de.teamlapen.faction.common.world.entities.customization;

import de.teamlapen.faction.api.world.entities.ICustomizationOption;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;

import java.util.Map;

public interface ICustomizable {

    /**
     * @return {@code true} if anything changed
     */
    default boolean apply(CustomizationData data) {
        boolean flag = false;
        for (Map.Entry<Identifier, Object> identifierObjectEntry : data.data().entrySet()) {
            ICustomizationOption<?> iCustomizationOption = CustomizationData.get(identifierObjectEntry.getKey());
            if (iCustomizationOption != null) {
                //noinspection unchecked,rawtypes
                flag |= this.apply((ICustomizationOption) iCustomizationOption, identifierObjectEntry.getValue());
            }
        }
        return flag;
    }

    /**
     * @return {@code true} if anything changed
     */
    <T> boolean apply(ICustomizationOption<T> options, T value);
}
