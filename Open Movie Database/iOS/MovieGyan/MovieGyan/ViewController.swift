//
//  ViewController.swift
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

class ViewController: UIViewController,NetworkManagerDelegate  {
    var names, year, rated, released, runtime, genre, actors, plot:AnyObject?
    
    
    @IBOutlet weak var mov: UILabel!
    @IBOutlet weak var years: UILabel!
    @IBOutlet weak var ratedmov: UILabel!
    @IBOutlet weak var runtimemov: UILabel!
    @IBOutlet weak var releasedmov: UILabel!
    @IBOutlet weak var genremov: UITextView!
    @IBOutlet weak var actorsmov: UITextView!
    @IBOutlet weak var plotmov: UITextView!
    @IBAction func buttclick(sender: UIButton) {
        mov.text = names as! String
        years.text = year as! String
        ratedmov.text = rated as! String
        runtimemov.text = runtime as! String
        releasedmov.text = released as! String
        genremov.text = genre as! String
        actorsmov.text = actors as! String
        plotmov.text = plot as! String
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        //Create a class which will handle the fetching json details from the internet url
        let manager=NetworkController()
        manager.delegate=self
        manager.getInfo()
    }
    
    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func didReceiveResponse(info: [String : AnyObject]) {
        names=info["Title"]
        year=info["Year"]
        rated=info["Rated"]
        released=info["Released"]
        runtime=info["Runtime"]
        genre=info["Genre"]
        actors=info["Actors"]
        plot=info["Plot"]
        print("Name: \(names!)")
    }
    
    func didFailTOReceiveResponse() {
        print("There was an error")
    }
    
}

