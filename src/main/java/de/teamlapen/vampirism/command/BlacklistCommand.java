package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.command.arguments.ModSuggestionProvider;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.List;

public class BlacklistCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_SELECTED_ENTITY = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.blacklist.no_entity"));


    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("blacklist")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
                .then(Commands.literal("entity")
                        .executes(context -> blacklistEntity(context.getSource().asPlayer()))
                        .then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(ModSuggestionProvider.ENTITIES)
                                .executes(context -> blacklistEntity(context.getSource().asPlayer(), EntitySummonArgument.getEntityId(context, "entity")))));
    }

    private static int blacklistEntity(ServerPlayerEntity player) throws CommandSyntaxException {
        Vec3d vec3d = player.getEyePosition(1.0F);
        double d0 = 50;

        Vec3d vec3d1 = player.getLook(1.0F);
        Vec3d vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
        AxisAlignedBB axisalignedbb = player.getBoundingBox().expand(vec3d1.scale(d0)).grow(1);


        EntityRayTraceResult result = ProjectileHelper.rayTraceEntities(player.world, player, vec3d, vec3d2, axisalignedbb, (a) -> !a.isSpectator(), d0);
        if (result == null) {
            throw NO_SELECTED_ENTITY.create();
        } else {
            Entity entity = result.getEntity();
            EntityType<?> entityType = entity.getType();
            blacklistEntity(player, entityType.getRegistryName());
        }
        return 0;

    }

    private static int blacklistEntity(ServerPlayerEntity player, ResourceLocation entity) {
        List<? extends String> list = VampirismConfig.SERVER.blacklistedBloodEntity.get();
        if (!list.contains(entity.toString())) {
            ((List<String>) list).add(entity.toString());
            player.sendMessage(new TranslationTextComponent("command.vampirism.base.blacklist.blacklisted", entity));
        } else {
            list.remove(entity.toString());
            player.sendMessage(new TranslationTextComponent("command.vampirism.base.blacklist.not_blacklisted", entity));
        }
        VampirismConfig.SERVER.blacklistedBloodEntity.set(list);

        return 0;
    }
}
