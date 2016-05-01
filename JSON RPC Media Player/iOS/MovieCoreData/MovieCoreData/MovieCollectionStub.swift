//
//  MovieCollectionStub.swift
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
import Foundation

class MovieCollectionStub: NSObject {
    
    static var id:Int = 0
    var url:String
    init(urlString: String){
        self.url = urlString
    }
    
    // asyncHttpPostJson creates and posts a URLRequest that attaches a JSONRPC request as an NSData object
    func asyncHttpPostJSON(url: String,  data: NSData, callback: (String, String?) -> Void) {
        
        let request = NSMutableURLRequest(URL: NSURL(string: url)!)
        request.HTTPMethod = "POST"
        request.addValue("application/json",forHTTPHeaderField: "Content-Type")
        request.addValue("application/json",forHTTPHeaderField: "Accept")
        request.HTTPBody = data
        sendHttpRequest(request, callback: callback)
    }

    // sendHttpRequest
    func sendHttpRequest(request: NSMutableURLRequest,
        callback: (String, String?) -> Void) {
            // task.resume causes the shared session http request to be posted in the background (non-UI Thread)
            // the use of the dispatch_async on the main queue causes the callback to be performed on the UI Thread
            // after the result of the post is received.
            let task = NSURLSession.sharedSession().dataTaskWithRequest(request) {
                (data, response, error) -> Void in
                if (error != nil) {
                    callback("", error!.localizedDescription)
                } else {
                    dispatch_async(dispatch_get_main_queue(),
                        {callback(NSString(data: data!,
                            encoding: NSUTF8StringEncoding)! as String, nil)})
                }
            }
            task.resume()
    }
    
    
    //Get the method stub to get the movie list
    func getNames(callback: (String, String?) -> Void) -> Bool{
        print("In getTitles")
        var ret:Bool = false
        MovieCollectionStub.id = MovieCollectionStub.id + 1
        do {
            let dict:[String:AnyObject] = ["jsonrpc":"2.0", "method":"getTitles", "params":[ ], "id":MovieCollectionStub.id]
            let reqData:NSData = try NSJSONSerialization.dataWithJSONObject(dict, options: NSJSONWritingOptions(rawValue: 0))
            self.asyncHttpPostJSON(self.url, data: reqData, callback: callback)
            ret = true
        } catch let error as NSError {
            print(error)
        }
        return ret
    }
    
    
    //Get the method stub to get the information of movies
    func get(name:String, callback: (String, String?) -> Void) -> Bool{
        print("In get")
        var ret:Bool = false
        MovieCollectionStub.id = MovieCollectionStub.id + 1
        do {
            let dict:[String:AnyObject] = ["jsonrpc":"2.0", "method":"get", "params":[name], "id":MovieCollectionStub.id]
            let reqData:NSData = try NSJSONSerialization.dataWithJSONObject(dict, options: NSJSONWritingOptions(rawValue: 0))
            self.asyncHttpPostJSON(self.url, data: reqData, callback: callback)
            ret = true
        } catch let error as NSError {
            print(error)
        }
        return ret
    }
    
}
