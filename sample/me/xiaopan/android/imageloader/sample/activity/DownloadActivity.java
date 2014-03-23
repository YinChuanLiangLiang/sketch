package me.xiaopan.android.imageloader.sample.activity;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import me.xiaoapn.android.imageloader.R;
import me.xiaopan.android.imageloader.ImageLoader;
import me.xiaopan.android.imageloader.task.download.DownloadOptions;
import me.xiaopan.android.imageloader.task.download.DownloadRequest;

import java.io.File;

public class DownloadActivity extends Activity {
    private EditText periodOfValidityEdit;
	private ImageView imageView;
	private ProgressBar progressBar;
    private ToggleButton diskCacheToggleButton;
    private DrawerLayout drawerLayout;

    private DownloadOptions downloadOptions;
    private String uri = "http://tupian.enterdesk.com/2013/xll/0112/taiqiumeinv/taiqiumeinv%20(3).jpg.680.510.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_download);
        periodOfValidityEdit = (EditText) findViewById(R.id.edit_download_periodOfValidity);
        diskCacheToggleButton = (ToggleButton) findViewById(R.id.toggle_download_diskCache);
		imageView = (ImageView) findViewById(R.id.image_download);
		progressBar = (ProgressBar) findViewById(R.id.progressBar_download);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_download);

        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_left, GravityCompat.START);
        drawerLayout.setDrawerShadow(R.drawable.shape_drawer_shaow_down_right, GravityCompat.END);
        drawerLayout.openDrawer(Gravity.START);

        downloadOptions = new DownloadOptions();
        periodOfValidityEdit.setText("" + downloadOptions.getDiskCachePeriodOfValidity());

        diskCacheToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                downloadOptions.setEnableDiskCache(isChecked);
                periodOfValidityEdit.setEnabled(isChecked);
            }
        });
        diskCacheToggleButton.setChecked(downloadOptions.isEnableDiskCache());

        periodOfValidityEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = periodOfValidityEdit.getEditableText().toString().trim();
                if(text != null && !"".equals(text)){
                    downloadOptions.setDiskCachePeriodOfValidity(Long.valueOf(text));
                }else{
                    downloadOptions.setDiskCachePeriodOfValidity(0);
                }
            }
        });

        drawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View view, float v) {

            }

            @Override
            public void onDrawerOpened(View view) {

            }

            @Override
            public void onDrawerClosed(View view) {
                ImageLoader.getInstance(getBaseContext()).download(uri, downloadOptions, new DownloadRequest.DownloadListener() {
                    @Override
                    public void onStart() {
                        progressBar.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(null);
                    }

                    @Override
                    public void onUpdateProgress(final long totalLength, final long completedLength) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progressBar.setProgress((int) (((float)completedLength/totalLength) * 100));
                                progressBar.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onComplete(final byte[] data) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageBitmap(BitmapFactory.decodeByteArray(data, 0, data.length));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onComplete(final File cacheFile) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                imageView.setImageURI(Uri.fromFile(cacheFile));
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onFailure() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "下载失败", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                    @Override
                    public void onCancel() {

                    }
                });
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });
	}
}