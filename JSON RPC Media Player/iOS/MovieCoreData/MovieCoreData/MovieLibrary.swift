//
//  MovieLibrary.swift
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

import Foundation
import CoreData
import UIKit


// Contains Model to call the background calls from Controller MovieLibraryController.swift
class MovieLibrary{
   
    var data = [String : String]()
    var fullMovList = [Int : [String : String]]()
    var appDel:AppDelegate?
    var mContext:NSManagedObjectContext?
    var arr = [String]()
    var movIndex = 1
    
    //Run as callback to load data from Core Data into the primary display as well loading movie names
    func loadCoreData(completions: (result:String, output:[String], fullMov: [Int : [String : String]]) -> Void){
        
        
        //Initialize entities and call the core data
        let fetchRequest = NSFetchRequest(entityName: "Movies")
        fetchRequest.returnsObjectsAsFaults = false
       
        let fetchRequestScore = NSFetchRequest(entityName: "MovieTitles")
        fetchRequestScore.returnsObjectsAsFaults = false
        
        var movFields = [String : String]()
        
        // These vars are used to access the Movies entities
        appDel = (UIApplication.sharedApplication().delegate as! AppDelegate)
        mContext = appDel!.managedObjectContext
        
        do{
            let movieList = try mContext!.executeFetchRequest(fetchRequest)
            //let movieListScore = try mContext!.executeFetchRequest(fetchRequestScore)
            //print(movieList)
            if movieList.count > 0 {
                for result in movieList as! [NSManagedObject] {
                    print(result.valueForKey("title")!)
                    arr.append(result.valueForKey("title")! as! String)
                    
                    //Retievev fields from core data for the movies
                    movFields["title"] = result.valueForKey("title")! as? String
                    movFields["plot"] = result.valueForKey("plot")! as? String
                    movFields["rated"] = result.valueForKey("rated")! as? String
                    movFields["actors"] = result.valueForKey("actors")! as? String
                    movFields["director"] = result.valueForKey("director")! as? String
                    movFields["genre"] = result.valueForKey("genre")! as? String
                    movFields["poster"] = result.valueForKey("poster")! as? String
                    
                    fetchRequestScore.predicate = NSPredicate(format: "title = %@", movFields["title"]!)
                    let movieListScore = try mContext!.executeFetchRequest(fetchRequestScore)
                    let v = movieListScore[0]
                    movFields["imdbRating"] = v.valueForKey("imdbscore")! as? String

                    fullMovList[movIndex] = movFields
                    movIndex++
                }

                // Call the completion handler
                completions(result: "JSONSerialization Successful", output: arr, fullMov: fullMovList)
            }
            else{
                print("No rows")
            }
        }
        catch let error as NSError{
            NSLog("error selecting Movies \(error)")
        }
        
    }

 }