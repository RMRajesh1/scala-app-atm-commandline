package app.atm

import scala.collection.immutable.ListMap

class Display {

  def welcomeUser(): Unit = {
    println(":- WELCOME TO OUR BANK -:")
  }

  def showAvailableServices(): Unit = {
    val availableServices:ListMap[Int, String] = ListMap(1 -> "CASH WITHDRAWAL", 2 -> "BALANCE INQUIRY", 3 -> "DEPOSIT MONEY", 4 -> "PIN CHANGE", 5 -> "CREATE NEW ACCOUNT", 6 -> "GO TO MENU", 7 -> "CANCEL TRANSACTION")
    println()
    availableServices.foreach{
      case(key, value) => println(key + " -> " + value)
    }
  }

  def createAccountStatus(status:ListMap[String, Int]): Boolean = {
    if (status("status") == 200) {
      println("Account created successfully!")
      return true
    } else if (status("status") == 409) {
      println("Account already exist")
    } else {
      println("Unsuccessful")
    }
    false
  }

  def amountMismatchedClientSideMistake(): Unit = {
    println("Total amount and receipt total is mismatch!\nPlease check and get back to us")
  }

  def invalid(): Unit = {
    println("Invalid!")
  }

  def invalid(message:String): Unit = {
    println("Invalid "+message+"!")
  }

  def message(content:String): Unit = {
    println(content)
  }

}
