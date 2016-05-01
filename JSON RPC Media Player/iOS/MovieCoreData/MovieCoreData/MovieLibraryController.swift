//
//  MovieLibraryController.swift
//  MovieCoreData
//
//  Created by sgowdru on 4/21/16.
//  Copyright © 2016 sgowdru. All rights reserved.
//
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
* Purpose: Showcase the RPC server and Core data support in iOS, how to CRUD , HOW to stream Media player in the app
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
* @version 4/21/16
*/


import UIKit
import CoreData

class FirstTableViewController: UITableViewController, MovieCreate {
    
    var cellContent = [String]()
    var arr = [String]()
    let api=MovieLibrary()
    var fullMov = [Int: [String : String]]()
    var urlString:String = String()
    var movies:[String] = [String]()

    // Define required for working with core data
    var movieManagedObject = [NSManagedObject]()
    var appDel:AppDelegate?
    var mContext:NSManagedObjectContext?
    
    //Outlet for the current Table View
    @IBOutlet var myTable: UITableView!
    
    
    var server_host:String?
    var server_port:String?

    //loadData is called by iOS application as soon as view is loaded, it has completionHandler which execute didLoadData() when loadData() is completed
    override func viewDidLoad() {
        super.viewDidLoad()
        
        //From the plist we can load the parameters for Server host and port, in file Server.plist
        if let path = NSBundle.mainBundle().pathForResource("Server", ofType: "plist"){
            if let dict = NSDictionary(contentsOfFile: path) as? [String:AnyObject] {
                server_host = dict["server_host"] as? String
                server_port = dict["server_port"] as? String
            }
        }
        
        // These vars are used to access the Movie entities
        appDel = (UIApplication.sharedApplication().delegate as! AppDelegate)
        mContext = appDel!.managedObjectContext
        
        //Form UrlString to connect to the server
        urlString = "http://"+server_host!+":"+server_port!
  
        //Launch this snippet the first time the app is installed in the mobile to retieve the Movie details from
        //RPC Server and store in Core Data
        //Uses a chain of callbacks, first callback retreives the names of movies
        //Second Chain retrieves its full details using names from previous callback and loads to Core Data
        let launchedBefore = NSUserDefaults.standardUserDefaults().boolForKey("launchedBefore")
        if launchedBefore  {
            print("Not first launch.")
        }
        else {
            print("First launch, setting NSUserDefault.")
            NSUserDefaults.standardUserDefaults().setBool(true, forKey: "launchedBefore")
            
            //didGetNames is the callback handler for callGetNames, which retrieves the names of the movies
            callGetNames(didGetNames)
            
        }//Remember that all of the above snippet is executed only once when app is installed to load RPC
  
        //Load from locally stored Core Data
        api.loadCoreData(didLoadData)
    }
    
    func callGetNames(completion:(result : [String]) -> Void){
        let aConnect:MovieCollectionStub = MovieCollectionStub(urlString: urlString)
         aConnect.getNames({ (res: String, err: String?) -> Void in
            if err != nil {
                NSLog(err!)
            }else{
                NSLog(res)
                if let data: NSData = res.dataUsingEncoding(NSUTF8StringEncoding){
                    do{
                        let dict = try NSJSONSerialization.JSONObjectWithData(data,options:.MutableContainers) as?[String:AnyObject]
                        self.movies = (dict!["result"] as? [String])!
                        //on complention call parameters passed consisting of movie title array
                        completion(result: self.movies)
                    } catch {
                        print("unable to convert to dictionary")
                    }
                }
                
            }
        })
    }
    
    
    //Callback handler for callGetNames, it chains to callGetMovieDetails
    func didGetNames(movList:[String]){
        NSLog("Total Number of Movies in RPC is " + String(movies.count))
        //In for loop we call for each of the movie titles and load the details into core data
        for i in self.movies{
            NSLog("Loaded Movie Name \(i) from RPC Server")
            callGetMovieDetails(i, completion: didLoadMovObject)
        }
    }
    
