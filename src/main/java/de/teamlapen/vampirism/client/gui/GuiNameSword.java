package de.teamlapen.vampirism.client.gui;

import de.teamlapen.lib.lib.util.UtilLib;
import de.teamlapen.vampirism.VampirismMod;
import de.teamlapen.vampirism.items.VampirismVampireSword;
import de.teamlapen.vampirism.network.InputEventPacket;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@SideOnly(Side.CLIENT)
public class GuiNameSword extends GuiScreen {
    /**
     * Suggested sword names.
     * Credits to http://www.fantasynamegenerators.com
     */
    private final static String[] sword_names = new String[]{"Felthorn", "Aetherius", "Agatha", "Alpha", "Amnesia", "Anduril", "Apocalypse", "Armageddon", "Arondite", "Ashrune", "Betrayal", "Betrayer", "Blackout", "Blazefury", "Blazeguard", "Blinkstrike", "Bloodquench", "Bloodweep", "Brutality", "Celeste", "Chaos", "Cometfell", "Convergence", "Darkheart", "Dawn", "Dawnbreaker", "Deathbringer", "Deathraze", "Decimation", "Desolation", "Destiny's Song", "Dirge", "Doomblade", "Doombringer", "Draughtbane", "Due Diligence", "Echo", "Eclipse", "Endbringer", "Epilogue", "Espada", "Extinction", "Faithkeeper", "Fate", "Fleshrender", "Florance", "Frenzy", "Fury", "Ghost Reaver", "Ghostwalker", "Gladius", "Glimmer", "Godslayer", "Grasscutter", "Gutrender", "Hatred's Bite", "Heartseeker", "Heartstriker", "Hell's Scream", "Hellfire", "Piece Maker", "Hellreaver", "Honor's Call", "Hope's End", "Infamy", "Interrogator", "Justifier", "Kinslayer", "Klinge", "Knightfall", "Lament", "Lazarus", "Lifedrinker", "Light's Bane", "Lightbane", "Lightbringer", "Lightning", "Limbo", "Loyalty", "Malice", "Mangler", "Massacre", "Mercy", "Misery", "Mournblade", "Narcoleptic", "Needle", "Nethersbane", "Night's Edge", "Night's Fall", "Nightbane", "Nightcrackle", "Nightfall", "Nirvana", "Oathbreaker", "Oathkeeper", "Oblivion", "Omega", "Orenmir", "Peacekeeper", "Persuasion", "Prick", "Purifier", "Rage", "Ragespike", "Ragnarok", "Reckoning", "Reign", "Remorse", "Requiem", "Retirement", "Rigormortis", "Savagery", "Scalpel", "Scar", "Seethe", "Severance", "Shadow Strike", "Shadowsteel", "Silence", "Silencer", "Silver Saber", "Silverlight", "Skullcrusher", "Slice of Life", "Soul Reaper", "Soulblade", "Soulrapier", "Spada", "Spike", "Spineripper", "Spiteblade", "Stalker", "Starshatterer", "Sting", "Stinger", "Storm", "Storm Breaker", "Stormbringer", "Stormcaller", "Story-Weaver", "Striker", "Sun Strike", "Suspension", "Swan Song", "The Ambassador", "The Black Blade", "The End", "The Facelifter", "The Light", "The Oculus", "The Stake", "The Untamed", "The Unyielding", "The Void", "Thorn", "Thunder", "Toothpick", "Tranquility", "Treachery", "Trinity", "Tyrhung", "Unending Tyranny", "Unholy Might", "Valkyrie", "Vanquisher", "Vengeance", "Venom", "Venomshank", "Warmonger", "Widow Maker", "Willbreaker", "Winterthorn", "Wit's End", "Witherbrand", "Wolf", "Worldbreaker", "Worldslayer"};
    private final String yes;
    private final String no;
    private final List<String> listLines = new ArrayList<>();
    private final String text1;
    private final String text2;
    private final ItemStack sword;
    private GuiTextField nameField;

    public GuiNameSword(ItemStack sword) {
        this.yes = UtilLib.translate("gui.yes");
        this.no = UtilLib.translate("gui.no");
        this.text1 = UtilLib.translate("gui.vampirism.name_sword.title");
        this.text2 = UtilLib.translate("gui.vampirism.name_sword.text");
        this.sword = sword;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.yes));
        this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.no));
        this.nameField = new GuiTextField(2, this.fontRenderer, this.width / 2 - 155 + 77, this.height / 6 + 70, 155, 12);
        this.nameField.setTextColor(-1);
        this.nameField.setDisabledTextColour(-1);
        this.nameField.setEnableBackgroundDrawing(true);
        this.nameField.setMaxStringLength(35);
        this.listLines.clear();
        this.listLines.addAll(this.fontRenderer.listFormattedStringToWidth(this.text2, this.width - 50));
        this.nameField.setText(sword_names[new Random().nextInt(sword_names.length)]);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        this.nameField.textboxKeyTyped(typedChar, keyCode);
        super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            String name = nameField.getText();
            if (!StringUtils.isBlank(name)) {
                sword.setStackDisplayName(name);
                VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, name));
            }
        } else if (button.id == 1) {
            VampirismMod.dispatcher.sendToServer(new InputEventPacket(InputEventPacket.NAME_ITEM, VampirismVampireSword.DO_NOT_NAME_STRING));
        }
        this.mc.displayGuiScreen(null);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.nameField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRenderer, this.text1, this.width / 2, 70, 16777215);
        int i = 90;
        for (String s : this.listLines) {
            this.drawCenteredString(this.fontRenderer, s, this.width / 2, i, 16777215);
            i += this.fontRenderer.FONT_HEIGHT;
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
        this.nameField.drawTextBox();
    }
}
