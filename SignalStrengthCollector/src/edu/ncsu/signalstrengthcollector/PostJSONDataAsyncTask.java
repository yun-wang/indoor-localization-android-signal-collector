package edu.ncsu.signalstrengthcollector;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class PostJSONDataAsyncTask extends AsyncTask<Object, Void, String>{
    //private Exception exception;
    private Context context;
    public ProgressDialog dialog;
    private JSONObject jsonObject;
    private String postURL;
    private boolean showProgressDialog;
    
    /**
     * 
     * @param context
     * @param jsonObject // the object to post
     * @param postURL    // the address to post to
     */
    public PostJSONDataAsyncTask(Context context, JSONObject jsonObject, String postURL, boolean showProgressDialog){
        this.context = context;
        this.jsonObject = jsonObject;
        this.postURL = postURL;
        this.showProgressDialog = showProgressDialog;
    }
    
    @Override
    protected void onPreExecute(){
        this.dialog = new ProgressDialog(context);
        this.dialog.setMessage("Updating");
        if (showProgressDialog) {
            this.dialog.show();
        }
    }
    
    protected String doInBackground(Object... arg){

        try{
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String responseBody = null;
            
            // If this is null, it means we'll just execute HTTP directly without POST
            if(jsonObject == null){
                // response = httpclient.execute(new HttpGet(postURL));
                responseBody = httpclient.execute(new HttpGet(postURL), responseHandler);
                //Log.d("sigstr", responseBody);
            }
            // Execute HTTP POST
            else {
                JSONArray postjson = new JSONArray();
                postjson.put(jsonObject);
    
                // Post the data:
                HttpPost httppost = new HttpPost(postURL);
                httppost.setHeader("json", jsonObject.toString());
                httppost.getParams().setParameter("jsonpost", postjson);
    
                // Execute HTTP Post Request
                Log.d("sigstr", jsonObject.toString());
    
                // response = httpclient.execute(httppost);
                responseBody = httpclient.execute(httppost, responseHandler);
                
            }
            
            return responseBody;
        }
        catch (Exception e){
            Log.e("", e.toString());
            //this.exception = e;
            return null;
        }
    }
    
    // This should be overridden on each 
    protected void onPostExecute(String response){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