    //With callGetMovieDetails get Json Object of the movie
    //parse our nodes and store it in the core data
    func callGetMovieDetails(title:String, completion:(movObject:[String : String]) -> Void){
        let aConnect:MovieCollectionStub = MovieCollectionStub(urlString: urlString)
         aConnect.get(title, callback: { (res: String, err: String?) -> Void in
            if err != nil {
                NSLog(err!)
            }else{
                NSLog(res)
                if let data: NSData = res.dataUsingEncoding(NSUTF8StringEncoding){
                    do{
                        let jsonObject = try NSJSONSerialization.JSONObjectWithData(data,options:.MutableContainers) as?[String:AnyObject]
                        let movObj = (jsonObject!["result"] as? [String : String])!
                        
                        //Call the callback function with parameters
                        completion(movObject: movObj)
                    } catch {
                        print("unable to convert to dictionary")
                    }
                }
                
            }
        })  // end of method call to getNames
    }

    //With callGetMovieDetails get Json Object of the movie
    //parse our nodes and store it in the core data
    func didLoadMovObject(movObject : [String : String]){
        NSLog(movObject["Filename"]!)
        
        let entity = NSEntityDescription.entityForName("Movies", inManagedObjectContext: mContext!)
        let movie = NSManagedObject(entity: entity!, insertIntoManagedObjectContext: mContext)
        
        
        //Get the movie database and update in core data
        let fetchRequest = NSFetchRequest(entityName: "Movies")
        fetchRequest.returnsObjectsAsFaults = false
        do{
            //Some values are "NA" as instructor provided JSON does not give these values but are essential to maintain
            //commonality between older version of apps and handle android app as well
            movie.setValue(movObject["Title"]!, forKey: "title")
            movie.setValue("NA", forKey: "director")
            movie.setValue(movObject["Actors"]!, forKey: "actors")
            movie.setValue(movObject["Genre"]!, forKey: "genre")
            movie.setValue(movObject["Rated"]!, forKey: "rated")
            movie.setValue(movObject["Plot"]!, forKey: "plot")
            movie.setValue("NA", forKey: "poster")
            movie.setValue(movObject["Filename"]!, forKey: "videoFilename")
            
            //Save the managed object
            try mContext!.save()
            //Append to database
            movieManagedObject.append(movie)
        }
        catch let error as NSError{
            NSLog("error selecting all Movies Core Data table \(error)")
        }

        //We had two tables defined in earlier app with second tables consisting of imdb score and movie name
        let movieScoreEntity = NSEntityDescription.entityForName("MovieTitles", inManagedObjectContext: mContext!)
        let movieScore = NSManagedObject(entity: movieScoreEntity!, insertIntoManagedObjectContext: mContext)
        
        do{
            movieScore.setValue(movObject["Title"], forKey: "title")
            movieScore.setValue("NA", forKey: "imdbscore")
            
            //Save the managed object
            try mContext!.save()
            //Append to database
            movieManagedObject.append(movieScore)
        }
        catch let error as NSError{
            NSLog("error selecting in MovieTitles Core Data table \(error)")
        }
        
        
        let selectRequestScore = NSFetchRequest(entityName: "MovieTitles")
        do{
            let results = try mContext!.executeFetchRequest(selectRequestScore)
            if results.count == 3 {
                //Load from locally stored Core Data
                api.loadCoreData(didLoadData)
                myTable.reloadData()
            }
        }catch let error as NSError{
            NSLog("error selecting all movie \(error)")
        }
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didLoadData(result:String, output: [String], fullMovList: [Int : [String : String]]) -> Void{
        cellContent = output
        fullMov =  fullMovList
        myTable.reloadData()
    }
    
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        //Number of sections in the app, just one
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        //number of entries
        return cellContent.count
    }
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell{
        let cell = tableView.dequeueReusableCellWithIdentifier("CellFirst", forIndexPath: indexPath)
        cell.textLabel?.text = cellContent[indexPath.row]
        
        return cell
    }
    
    
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    
    
