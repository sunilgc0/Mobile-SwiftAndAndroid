//
//  FirstTableViewController.swift
//  MoviePlus
//
//  Created by sgowdru on 2/21/16.
//  Copyright © 2016 sgowdru. All rights reserved.
//
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
* Purpose: Example classes conversion to/from json
* This example shows the use of Swift
* And use MVC model to generate views using segues
*
* I hereby give the instructors, TA right to use of building and evaluating
* the software package for the purpose of determining your grade and program assessment.
*
* SER 598 - Mobile Systems
* @author Sunil Gowdru C
* mailto:sunil.gowdru@asu.edu
* Software Engineering, CIDSE, IAFSE, ASU Poly
* @version 2/22/16
*/

import UIKit

class FirstTableViewController: UITableViewController, MovieCreate {

    var cellContent = [""]
    let api=MovieLibrary()
    var fullMov = [String: AnyObject]()
    
    //Outlet for the current Table View
    @IBOutlet var myTable: UITableView!
    
    //Created new Movie or editing the existing movie
    func creatingOrEditingMovie(newMov : [String : String], rowNum: Int, viewingExistingMov: Bool){
        
        //If we are editing an existing movie
        if(viewingExistingMov){
            //delete current value and update it with the new edited value
            fullMov.removeValueForKey((String)(rowNum))
            fullMov[(String)(rowNum)] = newMov
        }
        else{
            //Add the new movies title and the object itself and the refresh it
            cellContent.append(newMov["Title"]!)
            fullMov[(String)(cellContent.count)] = newMov
            myTable.reloadData()
        }
    }
    
    //Load Data from Json and Parse it using dispatch async, task will be in suspended
    //state and therefore we use task.resume() method.  We can also see the closure call in dataTaskWithURL method
    //loadData is called by iOS application as soon as view is loaded, it has completionHandler which execute didLoadData() when loadData() is completed
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        api.loadData("https://gist.githubusercontent.com/sunilgc0/31efe60e0792ffefa009/raw/756f66ec1f00ee68cea7fad5122978bf7ab39b6c/omdb.json",completions:didLoadData)
        
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didLoadData(result:String, output: [String]) -> Void{
        cellContent=output
        fullMov =  api.getFullDic()
        myTable.reloadData()
    }

    // MARK: - Table view data source

    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        // #warning Incomplete implementation, return the number of sections
        return 1
    }

    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        // #warning Incomplete implementation, return the number of rows
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
    

    
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            cellContent.removeAtIndex(indexPath.row)
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }
        myTable.reloadData()
    }

    
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }


    /*
    // MARK: - Navigation

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
            navigationController.movieObject = cellContent
            navigationController.rowNum = (selectedIndex?.row)! + 1
            navigationController.fullMovDic = fullMov
            //define a deligate object of type MovieLibraryController in MovieDescriptionController class and  then set it current instance of MovieLibraryController
            navigationController.delegateMain=self
        }
      //Are you adding the a new movie
        else if segue.identifier == "add"{
            //define a variable "add" which will be useful in navigating to the said class
            let add = segue.destinationViewController as! MvDesc
             add.viewingExistingMov = false
            //define a deligate object of type MovieLibraryController in MovieDescriptionController class and  then set it current instance of MovieLibraryController
             add.delegateMain=self
             add.rowNum = cellContent.count + 1
        }
    }


}
