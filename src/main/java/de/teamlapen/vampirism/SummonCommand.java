package de.teamlapen.vampirism;

import de.teamlapen.vampirism.proxy.CommonProxy;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.server.CommandSummon;
import net.minecraft.entity.EntityList;
import net.minecraft.util.BlockPos;

import java.util.List;

/**
 * Vampirism's version of the summon command
 */
public class SummonCommand extends CommandSummon {

	@Override
	public String getName() {
		if(VampirismMod.inDev)return "sum";
		return "vampirism-summon";
	}

	public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos)
	{
		return args.length == 1 ? func_175762_a(args, CommonProxy.spawnableEntityNames) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : null);
	}

}
