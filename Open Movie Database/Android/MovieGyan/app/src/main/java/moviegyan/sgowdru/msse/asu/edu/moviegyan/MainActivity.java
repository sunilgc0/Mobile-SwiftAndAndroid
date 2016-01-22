/* Copyright 2016 Sunil Gowdru C,
        *
        * Licensed under the Apache License, Version 2.0 (the "License");
        * you may not use this file except in compliance with the License.
        * You may obtain a copy of the License at
        *
        * http://www.apache.org/licenses/LICENSE-2.0
        *
        * Unless required by applicable law or agreed to in writing, software
        * distributed under the License is distributed on an "AS IS" BASIS,
        * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
        * See the License for the specific language governing permissions and
        * limitations under the License.
        *
        * Purpose: Example classes conversion to/from json
        * This example shows the use of Swift
        * reflection in creating json string of a Swift object.
        *
        * I hereby give the instructors, TA right to use of building and evaluating
        * the software package for the purpose of determining your grade and program assessment.
        *
        * SER 598 - Mobile Systems
        * @author Sunil Gowdru C
        * mailto:sunil.gowdru@asu.edu
        * Software Engineering, CIDSE, IAFSE, ASU Poly
        * @version January 2016
        */

package moviegyan.sgowdru.msse.asu.edu.moviegyan;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView info[] = new TextView[8];
        info[0] = (TextView) findViewById(R.id.title);
        info[1] = (TextView) findViewById(R.id.year);
        info[2] = (TextView) findViewById(R.id.rated);
        info[3] = (TextView) findViewById(R.id.released);
        info[4] = (TextView) findViewById(R.id.runtime);
        info[5] = (TextView) findViewById(R.id.genre);
        info[6] = (TextView) findViewById(R.id.actors);
        info[7] = (TextView) findViewById(R.id.plot);

        //Runs Async task to fetch Json from URL and display
        //MovieInfo is a private class which extends Async task and create new thread to execute the below function
        new MovieInfo(info).execute("http://www.omdbapi.com/?t=Frozen&y=&plot=short&r=json");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    private class MovieInfo extends AsyncTask<String, Void, String> {
        JSONObject json = null;
        private TextView textView[]; // To store the textfields of the main screen
        public MovieInfo(TextView textView[]) {
            this.textView = textView;
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                InputStream is = new URL(strings[0]).openStream();

                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                String jsonText = readAll(rd);
                System.out.println(jsonText);
                json = new JSONObject(jsonText);


            } catch (Exception e) {
                e.printStackTrace();
            }

            return json.toString();
        }

        @Override
        protected void onPostExecute(String temp) {
            // Setting the value for each of the texfields from json value
            try {
                textView[0].setText(json.getString("Title"));
                textView[1].setText(json.getString("Year"));
                textView[2].setText(json.getString("Rated"));
                textView[3].setText(json.getString("Released"));
                textView[4].setText(json.getString("Runtime"));
                textView[5].setText(json.getString("Genre"));
                textView[6].setText(json.getString("Actors"));
                textView[7].setText(json.getString("Plot"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


