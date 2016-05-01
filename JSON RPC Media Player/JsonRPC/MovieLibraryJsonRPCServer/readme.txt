Author: Tim Lindquist (Tim.Lindquist@asu.edu), ASU Polytechnic, CIDSE, SE
Version: April 2016

See http://pooh.poly.asu.edu/Mobile

Purpose: Sample Json-RPC Movie server and movie streamer for developing Assignment 9

The project includes a movie library jsonrpc service and a second http server that will
stream mp4 media files for the movies that are accessible through the jsonrpc service.
The JsonRPC method
public MovieDescription get (String aTitle)
returns the json of a MovieDescription that includes the following attributes (keys):
 Runtime
 Released
 Year
 Rated
 Plot
 Poster
 Title
 Filename
 Actors
 Genre
All of these are consistent with those returned by the Json Web service provided
by OMDB, except for Filename. Filename is the filename of the playback or media file
that is available by download using the streamer server. The filename can be used to access
the particular mp4 file. For example, if the streamer is running on port 8888 on the machine
hostname then the following url would cause the Minions Banana Song to be streamed:
http://hostname:8888/MinonsBananaSong.mp4
The filename will be used in your Android and iOS apps to cause the movie to be played by the
app.

Reference the following sources for background on these technologies:

JSON (JavaScript Object Notation):
 http://en.wikipedia.org/wiki/JSON
 The JSON web site: http://json.org/

JSON-RPC (JSON Remote Procedure Call):
 http://www.jsonrpc.org
 http://en.wikipedia.org/wiki/JSON-RPC

This example depends on the following frameworks:
1. Ant (although you can run the servers directly with java)
   see: http://ant.apache.org/
2. Json for the jdk as implemented by Doug Crockford.
   See: https://github.com/stleary/JSON-java

The server and streamer are deployed as separate stand alone Java applications (from
separate terminal windows).

No source code is included in this example. To run the example, you really don't
need to have Ant installed on your system, but both are executable through an
ant build file.

Streamer:
java -jar lib/tools.jar -port 8888 -dir MediaFiles -trees -verbose

JsonRPC Server:
java -jar lib/jsonrpcserver.jar 8080

To execute these using ant do the following from separate terminal windows in the
MovieLibraryJsonRPCServer folder:

ant execute.streamer

ant execute.server

This folder also contains sample calls to each of the JsonRPC server methods by including
shell scripts of curl commands that call each method. Since the projects are saved in a java
archive, you will need to add back execute permissions for these shell files with the command:

chmod u+x *.sh

After doing this you can cause the commands to run such as:

./sampleCurlGet.sh




