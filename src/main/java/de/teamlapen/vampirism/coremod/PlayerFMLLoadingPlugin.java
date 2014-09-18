package de.teamlapen.vampirism.coremod;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import de.teamlapen.vampirism.util.Logger;

public class PlayerFMLLoadingPlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[]{PlayerClassTransformer.class.getName()};
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
