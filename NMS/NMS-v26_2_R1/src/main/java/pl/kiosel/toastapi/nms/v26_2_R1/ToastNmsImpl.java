package pl.kiosel.toastapi.nms.v26_2_R1;

import pl.kiosel.toastapi.nms.api.NMS;
import pl.kiosel.toastapi.nms.api.NmsToasts;

public final class NmsImpl implements NMS {

	private final NmsToasts toast;

	public NmsImpl() {
		this.toast = new ToastImpl();
	}

	@Override
	public NmsToasts getToast() {
		return this.toast;
	}
}
