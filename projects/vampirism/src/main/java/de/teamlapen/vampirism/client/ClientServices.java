package de.teamlapen.vampirism.client;

import de.teamlapen.vampirism.client.core.ClientEventHandler;
import de.teamlapen.vampirism.client.core.ModKeys;
import de.teamlapen.vampirism.client.gui.ScreenEventHandler;
import de.teamlapen.factions.client.gui.overlay.CustomBossEventOverlay;
import de.teamlapen.vampirism.client.gui.overlay.FullScreenOverlay;
import de.teamlapen.vampirism.client.gui.overlay.VampirismHUDOverlay;
import de.teamlapen.vampirism.client.models.armor.ArmorModels;
import de.teamlapen.vampirism.client.renderer.BloodVisionRenderer;
import de.teamlapen.vampirism.client.renderer.RenderHandler;
import de.teamlapen.vampirism.common.util.PlayerSkinHelper;
import de.teamlapen.vampirism.common.util.Services;
import de.teamlapen.vampirism.data.reloadlistener.vampirebook.VampireBooks;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.client.event.ClientTickEvent;

public class ClientServices extends Services {

    //<editor-fold desc="Services" >

    private final VampirismHUDOverlay vampirismHUDOverlay = new VampirismHUDOverlay();
    private final RenderHandler renderHandler = new RenderHandler();
    private final BloodVisionRenderer bloodVisionRenderer = new BloodVisionRenderer();
    private final VampireBooks vampireBooks = new VampireBooks();
    private final ArmorModels armorModels = new ArmorModels();
    private final ModKeys modKeys = new ModKeys();
    private final ScreenEventHandler screenEventHandler = new ScreenEventHandler();
    private final ClientEventHandler clientEventHandler = new ClientEventHandler();
    private final PlayerSkinHelper playerSkinHelper = new PlayerSkinHelper();
    private final ClientTooltips clientTooltips = new ClientTooltips();
    private final FullScreenOverlay fullScreenOverlay = new FullScreenOverlay();

    //</editor-fold>

    public ClientServices(ModContainer container) {
        super(container);
    }

    //<editor-fold desc="Getters" >

    public VampirismHUDOverlay hud() {
        return this.vampirismHUDOverlay;
    }

    public FullScreenOverlay fullScreenOverlay() {
        return this.fullScreenOverlay;
    }

    public RenderHandler renderHandler() {
        return this.renderHandler;
    }

    public BloodVisionRenderer bloodVisionRenderer() {
        return this.bloodVisionRenderer;
    }

    public VampireBooks vampireBooks() {
        return this.vampireBooks;
    }

    public ArmorModels armorModels() {
        return this.armorModels;
    }

    public ModKeys modKeys() {
        return this.modKeys;
    }

    public ScreenEventHandler screenEventHandler() {
        return this.screenEventHandler;
    }

    public ClientEventHandler clientEventHandler() {
        return this.clientEventHandler;
    }

    public PlayerSkinHelper playerSkinHelper() {
        return this.playerSkinHelper;
    }

    //</editor-fold>

    //<editor-fold desc="Register" >

    @Override
    protected void registerModBus(IEventBus bus) {
        bus.register(this.armorModels);
        bus.addListener(this.bloodVisionRenderer::onClientSetup);
        bus.register(this.vampireBooks);
        bus.addListener(this.clientTooltips::registerTooltipRenderer);
        bus.addListener(this.modKeys::registerKeyMapping);
    }

    @Override
    protected void registerGameBus(IEventBus bus) {
        bus.register(this.vampirismHUDOverlay);
        bus.register(this.renderHandler);
        bus.register(this.bloodVisionRenderer);
        bus.register(this.modKeys);
        bus.register(this.screenEventHandler);
        bus.register(this.clientEventHandler);
        bus.register(this.playerSkinHelper);
        bus.register(this.clientTooltips);
        bus.addListener(ClientTickEvent.Pre.class, event -> this.fullScreenOverlay.update());
    }

    //</editor-fold>
}
