package de.teamlapen.vampirism.client.gui;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.GuiConfigEntries;
import cpw.mods.fml.client.config.GuiConfigEntries.CategoryEntry;
import cpw.mods.fml.client.config.IConfigElement;
import de.teamlapen.vampirism.Configs;
import de.teamlapen.vampirism.util.REFERENCE;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;

public class ModConfigGui extends GuiConfig {

	public static class BalanceEntry extends CategoryEntry {

		@SuppressWarnings("rawtypes")
		private static List<IConfigElement> getConfigElements() {
			List<IConfigElement> list = new ArrayList<IConfigElement>();
			list.add(new DummyConfigElement.DummyCategoryElement("balance_level", "category.vampirism.balance_level", BalanceLevelEntry.class));
			list.add(new DummyConfigElement.DummyCategoryElement("balance_player_mod", "category.vampirism.balance_player_mod", BalancePlayerModEntry.class));
			list.add(new DummyConfigElement.DummyCategoryElement("balance_player_skills", "category.vampirism.balance_player_skills", BalancePlayerSkillsEntry.class));
			list.add(new DummyConfigElement.DummyCategoryElement("balance_mob_prop", "category.vampirism.balance_mob_prop", BalanceMobPropEntry.class));
			list.add(new DummyConfigElement.DummyCategoryElement("balance_vv_prop", "category.vampirism.balance_vv_prop", BalanceVillagePropEntry.class));
			list.addAll(new ConfigElement(Configs.balance.getCategory(Configs.CATEGORY_BALANCE)).getChildElements());

			return list;

		}

		public BalanceEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement prop) {
			super(owningScreen, owningEntryList, prop);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, getConfigElements(), this.owningScreen.modID, Configs.CATEGORY_BALANCE, true, true, this.owningScreen.title,
					((this.owningScreen.titleLine2 == null ? "" : this.owningScreen.titleLine2) + " > " + this.name));
		}

	}


	public static class BalanceLevelEntry extends CategoryEntry {

		public BalanceLevelEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, (new ConfigElement(Configs.balance.getCategory(Configs.CATEGORY_BALANCE_LEVELING))).getChildElements(), this.owningScreen.modID,
					Configs.CATEGORY_BALANCE_LEVELING, true, false, REFERENCE.NAME + " Balance");
		}

	}

	public static class BalanceMobPropEntry extends CategoryEntry {

		public BalanceMobPropEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, (new ConfigElement(Configs.balance.getCategory(Configs.CATEGORY_BALANCE_MOBPROP))).getChildElements(), this.owningScreen.modID,
					Configs.CATEGORY_BALANCE_MOBPROP, true, false, REFERENCE.NAME + " Balance");
		}
	}

	public static class BalancePlayerModEntry extends CategoryEntry {

		public BalancePlayerModEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, (new ConfigElement(Configs.balance.getCategory(Configs.CATEGORY_BALANCE_PLAYER_MOD))).getChildElements(), this.owningScreen.modID,
					Configs.CATEGORY_BALANCE_PLAYER_MOD, true, false, REFERENCE.NAME + " Balance");
		}
	}

	public static class BalancePlayerSkillsEntry extends CategoryEntry {

		public BalancePlayerSkillsEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, (new ConfigElement(Configs.balance.getCategory(Configs.CATEGORY_BALANCE_PLAYER_SKILLS))).getChildElements(), this.owningScreen.modID,
					Configs.CATEGORY_BALANCE_PLAYER_SKILLS, true, false, REFERENCE.NAME + " Balance");
		}
	}

	public static class BalanceVillagePropEntry extends CategoryEntry {

		public BalanceVillagePropEntry(GuiConfig owningScreen, GuiConfigEntries owningEntryList, IConfigElement configElement) {
			super(owningScreen, owningEntryList, configElement);
		}

		@Override
		protected GuiScreen buildChildScreen() {
			return new GuiConfig(this.owningScreen, (new ConfigElement(Configs.balance.getCategory(Configs.CATEGORY_BALANCE_VVPROP))).getChildElements(), this.owningScreen.modID,
					Configs.CATEGORY_BALANCE_VVPROP, true, false, REFERENCE.NAME + " Balance");
		}
	}

	@SuppressWarnings("rawtypes")
	private static List<IConfigElement> getConfigElements() {
		List<IConfigElement> list = new ArrayList<IConfigElement>();
		list.addAll(new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_GENERAL)).getChildElements());
		list.add(new DummyConfigElement.DummyCategoryElement("village", "category.vampirism.village", new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_VILLAGE)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement("disable", "category.vampirism.disable", new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_DISABLE)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement("gui", "category.vampirism.gui", new ConfigElement(Configs.config.getCategory(Configs.CATEGORY_GUI)).getChildElements()));
		list.add(new DummyConfigElement.DummyCategoryElement("balance", "category.vampirism.balance", BalanceEntry.class));
		return list;

	}

	public ModConfigGui(GuiScreen parentScreen) {
		super(parentScreen, getConfigElements(), REFERENCE.MODID, true, true, REFERENCE.NAME + " Config");
	}

}