    func creatingOrEditingMovie(newMov : [String : String], rowNum: Int, viewingExistingMov: Bool){
        
        //If we are editing an existing movie
        if(viewingExistingMov){
            //delete current value and update it with the new edited valuenewMovie
            print("Editing existing movie")
            fullMov[rowNum] = newMov
        }
        else{
            //Add the new movies title and the object itself and the refresh it
            print("Adding new movie")
            cellContent.append(newMov["title"]!)
            fullMov[rowNum] = newMov
        }
        //reload table entries
        myTable.reloadData()
    }

    
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        
        // If movie is deleted by left swipe style
        if editingStyle == .Delete {
            // Delete the row from the data source
            let aMovName:String = cellContent[indexPath.row]
            let selectRequest = NSFetchRequest(entityName: "Movies")
            //Set the condition or predicate (not always necessary will return everything if not specified
            selectRequest.predicate = NSPredicate(format: "title == %@", aMovName)
            do{
                let results = try mContext!.executeFetchRequest(selectRequest)
                if results.count > 0 {
                    //delete the object from NSManaged object
                    mContext!.deleteObject(results[0] as! NSManagedObject)
                    // save the changes
                    try mContext?.save()
                }
                else{
                    print("No rows")
                }
            } catch let error as NSError{
                NSLog("error selecting all movie \(error)")
            }
            
            let selectRequestScore = NSFetchRequest(entityName: "MovieTitles")
            //Set the condition or predicate (not always necessary will return everything if not specified
            selectRequestScore.predicate = NSPredicate(format: "title == %@", aMovName)
            do{
                let results = try mContext!.executeFetchRequest(selectRequestScore)
                if results.count > 0 {
                    //delete the object from NSManaged object
                    mContext!.deleteObject(results[0] as! NSManagedObject)
                    // save the changes
                    try mContext?.save()
                }
                else{
                    print("No rows")
                }
            } catch let error as NSError{
                NSLog("error selecting all movie \(error)")
            }

            //Find the key to show the movie, as keys get dynamically updated as addition and deletions are in any ways
            var properIndex = 0
            if(cellContent.contains(aMovName)){
                let v = cellContent.indexOf(aMovName)
                let selectedMov = cellContent[v!]
                
                let vd  = fullMov.keys
                
                var pair = [String : String]()
                for movi in vd{
                    pair =  fullMov[movi]!
                    if(pair["title"] == selectedMov){
                        properIndex = movi
                    }
                }
            }
            
            //remove movie from full movie dictionary
            fullMov.removeValueForKey(properIndex)
            //remove movie from array of movie names
            cellContent.removeAtIndex(indexPath.row)

            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
            myTable.reloadData()
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }
    }
    
   
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation */
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        //Are you viewing the existing movie
        
        if segue.identifier == "showMov"{
            //define a variable "navigationController" which will be useful in navigating to the said class
            let navigationController = segue.destinationViewController as! MvDesc
            //What is the index of current row selected from the table
            let selectedIndex = self.tableView.indexPathForSelectedRow

            //Find the key to show the movie, as keys get dynamically updated as addition and deletions are in any ways
            let v = (selectedIndex?.row)!
            let selectedMov = cellContent[v]
            let vd  = fullMov.keys
            var properIndex = 0
            var pair = [String : String]()
            for movi in vd{
                    pair =  fullMov[movi]!
                    if(pair["title"] == selectedMov){
                        properIndex = movi
                }
            }
            
            //Set the value to segue variable declared in next class
            navigationController.movieObject = cellContent
            navigationController.rowNum = properIndex
            navigationController.fullMovDic = fullMov
            //define a deligate object of type MovieLibraryController in MovieDescriptionController class and  then set it current instance of MovieLibraryController
            navigationController.delegateMain=self
        }
            //Are you adding the a new movie
        else if segue.identifier == "add"{
            print("In add segue")
            //define a variable "add" which will be useful in navigating to the said class
            let add = segue.destinationViewController as! MvDesc
            add.viewingExistingMov = false
            //define a deligate object of type MovieLibraryController in MovieDescriptionController class and  then set it current instance of MovieLibraryController
            add.delegateMain=self
            
            //Generate key for inserting new movie, keys get dynamically generated as addition and deletions are in any ways and depending on available keys
            let vd  = fullMov.keys
            var i = 1
            if(vd.count != 0){
                for _ in 1...vd.count{
                   if(vd.contains(i)){
                     i++
                 }
               }
            }
            //set the row key to insert and pass it
            add.rowNum = i
            
        }
    }
}
