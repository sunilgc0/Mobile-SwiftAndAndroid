//
//  MvDesc.swift
//  MoviePlus
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

//Define Custom Protocol which will be implemented by the MovieLibraryController
//function creatingMovie should be implemented by all classes which implement this protocol
protocol MovieCreate{
    func creatingOrEditingMovie(newMov : [String:String], rowNum : Int,viewingExistingMov: Bool)
}

class MvDesc: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {

    var movieObject = [String]()
    var rowNum = 0
    let api=MovieLibrary()
    var fullMovDic = [String: AnyObject]()
    var movDesc = ["":""]
    var viewingExistingMov = true
    var delegateMain:MovieCreate!
    var newMovie = [String:String]()

    //Outlet for the Save button after creating new movie
    @IBOutlet weak var saveButton: UIBarButtonItem!
    
    //Outlet for the Texfields
    @IBOutlet weak var movTitle: UITextField!
    @IBOutlet weak var director: UITextField!
    @IBOutlet weak var actors: UITextView!
    @IBOutlet weak var genre: UITextField!
    @IBOutlet weak var rated: UITextField!
    @IBOutlet weak var plot: UITextView!
    
    //Outlet for the picker
    @IBOutlet weak var picker: UIPickerView!
    //Data source for Genre
    var pickerDataSource = ["Animation", "Action", "Adventure", "Drama", "Thriller", "Comedy"];
    
    //
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        //Set picker outlet datasource and delegate to self
        picker.dataSource = self;
        picker.delegate = self;
        
        //If we are just viewing an existing movie
        if(viewingExistingMov){
        movDesc = fullMovDic["\(rowNum)"]! as! [String:String]
        movTitle.text = movDesc["Title"]
        director.text = movDesc["Director"]
        actors.text = movDesc["Actors"]
        genre.text = movDesc["Genre"]
        rated.text = movDesc["Rated"]
        plot.text = movDesc["Plot"]
        
        //saveButton.enabled = false
        }
        else{
            movTitle.enabled = true
            genre.text = pickerDataSource[0]
        }
    }
    

    @IBAction func createMovie(sender: AnyObject) {
        
        newMovie["Title"] = movTitle.text!
        newMovie["Director"] = director.text!
        newMovie["Actors"] = actors.text!
        newMovie["Genre"] = genre.text!
        newMovie["Rated"] = rated.text!
        newMovie["Plot"] = plot.text!

        //if(viewingExistingMov)
        //Call the delegate, which is in this case in main view controller
        delegateMain.creatingOrEditingMovie(newMovie, rowNum: rowNum, viewingExistingMov: viewingExistingMov)
        
        //Pop your view out from child view to its parent view
        navigationController?.popToRootViewControllerAnimated(true)
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
}
