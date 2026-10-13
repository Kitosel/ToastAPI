package pl.kiosel.toastapi;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kiosel.toastapi.nms.api.NmsToasts;
import pl.kiosel.toastapi.nms.api.ToastNMS;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ToastAPI {

    private final ToastNMS nms;
    private final JavaPlugin plugin;
    final Logger logger = Logger.getLogger("ToastAPI");
    private final boolean debug;

    public ToastAPI(JavaPlugin plugin) {
        this(plugin, false);
    }

    public ToastAPI(JavaPlugin plugin, boolean debug) {
        Objects.requireNonNull(plugin, "plugin is null");

        String specVersion = System.getProperty("java.specification.version");
        if (Integer.parseInt(specVersion) <= 16) {
            throw new RuntimeException("ToastAPI cannot be run on versions of Java lower than 16");
        }

        this.plugin = plugin;
        this.nms = NmsResolver.resolve(plugin, this);
        this.debug = debug;
    }

    public void sendToast(Player player, ToastHolder holder) {
        if (debug)
            logger.log(Level.INFO, "Sending toast with holder to " + player.getName());
        this.sendToast(player, holder.getTitle(), holder.getMaterial(), holder.getFrameType());
    }

    public void sendToast(Player player, String title, Material material, NmsToasts.NmsAdvancementType frameType) {
        Objects.requireNonNull(player, "player is null");
        Objects.requireNonNull(title, "title is null");
        Objects.requireNonNull(material, "material is null");
        Objects.requireNonNull(frameType, "frameType is null");

        if (nms == null) {
            logger.warning("ToastAPI could not find an NMS adapter for the version " + Version.getVersion().getPackageName());
            return;
        }
        try {
            if (!nms.getToast().sendToast(plugin, player, title, material, frameType)) {
                if (debug)
                    logger.log(Level.INFO, "Could not send nms toast to " + player.getName());
                sendBukkitToast(player, title, material, frameType);
            }
        } catch (Throwable throwable) {
            try {
				sendBukkitToast(player, title, material, frameType);
			} catch (Throwable throwable1) {
                logger.log(Level.SEVERE, "The toast could not be sent via the adapter " + nms.getClass().getName(), throwable1);
            }
        }
    }

    private void sendBukkitToast(Player player, String title, Material material, NmsToasts.NmsAdvancementType frameType) {
        if (debug)
            logger.log(Level.INFO, "Sending bukkit toast to " + player.getName());
        BukkitToast.displayTo(plugin, player, title, material.toString().toLowerCase(), frameType);
    }
}
