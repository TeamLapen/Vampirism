package de.teamlapen.vampirism.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.command.arguments.BiomeArgument;
import de.teamlapen.vampirism.command.arguments.ModSuggestionProvider;
import de.teamlapen.vampirism.config.VampirismConfig;
import de.teamlapen.vampirism.util.RegUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_SELECTED_ENTITY = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.config.bloodvalues.blacklist.no_entity"));
    private static final SimpleCommandExceptionType NO_CONFIG_TYPE = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.config.no_config"));
    private static final SimpleCommandExceptionType NO_BLOOD_VALUE_TYPE = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.config.bloodvalues.no_type"));
    private static final SimpleCommandExceptionType NO_BLOOD_VALUE_BLACKLIST_TYPE = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.config.bloodvalues.blacklist.no_type"));
    private static final SimpleCommandExceptionType NO_SUN_DAMAGE_TYPE = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.config.sun_damage.no_type"));
    private static final SimpleCommandExceptionType NO_SUN_DAMAGE_BLACKLIST_TYPE = new SimpleCommandExceptionType(Component.translatable("command.vampirism.base.config.sun_damage.blacklist.no_type"));


    public static ArgumentBuilder<CommandSourceStack, ?> register(@NotNull CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        return Commands.literal("config")
                .requires(context -> context.hasPermission(PERMISSION_LEVEL_ADMIN))
                .executes(context -> {
                    throw NO_CONFIG_TYPE.create();
                })
                .then(Commands.literal("bloodvalues")
                        .executes(context -> {
                            throw NO_BLOOD_VALUE_TYPE.create();
                        })
                        .then(Commands.literal("blacklist")
                                .executes(context -> {
                                    throw NO_BLOOD_VALUE_BLACKLIST_TYPE.create();
                                })
                                .then(Commands.literal("entity")
                                        .executes(context -> blacklistEntity(context.getSource().getPlayerOrException()))
                                        .then(Commands.argument("entity", ResourceArgument.resource(buildContext, Registries.ENTITY_TYPE)).suggests(ModSuggestionProvider.ENTITIES)
                                                .executes(context -> blacklistEntity(context.getSource().getPlayerOrException(), ResourceArgument.getEntityType(context, "entity").key().location()))))))
                .then(Commands.literal("sundamage")
                        .executes(context -> {
                            throw NO_SUN_DAMAGE_TYPE.create();
                        })
                        .then(Commands.literal("blacklist")
                                .executes(context -> {
                                    throw NO_SUN_DAMAGE_BLACKLIST_TYPE.create();
                                })
                                .then(Commands.literal("biome")
                                        .executes(context -> blacklistBiome(context.getSource().getPlayerOrException()))
                                        .then(Commands.argument("biome", BiomeArgument.biome()).suggests(ModSuggestionProvider.BIOMES)
                                                .executes(context -> blacklistBiome(context.getSource().getPlayerOrException(), BiomeArgument.getBiomeId(context, "biome")))))
                                .then(Commands.literal("dimension")
                                        .executes(context -> blacklistDimension(context.getSource().getPlayerOrException()))
                                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                                .executes(context -> blacklistDimension(context.getSource().getPlayerOrException(), DimensionArgument.getDimension(context, "dimension"))))))
                        .then(Commands.literal("enforce")
                                .then(Commands.literal("dimension")
                                        .executes(context -> enforceDimension(context.getSource().getPlayerOrException()))
                                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                                .executes(context -> enforceDimension(context.getSource().getPlayerOrException(), DimensionArgument.getDimension(context, "dimension")))))))
                .then(Commands.literal("bat-dimension-blacklist")
                        .then(Commands.argument("dimension", DimensionArgument.dimension())
                                .executes(context -> batBlacklistDimension(context.getSource().getPlayerOrException(), DimensionArgument.getDimension(context, "dimension")))
                        )
                );
    }

    private static int blacklistEntity(@NotNull ServerPlayer player) throws CommandSyntaxException {
        Vec3 vec3d = player.getEyePosition(1.0F);
        double d0 = 50;

        Vec3 vec3d1 = player.getViewVector(1.0F);
        Vec3 vec3d2 = vec3d.add(vec3d1.x * d0, vec3d1.y * d0, vec3d1.z * d0);
        AABB axisalignedbb = player.getBoundingBox().expandTowards(vec3d1.scale(d0)).inflate(1);


        EntityHitResult result = ProjectileUtil.getEntityHitResult(player.level, player, vec3d, vec3d2, axisalignedbb, (a) -> !a.isSpectator());
        if (result == null) {
            throw NO_SELECTED_ENTITY.create();
        } else {
            Entity entity = result.getEntity();
            EntityType<?> entityType = entity.getType();
            return blacklistEntity(player, RegUtil.id(entityType));
        }
    }

    private static int blacklistEntity(@NotNull ServerPlayer player, @NotNull ResourceLocation entity) {
        return modifyList(player, entity, VampirismConfig.SERVER.blacklistedBloodEntity, "command.vampirism.base.config.entity.blacklisted", "command.vampirism.base.config.entity.not_blacklisted");
    }

    private static int blacklistBiome(@NotNull ServerPlayer player) {
        return blacklistBiome(player, player.getCommandSenderWorld().getBiome(player.blockPosition()).unwrap().map(ResourceKey::location, RegUtil::id));
    }

    private static int blacklistBiome(@NotNull ServerPlayer player, @NotNull ResourceLocation biome) {
        return modifyList(player, biome, VampirismConfig.SERVER.sundamageDisabledBiomes, "command.vampirism.base.config.biome.blacklisted", "command.vampirism.base.config.biome.not_blacklisted");
    }

    private static int blacklistDimension(@NotNull ServerPlayer player) {
        return blacklistDimension(player, player.getLevel());
    }

    private static int blacklistDimension(@NotNull ServerPlayer player, @NotNull ServerLevel dimension) {
        return modifyList(player, dimension.dimension().location(), VampirismConfig.SERVER.sundamageDimensionsOverrideNegative, "command.vampirism.base.config.dimension.blacklisted", "command.vampirism.base.config.dimension.not_blacklisted");
    }

    private static int enforceDimension(@NotNull ServerPlayer player) {
        return enforceDimension(player, player.getLevel());
    }

    private static int enforceDimension(@NotNull ServerPlayer player, @NotNull ServerLevel dimension) {
        return modifyList(player, dimension.dimension().location(), VampirismConfig.SERVER.sundamageDimensionsOverridePositive, "command.vampirism.base.config.dimension.enforced", "command.vampirism.base.config.dimension.not_enforced");
    }

    private static int batBlacklistDimension(@NotNull ServerPlayer player, @NotNull ServerLevel dim) {
        return modifyList(player, dim.dimension().location(), VampirismConfig.SERVER.batDimensionBlacklist, "command.vampirism.base.config.bat_dim.blacklisted", "command.vampirism.base.config.bat_dim.not_blacklisted");
    }

    @SuppressWarnings("SameReturnValue")
    private static int modifyList(@NotNull ServerPlayer player, @NotNull ResourceLocation id, ForgeConfigSpec.@NotNull ConfigValue<List<? extends String>> configList, @NotNull String blacklist, @NotNull String not_blacklist) {
        List<? extends String> list = configList.get();
        if (!list.contains(id.toString())) {
            //noinspection unchecked
            ((List<String>) list).add(id.toString());
            player.displayClientMessage(Component.translatable(blacklist, id), false);
        } else {
            list.remove(id.toString());
            player.displayClientMessage(Component.translatable(not_blacklist, id), false);
        }
        configList.set(list);
        return 0;
    }

}
