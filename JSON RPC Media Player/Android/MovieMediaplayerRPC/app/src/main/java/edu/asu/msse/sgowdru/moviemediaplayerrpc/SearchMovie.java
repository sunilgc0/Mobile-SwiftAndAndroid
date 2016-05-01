package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

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

public class SearchMovie extends AppCompatActivity {
    private static final String[] gen={"Action", "Animation", "Comedy", "Horror", "Adventure", "Biography", "Crime", "Documentary","Drama",
            "Fantasy", "Mystery", "Romance", "Sport", "Thriller", "War"};
    Intent intent;
    TextView info[];
    Spinner dropdown;

    String videoFile;
    JSONObject result;
    Button addButton;
    Button playButton;
    MoviesDB db;
    SQLiteDatabase crsDB;
    Cursor cur;
    ArrayAdapter<CharSequence> adapter;
    Context context;
    CharSequence text;
    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_movie);
        //Collect the intent object with parameters sent from the caller
        intent = getIntent();

        info = new TextView[5];
        //In the info array of TextView type store id of each field
        info[0] = (TextView) findViewById(R.id.autoCompleteTextView);
        info[1] = (TextView) findViewById(R.id.editTitleSearch);
        info[2] = (TextView) findViewById(R.id.editGenreSearch);
        info[3] = (TextView) findViewById(R.id.editYearSearch);
        info[4] = (TextView) findViewById(R.id.editActorsSearch);

        //Ratings field is of type Spinner class with field values (PG, PG-13, R rated)
        dropdown = (Spinner) findViewById(R.id.spinnerSearch);
        adapter = ArrayAdapter.createFromResource(this, R.array.Ratings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);

        addButton = (Button) findViewById(R.id.addSearch);

        //Set the Video player ID, initially set the visibility to NONE
        playButton =(Button)findViewById(R.id.play);
        playButton.setVisibility(View.GONE);

        //There is no file available to play by default
        videoFile = null;

        context= getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        db = new MoviesDB(this);
        try {
            crsDB = db.openDB();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, gen);
        AutoCompleteTextView textView = (AutoCompleteTextView)
                findViewById(R.id.editGenreSearch);
        textView.setAdapter(adapter);

    }

    public void searchButtonClicked(View view) {
        String searchString = info[0].getText().toString();

        android.util.Log.w(getClass().getSimpleName(), searchString);

        //Check if the movie name is available in RPC server
        boolean isInRPC = isMovieInRPC(searchString);
        //If the movie name is available retrieve its detail
        //Set the video player visibility to VISIBLE
        if(isInRPC) {
            System.out.println(searchString +" Movie in RPC!");
            //Movie not present in local DB, raise a toast message
            text = " being retrieved from RPC Server now.";
            Toast toast = Toast.makeText(context, "Movie " + searchString + text, duration);
            toast.show();

            playButton.setVisibility(view.VISIBLE);

            getMovieDetails(searchString);
        }
        else{
            try {
                cur = crsDB.rawQuery("select Title from Movies where Title='" + searchString + "';", new String[]{});
                android.util.Log.w(getClass().getSimpleName(), searchString);

                //If the Movie Exists in the Local Database, we will retrieve it from the Local DB
                if (cur.getCount() != 0) {
                    //Raise toast message that the movie is already present in local DB
                    text =  " already present in DB, Retrieving..";
                    Toast toast = Toast.makeText(context, "Movie " + searchString + text, duration);
                    toast.show();

                    //Retrieving the Movie since we know that the movie exists in DB
                    cur = crsDB.rawQuery("select Title, Genre, Years, Rated, Actors, VideoFile from Movies where Title = ?;", new String[]{searchString});

                    //Movie Already present hence disabling the Add Button
                    addButton.setEnabled(false);

                    //Move the Cursor and set the Fields
                    cur.moveToNext();
                    info[1].setText(cur.getString(0));
                    info[2].setText(cur.getString(1));
                    info[3].setText(cur.getString(2));
                    info[4].setText(cur.getString(4));

                    //Set the Ratings dropdown
                    if (cur.getString(3).equals("PG"))
                        dropdown.setSelection(0);
                    else if (cur.getString(3).equals("PG-13"))
                        dropdown.setSelection(1);
                    else if (cur.getString(3).equals("R"))
                        dropdown.setSelection(2);

                    String video = cur.getString(5);
                    if(video != null){
                        playButton.setVisibility(view.VISIBLE);
                    }

                }

                //If the Movie Does not exist in the Local Database, we will retrieve it from the OMDB
                else {
                    //Movie not present in local DB, raise a toast message
                    text = " being retrieved from OMDB now.";
                    Toast toast = Toast.makeText(context, "Movie " + searchString + text, duration);
                    toast.show();

                    //Encode the search string to be appropriate to be placed in a url
                    String encodedUrl = null;
                    try {
                        encodedUrl = URLEncoder.encode(searchString, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        android.util.Log.e(getClass().getSimpleName(),e.getMessage());
                    }

                    //ASync thread running the query from OMDB and retrieving the movie details as JSON
                    JSONObject result = new MovieGetInfoAsync().execute("http://www.omdbapi.com/?t=\"" + encodedUrl + "\"&r=json").get();

                    //Check if the Movie query was successful
                    if(result.getString("Response").equals("True")) {
                        info[1].setText(result.getString("Title"));
                        info[2].setText(result.getString("Genre"));
                        info[3].setText(result.getString("Year"));
                        info[4].setText(result.getString("Actors"));

                        if (result.getString("Rated").equals("PG"))
                            dropdown.setSelection(0);
                        else if (result.getString("Rated").equals("PG-13"))
                            dropdown.setSelection(1);
                        else if (result.getString("Rated").equals("R"))
                            dropdown.setSelection(2);

                        if(result.has("Filename")){
                            videoFile = result.getString("Filename");
                        }else {
                            playButton.setVisibility(View.GONE);
                            videoFile = null;
                        }
                    }
                    //Search query was unsuccessful in getting movie with such a name
                    else if(result.getString("Response").equals("False")){
                        //Raise a toast message
                        text = " not present in OMDB, You can add it manually!";
                        toast = Toast.makeText(context, "Movie "+ searchString + text, duration);
                        toast.show();
                    }
                }
            } catch (Exception e) {
                android.util.Log.w(getClass().getSimpleName(), e.getMessage());
            }
        }
    }

    //If the user clicks on Add Button, add it to local DB
    public void addButtonClicked(View view) {
        //Read all the fields
        String title = info[1].getText().toString();
        String genre = info[2].getText().toString();
        String year = info[3].getText().toString();
        String actors = info[4].getText().toString();
        String dd = dropdown.getSelectedItem().toString();

        //If there are multiple genre for a movie, get the first genre and add the movie under it
        //If the first genre is not in local types of genre go to subsequent genre and check if that is there and so on
        //If no genre is present, add it under Action Genre
        String movieGenre[] = genre.trim().split(",");

        List<String> mov = Arrays.asList(movieGenre);
        List<String> ourGenre = Arrays.asList(movieGenre);
        boolean flag = false;
        for (String str : mov) {
            if(ourGenre.contains(str)) {
                genre = str;
                flag = true;
                break;
            }
        }
        if(!flag)
            genre = "Action";

        //Execute the Sqlite query to insert into local DB
        try {
            if(videoFile == null)
                crsDB.execSQL("Insert into Movies (Title, Years, Rated, Genre, Actors) VALUES('"
                        + title + "', '" + year + "', '" + dd + "', '" + genre + "', '" + actors + "');");
            else
                crsDB.execSQL("Insert into Movies (Title, Years, Rated, Genre, Actors, VideoFile) VALUES('"
                        + title + "', '" + year + "', '" + dd + "', '" + genre + "', '" + actors + "', '"+ videoFile +"');");

        } catch (Exception e) {
            android.util.Log.w(getClass().getSimpleName(), e.getMessage());
        }

        //Go back
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        setResult(Activity.RESULT_OK,intent);
        finish();
    }

    //Method to check by async if the movie name supplied is seen in RPC server movie names
    boolean isMovieInRPC(String searchString){
        JSONArray arr;
        boolean isInRPC = false;
        try{
            //Call async method to retrieve the movie list names and check in the movie list
            MethodInformation mi = new MethodInformation("http://10.0.2.2:8080/","getTitles", new String[]{});
            JSONObject ac =  new AsyncCollectionConnect().execute(mi).get();
            arr = (JSONArray)ac.get("result");

            //Check if the list has our movie name and set flag
            for(int i=0;i<arr.length(); i++) {
                System.out.println(arr.get(i));
                String mv = (String) arr.get(i);
                if(mv.equalsIgnoreCase(searchString)) {
                    isInRPC = true;
                    break;
                }
            }
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception creating adapter: "+
                    ex.getMessage());
        }
        return isInRPC;
    }

    //If the movie name is available in RPC server
    //Call one more async method to retrieve its data as JSON object
    void getMovieDetails(String searchString){
        try{
            MethodInformation mi = new MethodInformation("http://10.0.2.2:8080/","get", new String[]{searchString});
            JSONObject ac =  new AsyncCollectionConnect().execute(mi).get();
            result = (JSONObject)ac.get("result");

            info[1].setText(result.getString("Title"));
            info[2].setText(result.getString("Genre"));
            info[3].setText(result.getString("Year"));
            info[4].setText(result.getString("Actors"));

            if (result.getString("Rated").equals("PG"))
                dropdown.setSelection(0);
            else if (result.getString("Rated").equals("PG-13"))
                dropdown.setSelection(1);
            else if (result.getString("Rated").equals("R"))
                dropdown.setSelection(2);

            videoFile = result.getString("Filename");
        } catch (Exception ex){
            android.util.Log.w(this.getClass().getSimpleName(),"Exception creating adapter: "+
                    ex.getMessage());
        }
    }

    //If the user clicks on Play Button, play trailer
    public void playButtonClicked(View view) {
        Intent play = new Intent(this, PlayMovie.class);
        play.putExtra("FileName",videoFile);
        startActivity(play);
    }
}
