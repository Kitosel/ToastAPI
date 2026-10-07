package pl.kiosel.toastapi.nms.v26_3_R1;

import net.minecraft.advancements.*;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.triggers.Criterion;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.ItemStackTemplate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import pl.kiosel.toastapi.nms.api.NmsToasts;

import java.util.*;

public final class ToastImpl implements NmsToasts {

	@Override
	public void sendToast(Plugin plugin, Player player, String titleText, Material material, NmsAdvancementType frameType) {
		Identifier toastId = Identifier.fromNamespaceAndPath("toastapi", "toast_" + UUID.randomUUID());

		List<ClientboundUpdateAdvancementsPacket.PositionedAdvancement> toAdd = getToAdd(titleText, material, frameType, toastId);
		ClientboundUpdateAdvancementsPacket packetShow = getPacketShow(toastId, toAdd);

		ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
		connection.send(packetShow);

		Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, () -> {
			if (player.isOnline()) {
				ClientboundUpdateAdvancementsPacket packetClear = new ClientboundUpdateAdvancementsPacket(
						false,
						Collections.emptyList(),
						Collections.singleton(toastId),
						Collections.emptyMap(),
						false
				);
				connection.send(packetClear);
			}
		}, 40L);
	}

	private ClientboundUpdateAdvancementsPacket getPacketShow(Identifier toastId, List<ClientboundUpdateAdvancementsPacket.PositionedAdvancement> toAdd) {
		Set<Identifier> toRemove = Collections.emptySet();

		AdvancementRequirements requirements = AdvancementRequirements.allOf(Collections.singleton("toast"));

		AdvancementProgress progress = new AdvancementProgress();
		progress.update(requirements);

		Iterable<String> remainingCriteria = progress.getRemainingCriteria();
		for (String criterion : remainingCriteria) {
			progress.grantProgress(criterion);
		}

		Map<Identifier, AdvancementProgress> toProgress = Collections.singletonMap(toastId, progress);

		return new ClientboundUpdateAdvancementsPacket(false, toAdd, toRemove, toProgress, true);
	}

	private List<ClientboundUpdateAdvancementsPacket.PositionedAdvancement> getToAdd(String titleText, Material material, NmsAdvancementType frameType, Identifier toastId) {
		org.bukkit.inventory.ItemStack bukkitItem = new org.bukkit.inventory.ItemStack(material);
		net.minecraft.world.item.ItemStack icon = CraftItemStack.asNMSCopy(bukkitItem);

		Component title = Component.literal(titleText);
		Component description = Component.empty();

		DisplayInfo displayInfo = new DisplayInfo(
				new ItemStackTemplate(icon.getItem()),
				title,
				description,
				Optional.empty(),
				AdvancementType.valueOf(frameType.getName().toUpperCase(Locale.ROOT)),
				true,
				false,
				true
		);

		Criterion<ImpossibleTrigger.TriggerInstance> criterionInstance =
				new Criterion<>(CriteriaTriggers.IMPOSSIBLE, new ImpossibleTrigger.TriggerInstance());

		Map<String, Criterion<?>> criteria = Collections.singletonMap("toast", criterionInstance);
		AdvancementRequirements requirements = AdvancementRequirements.allOf(Collections.singleton("toast"));

		Advancement advancement = new Advancement(
				Optional.empty(),
				Optional.of(displayInfo),
				AdvancementRewards.EMPTY,
				criteria,
				requirements,
				false
		);

		AdvancementHolder holder = new AdvancementHolder(toastId, advancement);

		// Dokładnie to wywołanie jest poprawne dla wersji 26.3
		ClientboundUpdateAdvancementsPacket.PositionedAdvancement positioned =
				ClientboundUpdateAdvancementsPacket.PositionedAdvancement.fromNode(new net.minecraft.advancements.AdvancementNode(holder, null));

		return new ArrayList<>(Collections.singletonList(positioned));
	}
}
