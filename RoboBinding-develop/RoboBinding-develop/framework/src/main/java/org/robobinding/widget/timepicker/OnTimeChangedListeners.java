package org.robobinding.widget.timepicker;

import android.widget.TimePicker;
import android.widget.TimePicker.OnTimeChangedListener;

import org.robobinding.viewattribute.AbstractListeners;

/**
 *
 * @since 1.0
 * @version $Revision: 1.0 $
 * @author Joachim Hill-Grannec
 */
public class OnTimeChangedListeners extends AbstractListeners<OnTimeChangedListener> implements OnTimeChangedListener {
    @Override
    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
        for (OnTimeChangedListener onTimeChangedListener : listeners) {
            onTimeChangedListener.onTimeChanged(view, hourOfDay, minute);
        }
    }
}
