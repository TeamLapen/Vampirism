package de.teamlapen.faction.common.world.entities.appearance;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public interface IAppearanceHolder {

    <T> void setAppearanceData(AppearanceKey<T> key, T data);

}
