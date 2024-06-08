package de.teamlapen.vampirism.command.arguments;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.teamlapen.vampirism.api.VampirismRegistries;
import de.teamlapen.vampirism.api.entity.player.actions.IAction;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ActionArgument<T> extends ResourceArgument<T> {

    private ActionArgument(CommandBuildContext pContext, ResourceKey<? extends Registry<T>> pRegistryKey) {
        super(pContext, pRegistryKey);
    }

    public static Holder.Reference<IAction<?>> getAction(CommandContext<CommandSourceStack> pContext, String pArgument) throws CommandSyntaxException {
        return ResourceArgument.getResource(pContext, pArgument, VampirismRegistries.Keys.ACTION);
    }
}
