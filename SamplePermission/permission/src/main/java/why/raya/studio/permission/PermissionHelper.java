package why.raya.studio.permission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wahyu.raya on 7/6/17.
 */

public class PermissionHelper {

    private List<String> listPermissionsNeeded = new ArrayList<>();
    private List<String> listPermissions = new ArrayList<>();
    private PermissionListener listener;
    private Activity activity;

    private int REQUEST_CODE = 99;
    private final int REQUEST_PERMISSION = 789;

    private String deniedPermissionMessage = "Dear user, this permissions are safe and required to do this task";
    private String neverAskPermissionMessage = "";

    public PermissionHelper(Activity activity){
        this.activity = activity;
    }

    public void setPermissionListener(PermissionListener listener){
        this.listener = listener;
    }

    public void checkAndRequestPermissions(List<String> listPermissions) {
        checkAndRequestPermissions(listPermissions, REQUEST_CODE);
    }

    public void checkAndRequestPermissions(List<String> listPermissions, int requestCode) {
        this.listPermissions = listPermissions;
        this.REQUEST_CODE = requestCode;
        checkAndRequestPermissions();
    }

    public void setRequestCode(int code) {
        this.REQUEST_CODE = code;
    }

    public int getRequestCode() {
        return REQUEST_CODE;
    }

    public void setDeniedPermissionMessage(String message) {
        if (message != null)
            deniedPermissionMessage = message;
    }

    public void setNeverAskPermissionMessage(String message) {
        if (message != null)
            neverAskPermissionMessage = message;
    }

    /**
     * Call this to check permission.
     * Will looping for check permission until user Approved it
     * */
    private boolean checkAndRequestPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            listPermissionsNeeded.clear();
            for (String permission : listPermissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED)
                    listPermissionsNeeded.add(permission);
            }
            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_PERMISSION);
                return false;
            }
        }
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        listener.onPermissionCheckDone(REQUEST_CODE);
        return true;
    }

    /**
     * Handling permission callback after you click deny or ok
     * */
    public void onRequestCallBack(int RequestCode,String[] permissions ,int[] grantResults){//2. call this inside onRequestPermissionsResult
        switch (RequestCode) {
            case REQUEST_PERMISSION: {
                if (grantResults.length > 0) {
                    boolean granted = true;
                    for (int i = 0; i < permissions.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED)
                            granted = false; //false, if one permission not granted
                    }

                    // all permission granted
                    if (granted)
                        checkAndRequestPermissions();
                    else {
                        // Some permissions are not granted ask again. Ask again explaining the usage of permission.
                        boolean neverAsk = true;
                        for (String permission : listPermissions) {
                            if (ActivityCompat.shouldShowRequestPermissionRationale(activity,permission))
                                neverAsk = false;
                        }
                        if (!neverAsk) {
                            showDialogOK(deniedPermissionMessage,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        else {
                            if (!neverAskPermissionMessage.isEmpty())
                                Toast.makeText(activity, neverAskPermissionMessage, Toast.LENGTH_LONG).show();
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                            intent.setData(uri);
                            activity.startActivityForResult(intent, REQUEST_CODE);
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(activity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .create()
                .show();
    }

    public interface PermissionListener{
        void onPermissionCheckDone(int REQUEST_CODE);
    }

}
