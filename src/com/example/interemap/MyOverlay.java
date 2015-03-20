package com.example.interemap;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;


public class MyOverlay{
  private final Bitmap bmp;
  private final double lat;
  private final double lng;
  GoogleMap mMap;

  public MyOverlay(GoogleMap mMap, Bitmap bmp, double lat, double lng) {
    this.bmp = bmp;
    this.lat = lat;
    this.lng = lng;
  }

  public void draw(Canvas canvas, MapView mapView, boolean shadow) {
	mMap.getProjection();
    //Projection pro = mMap.getProjection();//Mapと画面の位置を計算するオブジェクト
    //Point p = pro.toPixels(gpoint, null);    //ロケーションから、表示する位置を計算する
    canvas.drawBitmap(bmp, (float)this.lat, (float)this.lng, null);  //表示する場所へ画像を配置する。
  }
}