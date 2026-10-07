package pl.kiosel.toastapi.nms.v26_1_R1;

import pl.kiosel.toastapi.nms.api.ToastNMS;
import pl.kiosel.toastapi.nms.api.NmsToasts;

public final class ToastNmsImpl implements ToastNMS {

	private final NmsToasts toast;

	public ToastNmsImpl() {
		this.toast = new ToastImpl();
	}

	@Override
	public NmsToasts getToast() {
		return this.toast;
	}
}
