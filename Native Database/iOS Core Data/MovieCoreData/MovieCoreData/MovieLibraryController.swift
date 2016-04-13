//
//  MovieLibraryController.swift
//  MovieCoreData
//
//  Created by sgowdru on 4/6/16.
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

class FirstTableViewController: UITableViewController, MovieCreate {
    
    var cellContent = [String]()
    var arr = [String]()
    let api=MovieLibrary()
    var fullMov = [Int: [String : String]]()
    
    var appDel:AppDelegate?
    var mContext:NSManagedObjectContext?

    //Outlet for the current Table View
    @IBOutlet var myTable: UITableView!
    

    //loadData is called by iOS application as soon as view is loaded, it has completionHandler which execute didLoadData() when loadData() is completed
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        // These vars are used to access the Movie entities
        appDel = (UIApplication.sharedApplication().delegate as! AppDelegate)
        mContext = appDel!.managedObjectContext
        
        //Load from locally stored Core Data
        api.loadCoreData(didLoadData)
        
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
    
    // MARK: - Table view data source
    
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
