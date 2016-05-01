package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

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

public class AsyncCollectionConnect extends AsyncTask<MethodInformation, Void, JSONObject> {
    StringBuilder    sb = new StringBuilder();
    JSONObject json = null;

    //In the background do the retrieval of JSON object with streamer file name from RPC server which is started
    //Independently, depending on the method name and parameters supplied
    //we will retutn different JSON objects
    @Override
    protected JSONObject doInBackground(MethodInformation... aRequest) {
        try {
            JSONArray ja = new JSONArray(aRequest[0].params);
            String requestData = "{ \"jsonrpc\":\"2.0\", \"method\":\"" + aRequest[0].method + "\", \"params\":" + ja.toString() + ",\"id\":3}";
            JsonRPCRequestViaHttp conn = new JsonRPCRequestViaHttp((new URL(aRequest[0].urlString)));

            // If the method name is getTitles, retun json array of movie names
            if (aRequest[0].method.equals("getTitles")) {
                String resultStr = conn.call(requestData);
                System.out.println(resultStr);
                aRequest[0].resultAsJson = resultStr;
                json = new JSONObject(resultStr);
            }
            // If the method name is "get" return the movie details for the particular parameter that is passed to it
            else if (aRequest[0].method.equals("get")) {
                String resultStr = conn.call(requestData);
                System.out.println(resultStr);
                aRequest[0].resultAsJson = resultStr;
                json = new JSONObject(resultStr);
            }
        } catch (Exception ex) {
            android.util.Log.d(this.getClass().getSimpleName(), "exception in remote call " + ex.getMessage());
        }
        // Return JSON object in both the cases
        return json;
    }



}
