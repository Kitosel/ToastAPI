package pl.kiosel.toastapi.nms.v1_21_R2;

import pl.kiosel.toastapi.nms.api.NMS;
import pl.kiosel.toastapi.nms.api.NmsToasts;

public final class NmsImpl implements NMS {

	private final NmsToasts toasts;

	public NmsImpl() {
		this.toasts = new ToastImpl();
	}

	@Override
	public NmsToasts getToast() {
		return this.toasts;
	}
}
