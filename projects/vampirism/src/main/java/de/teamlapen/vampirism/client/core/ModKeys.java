package de.teamlapen.vampirism.client.core;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.InputConstants;
import de.teamlapen.faction.client.IMinecraftAccessor;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.api.util.VIdentifier;
import de.teamlapen.vampirism.api.world.entity.player.vampire.IDraculaPlayer;
import de.teamlapen.vampirism.client.gui.screens.SelectAmmoScreen;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundSimpleInputEvent;
import de.teamlapen.vampirism.common.network.packets.server.ServerboundStartFeedingPacket;
import de.teamlapen.vampirism.common.util.Helper;
import de.teamlapen.vampirism.common.world.entity.player.vampire.VampirePlayer;
import de.teamlapen.vampirism.common.world.entity.player.vampire.actions.VampireActions;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Optional;

/**
 * Handles all key/input related stuff
 */
public class ModKeys implements IMinecraftAccessor {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(VIdentifier.mod("main"));

    public static final KeyMapping SUCK_BLOOD = new KeyMapping("key.vampirism.suck_blood", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
    public static final KeyMapping TOGGLE_VISION = new KeyMapping("key.vampirism.toggle_vision", KeyConflictContext.IN_GAME, InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
    public static final KeyMapping SELECT_AMMO = new KeyMapping("key.vampirism.select_ammo", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);
    public static final KeyMapping TOGGLE_WINGS = new KeyMapping("key.vampirism.toggle_wings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_COMMA, CATEGORY);

    public void registerKeyMapping(@NotNull RegisterKeyMappingsEvent event) {
        event.registerCategory(CATEGORY);

        event.register(SUCK_BLOOD);
        event.register(TOGGLE_VISION);
        event.register(SELECT_AMMO);
        event.register(TOGGLE_WINGS);
    }

    private boolean suckKeyDown = false;

    private final List<ModKeys.KeyConfig> keyMappingActions;

    public ModKeys() {
        ImmutableList.Builder<KeyConfig> keyMappingActions = ImmutableList.builder();
        keyMappingActions.add(new KeyConfig(TOGGLE_VISION, this::switchVision, true));
        keyMappingActions.add(new KeyConfig(SELECT_AMMO, this::selectAmmo, true));
        keyMappingActions.add(new KeyConfig(TOGGLE_WINGS, this::growWings, true));
        this.keyMappingActions = keyMappingActions.build();
    }

    @SubscribeEvent
    public void handleMouseButton(InputEvent.MouseButton.Post event) {
        handleInputEvent(event, event.getAction());
    }

    @SubscribeEvent
    public void handleKey(InputEvent.Key event) {
        handleInputEvent(event, event.getAction());
    }

    public void handleInputEvent(InputEvent event, int action) {
        if (SUCK_BLOOD.isDown()) {
            suck();
        } else {
            endSuck();
            if (action == InputConstants.PRESS) {
                for (KeyConfig config : this.keyMappingActions) {
                    if (config.isDown()) {
                        config.run();
                        break;
                    }
                }
            }
        }
        updateWingsFlying();
    }

    private void suck() {
        if (!suckKeyDown) {
            HitResult mouseOver = Minecraft.getInstance().hitResult;
            suckKeyDown = true;
            LocalPlayer player = Minecraft.getInstance().player;
            if (mouseOver != null && !player.isSpectator()) {
                VampirePlayer vampire = VampirePlayer.get(player);
                if (vampire.getLevel() > 0 && !vampire.getActionHandler().isActionActive(VampireActions.BAT)) {
                    if (mouseOver instanceof EntityHitResult entityHitResult) {
                        VampirismMod.proxy.sendToServer(new ServerboundStartFeedingPacket(entityHitResult.getEntity().getId()));
                    } else if (mouseOver instanceof BlockHitResult blockHitResult) {
                        VampirismMod.proxy.sendToServer(new ServerboundStartFeedingPacket(blockHitResult.getBlockPos(), blockHitResult.getDirection()));
                    } else {
                        LOGGER.warn("Unknown mouse over type while trying to feed");
                    }
                }
            }
        }
    }

    private void updateWingsFlying() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && Helper.isVampire(player) && player.isFallFlying()) {
            Optional<IDraculaPlayer> draculaOpt = IDraculaPlayer.getDracula(player).filter(x -> x.asEntity().isFallFlying()).filter(x -> Minecraft.getInstance().options.keyJump.consumeClick());
            draculaOpt.ifPresent(dracula -> {
                VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.JUMP));
                dracula.swingWings();
            });
        }
    }

    private void endSuck() {
        if (suckKeyDown) {
            suckKeyDown = false;
            VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.FINISH_SUCK_BLOOD));
        }
    }

    private void switchVision() {
        VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.TOGGLE_VAMPIRE_VISION));
    }

    private void selectAmmo() {
        if (player().isAlive()) {
            SelectAmmoScreen.show();
        }
    }

    private void growWings() {
        VampirismMod.proxy.sendToServer(new ServerboundSimpleInputEvent(ServerboundSimpleInputEvent.Event.GROW_WINGS));
    }

    private record KeyConfig(KeyMapping mapping, Runnable action, boolean consume) {

        public boolean isDown() {
            return this.consume ? this.mapping.consumeClick() : this.mapping.isDown();
        }

        public void run() {
            this.action.run();
        }
    }
}
