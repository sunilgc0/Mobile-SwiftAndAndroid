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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //Create ExpandableListView for the main screen
    ExpandableListView expandableView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    //All the reading of JSON file and parsing happens in below function
        processData();
        Intent intent = new Intent(getApplicationContext(), MovieDetails.class);
        //intent.putExtra("SS",MovieDescription.class);
    }


    public void processData() {
       //MovieDescription holds all the JSON objects of movie
        MovieDescription mov = new MovieDescription();
        mov.readFile(this);

        // Get the process movie library in hash table
        final MovieLibrary movLib = mov.getMovieLibrary();
        //Create the Genre ArrayList
        final ArrayList<String> heading = new ArrayList<>();
        heading.add("Action");
        heading.add("Animation");
        heading.add("Comedy");
        heading.add("Horror");

        //Hashmap storing the List for each genre
        HashMap<String, List<String>> movielist = mov.getGenreMovies();

        //Get the id for ExpandableListView and set the movielist for it using Adapter
        expandableView = (ExpandableListView) findViewById(R.id.exp_list);
        //Call the MovieListAdapter constructor which will create the generated view from JSON file
        MovieListAdapter adapter = new MovieListAdapter(this, heading, movielist);
        expandableView.setAdapter(adapter);
        //On click on each movie in any genre opens new Intent for that movie
        expandableView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent = new Intent(getApplicationContext(), MovieDetails.class);

                //ArrrayList contains map which holds the field value for each movie
                ArrayList<Map<String, String>> arr = null;
                Map<String, String> map;
                ArrayList<String> fieldDetails;
                if (heading.get(groupPosition).equals("Action"))
                    arr = movLib.movDetails("Action");
                else if (heading.get(groupPosition).equals("Animation"))
                    arr = movLib.movDetails("Animation");
                else if (heading.get(groupPosition).equals("Comedy"))
                    arr = movLib.movDetails("Comedy");
                else if (heading.get(groupPosition).equals("Horror"))
                    arr = movLib.movDetails("Horror");

                //childPosition holds movie location and that is returned here
                map = arr.get(childPosition);
                fieldDetails = getFieldDetails(map);
                //Start the Intent for that movie with the fieldDetails, pass the ArrayList<String> fieldDetails
                intent.putStringArrayListExtra("fields", fieldDetails);

                //Launch the activity
                startActivity(intent);
                return true;
            }

    public ArrayList<String> getFieldDetails(Map<String, String> map) {
        //Create arraylist and field value for the movie and return
        ArrayList<String> fieldDetails = new ArrayList<>();
        fieldDetails.add(map.get("Title"));
        fieldDetails.add(map.get("Genre"));
        fieldDetails.add(map.get("Year"));
        fieldDetails.add(map.get("Rated"));
        fieldDetails.add(map.get("Actors"));
        return fieldDetails;
    }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

}
