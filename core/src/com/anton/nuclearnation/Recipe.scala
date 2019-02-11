package com.anton.nuclearnation

import com.anton.nuclearnation.MapScreen.MapLocation

case class Recipe(basicPrecondition:()=>Boolean, locationPrecondition:List[MapLocation]=>Boolean, craftingResult:()=>Unit)