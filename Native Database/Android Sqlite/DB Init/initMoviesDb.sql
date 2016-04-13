DROP TABLE if exists Movies;

CREATE TABLE Movies (
  Title TEXT,
  Years TEXT,
  Rated TEXT,
  Genre TEXT,
  Actors TEXT,
  Released Text,
  Plot TEXT,
  MovieId INTEGER PRIMARY KEY AUTOINCREMENT);

INSERT INTO Movies (Title, Years, Rated, Genre, Actors, Released, Plot) VALUES
   ("Kung Fu Panda 3","2016","PG", "Animation", "Jack Black, Angelina Jolie, Dustin Hoffman, Jackie Chan", "29 Jan 2016", "Continuing his legendary adventures of awesomeness, Po must face two hugely epic, but different threats: one supernatural and the other a little closer to his home.");
   
INSERT INTO Movies (Title, Years, Rated, Genre, Actors, Released, Plot) VALUES
   ( "The Revenant", "2015",  "R", "Action", "Leonardo DiCaprio, Tom Hardy, Domhnall Gleeson, Will Poulter", "08 Jan 2016", "A frontiersman on a fur trading expedition in the 1820's fights for survival after being mauled by a bear and left for dead by members of his own hunting team.");
   

INSERT INTO Movies (Title, Years, Rated, Genre, Actors, Released, Plot) VALUES
   ( "Star Wars The Force Awakens", "2015", "PG-13", "Comedy", "Harrison Ford, Mark Hamill, Carrie Fisher, Adam Driver", "18 Dec 2015", "Three decades after the defeat of the Galactic Empire, a new threat arises. The First Order attempts to rule the galaxy and only a ragtag group of heroes can stop them, along with the help of the Resistance.");
   
INSERT INTO Movies (Title, Years, Rated, Genre, Actors, Released, Plot) VALUES
   ("The Boy", "2016", "PG-13", "Horror", "Lauren Cohan, Rupert Evans, James Russell, Jim Norton", "22 Jan 2016", "An American nanny is shocked that her new English family's boy is actually a life-sized doll. After she violates a list of strict rules, disturbing events make her believe that the doll is really alive.");
   
   
