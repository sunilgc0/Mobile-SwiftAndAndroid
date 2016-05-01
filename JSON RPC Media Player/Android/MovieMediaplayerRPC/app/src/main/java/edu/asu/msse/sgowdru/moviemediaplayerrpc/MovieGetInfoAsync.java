package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.os.AsyncTask;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;

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
        * Purpose: Show Movie description and Video if available using JSON-RPC server streamer
        * By reading from Native Database if necessary or by requesting data from OMDB and storing in database
        * Also allowing Editing, removing, manual add etc
        *
        * I hereby give the instructors, TA right to use of building and evaluating
        * the software package for the purpose of determining grade and program assessment.
        *
        * SER 598 - Mobile Systems
        * @author Sunil Gowdru C
        * mailto:sunil.gowdru@asu.edu
        * Software Engineering, CIDSE, IAFSE, ASU Poly
        * @version April 2016
        */

public class MovieGetInfoAsync extends AsyncTask<String, Void, JSONObject> {

    JSONObject json = null;

    //Setting up reader for the stream
    String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    @Override
    protected JSONObject doInBackground(String... strings) {
        //Open the input stream and read data from internet in JSON format
        //return json from the this method
        try {
            InputStream is = new URL(strings[0]).openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = readAll(rd);
            json = new JSONObject(jsonText);
            android.util.Log.w(getClass().getSimpleName(), json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return json;
    }
}
