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

import java.util.ArrayList;
import java.util.Map;

public class MovieLibrary {
    //Create ArrayList of maps for each genre
    ArrayList<Map<String, String>> actionList, comedyList, horrorList, animationList;

    public MovieLibrary() {
        actionList = new ArrayList<>();
        comedyList = new ArrayList<>();
        horrorList = new ArrayList<>();
        animationList = new ArrayList<>();
    }

    public void addDB(Map<String, String> map) {
        //Add movie to each genre type
        if (map.get("Genre").equals("Action"))
            actionList.add(map);
        else if (map.get("Genre").equals("Comedy"))
            comedyList.add(map);
        else if (map.get("Genre").equals("Animation"))
            animationList.add(map);
        else if (map.get("Genre").equals("Horror"))
            horrorList.add(map);
    }

    public ArrayList<Map<String, String>> movDetails(String genre) {
        //Return the map depending on the argument passed to it
        if (genre.equals("Action"))
            return actionList;
        else if (genre.equals("Comedy"))
            return comedyList;
        else if (genre.equals("Animation"))
            return animationList;
        else if (genre.equals("Horror"))
            return horrorList;
        return null;
    }
}
