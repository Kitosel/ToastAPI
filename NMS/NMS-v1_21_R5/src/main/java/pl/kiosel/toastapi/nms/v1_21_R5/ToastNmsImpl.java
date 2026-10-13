package pl.kiosel.toastapi.nms.v1_21_R5;

import pl.kiosel.toastapi.nms.api.ToastNMS;
import pl.kiosel.toastapi.nms.api.NmsToasts;

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
