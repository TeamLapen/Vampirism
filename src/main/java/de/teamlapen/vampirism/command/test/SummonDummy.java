package de.teamlapen.vampirism.command.test;

import com.mojang.brigadier.builder.ArgumentBuilder;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.core.ModEntities;
import de.teamlapen.vampirism.entity.VampirismEntity;
import de.teamlapen.vampirism.entity.hunter.TrainingDummyHunterEntity;
import de.teamlapen.vampirism.entity.vampire.TrainingDummyVampireEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;


public class SummonDummy extends BasicCommand {

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("summonDummy")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.literal("vampire").executes(context -> summon(context.getSource().asPlayer(),true)))
                .then(Commands.literal("hunter").executes(context -> summon(context.getSource().asPlayer(),false)));
    }

    private static int summon(ServerPlayerEntity p,boolean b) {
        VampirismEntity t = b? new TrainingDummyVampireEntity(ModEntities.vampire,p.world) : new TrainingDummyHunterEntity(ModEntities.hunter, p.world);
        t.copyLocationAndAnglesFrom(p);
        p.world.addEntity(t);
        return 0;
    }
}
