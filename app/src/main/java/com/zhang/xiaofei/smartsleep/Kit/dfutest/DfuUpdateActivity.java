package com.zhang.xiaofei.smartsleep.Kit.dfutest;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.ScanResult;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ansen.http.net.HTTPCaller;
import com.ansen.http.net.Header;
import com.deadline.statebutton.StateButton;
import com.zhang.xiaofei.smartsleep.Kit.DB.YMUserInfoManager;
import com.zhang.xiaofei.smartsleep.Kit.dfutest.dfu_service.DfuService;
import com.zhang.xiaofei.smartsleep.Model.Login.UserModel;
import com.zhang.xiaofei.smartsleep.R;
import com.zhang.xiaofei.smartsleep.UI.Me.DeviceManageActivity;
import com.zhang.xiaofei.smartsleep.YMApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.lizhangqu.coreprogress.ProgressUIListener;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class DfuUpdateActivity extends BaseActivity implements View.OnClickListener {
    private BluetoothService mBluetoothService;
    private TextView tv_show;
    private String dfu_macAddress;
    private String dfu_device_name;
    private String path;
    private ProgressBar progressBar;
    private TextView tvTitle;
    private ImageButton ibLeft;
    private StateButton stateButton;
    String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dfu_update);
        stateButton = (StateButton)findViewById(R.id.startDfu);
        tvTitle = (TextView)findViewById(R.id.tv_title);
        tvTitle.setText("OTA");
        ibLeft = (ImageButton)findViewById(R.id.im_l);
        ibLeft.setImageResource(R.mipmap.suggest_icon_back);
        ibLeft.setVisibility(View.VISIBLE);
        ibLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent = getIntent();
        dfu_macAddress = intent.getStringExtra("mac");
        dfu_device_name = intent.getStringExtra("name");
        System.out.println("MACADDRESS: " + dfu_macAddress + "NAME: " + dfu_device_name);
        url = intent.getStringExtra("url");
        bindService();

        tv_show = (TextView) findViewById(R.id.tv_result);

        StateButton startBtn = (StateButton) findViewById(R.id.startDfu);
        startBtn.setOnClickListener(this);

        progressBar = (ProgressBar) findViewById(R.id.dfu_progress);

        checkPermissions();
    }


    public String getRealPathFromURI(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        if (null != cursor && cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
            cursor.close();
        }
        return res;
    }

    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @SuppressLint("NewApi")
    public String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {


        Cursor cursor = null;

        final String column = "_data";

        final String[] projection = {column};


        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Uri uri = data.getData();

            if (uri == null)
                return;
            if ("file".equalsIgnoreCase(uri.getScheme())) {//使用第三方应用打开
                path = uri.getPath();

            } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {//4.4以后
                path = getPath(this, uri);

            } else {//4.4以下下系统调用方法
                path = getRealPathFromURI(uri);

            }
            tv_show.setText(path);
            if (requestCode == 1) {
                progressBar.setVisibility(View.VISIBLE);
                final DfuServiceInitiator starter = new DfuServiceInitiator(dfu_macAddress)
                        .setDeviceName("Sleep_Baby")
                        .setKeepBond(true);
                starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
                starter.setZip(uri, path);
                final DfuServiceController controller = starter.start(this, DfuService.class);
            }
        }
    }

    private final DfuProgressListener dfuProgressListener = new DfuProgressListener() {
        @Override
        public void onDeviceConnecting(String deviceAddress) {
//          progressBar.setIndeterminate(true);
//          mTextPercentage.setText(R.string.dfu_status_connecting);
            Log.i("TEST", "onDeviceConnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceConnected(String deviceAddress) {
            Log.i("TEST", "onDeviceConnected: " + deviceAddress);
        }

        @Override
        public void onDfuProcessStarting(String deviceAddress) {
//          progressBar.setIndeterminate(true);
//          mTextPercentage.setText(R.string.dfu_status_starting);
            Log.i("TEST", "onDfuProcessStarting: " + deviceAddress);


        }

        @Override
        public void onDfuProcessStarted(String deviceAddress) {
            Log.i("TEST", "onDfuProcessStarted: " + deviceAddress);
        }

        @Override
        public void onEnablingDfuMode(String deviceAddress) {
            Log.i("TEST", "onEnablingDfuMode: " + deviceAddress);
        }

        @Override
        public void onProgressChanged(String deviceAddress, int percent, float speed, float avgSpeed, int currentPart, int partsTotal) {
            Log.i("TEST", "onProgressChanged: " + deviceAddress + "百分比" + percent + ",speed "
                    + speed + ",avgSpeed " + avgSpeed + ",currentPart " + currentPart
                    + ",partTotal " + partsTotal);
            tv_show.setText("升级进度：" + percent + "%");
        }

        @Override
        public void onFirmwareValidating(String deviceAddress) {
            Log.i("TEST", "onFirmwareValidating: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnecting(String deviceAddress) {
            Log.i("TEST", "onDeviceDisconnecting: " + deviceAddress);
        }

        @Override
        public void onDeviceDisconnected(String deviceAddress) {
            Log.i("TEST", "onDeviceDisconnected: " + deviceAddress);
        }

        @Override
        public void onDfuCompleted(String deviceAddress) {
            Log.i("TEST", "onDfuCompleted: " + deviceAddress);
//          progressBar.setIndeterminate(true);
            progressBar.setVisibility(View.GONE);
            Intent intentBroadcast = new Intent();   //定义Intent
            intentBroadcast.setAction("Filter");
            intentBroadcast.putExtra("arg0", 6);
            sendBroadcast(intentBroadcast);
            stateButton.setText(R.string.ota_updata_success);
        }

        @Override
        public void onDfuAborted(String deviceAddress) {
            Log.i("TEST", "onDfuAborted: " + deviceAddress);
            progressBar.setVisibility(View.GONE);
            stateButton.setText(R.string.start);
        }

        @Override
        public void onError(String deviceAddress, int error, int errorType, String message) {
            Log.i("TEST", "onError: " + deviceAddress + ",message:" + message);
            progressBar.setVisibility(View.GONE);
            stateButton.setText(R.string.start);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        DfuServiceListenerHelper.unregisterProgressListener(this, dfuProgressListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBluetoothService != null) {
            unbindService();
            //  mBluetoothService = null;
        }
    }


    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }


    private void unbindService() {
        this.unbindService(mFhrSCon);
    }

    private BluetoothService.Callback callback = new BluetoothService.Callback() {
        @Override
        public void onStartScan() {

        }

        @Override
        public void onScanning(ScanResult scanResult) {

        }

        @Override
        public void onScanComplete() {

        }

        @Override
        public void onConnecting() {

        }

        @Override
        public void onConnectFail() {

        }

        @Override
        public void onDisConnected() {
            tv_show.setText("Dfu蓝牙断开连接");
        }

        @Override
        public void onServicesDiscovered() {
            tv_show.setText("Dfu蓝牙连接成功");
        }
    };


    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~");
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            mBluetoothService.setScanCallback(callback);


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startDfu:
                if (!stateButton.getText().toString().equals(getResources().getString(R.string.start))) {
                    return;
                }
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                //intent.setType(“image/*”);//选择图片
//                //intent.setType(“audio/*”); //选择音频
//                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//                //intent.setType(“video/*;image/*”);//同时选择视频和图片
//                intent.setType("*/*");//无类型限制
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, 1);
                String saveFilePath = Environment.getExternalStorageDirectory() + "/Download/dfufile.zip";
                tv_show.setText(saveFilePath);
                File file = new File(saveFilePath);
                Uri uri = Uri.fromFile(file);
                progressBar.setVisibility(View.VISIBLE);
                final DfuServiceInitiator starter = new DfuServiceInitiator(dfu_macAddress)
                        .setDeviceName("Sleep_Baby")
                        .setKeepBond(true);
                starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
                starter.setZip(uri, saveFilePath);
                final DfuServiceController controller = starter.start(this, DfuService.class);
                stateButton.setText(R.string.ota_updating);
                break;

        }
    }

    private void downloadOTA() {
        YMUserInfoManager userManager = new YMUserInfoManager( DfuUpdateActivity.this);
        UserModel userModel = userManager.loadUserInfo();
        com.ansen.http.net.Header header = new com.ansen.http.net.Header("Content-Type", "application/x-www-form-urlencoded");
        com.ansen.http.net.Header headerToken = new com.ansen.http.net.Header("token", userModel.getToken());
        String saveFilePath = Environment.getExternalStorageDirectory() + "/Download/dfufile.zip";
        String cUrl = YMApplication.getInstance().domain() + "appResource/zips/ota/dfufile.zip";
        if (url != null && url.length() > 0) {
            cUrl = url;
        }
        HTTPCaller.getInstance().downloadFile(
                cUrl,
                saveFilePath,
                new Header[]{header, headerToken},
                new ProgressUIListener(){
                    @Override
                    public void onUIProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                        Log.i("ansen","dowload file content numBytes:"+numBytes+" totalBytes:"+totalBytes+" percent:"+percent+" speed:"+speed);
                    }
                });
    }

    private void checkPermissions(){

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }

    }

    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                downloadOTA();
                break;
        }
    }
}
