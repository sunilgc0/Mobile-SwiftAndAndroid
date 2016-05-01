Author: Sunil Gowdru
Version: March 2016
Project: Ser423/Cse494 Mobile Computing Database For Movie

Instructions for using sqlite3
***************************************************
I. Creating and Populating the Database Using sqlite3 from the terminal

   1. Create a new database moviedb
      sqlite3 moviedb.db

   2. To populate the database from the file initcoursedb.sql
       sqlite>.read initMoviesDb.sql

   3. To Create a backup of the database int the file backupMovieDb.db
       sqlite>.backup main backupMovieDb.db
  
   4. To recover the database from the backup created above
       sqlite>.restore main backupMovieDb.db

   5. To query or modify the database, use any of the queries below from
      the sqlite> prompt. Be sure to end the sql statement with a semi-colon

   6. To exit sqlite interpreter
      sqlite>.quit

II Some Queries. Execute each from the sqlite prompt. How are they executed
   by a mobile (iOS or Android) app?
a. What are the Movies and their id numbers?
    select Title, movieId from Movies;

b. update Movie Genre entry for a movie
   Update Movies set Genre='Comedy' where Title='The Boy';

c. How many entries are there in the Movie table?
     SELECT COUNT(movieId) FROM Movies;
