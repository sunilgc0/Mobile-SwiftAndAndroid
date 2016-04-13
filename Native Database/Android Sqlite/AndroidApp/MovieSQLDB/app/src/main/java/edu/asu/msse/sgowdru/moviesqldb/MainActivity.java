package edu.asu.msse.sgowdru.moviesqldb;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
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
        * Purpose: Show Movie description with multiple details
        * By reading from Native Database if necessary or by requesting data from OMDB and storing in database
        *
        * I hereby give the instructors, TA right to use of building and evaluating
        * the software package for the purpose of determining your grade and program assessment.
        *
        * SER 598 - Mobile Systems
        * @author Sunil Gowdru C
        * mailto:sunil.gowdru@asu.edu
        * Software Engineering, CIDSE, IAFSE, ASU Poly
        * @version March 2016
        */

public class MainActivity extends AppCompatActivity {

    //Create ExpandableListView for the main screen
    ExpandableListView expandableView;
    //List of our genre
    List<String> action, comedy, horror, animation, adventure, biography, crime, documentary, drama, fantasy, mystery, romance, sport, thriller, war;
    ArrayList<String> heading;
    //movie list in each genre
    HashMap<String, List<String>> movielist;
    MoviesDB db;
    SQLiteDatabase crsDB;
    Cursor cur;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_menu, menu);
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //In this method we process all the data and push it to expandle list view
        processData();
    }

    public void processData() {
        //Hashmap storing the List for each genre
        movielist = getGenreMovies();

        //Get the id for ExpandableListView and set the movielist for it using Adapter
        expandableView = (ExpandableListView) findViewById(R.id.exp_list);
        //Call the MovieListAdapter constructor which will create the generated view from JSON file
        MovieListAdapter adapter = new MovieListAdapter(this, heading, movielist);
        expandableView.setAdapter(adapter);

        //Expand all genre by default, so we can see movies under each genre by default
        for(int i=0; i<adapter.getGroupCount(); i++)
            expandableView.expandGroup(i);

        //On click on each movie in any genre opens new Intent for that movie
        expandableView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
                ArrayList<String> fieldDetails = new ArrayList<>();
                String str = heading.get(groupPosition);
                List<String> ls = movielist.get(str);
                //Fetch the movie name
                String movieName = ls.get(childPosition);

                //Get Movie details of a movie name
                try {
                    cur = crsDB.rawQuery("select Title, Genre, Years, Rated, Actors from Movies where Title = ?;", new String[]{movieName});
                    //Move to the result
                    cur.moveToNext();
                    //Read all the fields
                    fieldDetails.add(cur.getString(0));
                    fieldDetails.add(cur.getString(1));
                    fieldDetails.add(cur.getString(2));
                    fieldDetails.add(cur.getString(3));
                    fieldDetails.add(cur.getString(4));
                } catch (Exception e) {
                    android.util.Log.w(getClass().getSimpleName(), e.getMessage());
                }
                //Send the acquired details to other activity for display
                intent.putStringArrayListExtra("fields", fieldDetails);

                //Launch the activity
                startActivity(intent);
                return true;
            }
        });
    }

    public HashMap<String, List<String>> getGenreMovies() {
        //Create the Genre ArrayList
        heading = new ArrayList<>();
        String gen[]={"Action", "Animation", "Comedy", "Horror", "Adventure", "Biography", "Crime", "Documentary","Drama",
                "Fantasy", "Mystery", "Romance", "Sport", "Thriller", "War"};
        for(String s:gen)
            heading.add(s);

        action = new ArrayList<>();
        comedy = new ArrayList<>();
        horror = new ArrayList<>();
        animation = new ArrayList<>();
        adventure= new ArrayList<>();
        biography= new ArrayList<>();
        crime= new ArrayList<>();
        documentary= new ArrayList<>();
        drama= new ArrayList<>();
        fantasy= new ArrayList<>();
        mystery= new ArrayList<>();
        romance= new ArrayList<>();
        sport= new ArrayList<>();
        thriller= new ArrayList<>();
        war= new ArrayList<>();

        movielist = new HashMap<>();
        String title, genre;
        try {
            db = new MoviesDB(this);
            crsDB = db.openDB();
            cur = crsDB.rawQuery("select Title, Genre from Movies;", new String[]{});
            while (cur.moveToNext()) {
                title = cur.getString(0);
                genre = cur.getString(1);

                //Sort movie according to their genre and to there respective lists
                if (genre.equals("Action")) {
                    action.add(title);
                } else if (genre.equals("Animation")) {
                    animation.add(title);
                } else if (genre.equals("Horror")) {
                    horror.add(title);
                } else if (genre.equals("Comedy")) {
                    comedy.add(title);
                } else if (genre.equals("Adventure")) {
                    adventure.add(title);
                } else if (genre.equals("Biography")) {
                    biography.add(title);
                } else if (genre.equals("Crime")) {
                    crime.add(title);
                } else if (genre.equals("Documentary")) {
                    documentary.add(title);
                } else if (genre.equals("Drama")) {
                    drama.add(title);
                } else if (genre.equals("Fantasy")) {
                    fantasy.add(title);
                } else if (genre.equals("Mystery")) {
                    mystery.add(title);
                } else if (genre.equals("Romance")) {
                    romance.add(title);
                } else if (genre.equals("Sport")) {
                    sport.add(title);
                } else if (genre.equals("Thriller")) {
                    thriller.add(title);
                } else if (genre.equals("War")) {
                    war.add(title);
                }

                //Movielist stores info in format required by expandable List adapter
                movielist.put("Action", action);
                movielist.put("Comedy", comedy);
                movielist.put("Horror", horror);
                movielist.put("Animation", animation);
                movielist.put("Adventure", adventure);
                movielist.put("Biography", biography);
                movielist.put("Crime", crime);
                movielist.put("Documentary", documentary);
                movielist.put("Drama", drama);
                movielist.put("Fantasy", fantasy);
                movielist.put("Mystery", mystery);
                movielist.put("Romance", romance);
                movielist.put("Sport", sport);
                movielist.put("Thriller", thriller);
                movielist.put("War", war);
            }
        } catch (Exception ex) {
            android.util.Log.w(this.getClass().getSimpleName(), "Exception getting student info: " +
                    ex.getMessage());
        }
        return movielist;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //On options selected from action bar, launch this Dialog class activity
        Intent i = new Intent(this, SearchMovie.class);
        switch (item.getItemId()) {
            case R.id.search_movie:
                //Put the message to be displayed in the dialog activity which displays ratings of the movie
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
