package why.raya.studio.samplepermission;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import why.raya.studio.permission.PermissionHelper;

public class MainActivity extends AppCompatActivity {

    private PermissionHelper permissionHelper;
    private List<String> listPermissions = new ArrayList<>();
    private boolean needReCheck = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initPermission();
    }

    private void initPermission() {
        permissionHelper = new PermissionHelper(this);
        permissionHelper.setPermissionListener(new PermissionHelper.PermissionListener() {
            @Override
            public void onPermissionCheckDone(int REQUEST_CODE) {
                // put your code here
                //example
                switch (REQUEST_CODE) {
                    case 123 :
                        //function for camera
                        break;
                    case 456 :
                        //function for phone
                        break;
                    case 789 :
                        //function for every permission
                        break;
                }
                needReCheck = true;
            }
        });
        //sample permission, make sure all permission here also exist in your manifest.
        listPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        listPermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionHelper.checkAndRequestPermissions(listPermissions);
        // or if you want to call this method with different request code
        // permissionHelper.checkAndRequestPermissions(listPermissions, your_request_code);
    }

    /**
     * Handling permission callback after you click deny or ok
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.onRequestCallBack(requestCode, permissions, grantResults);
    }

    /**
     * When user denying your permission with never ask checked.
     * You will ask user to enabling it manually.
     * When user back from App Setting onActivityResult will handle it.
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == permissionHelper.getRequestCode())
            permissionHelper.checkAndRequestPermissions(listPermissions);
    }

    /**
     * to make sure that all of your needed permissions are granted, you can add your code on onResume()
     * fill the listPermissions with all permissions that you need to be checked
     *
     * to prevent onCreate and onResume do the same job and on the same time, the flag needed
     * */

    @Override
    protected void onResume() {
        super.onResume();
        if (needReCheck)
            permissionHelper.checkAndRequestPermissions(listPermissions);
    }
}
