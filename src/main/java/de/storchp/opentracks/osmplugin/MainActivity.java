package de.storchp.opentracks.osmplugin;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import static android.app.PendingIntent.getActivity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import de.storchp.opentracks.osmplugin.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {

/***********************************************************************************/
    //Mock data
    MockUser mockUser;                  //mock current user
    List<MockUser> FriendList;      //mock friend list

    int counterNotificationId = 1;

    /***********************************************************************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        de.storchp.opentracks.osmplugin.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar.mapsToolbar);

        binding.usageInfo.setMovementMethod(LinkMovementMethod.getInstance());
        binding.osmInfo.setMovementMethod(LinkMovementMethod.getInstance());
        binding.offlineMaps.setMovementMethod(LinkMovementMethod.getInstance());
        binding.versionInfo.setText(Html.fromHtml(getString(R.string.version_info, BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE), Html.FROM_HTML_MODE_COMPACT));

        if (BuildConfig.offline) {
            binding.offlineMapInfo.setVisibility(View.GONE);
        }

        Button mapButton=findViewById(R.id.button_get_location);
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mapIntent = new Intent(MainActivity.this, MapCurrentActivity.class);
                startActivity(mapIntent);
            }
        });

        //When app first opens, check if there are any friends in the same resort
        FriendsAlertOnOpen();


    }

//create mock user and friends data
    private void CreateMockData() {
        double p1 = 45.877582;
        double p2 = -74.142798;
        GeoPoint point = new GeoPoint(p1, p2);

        mockUser = new MockUser("Bob", point, "Saint-Sauveur");

        p1 = 65.456322;
        p2 = 52.123456;
        GeoPoint point1 = new GeoPoint(p1,p2);
        MockUser friend1 = new MockUser("Annie", point1, "Saint-Sauveur");

        p1 = -31.065421;
        p2 = 52.123456;
        GeoPoint point2 = new GeoPoint(p1,p2);
        MockUser friend2 = new MockUser("John", point2, "Mont-Tremblant");

        FriendList = new ArrayList<>();

        FriendList.add(friend1);
        FriendList.add(friend2);
    }

    //When the app is running, check if their friends are also in the same resort
    //If they are in the same resort, send a notification
    public void FriendsAlertOnOpen() {

        //Mock data while the other teams implement their code
        if(FriendList == null) {
            CreateMockData();
        }

        //check for build version to display notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                //go through list of user's friends
                for (MockUser friend : FriendList) {

                    //channel and manager to send notification
                    NotificationManager notification;
                    String ChannelID = "Friend Alert";
                    String ChannelName = "Friend Notification";
                    notification = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

                    NotificationChannel notificationChannel = new NotificationChannel(ChannelID,
                            ChannelName,
                            NotificationManager.IMPORTANCE_DEFAULT);
                    notification.createNotificationChannel(notificationChannel);

                    //check if they are in the same resort
                    if(friend.getCurrentResort().equals(mockUser.getCurrentResort()) && !friend.getIsAlert()) {

                        //save that friend has been notified
                        friend.wasAlerted();

                        //create the notification
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, ChannelID);
                        builder.setSmallIcon(R.drawable.ic_logo_color_24dp);
                        builder.setContentTitle(friend.getName() + " is in the same resort!");
                        builder.setContentText(friend.getName() + " is also in " + friend.getCurrentResort() + " at this location: (" + friend.getLocation() +")");

                        //send the notification
                        notification.notify(counterNotificationId, builder.build());
                        counterNotificationId++;

                    }
                }
            }
        }
    }

    //Send message about notification settings
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean o) {
            if (o) {
                Toast.makeText(MainActivity.this, "Post notification permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Post notification permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    });

    //simulate new friend arrive in the same resort
    private void MockNewFriendFound() {
        double p1 = 65.456322;
        double p2 = 52.123456;
        GeoPoint point1 = new GeoPoint(p1,p2);
        MockUser friend1 = new MockUser("Nelly", point1, "Saint-Sauveur");
        FriendList.add(friend1);
    }

    //Periodic check if there are new friends that are in the same resort
    private Handler handler = new Handler();
    private Runnable runnableCode = new Runnable() {
        @Override
        public void run() {
            // Mock code when a new friend is in the same resort
            if(!FriendList.isEmpty()) {

                //fake new friend is now in the same resort
                MockNewFriendFound();

                // Check to see if a friend is in the same resort and send a notification
                FriendsAlertOnOpen();
            }

            // Check every 5 min if there is a new friend in same resort
            handler.postDelayed(this, 3000000); // 1000 milliseconds = 1 second
        }
    };

    // Run the checker if a friend is in the same resort when the app start
    @Override
    protected void onStart() {
        super.onStart();
        handler.post(runnableCode);
    }

    // Stop the checker if a friend is in the same resort when the app stop
    @Override
    protected void onStop() {
        super.onStop();
        handler.removeCallbacks(runnableCode);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showNotification = sharedPreferences.getBoolean(getString(R.string.SHOW_NOTIFICATION_FRIEND), true);

        if (showNotification) {
            //logic for sending notification
            // Assume the notifications are enabled, then make an toast message
            Toast.makeText(getApplicationContext(), "Friend notification is on", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(), "Friend notification is off", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onNewIntent(final Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        super.onCreateOptionsMenu(menu, false);
        return true;
    }

}
