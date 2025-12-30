package de.teamlapen.faction.common.server.commands.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.faction.api.FactionRegistries;
import de.teamlapen.faction.api.factions.skills.ISkill;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class SkillArgument extends ResourceArgument<ISkill<?>> {

    public SkillArgument(CommandBuildContext pContext, ResourceKey<? extends Registry<ISkill<?>>> pRegistryKey) {
        super(pContext, pRegistryKey);
    }

    public static Holder.Reference<ISkill<?>> getSkill(CommandContext<CommandSourceStack> pContext, String pArgument) throws CommandSyntaxException {
        return ResourceArgument.getResource(pContext, pArgument, FactionRegistries.Keys.SKILL);
    }
}
