package app.atm

class ServerMachine {

  def isValidPinAndAccount(tuple2: Tuple2[Long, Int]): Boolean = {
    val db = new DBManager()
    db.checkPinAndAccount(tuple2._1, tuple2._2)
  }

  def depositMoney(amount:Int, accountNumber:Long) {
    val db = new DBManager()
    val isDeposit = true
    db.updateMoneyToAccount(amount, accountNumber, isDeposit)
  }

  def checkAccountBalance(accountNumber:Long, amount:Int): Boolean = {
    val db = new DBManager()
    db.isEnoughAmountExistInBalance(accountNumber, amount)
  }

  def updateMoneyAfterWithdraw(amount:Int, accountNumber:Long) {
    val db = new DBManager()
    val isDeposit = false
    db.updateMoneyToAccount(amount, accountNumber, isDeposit)
  }

  def checkBalance(accountNumber:Long): Int = {
    val db = new DBManager()
    db.getBalance(accountNumber)
  }

  def checkCurrentPin(accountNumber:Long, pin:Int): Boolean = {
    val db = new DBManager()
    db.checkCurrentPin(accountNumber, pin)
  }

  def changePin(accountNumber:Long, pin:Int): Boolean = {
    val db = new DBManager()
    db.changePin(accountNumber, pin)
  }

}
