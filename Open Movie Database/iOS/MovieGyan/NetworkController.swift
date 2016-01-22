//
//  NetworkController.swift
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
* reflection in creating json string of a Swift object.
*
* I hereby give the instructors, TA right to use of building and evaluating
* the software package for the purpose of determining your grade and program assessment.
*
* SER 598 - Mobile Systems
* @author Sunil Gowdru C
* mailto:sunil.gowdru@asu.edu
* Software Engineering, CIDSE, IAFSE, ASU Poly
* @version January 2016
*/

import UIKit

@objc protocol NetworkManagerDelegate{
    optional func didReceiveResponse(info: [String:AnyObject])
    optional func didFailTOReceiveResponse()
}
class NetworkController: NSObject, NSURLSessionDelegate {
    private let requestURL=NSURL(string: "http://www.omdbapi.com/?t=Frozen&y=&plot=short&r=json")
    var delegate:NetworkManagerDelegate?
    
    override init(){
        super.init()
    }
    
    func getInfo(){
        let defaultConfigObject=NSURLSessionConfiguration.defaultSessionConfiguration()
        let defaultSession=NSURLSession(configuration: defaultConfigObject, delegate: self, delegateQueue: NSOperationQueue.mainQueue())
        let request = NSMutableURLRequest(URL: requestURL!, cachePolicy: NSURLRequestCachePolicy.ReloadIgnoringCacheData, timeoutInterval: 60)
        
        request.HTTPMethod="POST"
        
        request.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        let datatask=defaultSession.dataTaskWithRequest(request,completionHandler: {
            (data:NSData?, response:NSURLResponse?, error:NSError?) in
            if let responseError=error{
                self.delegate?.didFailTOReceiveResponse?()
                print("Response Error: \(responseError)")
            } else{
                do{
                    let dictionary=try NSJSONSerialization.JSONObjectWithData(data!, options: NSJSONReadingOptions.MutableContainers) as! [String:AnyObject]
                    self.delegate?.didReceiveResponse?(dictionary)
                    print("Response: \(dictionary)")
                }
                catch let jsonError as  NSError{
                    self.delegate?.didFailTOReceiveResponse?()
                    print("JSON Error \(jsonError.localizedDescription)")
                }
            }
        })
        datatask.resume()
    }
    
}
