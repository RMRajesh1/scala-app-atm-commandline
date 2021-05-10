package app.atm

import scala.collection.immutable.ListMap

class ServerMachine {

  val db = new DBManager()
  val display = new Display()

  def createNewAccount(data:ListMap[String, String]): Boolean = {
    val isExistingAccount = db.isExistingAccount(data("accountNumber"))
    var processStatus:ListMap[String, Int] = ListMap.empty
    if (isExistingAccount) processStatus = ListMap("status" -> 409)
    else processStatus = db.createNewAccount(data)
    display.createAccountStatus(processStatus)
  }

  def isValidPinAndAccount(data: ListMap[String, String]): Boolean = {
    db.checkPinAndAccount(data("accountNumber"), data("pin").toInt)
  }

  def depositMoney(amount:Int, accountNumber:String): Unit = {
    val isDeposit = true
    db.updateMoneyToAccount(amount, accountNumber, isDeposit)
  }

  def checkAccountBalance(accountNumber:String, amount:Int): Boolean = {
    db.isEnoughAmountExistInBalance(accountNumber, amount)
  }

  def updateMoneyAfterWithdraw(amount:Int, accountNumber:String): Unit = {
    val isDeposit = false
    db.updateMoneyToAccount(amount, accountNumber, isDeposit)
  }

  def checkBalance(accountNumber:String): Int = {
    db.getBalance(accountNumber)
  }

  def checkCurrentPin(accountNumber:String, pin:Int): Boolean = {
    db.checkCurrentPin(accountNumber:String, pin:Int)
  }

  def changePin(accountNumber:String, pin:Int): Boolean = {
    db.changePin(accountNumber, pin)
  }

}
