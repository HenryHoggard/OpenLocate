/*package henry.locate;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import henry.locate.MainActivity;
public class TaskReceiver extends BroadcastReceiver {
    public TaskReceiver() {
    }

    private static final String TAG = "LocateTaskReceiver";
    private static final String INTENT_ACTION = "henry.locate.PERIODIC_TASK_HEART_BEAT";
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        throw new UnsupportedOperationException("Not yet implemented");
        doPeriodicTask(context, myApplication);
    }


    private void doPeriodicTask(Context context, MainActivity MainActivity) {
        // Periodic task(s) go here ...

        AlarmManager alarmManager = new AlarmManager;
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), AlarmManager.INTERVAL_FIFTEEN_MINUTES, pendingIntent);
    }
}
*/