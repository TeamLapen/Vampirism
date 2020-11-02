package de.teamlapen.vampirism.command;

import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import de.teamlapen.lib.lib.util.BasicCommand;
import de.teamlapen.vampirism.command.arguments.BiomeArgument;
import de.teamlapen.vampirism.command.arguments.ModSuggestionProvider;
import de.teamlapen.vampirism.config.VampirismConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.DimensionArgument;
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
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class ConfigCommand extends BasicCommand {

    private static final SimpleCommandExceptionType NO_SELECTED_ENTITY = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.config.bloodvalues.blacklist.no_entity"));
    private static final SimpleCommandExceptionType NO_CONFIG_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.config.no_config"));
    private static final SimpleCommandExceptionType NO_BLOOD_VALUE_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.config.bloodvalues.no_type"));
    private static final SimpleCommandExceptionType NO_BLOOD_VALUE_BLACKLIST_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.config.bloodvalues.blacklist.no_type"));
    private static final SimpleCommandExceptionType NO_SUN_DAMAGE_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.config.sun_damage.no_type"));
    private static final SimpleCommandExceptionType NO_SUN_DAMAGE_BLACKLIST_TYPE = new SimpleCommandExceptionType(new TranslationTextComponent("command.vampirism.base.config.sun_damage.blacklist.no_type"));


    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("config")
                .requires(context -> context.hasPermissionLevel(PERMISSION_LEVEL_ADMIN))
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
                                        .executes(context -> blacklistEntity(context.getSource().asPlayer()))
                                        .then(Commands.argument("entity", EntitySummonArgument.entitySummon()).suggests(ModSuggestionProvider.ENTITIES)
                                                .executes(context -> blacklistEntity(context.getSource().asPlayer(), EntitySummonArgument.getEntityId(context, "entity")))))))
                .then(Commands.literal("sundamage")
                        .executes(context -> {
                            throw NO_SUN_DAMAGE_TYPE.create();
                        })
                        .then(Commands.literal("blacklist")
                                .executes(context -> {
                                    throw NO_SUN_DAMAGE_BLACKLIST_TYPE.create();
                                })
                                .then(Commands.literal("biome")
                                        .executes(context -> blacklistBiome(context.getSource().asPlayer()))
                                        .then(Commands.argument("biome", BiomeArgument.biome()).suggests(ModSuggestionProvider.BIOMES)
                                                .executes(context -> blacklistBiome(context.getSource().asPlayer(), BiomeArgument.getBiomeId(context, "biome")))))
                                .then(Commands.literal("dimension")
                                        .executes(context -> blacklistDimension(context.getSource().asPlayer()))
                                        .then(Commands.argument("dimension", DimensionArgument.getDimension())
                                                .executes(context -> blacklistDimension(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension"))))))
                        .then(Commands.literal("enforce")
                                .then(Commands.literal("dimension")
                                        .executes(context -> enforceDimension(context.getSource().asPlayer()))
                                        .then(Commands.argument("dimension", DimensionArgument.getDimension())
                                                .executes(context -> enforceDimension(context.getSource().asPlayer(), DimensionArgument.getDimensionArgument(context, "dimension")))))));
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
            return blacklistEntity(player, entityType.getRegistryName());
        }
    }

    private static int blacklistEntity(ServerPlayerEntity player, ResourceLocation entity) {
        return modifyList(player, entity, VampirismConfig.SERVER.blacklistedBloodEntity, "command.vampirism.base.config.entity.blacklisted", "command.vampirism.base.config.entity.not_blacklisted");
    }

    private static int blacklistBiome(ServerPlayerEntity player) {
        return blacklistBiome(player, player.getEntityWorld().getBiome(player.getPosition()).getRegistryName());
    }

    private static int blacklistBiome(ServerPlayerEntity player, ResourceLocation biome) {
        return modifyList(player, biome, VampirismConfig.SERVER.sundamageDisabledBiomes, "command.vampirism.base.config.biome.blacklisted", "command.vampirism.base.config.biome.not_blacklisted");
    }

    private static int blacklistDimension(ServerPlayerEntity player) {
        return blacklistDimension(player, player.dimension);
    }

    private static int blacklistDimension(ServerPlayerEntity player, DimensionType dimension) {
        return modifyList(player, dimension.getRegistryName(), VampirismConfig.SERVER.sundamageDimensionsOverrideNegative, "command.vampirism.base.config.dimension.blacklisted", "command.vampirism.base.config.dimension.not_blacklisted");
    }

    private static int enforceDimension(ServerPlayerEntity player) {
        return enforceDimension(player, player.dimension);
    }

    private static int enforceDimension(ServerPlayerEntity player, DimensionType dimension) {
        return modifyList(player, dimension.getRegistryName(), VampirismConfig.SERVER.sundamageDimensionsOverridePositive, "command.vampirism.base.config.dimension.enforced", "command.vampirism.base.config.dimension.not_enforced");
    }

    private static int modifyList(ServerPlayerEntity player, ResourceLocation id, ForgeConfigSpec.ConfigValue<List<? extends String>> configList, String blacklist, String not_blacklist) {
        List<? extends String> list = configList.get();
        if (!list.contains(id.toString())) {
            ((List<String>) list).add(id.toString());
            player.sendMessage(new TranslationTextComponent(blacklist, id));
        } else {
            list.remove(id.toString());
            player.sendMessage(new TranslationTextComponent(not_blacklist, id));
        }
        configList.set(list);
        return 0;
    }

}
