package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;

import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.api.VampirismAPI;
import de.teamlapen.vampirism.api.entity.player.IFactionPlayer;
import de.teamlapen.vampirism.api.entity.player.skills.ISkill;
import de.teamlapen.vampirism.api.entity.player.skills.ISkillHandler;
import de.teamlapen.vampirism.core.ModRegistries;
import de.teamlapen.vampirism.entity.factions.FactionPlayerHandler;
import de.teamlapen.vampirism.player.skills.SkillManager;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

/**
 * 
 * @authors Cheaterpaul, Maxanier
 */
public class SkillCommand extends BasicCommand {

	public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("skill")
        		.requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
        		.then(Commands.argument("type", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            return ISuggestionProvider.suggest(ModRegistries.SKILLS.getKeys().stream().map(id -> id.toString()), builder);
                        })
                        .suggests((context, builder) -> {
                            return ISuggestionProvider.suggest(new String[] { "list", "disableall" }, builder);
                        })
        				.executes(context -> {
                            return skill(context.getSource(), context.getSource().asPlayer(), StringArgumentType.getString(context, "type"));
        				}));
    }

    private static int skill(CommandSource commandSource, ServerPlayerEntity asPlayer, String type) {
		IFactionPlayer factionPlayer = FactionPlayerHandler.get(asPlayer).getCurrentFactionPlayer();
		if (factionPlayer == null) {
            commandSource.sendFeedback(new StringTextComponent("You have to be in a faction"), true);
            return 0;
        }
        if ("list".equals(type)) {
            ((SkillManager) VampirismAPI.skillManager()).printSkills(factionPlayer.getFaction(), commandSource);
            return 0;
        }
        if ("disableall".equals(type)) {
            (factionPlayer.getSkillHandler()).resetSkills();
            return 0;
        }
        ISkill skill = ModRegistries.SKILLS.getValue(new ResourceLocation(type));
        if (skill == null) {
            commandSource.sendFeedback(new StringTextComponent("Skill with id " + type + " could not be found for faction " + factionPlayer.getFaction().name()), true);
            return 0;
        }
        if (factionPlayer.getSkillHandler().isSkillEnabled(skill)) {
            factionPlayer.getSkillHandler().disableSkill(skill);
            commandSource.sendFeedback(new StringTextComponent("Disabled skill"), true);
            return 0;
        }
        ISkillHandler.Result result = factionPlayer.getSkillHandler().canSkillBeEnabled(skill);
        if (result == ISkillHandler.Result.OK) {
            factionPlayer.getSkillHandler().enableSkill(skill);
            commandSource.sendFeedback(new StringTextComponent("Enabled skill"), true);
        } else {
            commandSource.sendFeedback(new StringTextComponent("Could not enable skill " + result), true);
        }
        return 0;
	}
}
