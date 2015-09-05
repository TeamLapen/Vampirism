package de.teamlapen.vampirism.coremod;

import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;

/**
 * Coremod loader plugin
 * 
 * @author Maxanier
 *
 */
@IFMLLoadingPlugin.Name(value = REFERENCE.NAME)
// The readable mod name
@IFMLLoadingPlugin.MCVersion(value = "1.7.10")
// The MC version it is designed for (Remember? Upwards/Downwards compatibility
// lost!)
@IFMLLoadingPlugin.TransformerExclusions(value = "de.teamlapen.vampirism")
// Your whole core mod package - Whatever you don't want the transformers to run
// over to prevent circularity Exceptions
@IFMLLoadingPlugin.SortingIndex(value = 1500)
// How early your core mod is called - Use > 1000 to work with srg names
public class VampirismFMLLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { EntityLivingBaseClassTransformer.class.getName(), PlayerClassTransformer.class.getName(), EntityRendererClassTransformer.class.getName(),
				RenderClassTransformer.class.getName(), PlayerMPClassTransformer.class.getName() };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {

	}

}
