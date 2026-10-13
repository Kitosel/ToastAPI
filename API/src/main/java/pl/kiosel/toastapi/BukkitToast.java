package pl.kiosel.toastapi;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import pl.kiosel.toastapi.nms.api.NmsToasts;

import java.util.UUID;

final class BukkitToast {

	private final NamespacedKey key;
	private final JavaPlugin plugin;
	private final String icon;
	private final String title;
	private final NmsToasts.NmsAdvancementType frameType;

	private BukkitToast(JavaPlugin plugin, String title, String icon, NmsToasts.NmsAdvancementType frameType) {
		this.key = new NamespacedKey(plugin, "toast_" + UUID.randomUUID());
		this.plugin = plugin;
		this.icon = icon;
		this.title = title;
		this.frameType = frameType;
	}

	private void show(Player player) {
		createAdvancement();
		grantAdvancement(player);

		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			revokeAdvancement(player);
		}, 10);
	}

	private void createAdvancement() {
		Bukkit.getUnsafe().loadAdvancement(key, "{\n" +
				"    \"criteria\": {\n" +
				"        \"trigger\": {\n" +
				"            \"trigger\": \"minecraft:impossible\"\n" +
				"        }\n" +
				"    },\n" +
				"    \"display\": {\n" +
				"        \"icon\": {\n" +
				"            \"item\": \"minecraft:" + icon + "\"\n" +
				"        },\n" +
				"        \"title\": {\n" +
				"            \"text\": \"" + title.replace("|", "\n") + "\"\n" +
				"        },\n" +
				"        \"description\": {\n" +
				"            \"text\": \"\"\n" +
				"        },\n" +
				"        \"background\": \"minecraft:textures/gui/advancements/backgrounds/adventure.png\",\n" +
				"        \"frame\": \"" + frameType.toString().toLowerCase() + "\",\n" +
				"        \"announce_to_chat\": false,\n" +
				"        \"show_toast\": true,\n" +
				"        \"hidden\": true\n" +
				"    },\n" +
				"    \"requirements\": [\n" +
				"        [\n" +
				"            \"trigger\"\n" +
				"        ]\n" +
				"    ]\n" +
				"}");
	}

	private void grantAdvancement(Player player) {
		player.getAdvancementProgress(Bukkit.getAdvancement(key)).awardCriteria("trigger");
	}

	private void revokeAdvancement(Player player) {
		player.getAdvancementProgress(Bukkit.getAdvancement(key)).revokeCriteria("trigger");
	}

	public static void displayTo(JavaPlugin plugin, Player player, String title, String icon, NmsToasts.NmsAdvancementType frameType) {
		new BukkitToast(plugin, icon, title, frameType).show(player);
	}
}