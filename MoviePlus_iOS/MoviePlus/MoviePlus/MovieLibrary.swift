//
//  MovieLibrary.swift
//  MoviePlus
//
//  Created by sgowdru on 2/22/16.
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

import Foundation

class MovieLibrary{
    var movName = [String]()
    var data : NSDictionary = ["":""]
    var fullMov : [String: AnyObject] = ["":""]
    
    
    //Load Data from Json and Parse it using dispatch async, task will be in suspended
    //state and therefore we use task.resume() method.  We can also see the closure call in dataTaskWithURL method
    //loadData is called by iOS application as soon as app is loaded
    func loadData(urlString : String, completions: (result:String, output:[String]) -> Void){
        
        //sharedSession() is a singleton object creator, it is shared by whole application
        let session = NSURLSession.sharedSession()
        let url = NSURL(string:urlString)!
        
        //closure call in dataTaskWithURL method
        let task = session.dataTaskWithURL(url){
            (data, response, error) -> Void in
            dispatch_async(dispatch_get_main_queue()){
                if error != nil {
                    completions(result : (error!.localizedDescription), output: [])
                }
                else{
                    do{
                        //The if-let-try of swift tries to set value, if failed goes to catch
                        //Exception and verifies, very useful
                        if let json = try NSJSONSerialization.JSONObjectWithData(data!,
                            options: .AllowFragments)
                            as? [String: AnyObject] {
                                self.fullMov = json
                                self.movName = self.showMovies(json)
                                completions(result: "JSONSerialization Successful",output: self.getMovNames())
                        }
                    }catch{
                        dispatch_async(dispatch_get_main_queue()){
                            completions(result: "Error in NSJSONSerialization",output: [])
                        }
                    }
                }
            }
        }
        task.resume()
        
    }
    
    //Has all the movie details stored in Dictionary of Strings
    func showMovies(mov:[String: AnyObject] ) -> [String]{
        var index : String
        var movNam : [String] = []
        for i in 1...10{
            index = (String)(i)
            data = mov[index]! as! NSDictionary
            movNam.append(data["Title"] as! String)
        }
        movName=movNam
        return movNam
    }
    
    //Getter method for getting just the movie names
    func getMovNames() -> [String]{
        return movName
    }
    
    //Getter method for getting every movie with its field description
    func getFullDic() -> [String: AnyObject]{
        return fullMov
    }
    
}