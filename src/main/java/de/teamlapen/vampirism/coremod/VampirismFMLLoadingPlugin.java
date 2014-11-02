package de.teamlapen.vampirism.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import de.teamlapen.vampirism.util.Logger;
import de.teamlapen.vampirism.util.REFERENCE;

@IFMLLoadingPlugin.Name(value = REFERENCE.NAME) // The readable mod name
@IFMLLoadingPlugin.MCVersion(value = "1.7.10") // The MC version it is designed for (Remember? Upwards/Downwards compatibility lost!)
@IFMLLoadingPlugin.TransformerExclusions(value = "de.teamlapen.vampirism") // Your whole core mod package - Whatever you don't want the transformers to run over to prevent circularity Exceptions
@IFMLLoadingPlugin.SortingIndex(value = 999) // How early your core mod is called - Use > 1000 to work with srg names
public class VampirismFMLLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{EntityLivingBaseClassTransformer.class.getName(),PlayerClassTransformer.class.getName(),EntityRendererClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSetupClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAccessTransformerClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
