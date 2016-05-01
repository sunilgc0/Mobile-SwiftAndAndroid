package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

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

public class MovieListAdapter extends BaseExpandableListAdapter {
    //BaseExpandableListAdapter interface is implemented here
    private List<String> heading;
    private HashMap<String, List<String>> movielist;
    private Context ctx;

    public MovieListAdapter(Context ctx, List<String> heading, HashMap<String, List<String>> movielist) {
        this.ctx = ctx;
        this.heading = heading;
        this.movielist = movielist;
    }

    @Override
    public int getGroupCount() {
        //return the number of genre of movies
        return heading.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return the number of movies in each genre of movies
        return (movielist.get(heading.get(groupPosition))).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        //return the group of each genre
        return heading.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        //return the particular movie in a genre
        return movielist.get(heading.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //Create view with the layout file parent_group and inflate it
        String title = (String) getGroup(groupPosition);
        if (convertView == null) {
            //Create view with the layout file parent_group and inflate it
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.parent_group, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.parent_id);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setText(title);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //Create view with the layout file child_group and inflate it
        String title = (String) getChild(groupPosition, childPosition);
        if (convertView == null) {
            //Create view with the layout file child_group and inflate
            LayoutInflater layoutInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.child_group, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.child_id);
        textView.setText(title);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        //Can child be selected or clicked, Yes
        return true;
    }


}

