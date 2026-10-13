package pl.kiosel.toastapi;

import org.bukkit.Material;
import pl.kiosel.toastapi.nms.api.NmsToasts;

public class ToastHolder {

	private final String title;
	private final Material material;
	private final NmsToasts.NmsAdvancementType frameType;

	public ToastHolder(String title) {
		this(title, Material.DIAMOND, NmsToasts.NmsAdvancementType.GOAL);
	}

	public ToastHolder(String title, Material material) {
		this(title, material, NmsToasts.NmsAdvancementType.GOAL);
	}

	public ToastHolder(String title, NmsToasts.NmsAdvancementType frameType) {
		this(title, Material.DIAMOND, frameType);
	}

	public ToastHolder(String title, Material material, NmsToasts.NmsAdvancementType frameType) {
		this.title = title;
		this.material = material;
		this.frameType = frameType;
	}

	public String getTitle() {
		return title;
	}

	public Material getMaterial() {
		return material;
	}

	public NmsToasts.NmsAdvancementType getFrameType() {
		return frameType;
	}

	public static ToastHolder of(String title) {
		return new ToastHolder(title);
	}

	public static ToastHolder of(String title, Material material, NmsToasts.NmsAdvancementType frameType) {
		return new ToastHolder(title, material, frameType);
	}

}
