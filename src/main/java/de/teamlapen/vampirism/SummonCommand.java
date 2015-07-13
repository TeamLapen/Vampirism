package de.teamlapen.vampirism;

import de.teamlapen.vampirism.proxy.CommonProxy;
import net.minecraft.command.server.CommandSummon;

/**
 * Vampirism's version of the summon command
 */
public class SummonCommand extends CommandSummon {
	@Override public String getCommandName() {
		if(VampirismMod.inDev)return "sum";
		return "vampirism-summon";
	}

	@Override protected String[] func_147182_d() {
		return CommonProxy.spawnableEntityNames.toArray(new String[CommonProxy.spawnableEntityNames.size()]);
	}
}
