//
//  MovieDescription.swift
//  MoviePlus
//
//  Created by sgowdru on 2/22/16.
//  Copyright © 2016 sgowdru. All rights reserved.
//

import Foundation

class MovieDescription{
    var movName = [String]()
    var data : NSDictionary = ["":""]
    var fullMov : [String: AnyObject] = ["":""]
    
    //In MovieLibrary Object we Load Data from Json and Parse it using dispatch async, task will be in suspended
    //state and therefore we use task.resume() method.  We can also see the closure call in dataTaskWithURL method
    //loadData is called by iOS application as soon as app is loaded
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
    func getFullDic() -> NSDictionary{
        return fullMov
    }
    
}