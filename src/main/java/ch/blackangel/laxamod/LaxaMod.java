package ch.blackangel.laxamod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.MainMenuScreen;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.event.TickEvent; // Import ajouté
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.Util;
import net.minecraftforge.client.event.GuiScreenEvent;
import java.io.InputStream;

@Mod(LaxaMod.MOD_ID)
public class LaxaMod {
    public static final String MOD_ID = "laxamod";

    public LaxaMod() {
        MinecraftForge.EVENT_BUS.register(this);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onClientSetup);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            try {
                InputStream is16 = getClass().getResourceAsStream("/assets/laxamod/textures/gui/icon_16.png");
                InputStream is32 = getClass().getResourceAsStream("/assets/laxamod/textures/gui/icon_32.png");

                if (is16 != null && is32 != null) {
                    Minecraft.getInstance().getWindow().setIcon(is16, is32);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    // CETTE MÉTHODE FORCE LE TITRE 20 FOIS PAR SECONDE PARTOUT
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        // On vérifie qu'on est à la fin du tick
        if (event.phase == TickEvent.Phase.END) {
            Minecraft mc = Minecraft.getInstance();
            // On vérifie que la fenêtre existe et que l'utilisateur est chargé
            if (mc.getWindow() != null && mc.getUser() != null) {
                String targetTitle = "Laxacube V1 | " + mc.getUser().getName();
                mc.getWindow().setTitle(targetTitle);
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        // 1. Remplace le menu de démarrage (ce que tu as déjà)
        if (event.getGui() instanceof MainMenuScreen && !(event.getGui() instanceof LaxaMenu)) {
            event.setGui(new LaxaMenu());
        }

        // 2. FORCE le retour au menu personnalisé lors d'une déconnexion
        // Au lieu d'aller sur la liste des serveurs (MultiplayerScreen), on va sur LaxaMenu
        if (event.getGui() instanceof net.minecraft.client.gui.screen.MultiplayerScreen) {
            event.setGui(new LaxaMenu());
        }
    }
    @SubscribeEvent
    public void onGuiInit(GuiScreenEvent.InitGuiEvent.Post event) {
        // On vérifie si l'écran qui s'ouvre est le menu Échap
        if (event.getGui() instanceof IngameMenuScreen) {
            int x = event.getGui().width / 2;
            int y = event.getGui().height / 4 + 132; // Positionné en bas du menu

            // On ajoute le bouton Wiki
            event.addWidget(new Button(x - 100, y, 200, 20, new StringTextComponent("§e§lWiki LaxaCube"), (btn) -> {
                Util.getPlatform().openUri("https://laxacube.vercel.app/pages/wiki.html"); // Remplace par ton lien
            }));
        }
    }
}