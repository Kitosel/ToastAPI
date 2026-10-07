package pl.kiosel.toastapi.version;

import pl.kiosel.toastapi.nms.api.ToastNMS;

import java.util.Objects;

public final class NmsResolver {

	private NmsResolver() {
	}

	public static ToastNMS resolve(ClassLoader classLoader) {
		Objects.requireNonNull(classLoader, "classLoader");
		Version version = Version.getVersion();
		String revision = version.getPackageName();
		if (version.isBelow(Version.V1_17_R1)) {
			return null;
		}

		String apiPackage = ToastNMS.class.getPackage().getName();
		String className = apiPackage.substring(0, apiPackage.length() - ".api".length())
				+ "." + revision + ".ToastNmsImpl";
		try {
			return Class.forName(className, true, classLoader).asSubclass(ToastNMS.class).getDeclaredConstructor().newInstance();
		} catch (ClassNotFoundException exception) {
			throw new UnsupportedOperationException("ToastAPI does not contain " + className, exception);
		} catch (ReflectiveOperationException | ClassCastException exception) {
			throw new IllegalStateException("Could not create " + className, exception);
		} catch (LinkageError error) {
			throw new IllegalStateException("NMS implementation is incompatible with this server: " + className, error);
		}
	}

}