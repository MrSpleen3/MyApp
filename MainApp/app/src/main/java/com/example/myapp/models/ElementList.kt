package com.example.myapp.models

import java.util.Date

class ElementList(val day : Int,val month : Int,val year : Int, val timeSlot : Int,val flag : Boolean, val id : String,val place : String?) : Comparable<ElementList> {
    val date: Date = Date(year,month,day)
    override fun compareTo(other: ElementList): Int {
        return this.date.compareTo(other.date)
    }
}