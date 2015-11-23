package de.teamlapen.vampirism;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.common.Mod;

/**
 * Main class for Vampirism
 */
@Mod(modid = REFERENCE.MODID,name=REFERENCE.NAME,version = REFERENCE.VERSION,acceptedMinecraftVersions = "["+REFERENCE.MINECRAFT_VERSION+"]",dependencies = "required-after:Forge@["+REFERENCE.FORGE_VERSION+",)")
public class VampirismMod {

    @Mod.Instance(value = REFERENCE.MODID)
    public static VampirismMod instance;

}
