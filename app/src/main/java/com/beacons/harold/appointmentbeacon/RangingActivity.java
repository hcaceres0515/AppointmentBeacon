package com.beacons.harold.appointmentbeacon;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class RangingActivity extends AppCompatActivity implements BeaconConsumer{

    protected static final String TAG = "RangingActivity";
    private BeaconManager beaconManager;

    public Toast toastScanning;
    public ProgressDialog progressScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranging);

        //toastScanning = Toast.makeText(this.getApplicationContext(), "Scanning beacons", Toast.LENGTH_SHORT);
        //toastScanning.show();
        progressScanning = new ProgressDialog(RangingActivity.this);
        progressScanning.setTitle("Scanning Beacons");
        progressScanning.setMessage("Scanning ... Please wait");
        //progressScanning.setCancelable(false);
        progressScanning.show();

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        beaconManager.getBeaconParsers().add(new BeaconParser().
                setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));

        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {

                    progressScanning.dismiss();

                    //Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                    for (Beacon beacon: beacons) {
                        if (beacon.getDistance() < 2.0) {
                            Log.i(TAG, "The first beacon I see is about "+beacons.iterator().next().getDistance()+" meters away.");
                            Log.d(TAG, "Bluetooth Name" + beacon.toString());

                            Intent sendEmailIntent = new Intent(Intent.ACTION_SEND);
                            sendEmailIntent.setType("plain/text");
                            //sendEmailIntent.setClassName("com.google.android.gm", "com.google.android.gm.ComposeActivityGmail");
                            sendEmailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"recipient@example.com"});
                            sendEmailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject");
                            sendEmailIntent.putExtra(Intent.EXTRA_TEXT   , "body");
                            try {
                                startActivity(Intent.createChooser(sendEmailIntent, "Select app to send email"));
                                beaconManager.stopRangingBeaconsInRegion(region);
                                beaconManager.applySettings();
                            } catch (android.content.ActivityNotFoundException ex) {

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                            // Perform distance-specific action here
                        }

                        else {
//                            toastScanning.setText("Beacon is more than 5 meters away");
//                            toastScanning.show();
                        }
                    }
                }
            }

        });

        try {

            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));

        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
