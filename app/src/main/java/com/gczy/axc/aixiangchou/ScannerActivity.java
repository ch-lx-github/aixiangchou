package com.gczy.axc.aixiangchou;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import com.journeyapps.barcodescanner.CaptureManager;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;

/**
 * Created by lixin on 18/11/22.
 */

public class ScannerActivity extends AppCompatActivity {

    private ImageButton imageButton;
    private DecoratedBarcodeView scanner;

    private CaptureManager capture;
    private PermissionResultHolder resultHolder;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        initView();
        initScanner();
    }

    private void initView() {
        imageButton = (ImageButton) findViewById(R.id.close_button);
        scanner = (DecoratedBarcodeView) findViewById(R.id.scanner);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void initScanner() {
        this.capture = new CaptureManager(this, scanner);
        capture.decode();
        this.capture.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        this.resultHolder = new PermissionResultHolder(requestCode, permissions, grantResults);
        tryHandlePermissionResultHolder();
    }

    public void handlePermissionsResult(final PermissionResultHolder prh) {
        this.capture.onRequestPermissionsResult(prh.getRequestCode(), prh.getPermissions(), prh.getGrantResults());
    }

    private void tryHandlePermissionResultHolder() {
        if (this.resultHolder == null) return;
        this.handlePermissionsResult(this.resultHolder);
        this.resultHolder = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (this.capture != null) {
                this.capture.onPause();
            }
        } catch (final IllegalArgumentException ex) {
            ex.printStackTrace();
        }
    }
}
