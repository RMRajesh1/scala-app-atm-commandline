package app.atm

import scala.collection.immutable.ListMap
import scala.io.StdIn.readLine

class Display {

  def welcomeUser() {
    println(":- WELCOME TO OUR BANK -:")
  }

  def showAvailableServices() {
    val availableServices:ListMap[Int, String] = ListMap(1 -> "CASH WITHDRAWAL", 2 -> "BALANCE INQUIRY", 3 -> "DEPOSIT MONEY", 4 -> "PIN CHANGE", 5 -> "GO TO MENU", 6 -> "CANCEL TRANSACTION")
    availableServices.foreach{
      case(key, value) => println(key + " -> " + value)
    }
  }

  def getUserInput(label:String = "Enter your input : "): String = {
    readLine(label)
  }

  def message(content:String) {
    println(content)
  }

  def invalid(message:String = "") {
    println("Invalid "+message+"!")
  }

}
