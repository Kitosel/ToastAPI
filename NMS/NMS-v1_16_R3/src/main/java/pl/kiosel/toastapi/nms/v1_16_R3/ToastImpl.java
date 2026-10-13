package pl.kiosel.toastapi.nms.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.kiosel.toastapi.nms.api.NmsToasts;

import java.util.*;

public final class ToastImpl implements NmsToasts {

	@Override
	public boolean sendToast(Plugin plugin, Player player, String titleText, Material material, NmsAdvancementType frameType) {
		MinecraftKey toastId = MinecraftKey.a("toastapi:toast" + UUID.randomUUID());

		if (toastId == null) return false;

		Criterion toastCriterion = new Criterion(new CriterionTriggerImpossible.a());
		Map<String, Criterion> criteria = Collections.singletonMap("toast", toastCriterion);
		String[][] requirements = AdvancementRequirements.AND.createRequirements(Collections.singleton("toast"));

		Advancement advancement = getAdvancement(toastId, titleText, material, frameType, criteria, requirements);
		PacketPlayOutAdvancements packetShow = getPacketShow(toastId, advancement, criteria, requirements);

		PlayerConnection connection;
		try {
			connection = ((CraftPlayer) player).getHandle().playerConnection;
			connection.sendPacket(packetShow);
		} catch (Exception e) {
			return false;
		}

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			if (player.isOnline()) {
				PacketPlayOutAdvancements packetClear = new PacketPlayOutAdvancements(
						false,
						Collections.emptyList(),
						Collections.singleton(toastId),
						Collections.emptyMap()
				);
				connection.sendPacket(packetClear);
			}
		}, 40L);
		return true;
	}

	private PacketPlayOutAdvancements getPacketShow(MinecraftKey toastId, Advancement advancement, Map<String, Criterion> criteria, String[][] requirements) {
		Collection<Advancement> toAdd = Collections.singletonList(advancement);
		Set<MinecraftKey> toRemove = Collections.emptySet();

		AdvancementProgress progress = new AdvancementProgress();
		progress.a(criteria, requirements);

		Iterable<String> remainingCriteria = progress.getRemainingCriteria();
		for (String criterion : remainingCriteria) {
			progress.a(criterion);
		}

		Map<MinecraftKey, AdvancementProgress> toProgress = Collections.singletonMap(toastId, progress);

		return new PacketPlayOutAdvancements(false, toAdd, toRemove, toProgress);
	}

	private Advancement getAdvancement(MinecraftKey toastId, String titleText, Material material, NmsAdvancementType frameType, Map<String, Criterion> criteria, String[][] requirements) {
		org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(material);
		net.minecraft.server.v1_16_R3.ItemStack icon = CraftItemStack.asNMSCopy(bukkitItem);

		IChatBaseComponent title = IChatBaseComponent.ChatSerializer.a(titleText);
		IChatBaseComponent description = IChatBaseComponent.ChatSerializer.a("");

		AdvancementDisplay displayInfo = new AdvancementDisplay(
				icon,
				title,
				description,
				null,
				AdvancementFrameType.valueOf(frameType.getName().toUpperCase(Locale.ROOT)),
				true,
				false,
				true
		);

		return new Advancement(
				toastId,
				null,
				displayInfo,
				AdvancementRewards.a,
				criteria,
				requirements
		);
	}
}
