package app.atm

import java.sql.{Connection, DriverManager, ResultSet}

class DBManager {
  classOf[org.postgresql.Driver]
  val url = "jdbc:postgresql://localhost:5432/atm_machine"
  val user = "scala"
  val password = "servlet"

  def checkPinAndAccount(accountNumber:Long, pin:Int): Boolean = {
    var isValidData:Boolean = false
    var dbConnection:Connection = null
    try {
      dbConnection = DriverManager.getConnection(url, user, password)
      val query = "SELECT account_number, pin FROM account WHERE account_number = ? AND pin = ?"
      val statement = dbConnection.prepareStatement(query)
      statement.setLong(1, accountNumber)
      statement.setInt(2, pin.toInt)
      val result = statement.executeQuery()
      while (result.next()) {
        isValidData = true
      }
    } catch {
        case exception: Exception => isValidData = false
    }
    isValidData
  }

  def getBalance(accountNumber:Long): Int = {
    var balance:Int = 0
    var dbConnection:Connection = null
    try {
      dbConnection = DriverManager.getConnection(url, user, password)
      val query = "SELECT balance FROM balance WHERE account_number = ?"
      val statement = dbConnection.prepareStatement(query)
      statement.setLong(1, accountNumber)
      val result = statement.executeQuery()
      while (result.next()) {
        balance = result.getInt(1)
      }
    } catch {
        case exception: Exception => exception.getMessage
    }
    balance
  }

  def updateMoneyToAccount(amount:Int, accountNumber:Long, isDeposit:Boolean): Boolean = {
    var dbConnection:Connection = null
    try {
      dbConnection = DriverManager.getConnection(url, user, password)
      val query = "UPDATE balance SET balance = ? WHERE account_number = ?"
      val statement = dbConnection.prepareStatement(query)
      var balance:Int = 0
      if (isDeposit) {
        balance = getBalance(accountNumber) + amount
      } else {
        balance = getBalance(accountNumber) - amount
      }
      statement.setInt(1, balance)
      statement.setLong(2, accountNumber)
      statement.executeUpdate()
      true
    } catch {
        case exception: Exception =>  false
    }
  }

  def isEnoughAmountExistInBalance(accountNumber:Long, amount:Int): Boolean = {
    var dbConnection:Connection = null
    try {
      dbConnection = DriverManager.getConnection(url, user, password)
      val query = "SELECT balance FROM balance WHERE account_number = ?"
      val statement = dbConnection.prepareStatement(query)
      statement.setLong(1, accountNumber)
      val result = statement.executeQuery()
      while(result.next()) {
        if (result.getInt(1) >= amount) return true
      }
      false
    } catch {
        case exception: Exception => false
    }
  }

  def checkCurrentPin(accountNumber:Long, pin:Int): Boolean = {
    var dbConnection:Connection = null
    try {
      dbConnection = DriverManager.getConnection(url, user, password)
      val query = "SELECT pin FROM account WHERE account_number = ?"
      val statement = dbConnection.prepareStatement(query)
      statement.setLong(1, accountNumber)
      val result = statement.executeQuery()
      while(result.next()) {
        if (result.getInt(1) == pin) {
          return true
        }
      }
      false
    } catch {
        case exception: Exception => false
    }
  }

  def changePin(accountNumber:Long, pin:Int): Boolean = {
    var dbConnection:Connection = null
    try {
      dbConnection = DriverManager.getConnection(url, user, password)
      val query = "UPDATE account SET pin = ? WHERE account_number = ?"
      val statement = dbConnection.prepareStatement(query)
      statement.setInt(1, pin)
      statement.setLong(2, accountNumber)
      statement.executeUpdate()
      true
    } catch {
        case exception: Exception => false
    }
  }

}