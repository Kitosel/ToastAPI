package pl.kiosel.toastapi.nms.v1_20_R4;

import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_20_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R4.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.kiosel.toastapi.nms.api.NmsToasts;

import java.util.*;

public final class ToastImpl implements NmsToasts {

	@Override
	public boolean sendToast(Plugin plugin, Player player, String titleText, Material material, NmsAdvancementType frameType) {
		ResourceLocation toastId = ResourceLocation.tryBuild("toastapi", "toast" + UUID.randomUUID());
		if (toastId == null) return false;

		Collection<AdvancementHolder> toAdd = getToAdd(titleText, material, frameType, toastId);
		ClientboundUpdateAdvancementsPacket packetShow = getPacketShow(toastId, toAdd);

		ServerGamePacketListenerImpl connection;
		try {
			connection = ((CraftPlayer) player).getHandle().connection;
			connection.send(packetShow);
		} catch (Exception e) {
			return false;
		}

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			if (player.isOnline()) {
				ClientboundUpdateAdvancementsPacket packetClear = new ClientboundUpdateAdvancementsPacket(
						false,
						Collections.emptyList(),
						Collections.singleton(toastId),
						Collections.emptyMap()
				);
				connection.send(packetClear);
			}
		}, 40L);
		return true;
	}

	private ClientboundUpdateAdvancementsPacket getPacketShow(ResourceLocation toastId, Collection<AdvancementHolder> toAdd) {
		Set<ResourceLocation> toRemove = Collections.emptySet();

		AdvancementRequirements requirements = AdvancementRequirements.allOf(Collections.singleton("toast"));

		AdvancementProgress progress = new AdvancementProgress();
		progress.update(requirements);

		Iterable<String> remainingCriteria = progress.getRemainingCriteria();
		for (String criterion : remainingCriteria) {
			progress.grantProgress(criterion);
		}

		Map<ResourceLocation, AdvancementProgress> toProgress = Collections.singletonMap(toastId, progress);

		return new ClientboundUpdateAdvancementsPacket(false, toAdd, toRemove, toProgress);
	}

	private Collection<AdvancementHolder> getToAdd(String titleText, Material material, NmsAdvancementType frameType, ResourceLocation toastId) {
		org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(material);
		net.minecraft.world.item.ItemStack icon = CraftItemStack.asNMSCopy(bukkitItem);

		Component title = Component.literal(titleText);
		Component description = Component.empty();

		DisplayInfo displayInfo = new DisplayInfo(
				icon,
				title,
				description,
				Optional.empty(),
				AdvancementType.valueOf(frameType.getName().toUpperCase(Locale.ROOT)),
				true,
				false,
				true
		);

		Criterion<?> toastCriterion = new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance());

		Map<String, Criterion<?>> criteria = Collections.singletonMap("toast", toastCriterion);

		AdvancementRequirements requirements = AdvancementRequirements.allOf(Collections.singleton("toast"));

		Advancement advancement = new Advancement(
				Optional.empty(),
				Optional.of(displayInfo),
				AdvancementRewards.EMPTY,
				criteria,
				requirements,
				false
		);

		return Collections.singletonList(new AdvancementHolder(toastId, advancement));
	}
}
