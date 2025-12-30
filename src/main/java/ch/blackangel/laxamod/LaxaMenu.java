package ch.blackangel.laxamod;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.OptionsScreen;
import net.minecraft.client.gui.screen.ConnectingScreen;
import net.minecraft.client.gui.screen.LanguageScreen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;

public class LaxaMenu extends Screen {

    private static final ResourceLocation CUSTOM_BG = new ResourceLocation("laxamod", "textures/gui/background.png");
    private static final ResourceLocation LOGO = new ResourceLocation("laxamod", "textures/gui/logo.png");

    // MODIFIE ICI : Mets sur 'true' pour tester l'effet de fête immédiatement
    private boolean forcePartyMode = false;
    private int partyTick = 0;

    public LaxaMenu() {
        super(new StringTextComponent("Laxa Menu"));
    }

    @Override
    protected void init() {
        if (this.minecraft != null) {
            this.minecraft.getWindow().setTitle("Laxacube V1 | " + this.minecraft.getUser().getName());
        }

        int w = this.width / 2;
        int h = this.height / 2;

        // 1. BOUTONS PRINCIPAUX
        this.addButton(new Button(w - 100, h - 10, 200, 20, new StringTextComponent("§5§lRejoindre LaxaCube"), (btn) -> {
            ServerData data = new ServerData("Laxa Server", "laxacube.servegame.com", false);
            if (this.minecraft != null) {
                this.minecraft.setScreen(new ConnectingScreen(this, this.minecraft, data));
            }
        }));

        this.addButton(new Button(w - 100, h + 15, 200, 20, new StringTextComponent("Options"), (btn) -> {
            if (this.minecraft != null) this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));

        // 2. LIGNE DES RÉSEAUX
        this.addButton(new Button(w - 100, h + 40, 64, 20, new StringTextComponent("§9Discord"), (btn) -> Util.getPlatform().openUri("https://discord.gg/hUsDA8teaR")));
        this.addButton(new Button(w - 32, h + 40, 64, 20, new StringTextComponent("§6Boutique"), (btn) -> Util.getPlatform().openUri("https://laxacube.craftingstore.net/")));
        this.addButton(new Button(w + 36, h + 40, 64, 20, new StringTextComponent("§eSite"), (btn) -> Util.getPlatform().openUri("https://laxacube.vercel.app")));

        // 3. LIGNE DU BAS (Langue + Quitter)
        this.addButton(new Button(w - 100, h + 75, 20, 20, new StringTextComponent(""), (btn) -> {
            if (this.minecraft != null) {
                this.minecraft.setScreen(new LanguageScreen(this, this.minecraft.options, this.minecraft.getLanguageManager()));
            }
        }) {
            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
                Minecraft.getInstance().getTextureManager().bind(Button.WIDGETS_LOCATION);
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                int i = this.isHovered() ? 20 : 0;
                this.blit(matrixStack, this.x, this.y, 0, 106 + i, 20, 20);
            }
        });

        this.addButton(new Button(w - 75, h + 75, 175, 20, new StringTextComponent("§cQuitter le jeu"), (btn) -> {
            if (this.minecraft != null) this.minecraft.stop();
        }));

        // 4. BOUTON GITHUB
        String fullDevText = "Dev : BlackAngel_TV_";
        int textWidth = this.font.width(fullDevText);
        this.addButton(new Button(this.width - textWidth - 5, this.height - 15, textWidth, 10, new StringTextComponent(""), (btn) -> {
            Util.getPlatform().openUri("https://github.com/BlackAngelTVdev");
        }) {
            @Override public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {}
        });
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if (this.minecraft == null) return;

        // --- LOGIQUE DE FÊTE ---
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        boolean isJan16 = (calendar.get(java.util.Calendar.MONTH) == 0 && calendar.get(java.util.Calendar.DAY_OF_MONTH) == 16);
        boolean activeParty = isJan16 || forcePartyMode;

        if (activeParty) {
            partyTick++; // Fait avancer l'animation
        }

        // 1. DESSIN DU BACKGROUND
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CUSTOM_BG);
        blit(matrixStack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        // 2. DESSIN DU LOGO
        this.minecraft.getTextureManager().bind(LOGO);
        RenderSystem.enableBlend();
        int logoOffset = activeParty ? (int)(Math.sin(partyTick * 0.2) * 5) : 0;
        blit(matrixStack, (this.width / 2) - 50, 30 + logoOffset, 0, 0, 100, 50, 100, 50);
        RenderSystem.disableBlend();

        // 3. INFOS BAS GAUCHE
        String playerName = this.minecraft.getUser().getName();
        String displayTag = "§7Joueur : §f";

        if (playerName.equalsIgnoreCase("BlackAngel_TV_") || playerName.equalsIgnoreCase("Pseudo_Associe")) {
            displayTag = "§6§lLE PATRON : §b";
        } else if (playerName.equalsIgnoreCase("zarroc12")) {
            displayTag = "§2§lLE PATRON : §f";
        }

        drawString(matrixStack, this.font, displayTag + playerName, 5, this.height - 25, 0xFFFFFF);

        String versionText = activeParty ? "§e§lBONNE FÊTE LaxaCube ! §7v1.0" : "§7Laxamod v1.0";
        drawString(matrixStack, this.font, versionText, 5, this.height - 15, 0xFFFFFF);

        // 4. INFOS BAS DROITE
        String copyright = "§f© LaxaCube - Tous droits réservés";
        drawString(matrixStack, this.font, copyright, this.width - this.font.width(copyright) - 5, this.height - 25, 0xFFFFFF);

        // 5. GITHUB
        String devPrefix = "§7Dev : ";
        String pseudo = "§bBlackAngel_TV_";
        int devX = this.width - this.font.width("Dev : BlackAngel_TV_") - 5;
        if (mouseX >= devX && mouseX <= devX + this.font.width("Dev : BlackAngel_TV_") && mouseY >= this.height - 15) {
            pseudo = "§eBlackAngel_TV_";
        }
        drawString(matrixStack, this.font, devPrefix + pseudo, devX, this.height - 15, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }
}