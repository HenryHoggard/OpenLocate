package henry.OpenLocate;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBroadcastReceiver extends BroadcastReceiver {
    public MyBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Intent startServiceIntent = new Intent(context, LocationService.class);
        context.startService(startServiceIntent);

        //   throw new UnsupportedOperationException("Not yet implemented");

    }
}
