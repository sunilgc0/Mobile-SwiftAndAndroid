package edu.asu.msse.sgowdru.moviesqldb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

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

public class DialogActivity extends AppCompatActivity {
    TextView dialogBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d(this.getClass().getSimpleName(), "called onCreate()");
        setContentView(R.layout.plot_dialog);

        //Set the id of dialogBox and set its Rating message
        dialogBox = (TextView) findViewById(R.id.dialog_textview);
        Intent i = getIntent();
        //Set the id of dialogBox and set its Rating message from intent
        dialogBox.setText(i.getStringExtra("message"));
    }

    //Call this when Ok button is clicked in the Dialog box layout
    public void finishDialog(View v) {
        finish();
    }
}
