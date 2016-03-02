package edu.asu.msse.sgowdru.movieplus;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.Serializable;


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
public class MovieDetails extends AppCompatActivity implements Serializable {

    //intent created to launch dialog activity in the Actionbar
    Intent intent;

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
        TextView info[] = new TextView[4];
        info[0] = (TextView) findViewById(R.id.editTitle);
        info[1] = (TextView) findViewById(R.id.editGenre);
        info[2] = (TextView) findViewById(R.id.editYear);
        info[3] = (TextView) findViewById(R.id.editActors);

        //collect back values sent from caller using intent.getStringArrayListExtra for each of the field
        //And set the Textvie field value
        info[0].setText(intent.getStringArrayListExtra("fields").get(0));
        info[1].setText(intent.getStringArrayListExtra("fields").get(1));
        info[2].setText(intent.getStringArrayListExtra("fields").get(2));
        info[3].setText(intent.getStringArrayListExtra("fields").get(4));

        //Ratings field is of type Spinner class with field values (PG, PG-13, R rated)
        Spinner dropdown = (Spinner) findViewById(R.id.spinner);
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
    }

    public void addButtonClicked(View view) {
        //Get the current field on focus in the window
        TextView v = (TextView) getWindow().getCurrentFocus();
        CharSequence str = v.getText();
        //get the current string entered in  the field and set its value
        v.setText(str);
        Snackbar.make(view, "Field Set Added", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void removeButtonClicked(View view) {
        //Get the current field on focus in the window
        TextView v = (TextView) getWindow().getCurrentFocus();
        //Clear the field
        v.setText("");
        Snackbar.make(view, "Field Set Removed", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void editButtonClicked(View view) {
        //Get the current field on focus in the window
        TextView v = (TextView) getWindow().getCurrentFocus();
        CharSequence str = v.getText();
        //get the current string entered in  the field and set its value
        v.setText(str);
        Snackbar.make(view, "Field Set Edited", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
