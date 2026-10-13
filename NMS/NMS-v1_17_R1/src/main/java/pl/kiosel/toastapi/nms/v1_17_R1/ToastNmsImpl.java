package pl.kiosel.toastapi.nms.v1_17_R1;

import pl.kiosel.toastapi.nms.api.NmsToasts;
import pl.kiosel.toastapi.nms.api.ToastNMS;

public final class ToastNmsImpl implements ToastNMS {

	private final NmsToasts toasts;

	public ToastNmsImpl() {
		this.toasts = new ToastImpl();
	}

	@Override
	public NmsToasts getToast() {
		return this.toasts;
	}
}
