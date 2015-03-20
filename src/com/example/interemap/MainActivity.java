package com.example.interemap;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;

/*com.google.android.gms.location.LocationClientの代わりがGoogleApiClient*/

/**
 * 非同期通信でPOSTリクエストをする
 *
 */
public class MainActivity extends FragmentActivity implements OnClickListener {
	private Button btnGood = null;
	private Button btnBad = null;
	private GoogleMap mMap;
	private Handler mHandler = new Handler();
	private Runnable updateGet;
	LatLng curr;
	LocationManager locationManager;
	GroundOverlay imageOverlay;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.main);
		setContentView(R.layout.activity_main);

		// システムサービスのLOCATION_SERVICEからLocationManager objectを取得
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		// mapクラス関連の初期化
		mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();

		// 現在位置を獲得する
		mMap.setMyLocationEnabled(true);

		mMap.setOnMyLocationChangeListener(new OnMyLocationChangeListener() {
			@Override
			public void onMyLocationChange(Location loc) {
				curr = new LatLng(loc.getLatitude(), loc.getLongitude());
				mMap.animateCamera(CameraUpdateFactory.newLatLng(curr));
				Log.v("Latitude", String.valueOf(loc.getLatitude()));
				Log.v("Longitude", String.valueOf(loc.getLongitude()));
			}
		});



		mMap.getMaxZoomLevel();



		btnGood = (Button) findViewById(R.id.btn1);
		btnBad = (Button) findViewById(R.id.btn2);
		// tv = (TextView) findViewById(R.id.tv1);

		btnGood.setOnClickListener(this);
		btnBad.setOnClickListener(this);

		updateGet = new Runnable() {
			public void run() {
				exec_get();
				// mHandler.postDelayed(updateGet, 1000);
			}
		};
		mHandler.postDelayed(updateGet, 20000);
	}

	@Override
	public void onClick(View v) {
		// ボタン押下時
		// Good評価時 evaluation = 1
		if (v == btnGood) {
			postGood();
	        btnGood.setEnabled(false);
	        btnBad.setEnabled(false);
	        new Handler().postDelayed(new Runnable() {
	            public void run() {
	                btnGood.setEnabled(true);
	                btnBad.setEnabled(true);
	            }
	        }, 3000L);

			// Bad評価時 evaluation = 0
		} else if (v == btnBad) {
			postBad();
	        btnGood.setEnabled(false);
	        btnBad.setEnabled(false);
	        new Handler().postDelayed(new Runnable() {
	            public void run() {
	                btnGood.setEnabled(true);
	                btnBad.setEnabled(true);
	            }
	        }, 3000L);
		}
	}


	void postGood(){
		MyLocation myLocation = new MyLocation(curr.latitude, curr.longitude, 1);
		exec_post(myLocation);
	}

	void postBad(){
		MyLocation myLocation = new MyLocation(curr.latitude, curr.longitude, 0);
		exec_post(myLocation);
	}


	// GET通信を実行(AsyncTaskによる非同期処理を行う)
	private void exec_get() {
		AsyncGet asyncGet = new AsyncGet(new AsyncCallback() {
			public void onPreExecute() {

			}

			public void onProgressUpdate(int progress) {

			}

			public void onPostExecute(String result) {
				// result = {"result":"success",
				// "locations":["latitude":"11.111", "longitude":"111.11",
				// "evaluation":"1"]}
				JSONObject responseJson;

				// 文字列をJSONオブジェクトに変換する
				try {
					if(imageOverlay != null){
						imageOverlay.remove();
					}

					responseJson = new JSONObject(result);
					String resultValue = responseJson.getString("result");
					List<MyLocation> listLocation = new ArrayList<MyLocation>();

					if (resultValue.equals("success")) {
						JSONArray locatoinJsonArray = responseJson
								.getJSONArray("locations");

						for (int i = 0; i < locatoinJsonArray.length(); i++) {
							JSONObject locationJson = locatoinJsonArray
									.getJSONObject(i);
							double latitude = Double.valueOf(locationJson
									.getString("latitude"));
							double longitude = Double.valueOf(locationJson
									.getString("longitude"));
							int evaluation = Integer.valueOf(locationJson
									.getString("evaluation"));

							MyLocation location = new MyLocation(latitude,
									longitude, evaluation);

							listLocation.add(location);


							  /*// 画像を地図上に配置するオーバーレイ
							  Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_good);
							  MyOverlay overlay = new MyOverlay(mMap, bmp, listLocation.get(i)
										.getLatitude(), listLocation.get(i)
										.getLongitude());
							  List<GroundOverlay> list = mMap.
							  list.add(overlay);*/

							// 地図にアイコンをオーバーレイ
							LatLng tmpLocation;
							if (listLocation.get(i).getEvaluation() == 1) {
								tmpLocation = new LatLng(listLocation.get(i)
										.getLatitude(), listLocation.get(i)
										.getLongitude());

								// 貼り付け設定
								GroundOverlayOptions intereMapGood = new GroundOverlayOptions()
										.image(BitmapDescriptorFactory
												.fromResource(R.drawable.ic_good))
										.position(tmpLocation, 10f, 10f);

								// Add an overlay to the map, retaining a handle
								// to the GroundOverlay object.
								imageOverlay = mMap
										.addGroundOverlay(intereMapGood);

							} else if (listLocation.get(i).getEvaluation() == 0) {
								tmpLocation = new LatLng(listLocation.get(i)
										.getLatitude(), listLocation.get(i)
										.getLongitude());

								// 貼り付け設定
								GroundOverlayOptions intereMapBad = new GroundOverlayOptions()
										.image(BitmapDescriptorFactory
												.fromResource(R.drawable.ic_bad))
										.position(tmpLocation, 10f, 10f);

								// Add an overlay to the map, retaining a handle
								// to the GroundOverlay object.
								imageOverlay = mMap
										.addGroundOverlay(intereMapBad);

							}

							Log.v("LATITUDE", String.valueOf(listLocation
									.get(i).getLatitude()));
							Log.v("LONGITUDE", String.valueOf(listLocation.get(
									i).getLongitude()));
							Log.v("EVALUATION", String.valueOf(listLocation
									.get(i).getEvaluation()));

						}

						/*
						 * for (int i = tmp; i < locatoinJsonArray.length();
						 * i++) { Log.v("LATITUDE",
						 * String.valueOf(listLocation.get(i).getLatitude()));
						 * Log.v("LONGITUDE",
						 * String.valueOf(listLocation.get(i).getLongitude()));
						 * Log.v("EVALUATION",
						 * String.valueOf(listLocation.get(i).getEvaluation()));
						 * }
						 */

					} else {
						Log.v("TAG", "ERROR");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}

			public void onCancelled() {

			}
		});
		asyncGet.execute("http://157.7.216.12:8888/cakephp/locations?");
	}




	// POST通信を実行（AsyncTaskによる非同期処理を使うバージョン）
	private void exec_post(MyLocation location) {

		// 非同期タスクを定義
		HttpPostTask task = new HttpPostTask(this,
				"http://157.7.216.12:8888/cakephp/locations/add.json",

				// タスク完了時に呼ばれるUIのハンドラ
				new HttpPostHandler() {

					@Override
					public void onPostCompleted(String response) {
						// 受信結果をUIに表示
						// tv.setText(response); //
						// //////サーバから受信したリクエスト結果をTextviewでAndroidに表示する
						Log.v("RESPONSE", response);
						exec_get();

						Toast toast = Toast.makeText(MainActivity.this, "評価しました", Toast.LENGTH_LONG);
						toast.setGravity(Gravity.CENTER|Gravity.TOP, 0, 0);
						toast.show();

					}

					@Override
					public void onPostFailed(String response) {
						// tv.setText(response);
						Log.v("RESPONSE", response);
						Toast.makeText(getApplicationContext(), "エラーが発生しました。",
								Toast.LENGTH_LONG).show();
					}
				});

		task.addPostParam("latitude", String.valueOf(location.getLatitude())); // /////////実際にPOSTするキーバリューセットこれ
		task.addPostParam("longitude", String.valueOf(location.getLongitude())); // /////////実際にPOSTするキーバリューセットこれ
		task.addPostParam("evaluation", String.valueOf(location.getEvaluation())); // /////////実際にPOSTするキーバリューセットこれ

		// タスクを開始
		task.execute();




	}

}