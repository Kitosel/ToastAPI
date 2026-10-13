package pl.kiosel.toastapi;

import org.bukkit.plugin.java.JavaPlugin;
import pl.kiosel.toastapi.nms.api.ToastNMS;

import java.util.Objects;

final class NmsResolver {

	private NmsResolver() {
	}

	static ToastNMS resolve(JavaPlugin plugin, ToastAPI toastAPI) {
		Objects.requireNonNull(plugin.getClass().getClassLoader(), "classLoader");
		
		Version version = Version.getVersion();
		String revision = version.getPackageName();
		if (version.isBelow(Version.V1_16_R3)) {
			toastAPI.logger.warning("Server version is older than V1_16_R3!");
			return null;
		}

		String apiPackage = ToastNMS.class.getPackage().getName();
		String className = apiPackage.substring(0, apiPackage.length() - ".api".length())
				+ "." + revision + ".ToastNmsImpl";
		try {
			toastAPI.logger.info("Found version: " + version);
			return Class.forName(className, true, plugin.getClass().getClassLoader()).asSubclass(ToastNMS.class).getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException exception) {
			throw new UnsupportedOperationException("ToastAPI does not contain " + className, exception);
		} catch (ReflectiveOperationException | ClassCastException exception) {
			throw new IllegalStateException("Could not create " + className, exception);
		} catch (LinkageError error) {
			throw new IllegalStateException("NMS implementation is incompatible with this server: " + className, error);
		}
	}

}