package de.teamlapen.vampirism.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import de.teamlapen.vampirism.util.Logger;

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
