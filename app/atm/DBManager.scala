package app.atm

import java.sql.{Connection, DriverManager, ResultSet}
import scala.collection.immutable.ListMap

class DBManager {
  classOf[org.postgresql.Driver]
  val url = "jdbc:postgresql://localhost:5432/atm_machine"
  val user = "scala"
  val password = "servlet"
  val db_connection:Connection = DriverManager.getConnection(url, user, password)
  var result:ResultSet = null

  def isExistingAccount(newAccountNumber:String): Boolean = {
    var isExists:Boolean = false
    try {
      var existingAccountNumbers:List[String] = List.empty
      val query = "SELECT account_number FROM account"
      val statement = db_connection.createStatement()
      result = statement.executeQuery(query)
      while (result.next()) {
        existingAccountNumbers = result.getString(1) :: existingAccountNumbers
      }
      isExists = existingAccountNumbers.contains(newAccountNumber)
    } catch {
        case exception:Exception => exception.getMessage
    }
    isExists
  }

  def createNewAccount(data:ListMap[String, String]): ListMap[String, Int] = {
    var processStatus:ListMap[String, Int] = ListMap.empty
    try {
      val query = "INSERT INTO account(name, account_number, pin, age) VALUES (?, ?, ?, ?)"
      var statement = db_connection.prepareStatement(query)
      statement.setString(1, data("name"))
      statement.setString(2, data("accountNumber"))
      statement.setInt(3, data("pin").toInt)
      statement.setInt(4, data("age").toInt)
      statement.execute()
      val setBalanceQuery = "INSERT INTO balance(balance, account_number) VALUES (0, ?)"
      statement = db_connection.prepareStatement(setBalanceQuery)
      statement.setString(1, data("accountNumber"))
      statement.execute()
      processStatus = ListMap("status" -> 200)
    } catch {
        case exception:Exception => processStatus = ListMap("status" -> 429)
    }
    processStatus
  }

  def checkPinAndAccount(accountNumber:String, pin:Int): Boolean = {
    var isValidData:Boolean = false
    try {
      val query = "SELECT account_number, pin FROM account WHERE account_number = ? AND pin = ?"
      val statement = db_connection.prepareStatement(query)
      statement.setString(1, accountNumber)
      statement.setInt(2, pin.toInt)
      result = statement.executeQuery()
      while (result.next()) {
        isValidData = true
      }
    } catch {
        case exception: Exception => isValidData = false
    }
    isValidData
  }

  def getBalance(accountNumber:String): Int = {
    var balance:Int = 0
    try {
      val query = "SELECT balance FROM balance WHERE account_number = ?"
      val statement = db_connection.prepareStatement(query)
      statement.setString(1, accountNumber)
      result = statement.executeQuery()
      while (result.next()) {
        balance = result.getInt(1)
      }
    } catch {
        case exception: Exception => exception.getMessage
    }
    balance
  }

  def updateMoneyToAccount(amount:Int, accountNumber:String, isDeposit:Boolean): Boolean = {
    try {
      val query = "UPDATE balance SET balance = ? WHERE account_number = ?"
      val statement = db_connection.prepareStatement(query)
      var balance:Int = 0
      if (isDeposit) balance = getBalance(accountNumber) + amount
      else balance = getBalance(accountNumber) - amount
      statement.setInt(1, balance)
      statement.setString(2, accountNumber)
      statement.executeUpdate()
      true
    } catch {
        case exception: Exception =>  false
    }
  }

  def isEnoughAmountExistInBalance(accountNumber:String, amount:Int): Boolean = {
    try {
      val query = "SELECT balance FROM balance WHERE account_number = ?"
      val statement = db_connection.prepareStatement(query)
      statement.setString(1, accountNumber)
      result = statement.executeQuery()
      while(result.next()) {
        if (result.getInt(1) >= amount) return true
      }
      false
    } catch {
        case exception: Exception => false
    }
  }

  def checkCurrentPin(accountNumber:String, pin:Int): Boolean = {
    try {
      val query = "SELECT pin FROM account WHERE account_number = ?"
      val statement = db_connection.prepareStatement(query)
      statement.setString(1, accountNumber)
      result = statement.executeQuery()
      while(result.next()) {
        if (result.getInt(1) == pin) return true
      }
      false
    } catch {
        case exception: Exception => false
    }
  }

  def changePin(accountNumber:String, pin:Int): Boolean = {
    try {
      val query = "UPDATE account SET pin = ? WHERE account_number = ?"
      val statement = db_connection.prepareStatement(query)
      statement.setInt(1, pin)
      statement.setString(2, accountNumber)
      statement.executeUpdate()
      true
    } catch {
        case exception: Exception => false
    }
  }


}



/*
*
* Status codes
*   400 -> successful
*   409 -> already exists
*   429 -> unsuccessful
*
* */