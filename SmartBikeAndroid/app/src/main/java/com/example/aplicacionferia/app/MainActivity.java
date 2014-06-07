package com.example.AplicacionFeria.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.loopj.android.http.*;

import org.apache.http.Header;

public class MainActivity extends ActionBarActivity {

    Context thisActivityContext;
    ProgressDialog waitingServerResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivityContext = this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickButton(View v) {
        final TextView textview = (TextView)findViewById(R.id.tv);
        // Loading
        waitingServerResponse = ProgressDialog.show(thisActivityContext, "", "Conectándose con pagina benja");

        // Async HTTP
        AsyncHttpClient client = new AsyncHttpClient();     //<--- esta es la libreria, tienes que tenerla
        RequestParams parameters = new RequestParams();
        parameters.put("q", "blah");


        client.get("http://sleepy-cove-2909.herokuapp.com/", parameters, new AsyncHttpResponseHandler()
        {
            @Override
            public void onSuccess(String response)
            {
                String parsedResponse = new String(response);
                textview.setText("Respuesta : "+ response );
                Log.d("blah", "respuesta del server: " + response );
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error)
            {
                textview.setText("Falló");
                Log.d("Error", "Error " + statusCode);
            }

            @Override
            public void onFinish()
            {
                // Cierra loading
                waitingServerResponse.dismiss();
            }
        });
    }

}