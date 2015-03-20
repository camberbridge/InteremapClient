package com.example.interemap;

public class MyLocation {
  double latitude;
  double longitude;
  int evaluation;

  MyLocation(double latitude, double longitude, int evaluation){
	  this.latitude = latitude;
	  this.longitude = longitude;
	  this.evaluation = evaluation;
  }

  public double getLatitude(){
	  return this.latitude;
  }

  public double getLongitude(){
	  return this.longitude;
  }

  public double getEvaluation(){
	  return this.evaluation;
  }
}
