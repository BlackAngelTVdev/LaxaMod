package ch.blackangel.laxamod;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraft.client.gui.screen.MultiplayerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import java.io.InputStream;

@Mod(LaxaMod.MOD_ID)
public class LaxaMod {
    public static final String MOD_ID = "laxamod";

    // Variables pour Discord
    private final DiscordRPC lib = DiscordRPC.INSTANCE;
    private long startTimestamp;

    public LaxaMod() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);

        // Initialisation de Discord au lancement
        initDiscord();
    }

    private void initDiscord() {
        String applicationId = "1456699010541687036"; // Ton ID de l'image
        String steamId = "";
        DiscordEventHandlers handlers = new DiscordEventHandlers();
        lib.Discord_Initialize(applicationId, handlers, true, steamId);

        this.startTimestamp = System.currentTimeMillis() / 1000;
        updateDiscordPresence("Lancement du jeu...");
    }

    public void updateDiscordPresence(String details) {
        DiscordRichPresence presence = new DiscordRichPresence();
        presence.startTimestamp = this.startTimestamp;

        // Ligne principale : "Joue sur Laxacube avec X personnes"
        presence.details = details;

        // Sous-titre : IP du serveur + Site Web (puisque le bouton pose problème)
        presence.state = "IP: laxacube.servegame.com";

        presence.largeImageKey = "1024";
        presence.largeImageText = "Laxacube V1 - laxacube.vercel.app";

        // On utilise le champ "Small Image" pour mettre un logo de site web si tu en as un
        presence.smallImageKey = "web_icon"; // Optionnel : nom de l'image du site sur ton panel Discord
        presence.smallImageText = "Visiter le site web";

        lib.Discord_UpdatePresence(presence);
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.getWindow() != null && mc.getUser() != null) {
                mc.getWindow().setTitle("Laxacube V1 | " + mc.getUser().getName());

                if (mc.level != null) {
                    // On récupère le nombre de joueurs connectés
                    int playerCount = 0;
                    if (mc.getConnection() != null) {
                        playerCount = mc.getConnection().getOnlinePlayers().size();
                    }

                    String text = "Joue sur Laxacube avec " + playerCount + " personnes";
                    updateDiscordPresence(text);
                } else {
                    updateDiscordPresence("Dans les menus");
                }
            }
            lib.Discord_RunCallbacks();
        }
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                InputStream is16 = getClass().getResourceAsStream("/assets/laxamod/textures/gui/icon_16.png");
                InputStream is32 = getClass().getResourceAsStream("/assets/laxamod/textures/gui/icon_32.png");
                if (is16 != null && is32 != null) {
                    Minecraft.getInstance().getWindow().setIcon(is16, is32);
                }
            } catch (Exception e) { e.printStackTrace(); }
        });
    }



    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if (event.getGui() instanceof MainMenuScreen && !(event.getGui() instanceof LaxaMenu)) {
            event.setGui(new LaxaMenu());
        }
        if (event.getGui() instanceof MultiplayerScreen) {
            event.setGui(new LaxaMenu());
        }
    }

    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        if (event.getGui() instanceof IngameMenuScreen) {
            int x = event.getGui().width / 2;
            int y = event.getGui().height / 4 + 132;
            event.addWidget(new Button(x - 100, y, 200, 20, new StringTextComponent("§e§lWiki LaxaCube"), (btn) -> {
                Util.getPlatform().openUri("https://laxacube.vercel.app/pages/wiki.html");
            }));
        }
    }
}