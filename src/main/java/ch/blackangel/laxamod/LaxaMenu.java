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
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import java.util.Random;

public class LaxaMenu extends Screen {

    private static final ResourceLocation CUSTOM_BG = new ResourceLocation("laxamod", "textures/gui/background.png");
    private static final ResourceLocation LOGO = new ResourceLocation("laxamod", "textures/gui/logo.png");

    private boolean forcePartyMode = false;
    private int partyTick = 0;

    private final java.util.List<Flake> snowFlakes = new java.util.ArrayList<>();
    private boolean isSnowSeason = false;
    private boolean isLogoFlipped = false;

    public LaxaMenu() {
        super(new StringTextComponent("Laxa Menu"));
    }

    @Override
    protected void init() {
        java.util.Calendar cal = java.util.Calendar.getInstance();
        int month = cal.get(java.util.Calendar.MONTH);

        // Saison de neige : Novembre (10) à Mars (2)
        this.isSnowSeason = (month >= 10 || month <= 2);

        if (this.isSnowSeason && snowFlakes.isEmpty()) {
            java.util.Random rand = new java.util.Random();
            for (int i = 0; i < 50; i++) { // Moins de flocons pour un effet plus épuré
                snowFlakes.add(new Flake(rand, this.width));
            }
        }

        this.isLogoFlipped = new Random().nextInt(100) == 0;

        if (this.minecraft != null) {
            this.minecraft.getWindow().setTitle("Laxacube V1 | " + this.minecraft.getUser().getName());
        }

        int w = this.width / 2;
        int h = this.height / 2;

        // Bouton Rejoindre
        this.addButton(new Button(w - 100, h - 10, 200, 20, new StringTextComponent("§5§lRejoindre LaxaCube"), (btn) -> {
            ServerData data = new ServerData("Laxa Server", "188.62.148.92", false);
            data.setResourcePackStatus(ServerData.ServerResourceMode.ENABLED);
            if (this.minecraft != null) {
                this.minecraft.setScreen(new ConnectingScreen(this, this.minecraft, data));
            }
        }));

        this.addButton(new Button(w - 100, h + 15, 200, 20, new StringTextComponent("Options"), (btn) -> {
            if (this.minecraft != null) this.minecraft.setScreen(new OptionsScreen(this, this.minecraft.options));
        }));

        this.addButton(new Button(w - 100, h + 40, 64, 20, new StringTextComponent("§9Discord"), (btn) -> Util.getPlatform().openUri("https://discord.gg/hUsDA8teaR")));
        this.addButton(new Button(w - 32, h + 40, 64, 20, new StringTextComponent("§6Boutique"), (btn) -> Util.getPlatform().openUri("https://laxacube.craftingstore.net/")));
        this.addButton(new Button(w + 36, h + 40, 64, 20, new StringTextComponent("§eSite"), (btn) -> Util.getPlatform().openUri("https://laxacube.vercel.app")));

        // Bouton Langue
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

        // Bouton Invisible Github
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

        // Logique de fête
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        boolean isJan16 = (calendar.get(java.util.Calendar.MONTH) == 0 && calendar.get(java.util.Calendar.DAY_OF_MONTH) == 16);
        boolean activeParty = isJan16 || forcePartyMode;
        if (activeParty) partyTick++;

        // 1. DESSIN DU BACKGROUND
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(CUSTOM_BG);
        blit(matrixStack, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        // 2. DESSIN DE LA NEIGE (Entre le BG et le Logo)
        if (this.isSnowSeason) {
            RenderSystem.disableTexture(); // On dessine des rectangles blancs unis
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            for (Flake flake : snowFlakes) {
                flake.fall(this.width, this.height);
                // Dessine le flocon
                fill(matrixStack, (int)flake.x, (int)flake.y, (int)(flake.x + flake.size), (int)(flake.y + flake.size), 0xFFFFFFFF);
            }
            RenderSystem.disableBlend();
            RenderSystem.enableTexture();
        }

        // 3. DESSIN DU LOGO
        this.minecraft.getTextureManager().bind(LOGO);
        RenderSystem.enableBlend();

        int logoX = (this.width / 2) - 50;
        int logoY = 30;
        int logoWidth = 100;
        int logoHeight = 50;
        int logoOffset = activeParty ? (int)(Math.sin(partyTick * 0.2) * 5) : 0;

        if (this.isLogoFlipped) {
            matrixStack.pushPose();
            matrixStack.translate(logoX + (logoWidth / 2f), (logoY + logoOffset) + (logoHeight / 2f), 0);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrixStack.translate(-(logoWidth / 2f), -(logoHeight / 2f), 0);
            blit(matrixStack, 0, 0, 0, 0, logoWidth, logoHeight, 100, 50);
            matrixStack.popPose();
        } else {
            blit(matrixStack, logoX, logoY + logoOffset, 0, 0, logoWidth, logoHeight, 100, 50);
        }
        RenderSystem.disableBlend();

        // 4. INFOS BAS GAUCHE ET DROITE
        String playerName = this.minecraft.getUser().getName();
        String displayTag = "§7Joueur : §f";
        if (playerName.equalsIgnoreCase("BlackAngel_TV_")) displayTag = "§6§lLE PATRON : §b";
        if (playerName.equalsIgnoreCase("zarroc12")) displayTag = "§6§lLE PATRON : §b";
        drawString(matrixStack, this.font, displayTag + playerName, 5, this.height - 25, 0xFFFFFF);
        String versionText = activeParty ? "§e§lBONNE FÊTE LaxaCube !" : "§7Laxamod v1.0";
        drawString(matrixStack, this.font, versionText, 5, this.height - 15, 0xFFFFFF);

        String copyright = "§f© LaxaCube - Tous droits réservés";
        drawString(matrixStack, this.font, copyright, this.width - this.font.width(copyright) - 5, this.height - 25, 0xFFFFFF);

        // Github Text
        String devPrefix = "§7Dev : ";
        String pseudo = "§bBlackAngel_TV_";
        int devX = this.width - this.font.width("Dev : BlackAngel_TV_") - 5;
        if (mouseX >= devX && mouseX <= devX + this.font.width("Dev : BlackAngel_TV_") && mouseY >= this.height - 15) {
            pseudo = "§eBlackAngel_TV_";
        }
        drawString(matrixStack, this.font, devPrefix + pseudo, devX, this.height - 15, 0xFFFFFF);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    class Flake {
        public float x, y, speed, size;
        public Flake(java.util.Random rand, int width) {
            this.x = rand.nextInt(width);
            this.y = rand.nextInt(500);
            // VITESSE : On passe de 0.5-2.0 à 0.2-0.7 pour une chute très lente
            this.speed = 0.2f + rand.nextFloat() * 0.5f;
            // TAILLE : Des flocons légèrement plus petits (0.8 à 2.0 pixels)
            this.size = 0.8f + rand.nextFloat() * 1.2f;
        }

        public void fall(int width, int height) {
            this.y += this.speed;
            // Petit effet de balancement horizontal (optionnel mais joli)
            this.x += (float) Math.sin(this.y * 0.05f) * 0.2f;

            if (this.y > height) {
                this.y = -10;
                this.x = new java.util.Random().nextInt(width);
            }
        }
    }
}