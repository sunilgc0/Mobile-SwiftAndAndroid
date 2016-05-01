package edu.asu.msse.sgowdru.moviemediaplayerrpc;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.MediaController;
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
//Set the Movie Media player
public class PlayMovie extends AppCompatActivity implements MediaPlayer.OnPreparedListener{

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        //Get the video file name
        String filename = intent.getStringExtra("FileName");

        //Set the content and the movie
        //set the media controllers
        setContentView(R.layout.playmovie);
        mVideoView = (VideoView) findViewById(R.id.videoview);
        mVideoView.setVideoPath(getString(R.string.videourl) + filename);
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(mVideoView);
        mVideoView.setMediaController(mediaController);
        mVideoView.setOnPreparedListener(this);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        android.util.Log.d(this.getClass().getSimpleName(), "onPrepared called. Video Duration: "
                + mVideoView.getDuration());
        //start the movie
        mVideoView.start();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        android.util.Log.d(this.getClass().getSimpleName(), "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
    }
}
