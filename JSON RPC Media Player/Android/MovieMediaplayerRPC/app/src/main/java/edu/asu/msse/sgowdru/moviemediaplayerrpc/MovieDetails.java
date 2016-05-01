package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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


public class MovieDetails extends AppCompatActivity {
    //intent created to launch dialog activity in the Actionbar
    Intent intent;
    TextView info[];
    Spinner dropdown;
    Button playButton;
    String videoFile;
    MoviesDB db;
    SQLiteDatabase crsDB;


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //On options selected from action bar, launch this Dialog class activity
        Intent i = new Intent(this, DialogActivity.class);
        switch (item.getItemId()) {
            case R.id.action_plot:
                //Put the message to be displayed in the dialog activity which displays ratings of the movie
                i.putExtra("message", "Rated " + intent.getStringArrayListExtra("fields").get(3));
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movie_details);

        //Collect the intent object with parameters sent from the caller
        intent = getIntent();

        //In the info array of TextView type store id of each field
        info = new TextView[4];
        info[0] = (TextView) findViewById(R.id.editTitle);
        info[1] = (TextView) findViewById(R.id.editGenre);
        info[2] = (TextView) findViewById(R.id.editYear);
        info[3] = (TextView) findViewById(R.id.editActors);
        //Don't let the movie title be edited
        info[0].setKeyListener(null);
        //collect back values sent from caller using intent.getStringArrayListExtra for each of the field
        //And set the Textview field value
        info[0].setText(intent.getStringArrayListExtra("fields").get(0));
        info[1].setText(intent.getStringArrayListExtra("fields").get(1));
        info[2].setText(intent.getStringArrayListExtra("fields").get(2));
        info[3].setText(intent.getStringArrayListExtra("fields").get(4));

        //Ratings field is of type Spinner class with field values (PG, PG-13, R rated)
        dropdown = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Ratings, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        //Set the spinner attribute depending on the rating of the movie

        if (intent.getStringArrayListExtra("fields").get(3).equals("PG"))
            dropdown.setSelection(0);
        else if (intent.getStringArrayListExtra("fields").get(3).equals("PG-13"))
            dropdown.setSelection(1);
        else if (intent.getStringArrayListExtra("fields").get(3).equals("R"))
            dropdown.setSelection(2);

        playButton =(Button)findViewById(R.id.playDetail);
        playButton.setVisibility(View.GONE);
        videoFile = intent.getStringArrayListExtra("fields").get(5);

        if( videoFile != null) {
            playButton.setVisibility(View.VISIBLE);
        }
    }

    //Movie is Removed
    public void removeButtonClicked(View view) {
        //read the movie title of the field
        String title = info[0].getText().toString();

        //Execute sqlite query to delete the movie
        try {
            db = new MoviesDB(this);
            crsDB = db.openDB();
            crsDB.execSQL("Delete from Movies where Title='" + title + "';");
        } catch (Exception e) {
            android.util.Log.w(getClass().getSimpleName(), e.getMessage());
        }
        //Raise the Toast message to be displayed
        Context context = getApplicationContext();
        CharSequence text = " Movie Removed";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, title + text, duration);
        toast.show();

        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }

    //Movie is edited
    public void editButtonClicked(View view) {
        //read the fields of movie
        String title = info[0].getText().toString();
        String genre = info[1].getText().toString();
        String year = info[2].getText().toString();
        String actors = info[3].getText().toString();
        String dd = dropdown.getSelectedItem().toString();

        try {
            db = new MoviesDB((Context) this);
            crsDB = db.openDB();
            crsDB.execSQL("Update Movies set Genre='"
                    + genre + "', Years='" + year + "', Rated='" + dd + "', Actors='" + actors + "' where Title='" + title + "';");
        } catch (Exception e) {
            android.util.Log.w(getClass().getSimpleName(), e.getMessage());
        }

        //Raise the Toast message to be displayed
        Context context = getApplicationContext();
        CharSequence text = " Movie Edited";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, title + text, duration);
        toast.show();

        //Go back
        finish();
    }

    //If the user clicks on Play Button, play trailer
    public void playButtonClickedMovieDetails(View view) {
        Intent play = new Intent(this, PlayMovie.class);
        play.putExtra("FileName",videoFile);
        startActivity(play);
    }
}
