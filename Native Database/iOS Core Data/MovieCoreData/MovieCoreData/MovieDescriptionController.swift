//
//  MovieDescriptionController.swift
//  MovieCoreData
//
//  Created by sgowdru on 4/6/16.
//  Copyright © 2016 sgowdru. All rights reserved.

/*
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
* Purpose: Showcase the Core data support in iOS, how to retieve, store, edit, remove, add manually
* This example shows the use of Swift
* And use MVC model to generate views using segues
* Show persistent storage
* I hereby give the instructors, TA right to use of building and evaluating
* the software package for the purpose of determining your grade and program assessment.
*
* SER 598 - Mobile Systems
* @author Sunil Gowdru C
* mailto:sunil.gowdru@asu.edu
* Software Engineering, CIDSE, IAFSE, ASU Poly
* @version 4/09/16
*/

import UIKit
import CoreData

//Define Custom Protocol which will be implemented by the MovieLibraryController
//function creatingMovie should be implemented by all classes which implement this protocol
protocol MovieCreate{
    func creatingOrEditingMovie(newMov : [String:String], rowNum : Int,viewingExistingMov: Bool)
}

class MvDesc: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate, UISearchBarDelegate {
    
    var movieObject = [String]()
    var rowNum = 0
    let api=MovieDescription()
    var fullMovDic = [Int : [String : String]]()
    var movDesc = [ String :  String]()
    var viewingExistingMov = true
    var delegateMain:MovieCreate!
    var newMovie = [String : String]()
    
    // Define required for working with core data
    var movieManagedObject = [NSManagedObject]()
    var appDel:AppDelegate?
    var mContext:NSManagedObjectContext?
    

    //dOutlet for the Save button after creating new movie
    //@IBOutlet weak var saveButton: UIBarButtonItem!
    
    @IBOutlet weak var saveButton: UIBarButtonItem!
    //Outlet for textfields
    @IBOutlet weak var movTitle: UITextField!
    @IBOutlet weak var director: UITextField!
    @IBOutlet weak var actors: UITextView!
    @IBOutlet weak var genre: UITextField!
    @IBOutlet weak var rated: UITextField!
    @IBOutlet weak var plot: UITextView!
    
    @IBOutlet weak var imdbRating: UITextField!
      //Outlet for the picker
    @IBOutlet weak var picker: UIPickerView!
    
    @IBOutlet weak var imageView: UIImageView!
  
    @IBOutlet weak var searchBar: UISearchBar!
    //Data source for Genre
    var pickerDataSource = ["Animation", "Action", "Adventure", "Drama", "Thriller", "Comedy", "Documentary"];
    var searchActive : Bool = false
    var posterUrl : String = ""
    //
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        //Set picker outlet datasource and delegate to self
        picker.dataSource = self;
        picker.delegate = self;
        
        //set searchbar delegate to this class and show cancel button on search bar
        searchBar.delegate = self
        searchBar.showsCancelButton =  true
       
        
        // These vars are used to access the Movie Entities
        appDel = (UIApplication.sharedApplication().delegate as! AppDelegate)
        mContext = appDel!.managedObjectContext
        
        //If we are just viewing an existing movie
        if(viewingExistingMov){
            movDesc = fullMovDic[rowNum]! 
            movTitle.text = movDesc["title"]
            director.text = movDesc["director"]
            actors.text = movDesc["actors"]
            genre.text = movDesc["genre"]
            rated.text = movDesc["rated"]
            plot.text = movDesc["plot"]
            imdbRating.text = movDesc["imdbRating"]
            posterUrl = movDesc["poster"]!
            
            //Load the poster image from the url stored
            api.loadImage(posterUrl, completions: didLoadImage)
            
            //While viewing existing movie, there will be no search
            searchBar.userInteractionEnabled = false
            
        }
        else{
            
            //you can add your own movie or search from OMDB
            movTitle.enabled = true
            genre.text = pickerDataSource[0]
            saveButton.enabled = false
        }
        
