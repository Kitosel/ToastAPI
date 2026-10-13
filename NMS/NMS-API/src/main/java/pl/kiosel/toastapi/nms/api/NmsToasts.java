package pl.kiosel.toastapi.nms.api;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public interface NmsToasts {

	boolean sendToast(Plugin plugin, Player player, String titleText, Material material, NmsAdvancementType frameType);

	enum NmsAdvancementType {
		TASK("TASK"),
		CHALLENGE("CHALLENGE"),
		GOAL("GOAL");

		private final String name;

		NmsAdvancementType(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

}
