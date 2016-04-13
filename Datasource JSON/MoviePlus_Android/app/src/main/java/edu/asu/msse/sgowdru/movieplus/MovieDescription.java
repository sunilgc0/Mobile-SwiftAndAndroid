package edu.asu.msse.sgowdru.movieplus;

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
        * Purpose: Show Movie description with multiple details enhanced
        *
        * I hereby give the instructors, TA right to use of building and evaluating
        * the software package for the purpose of determining your grade and program assessment.
        *
        * SER 598 - Mobile Systems
        * @author Sunil Gowdru C
        * mailto:sunil.gowdru@asu.edu
        * Software Engineering, CIDSE, IAFSE, ASU Poly
        * @version Feb 2016
        */

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MovieDescription implements Serializable{
    //movieInfo stores value for each of the fields
    String movieInfo[] = new String[5];
    MovieLibrary lib = null;

    //Read the Json file and call the parser
    public void readFile(Context context) {
        String json = "";
        String line;

        try (
                //Set reader to omdb.json
                BufferedReader reader = new BufferedReader(new InputStreamReader(context.getAssets().open("omdb.json")));
        ) {
            while ((line = reader.readLine()) != null) {
                json += line;
            }
            Log.w(getClass().getSimpleName(), json);
        } catch (Exception e) {
            Log.w(getClass().getSimpleName(), "From exception");
            e.printStackTrace();
        }
        parseJson(json);
    }

    public void parseJson(String mstring) {
        //JSONArray is an array of JSONObjects, each JSONObject is a single movie
        JSONObject obj;
        try {
            //Convert the string of JSON file to JSON Array
            JSONArray array = new JSONArray(mstring);
            lib = new MovieLibrary();
            //Loop for total number of movies
            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                //Create a map of each movie got from JSON Array
                createMap(obj, lib);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void createMap(JSONObject obj, MovieLibrary lib) {
        //map has fiel name and its value, both Strings
        Map<String, String> map = new HashMap<>();
        try {
            movieInfo[0] = obj.getString("Title");
            movieInfo[1] = obj.getString("Genre");
            movieInfo[2] = obj.getString("Year");
            movieInfo[3] = obj.getString("Rated");
            movieInfo[4] = obj.getString("Actors");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // Put the info in the map and add to the lib movielibrary
        map.put("Title", movieInfo[0]);
        map.put("Genre", movieInfo[1]);
        map.put("Year",  movieInfo[2]);
        map.put("Rated", movieInfo[3]);
        map.put("Actors",movieInfo[4]);

        lib.addDB(map);             //Add each movie info to array of Map
    }

    //get MovieLibrary
    public MovieLibrary getMovieLibrary() {
        return lib;
    }

    public HashMap<String, List<String>> getGenreMovies() {
        //Get ArrayList for each genre
        ArrayList<Map<String, String>> action = lib.actionList;
        ArrayList<Map<String, String>> animation = lib.animationList;
        ArrayList<Map<String, String>> comedy = lib.comedyList;
        ArrayList<Map<String, String>> horror = lib.horrorList;

        //List of field value to be returned for each movie
        List<String> actiontitle = new ArrayList<>();
        List<String> animationtitle = new ArrayList<>();
        List<String> comedytitle = new ArrayList<>();
        List<String> horrortitle = new ArrayList<>();

        //Adapter class constructor only accepts Hashmap of String and List<String>, so we are creating it
        HashMap<String, List<String>> movielist = new HashMap<>();

        for (Map<String, String> obj : action)
            actiontitle.add(obj.get("Title"));
        for (Map<String, String> obj : animation)
            animationtitle.add(obj.get("Title"));
        for (Map<String, String> obj : comedy)
            comedytitle.add(obj.get("Title"));
        for (Map<String, String> obj : horror)
            horrortitle.add(obj.get("Title"));

        //Creating Hashmap of String and List<String>, required by BaseListAdapter Constructor
        movielist.put("Action", actiontitle);
        movielist.put("Animation", animationtitle);
        movielist.put("Comedy", comedytitle);
        movielist.put("Horror", horrortitle);

        return movielist;
    }

}