        //Track changes made in movie title field to do some actions in textFieldDidChange method
        movTitle.addTarget(self, action: "textFieldDidChange:", forControlEvents: UIControlEvents.AllEvents)
    }
    
    //Don't let movie with no title, so disabled save
    func textFieldDidChange(textField: UITextField) {
        if(movTitle.text!.isEmpty){
         saveButton.enabled = false
        }
        else{
            saveButton.enabled = true
        }
    }
    
    //Called when you want to Save is clicked
    @IBAction func createMovie(sender: AnyObject) {

        newMovie["title"] = movTitle.text!
        newMovie["director"] = director.text!
        newMovie["actors"] = actors.text!
        newMovie["genre"] = genre.text!
        newMovie["rated"] = rated.text!
        newMovie["plot"] = plot.text!
        newMovie["imdbRating"] = imdbRating.text!
        newMovie["poster"] = posterUrl
        
        // If it is a new movie or newly searched movie it is added to core data
        if(movTitle.enabled){
            let entity = NSEntityDescription.entityForName("Movies", inManagedObjectContext: mContext!)
            let movie = NSManagedObject(entity: entity!, insertIntoManagedObjectContext: mContext)

            
            //Get the movie database and update in core data
            let fetchRequest = NSFetchRequest(entityName: "Movies")
            fetchRequest.returnsObjectsAsFaults = false
            do{
                movie.setValue(movTitle.text!, forKey: "title")
                movie.setValue(director.text!, forKey: "director")
                movie.setValue(actors.text!, forKey: "actors")
                movie.setValue(genre.text!, forKey: "genre")
                movie.setValue(rated.text!, forKey: "rated")
                movie.setValue(plot.text!, forKey: "plot")
                movie.setValue(posterUrl, forKey: "poster")
                
                //Save the managed object
                try mContext!.save()
                //Append to database
                movieManagedObject.append(movie)
            }
            catch let error as NSError{
                NSLog("error selecting all smovies \(error)")
            }
            
            
            
            
            let movieScoreEntity = NSEntityDescription.entityForName("MovieTitles", inManagedObjectContext: mContext!)
            let movieScore = NSManagedObject(entity: movieScoreEntity!, insertIntoManagedObjectContext: mContext)
   

            do{
                movieScore.setValue(movTitle.text!, forKey: "title")
                movieScore.setValue(imdbRating.text!, forKey: "imdbscore")
                
                //Save the managed object
                try mContext!.save()
                //Append to database
                movieManagedObject.append(movieScore)
            }
            catch let error as NSError{
                NSLog("error selecting all smovies \(error)")
            }
            
            
        }
           
        // If it is an existing movie in core data, movie is updated
        else{
            let fetchRequest = NSFetchRequest(entityName: "Movies")
            //Searched with the predicate of the movie
            fetchRequest.predicate = NSPredicate(format: "title = %@", movTitle.text!)
            //
            do{
                let movieList = try mContext!.executeFetchRequest(fetchRequest)
                //count = movieList.count
                let movie = movieList[0]
                movie.setValue(movTitle.text!, forKey: "title")
                movie.setValue(director.text!, forKey: "director")
                movie.setValue(actors.text!, forKey: "actors")
                movie.setValue(genre.text!, forKey: "genre")
                movie.setValue(rated.text!, forKey: "rated")
                movie.setValue(plot.text!, forKey: "plot")
                
                //Dont append any new movie, just save the context, it will update
                try mContext!.save()
            }
            catch let error as NSError{
                NSLog("error selecting all Movies\(error)")
            }
            
                      
            //Get the movie database and update in core data
            let fetchRequestScore = NSFetchRequest(entityName: "MovieTitles")
            fetchRequestScore.predicate = NSPredicate(format: "title = %@", movTitle.text!)
            fetchRequestScore.returnsObjectsAsFaults = false
            do{
                let movieLists = try mContext!.executeFetchRequest(fetchRequestScore)
                let movies = movieLists[0]
                movies.setValue(movTitle.text!, forKey: "title")
                movies.setValue(imdbRating.text!, forKey: "imdbscore")
                
                //Save the managed object
                try mContext!.save()
                //Append to database
                //movieManagedObject.append(movie)
            }
            catch let error as NSError{
                NSLog("error selecting all smovies \(error)")
            }

        }
        
        //if(viewingExistingMov)
        //Call the delegate, which is in this case in main view controller
        delegateMain.creatingOrEditingMovie(newMovie, rowNum: rowNum, viewingExistingMov: viewingExistingMov)
        
        //Pop your view out from child view to its parent view
        navigationController?.popToRootViewControllerAnimated(true)
    }

    
    
    // touch events on this view. Get rid of keyboards (pickers) when the user touches outside any
    // control in the view.
    override func touchesBegan(touches: Set<UITouch>, withEvent event: UIEvent?) {
        self.movTitle.resignFirstResponder()
        self.director.resignFirstResponder()
        self.actors.resignFirstResponder()
        self.genre.resignFirstResponder()
        self.rated.resignFirstResponder()
        self.plot.resignFirstResponder()
        self.imdbRating.resignFirstResponder()
        self.searchBar.resignFirstResponder()
    }
    
    
    //Number of component of picker, we have one component in our picker
    func numberOfComponentsInPickerView(pickerView: UIPickerView) -> Int{
        return 1
    }
    
    //Number of rows in our datasource
    func pickerView(pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int{
        return pickerDataSource.count
    }
    
    
    //return the data row from pickerDataSource
    func pickerView(pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String?{
        return pickerDataSource[row]
    }
    
    //Set the Genre textField to the value selected from picker once it is selected
    func pickerView(pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int){
        genre.text = pickerDataSource[row]
        
    }
    
    //When search button is clicked, call new thread to do the background and recieve the callback
    func searchBarSearchButtonClicked(searchBar: UISearchBar) {
        searchActive = true
        print(searchBar.text! + " Movie Searched")
        let nameWithSpace = searchBar.text!
        //Escape characters and replce to be placed in URL
        let searchName = nameWithSpace.stringByAddingPercentEncodingWithAllowedCharacters(NSCharacterSet.URLFragmentAllowedCharacterSet())!
        //call the thread and wait for callback completions
        api.loadURLData("http://www.omdbapi.com/?t="+searchName+"&y=&plot=short&r=json", completions:didLoadMovie)
        self.searchBar.resignFirstResponder()
        
    }
    
    //If user clicks cancel on search, empty the search text
    func searchBarCancelButtonClicked(searchBar: UISearchBar) {
        searchActive = true
        searchBar.text = nil
    }
    
    //Completion handler for loadURLData, if the Response is true that i movie exists in OMDB store and populate fields
    //If Response if False, display alert message
    func didLoadMovie(result:String, output: [String : String]) -> Void{
        if(output["Response"] == "True"){
         movTitle.text = output["Title"]!
         director.text = output["Director"]!
         actors.text = output["Actors"]!
         genre.text = output["Genre"]!
         rated.text = output["Rated"]!
         plot.text = output["Plot"]!
         imdbRating.text = output["imdbRating"]!
        
        //Enable user to save the new searched movie
         saveButton.enabled = true
            
        //Load the poster image in background, define new callback
         posterUrl = output["Poster"]!
         api.loadImage(output["Poster"]!, completions:didLoadImage)
        }
            
        //Display alert message letting user know there is no such movie in OMDB, user can add it manually if they want
        else{
            let alert = UIAlertController(title: "Oops!", message: "No matching movie found in omdb, try using different name", preferredStyle: UIAlertControllerStyle.Alert)
            alert.addAction(UIAlertAction(title: "OK", style: UIAlertActionStyle.Default, handler: nil))
            self.presentViewController(alert, animated: true, completion: nil)
        }
    }
    
    
    //completion handler for loadImage
    func didLoadImage(result:String, data:NSData){
        //print(result)
        imageView.image = UIImage(data: data)
    }
    
}
