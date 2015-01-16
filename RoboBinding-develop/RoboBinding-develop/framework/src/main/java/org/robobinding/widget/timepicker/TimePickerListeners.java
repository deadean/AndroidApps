package org.robobinding.widget.timepicker;

import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import org.robobinding.widget.view.ViewListenersForView;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Joachim Hill-Grannec
 */
public class TimePickerListeners extends ViewListenersForView {
    protected final TimePicker timePicker;
    private OnTimeChangedListeners onTimeChangedListeners;

    public TimePickerListeners(TimePicker timePicker) {
        super(timePicker);
        this.timePicker = timePicker;
    }

    public void addOnTimeChangedListener(OnTimeChangedListener listener) {
        ensureOnTimeChangedListenersInitialized();
        onTimeChangedListeners.addListener(listener);
    }

    private void ensureOnTimeChangedListenersInitialized() {
        if (onTimeChangedListeners == null) {
            onTimeChangedListeners = new OnTimeChangedListeners();
            timePicker.setOnTimeChangedListener(onTimeChangedListeners);
        }
    }
}
