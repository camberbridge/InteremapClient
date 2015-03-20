package com.example.interemap;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;

/*
 * dolnBackground 非同期で実行したい処理
 * onPreExecute 非同期処理前に実行したい処理
 * onProgressUpdate 非同期処理中に実行したい処理
 * onPostExecute 非同期処理完了時に実行したい処理
 * onCancelled キャンセル時に実行したい処理
 */

public class AsyncGet extends AsyncTask<String, Integer, String> {

    private AsyncCallback _asyncCallback = null;;

    public AsyncGet(AsyncCallback asyncCallback) {
        this._asyncCallback = asyncCallback;
    }

    protected String doInBackground(String... urls) {
    	//パラメータを生成
    	ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
    	params.add(new BasicNameValuePair("latitude", "12.345"));
    	params.add(new BasicNameValuePair("longitude", "123.456"));
    	//パラメータをクエリに変換
    	String query = URLEncodedUtils.format(params, "UTF-8");
        HttpGet httpGet = new HttpGet(urls[0] + query);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            //ステータスコードを取得
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                httpResponse.getEntity().writeTo(outputStream);
                return outputStream.toString();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this._asyncCallback.onPreExecute();
    }

    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        this._asyncCallback.onProgressUpdate(values[0]);
    }

    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        this._asyncCallback.onPostExecute(result);
    }

    protected void onCancelled() {
        super.onCancelled();
        this._asyncCallback.onCancelled();
    }

}
