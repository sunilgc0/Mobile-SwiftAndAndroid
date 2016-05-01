//
//  MovieDescription.swift
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

class MovieDescription{
    var movName = [String]()
    var data = [ String : String]()
    var fullMov = [ String : String]()
    
    // Contains Model to call the background calls from Controller MovieDescriptionController.swift
    // Calls the background task to call the URL for a movie searched and retreive it
    func loadURLData(urlString : String, completions: (result:String, output:[String : String]) -> Void){

        //sharedSession() is a singleton object creator, it is shared by whole application
        let session = NSURLSession.sharedSession()
        let url = NSURL(string:urlString)!
        
        //closure call in dataTaskWithURL method
        let task = session.dataTaskWithURL(url){
            (data, response, error) -> Void in
            dispatch_async(dispatch_get_main_queue()){
                if error != nil {
                    completions(result : (error!.localizedDescription), output : [ : ])
                }
                else{
                    do{
                        //The if-let-try of swift tries to set value, if failed goes to catch
                        //Exception and verifies, very useful
                        if let json = try NSJSONSerialization.JSONObjectWithData(data!,
                            options: .AllowFragments)
                            as? [String : String] {
                                // Call completion handler for successful json retieval
                                completions(result: "JSONSerialization Successful",output : json)
                        }
                    }catch{
                        dispatch_async(dispatch_get_main_queue()){
                            completions(result: "Error in NSJSONSerialization",output : [ : ])
                        }
                    }
                }
            }
        }
        task.resume()
    }
    
    // Contains Model to call the background calls from Controller MovieDescriptionController.swift
    // Calls the background task to retrive the movie poster from its poster url in json
    func loadImage(urlString : String, completions: (result:String, output: NSData) -> Void){
        
        //
        let url = NSURL(string:urlString)!
        //Load the content of image
        let data = NSData(contentsOfURL:url)
        if data != nil {
            //call the completion handler back with image
            completions(result: "Image loaded", output: data!)
        }
    }

}