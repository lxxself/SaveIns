package com.lxxself.saveins;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class PictureActivity extends AppCompatActivity {

	PagerAdapter pagerAdapter;
	ViewPager viewPager;

	List<View> viewList;
	TextView tvIndex;
	List<String> urls;
	int index;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.other_act_picture);
		initData();
		initView();

	}


	private void initData() {
		urls = this.getIntent().getExtras().getStringArrayList("images");
		index = this.getIntent().getExtras().getInt("index", 0);
		if (null == urls) {
			urls = new ArrayList<String>();
		}
	}

	private void initView() {
		viewPager = (ViewPager) findViewById(R.id.view_pager);
		tvIndex = (TextView) findViewById(R.id.tv_index);
		tvIndex.setText("" + (index + 1) + "/" + urls.size());
		viewPager.setOffscreenPageLimit(3);

		viewList = new ArrayList<View>();
		for (int i = 0, j = urls.size(); i < j; i++) {
			PhotoView imageView =new PhotoView(this);
			final String imageUrl = urls.get(i);
			Glide.with(this).load(imageUrl).crossFade().into(imageView);

			imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float x, float y) {
					finish();
				}
			});
			imageView.setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					Intent shareIntent = new Intent(Intent.ACTION_SEND);
					shareIntent.setType("image/*");
					Uri uri = Uri.fromFile(new File(imageUrl));
					shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
					startActivity(Intent.createChooser(shareIntent, "请选择"));
					return true;
				}
			});
			viewList.add(imageView);
		}

		pagerAdapter = new PagerAdapter() {
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return viewList.size();
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(viewList.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container,
					final int position) {
				container.addView(viewList.get(position));
				return viewList.get(position);
			}
		};

		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int currentPosition) {
				tvIndex.setText("" + (currentPosition + 1) + "/" + urls.size());
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int currentPosition) {
			}
		});
		viewPager.setAdapter(pagerAdapter);

		if (index < urls.size()) {
			viewPager.setCurrentItem(index, true);
		}

	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	public static Bitmap revitionImageSize(String path) throws IOException {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(new File(path)));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(in, null, options);
			in.close();
			in = null;
			Bitmap bitmap;
			in = new BufferedInputStream(new FileInputStream(new File(
					path)));
			options.inSampleSize = 1;
			options.inJustDecodeBounds = false;
			bitmap = BitmapFactory.decodeStream(in, null, options);
			return bitmap;
		} catch (Exception e) {
			return null;
		} finally {
			if (null != in) {
				in.close();
			}
		}
	}
}


