package pl.kiosel.toastapi;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kiosel.toastapi.nms.api.NmsToasts;
import pl.kiosel.toastapi.nms.api.ToastNMS;
import pl.kiosel.toastapi.version.NmsResolver;
import pl.kiosel.toastapi.version.Version;

import java.util.Objects;

public final class ToastAPI {

    private final ToastNMS nms;
    private final JavaPlugin plugin;

    public ToastAPI(JavaPlugin plugin) {
        Objects.requireNonNull(plugin, "plugin is null");
        this.plugin = plugin;
        this.nms = NmsResolver.resolve(plugin.getClass().getClassLoader());
    }

    public void sendToast(Player player, String title, Material material, NmsToasts.NmsAdvancementType frameType) {
        if (nms == null) {
            plugin.getLogger().warning("ToastAPI could not find an NMS adapter for the version " + Version.getVersion().getPackageName());
            return;
        }
        try {
            nms.getToast().sendToast(plugin, player, title, material, frameType);
        } catch (Throwable throwable) {
            plugin.getLogger().log(java.util.logging.Level.SEVERE,
                    "Nie udało się wysłać toastu przez adapter " + nms.getClass().getName(), throwable);
        }
    }
}
