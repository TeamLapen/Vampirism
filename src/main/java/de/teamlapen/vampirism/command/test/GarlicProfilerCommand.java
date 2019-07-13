package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

import java.util.List;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class GarlicProfilerCommand extends BasicCommand {

	public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("garlic_profiler")
        		.requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
        		.executes(context -> {
                    return garlicProfiler(context.getSource());
        		});
    }

    private static int garlicProfiler(CommandSource commandSource) {
        commandSource.sendFeedback(new StringTextComponent("Tick"), true);
        print(commandSource, "tick");
        commandSource.sendFeedback(new StringTextComponent("Garlic"), true);
        print(commandSource, "vampirism_checkGarlic");
		return 0;
	}
	
	private static void print(CommandSource source, String id) {
        List<Profiler.Result> l = ServerLifecycleHooks.getCurrentServer().getProfiler().getProfilingData(id);
        for (Profiler.Result r : l) {
            source.sendFeedback(new StringTextComponent("" + r.profilerName + ": " + r.usePercentage + "|" + r.totalUsePercentage), true);
        }
    }
}
