package pl.kiosel.toastapi;

import org.bukkit.Bukkit;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Recognised Minecraft/CraftBukkit revisions. Recognition does not mean that
 * a toast NMS adapter is bundled for that revision.
 */
enum Version {

	V1_8_R1(10801), V1_8_R2(10802), V1_8_R3(10803),
	V1_9_R1(10901), V1_9_R2(10902),
	V1_10_R1(11001), V1_11_R1(11101), V1_12_R1(11201),
	V1_13_R1(11301), V1_13_R2(11302),
	V1_14_R1(11401), V1_15_R1(11501),
	V1_16_R1(11601), V1_16_R2(11602), V1_16_R3(11603),
	V1_17_R1(11701), V1_18_R1(11801), V1_18_R2(11802),
	V1_19_R1(11901), V1_19_R2(11902), V1_19_R3(11903),
	V1_20_R1(12001), V1_20_R2(12002), V1_20_R3(12003), V1_20_R4(12004),
	V1_21_R1(12101), V1_21_R2(12102), V1_21_R3(12103),
	V1_21_R4(12104), V1_21_R5(12105), V1_21_R6(12106), V1_21_R7(12107),
	V26_1_R1(260100), V26_2_R1(260200), V26_3_R1(260300),
	UNKNOWN(-1);

	private static final Map<String, Version> BY_REVISION;
	private static final Map<String, Version> BY_MINECRAFT_VERSION;

	static {
		Map<String, Version> revisions = new HashMap<>();
		for (Version version : values()) {
			if (version != UNKNOWN && version.getPackageName() != null) {
				revisions.put(version.getPackageName(), version);
			}
		}
		BY_REVISION = Collections.unmodifiableMap(revisions);

		Map<String, Version> minecraft = new HashMap<>();
		put(minecraft, V1_8_R3, "1.8", "1.8.8", "1.8.9");
		put(minecraft, V1_9_R1, "1.9", "1.9.1", "1.9.2");
		put(minecraft, V1_9_R2, "1.9.3", "1.9.4");
		put(minecraft, V1_10_R1, "1.10", "1.10.2");
		put(minecraft, V1_11_R1, "1.11", "1.11.2");
		put(minecraft, V1_12_R1, "1.12", "1.12.1", "1.12.2");
		put(minecraft, V1_13_R1, "1.13");
		put(minecraft, V1_13_R2, "1.13.1", "1.13.2");
		put(minecraft, V1_14_R1, "1.14", "1.14.1", "1.14.2", "1.14.3", "1.14.4");
		put(minecraft, V1_15_R1, "1.15", "1.15.1", "1.15.2");
		put(minecraft, V1_16_R1, "1.16", "1.16.1");
		put(minecraft, V1_16_R2, "1.16.2", "1.16.3");
		put(minecraft, V1_16_R3, "1.16.4", "1.16.5");
		put(minecraft, V1_17_R1, "1.17", "1.17.1");
		put(minecraft, V1_18_R1, "1.18", "1.18.1");
		put(minecraft, V1_18_R2, "1.18.2");
		put(minecraft, V1_19_R1, "1.19", "1.19.1", "1.19.2");
		put(minecraft, V1_19_R2, "1.19.3");
		put(minecraft, V1_19_R3, "1.19.4");
		put(minecraft, V1_20_R1, "1.20", "1.20.1");
		put(minecraft, V1_20_R2, "1.20.2");
		put(minecraft, V1_20_R3, "1.20.3", "1.20.4");
		put(minecraft, V1_20_R4, "1.20.5", "1.20.6");
		put(minecraft, V1_21_R1, "1.21", "1.21.1");
		put(minecraft, V1_21_R2, "1.21.2", "1.21.3");
		put(minecraft, V1_21_R3, "1.21.4");
		put(minecraft, V1_21_R4, "1.21.5");
		put(minecraft, V1_21_R5, "1.21.6", "1.21.7", "1.21.8");
		put(minecraft, V1_21_R6, "1.21.9", "1.21.10");
		put(minecraft, V1_21_R7, "1.21.11");
		put(minecraft, V26_1_R1, "26.1");
		put(minecraft, V26_2_R1, "26.2");
		put(minecraft, V26_3_R1, "26.3");
		BY_MINECRAFT_VERSION = Collections.unmodifiableMap(minecraft);
	}

	private final int versionId;
	private final String packageName;

	Version(int versionId) {
		this.versionId = versionId;
		this.packageName = "";
	}

	Version(int versionId, String packageName) {
		this.versionId = versionId;
		this.packageName = packageName;
	}

	public String getPackageName() {
		if (this == UNKNOWN) {
			return null;
		}
		String name = name().toLowerCase(Locale.ROOT).replace("_r", "_R");
		return name.startsWith("v") ? name : "v" + name;
	}

	public boolean isAtLeast(Version other) {
		return this != UNKNOWN && other != UNKNOWN && this.versionId >= other.versionId;
	}

	public boolean isBelow(Version other) {
		return this != UNKNOWN && other != UNKNOWN && this.versionId < other.versionId;
	}

	public boolean isAfter(Version other) {
		return this != UNKNOWN && other != UNKNOWN && this.versionId > other.versionId;
	}

	public static Version getVersion() {
		if (Bukkit.getServer() == null) return UNKNOWN;

		String serverPackage = Bukkit.getServer().getClass().getPackage().getName();
		int lastDot = serverPackage.lastIndexOf('.');
		if (lastDot >= 0) {
			String lastPart = serverPackage.substring(lastDot + 1);
			if (lastPart.startsWith("v")) {
				Version revision = BY_REVISION.get(lastPart);
				if (revision != null) return revision;
			}
		}

		String bukkitVersion = Bukkit.getBukkitVersion();
		if (bukkitVersion == null) return UNKNOWN;

		String minecraftVersion = bukkitVersion.split("-", 2)[0];

		Version exact = BY_MINECRAFT_VERSION.get(minecraftVersion);
		if (exact != null) return exact;

		String[] parts = minecraftVersion.split("\\.");
		if (parts.length >= 3) {
			String majorMinor = parts[0] + "." + parts[1];
			Version minor = BY_MINECRAFT_VERSION.get(majorMinor);
			if (minor != null) return minor;
		}

		return UNKNOWN;
	}

	private static void put(Map<String, Version> versions, Version version, String... names) {
		for (String name : names) versions.put(name, version);
	}
}
