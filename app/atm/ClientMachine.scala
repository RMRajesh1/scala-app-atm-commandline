package app.atm

import scala.collection.immutable.ListMap
import scala.io.StdIn.readLine
import java.util.regex._
import scala.util.control.Breaks.breakable
import scala.util.control.Breaks.break

class ClientMachine {
  val server = new ServerMachine()
  val display = new Display()
  var isAppLive = true
  val amountCategory:ListMap[String, Int] = ListMap("twoThousand" -> 2000, "fiveHundred" -> 500, "TwoHundred" -> 200, "oneHundred" -> 100, "fifty" -> 50, "twenty" -> 20, "ten" -> 10)
  var machineBalance:ListMap[String, Int] = ListMap("twoThousand" -> 2, "fiveHundred" -> 2, "TwoHundred" -> 5, "oneHundred" -> 5, "fifty" -> 5, "twenty" -> 10, "ten" -> 10, "total" -> 7050)
  var cardData:ListMap[String, String] = ListMap.empty

  def connectToUser(): Unit = {
    display.welcomeUser()
    val isValidUser = getUserdataAndValidate
    if (isValidUser) {
      display.message("\n\tOur services")
      while (isAppLive) {
        display.showAvailableServices()
        clientCommandReceiver()
      }
    } else {
        display.invalid()
    }
  }

  def clientCommandReceiver(): Unit = {
    val clientInput = readLine("\nEnter your choice : ")
    commandExecutor(clientInput)
  }

  def commandExecutor(command:String): Unit = {
    command match {
      case "1" => withDrawMoney()
      case "2" => balanceInquiry()
      case "3" => depositCash()
      case "4" => changePin()
      case "5" => createNewAccount()
      case "6" => display.message("\n\tOur services")
      case "7" => endSession()
      case "0" => display.message("Machine balance : "+machineBalance)
      case _ => display.invalid("command")
    }
  }

  def balanceInquiry(): Unit = {
    display.message("\n:- BALANCE INQUIRY -:")
    display.message("Checking your balance...")
    val balance:Int = server.checkBalance(cardData("accountNumber"))
    display.message("Your account balance : "+balance)
  }

  def changePin() {
    display.message("\n:- CHANGE PIN -:")
    val currentPin = readLine("Enter your current pin : ")
    if (isValidPin(currentPin)) {
      val isValidCurrentPin = server.checkCurrentPin(cardData("accountNumber"), currentPin.toInt)
      if (isValidCurrentPin) {
        val newPin = readLine("Enter new pin : ")
        if (isValidPin(newPin)) {
          val isPinChanged = server.changePin(cardData("accountNumber"), newPin.toInt)
          if (isPinChanged) display.message("Pin changed")
          else display.message("Failed!")
        } else display.invalid()
      } else display.message("Wrong pin")
    }  else display.invalid()
  }

  def withDrawMoney(): Unit = {
    display.message("\n:- CASH WITHDRAW-:")
    val clientRequestedAmount = readLine("Enter the amount : ")
    val isValidAmount = isItValidAmount(clientRequestedAmount)
    if (isValidAmount) {
      if (machineBalance("total") >= clientRequestedAmount.toInt) {
        display.message("Available amount!")
        val isBalanceEnough = server.checkAccountBalance(cardData("accountNumber"), clientRequestedAmount.toInt)
        if (isBalanceEnough) {
          display.message("get money "+clientRequestedAmount)
          divideCashesToTotalAmount(clientRequestedAmount.toInt)
        } else {
          balanceInquiry()
          display.message("Not enough balance in your account!")
        }
      } else display.message("Sorry! not enough money here. \n Try again in next time (after 12 hours)")
    } else display.invalid()
  }

  def divideCashesToTotalAmount(amount:Int): Unit = {
    var dividedCashes:ListMap[String, Int] = ListMap.empty
    amountCategory.foreach {
      case (key, value) =>
        var divided = amount / value
        if (divided > machineBalance(key)) divided = machineBalance(key)
        dividedCashes += key -> divided
    }
    display.message("Divided cashes : "+dividedCashes)

    if (calculateTotalAmount(dividedCashes) <= machineBalance("total")) {
      display.message("Calculate possibilities!")
      var targetAmount:Int = 0
      var separatedCashes:ListMap[String, Int] = ListMap.empty
      dividedCashes.foreach {
        case (key, value) =>
          var isMoneyPicked:Boolean = false
          var count:Int = value
          breakable {
            while (count > 0) {
              if ((targetAmount + (amountCategory(key) * count)) <= amount) {
                targetAmount += (amountCategory(key) * count)
                separatedCashes += key -> count
                isMoneyPicked = true
                break()
              }
              count -= 1
            }
          }
          if (!isMoneyPicked) separatedCashes += key -> 0
          display.message("separated cashes : "+separatedCashes)
      }
      if (calculateTotalAmount(separatedCashes) == amount) {
        display.message("we can give")
        var updatedCash:ListMap[String, Int] = ListMap.empty
        separatedCashes.foreach {
          case (key, value) =>
            updatedCash += key -> (machineBalance(key) - value)
        }
        updatedCash += "total" -> calculateTotalAmount(updatedCash)
        display.message("Machine balance = "+machineBalance)
        display.message("separated cash = "+separatedCashes)
        display.message("Updated cash = "+updatedCash)
        server.updateMoneyAfterWithdraw(amount, cardData("accountNumber"))
        giveMoneyToUser(separatedCashes)
        machineBalance = updatedCash
      } else display.message("Not available cashes for this amount!")
    } else display.message("Not enough amount available! try again after some time ")
  }

  def giveMoneyToUser(money:ListMap[String, Int]): Unit = {
    display.message("Collect your money!")
    money.foreach {
      case (key, value) =>
        if (value > 0) {
          display.message("\t₹" + amountCategory(key) + " * " + value + "\t = " + (amountCategory(key) * value))
        }
    }
    display.message("\t Total amount = " + calculateTotalAmount(money))
    display.message("your account balance = "+server.checkBalance(cardData("accountNumber")))
  }


  def depositCash() {
    display.message("\n:- DEPOSIT CASH -:")
    collectMoneyFromUser()
  }

  def getUserdataAndValidate: Boolean = {
    val cardDataMessages:ListMap[String, String] = ListMap("accountNumber" -> "Enter your card number : ", "pin" -> "Enter your pin : ")
    cardData = getDataFromUser(cardDataMessages)
    if (cardData("isCancelled").equals("true")) {
      display.message("Process cancelled!")
      false
    }
    else server.isValidPinAndAccount(cardData)
  }

  def collectMoneyFromUser(): Unit = {
    var clientMoney:ListMap[String, Int] = ListMap.empty
    var isInvalidAmount = true
    var depositAmount:Int = 0
      while (isInvalidAmount) {
        val enterAmount = readLine("Enter you total amount : ")
        if (isItValidAmount(enterAmount)) {
          isInvalidAmount = false
          depositAmount = enterAmount.toInt
        }
        else display.message("This is not acceptable!")
    }

    amountCategory.foreach {
      case (key, value) =>
        var isValidInput = false
        while (!isValidInput) {
          val count = readLine("Enter the count of "+value+" : ")
          isValidInput = checkIsNumber(count)
          if (!isValidInput) display.message("Invalid input")
          else clientMoney += key -> count.toInt
        }
    }
    val totalAmount:Int = calculateTotalAmount(clientMoney)
    clientMoney += "total" -> totalAmount

    if (depositAmount == totalAmount) {
      depositMoneyToMachine(clientMoney)
      server.depositMoney(clientMoney("total"), cardData("accountNumber"))
    } else {
      display.amountMismatchedClientSideMistake()
    }
  }

  def depositMoneyToMachine(data:ListMap[String, Int]): Unit = {
    var updatedBalance:ListMap[String, Int] = ListMap.empty
    machineBalance.foreach {
      case (key, value) =>
        updatedBalance += key -> (data(key) + value)
    }
    machineBalance = updatedBalance
    display.message("Machine balance : " + machineBalance)
  }

  def calculateTotalAmount(amount:ListMap[String, Int]): Int = {
    var total:Int = 0
    amount.foreach {
      case (key, value) => total += amountCategory(key) * value
    }
    total
  }

  def isItValidAmount(data:String): Boolean = {
    if (checkIsNumber(data)) {
      val amount:Int = data.toInt
      if ((amount % 10) == 0) return true
    }
    false
  }

  def checkIsNumber(data:String): Boolean = {
    try {
      val amount:Int = data.toInt
      true
    } catch {
        case exception: NumberFormatException => false
    }
  }

  def getDataFromUser(dataMessages:ListMap[String, String]): ListMap[String, String] = {
    var userData:ListMap[String, String] = ListMap.empty
    var isCancelled = false
    dataMessages.foreach {
      case (key, value) =>
        var isValidInput = false
        while(!isValidInput && !isCancelled) {
          val clientInput = readLine(value)
          if (clientInput.equals("cancel")) isCancelled = true
          else {
            isValidInput = validateInput(key, clientInput)
            if (isValidInput) userData += (key -> clientInput)
            else display.message("Please check your input! (invalid input found)")
          }
        }
    }
    userData += ("isCancelled" -> isCancelled.toString)
    userData
  }

  def createNewAccount() {
    display.message("\n:- CREATE NEW ACCOUNT -:")
    val accountDataMessages:ListMap[String, String] = ListMap("name" -> "Enter your name : ", "accountNumber" -> "Enter account number : ", "pin" -> "Enter your pin : ", "age" -> "Enter your age : ")
    val userData:ListMap[String, String] = getDataFromUser(accountDataMessages)
    if (userData("isCancelled").equals("false")) {
      val isNewAccountCreated = server.createNewAccount(userData)
      if (!isNewAccountCreated) createNewAccount()
    } else {
      display.message("Process cancelled!")
    }
  }

  def endSession(): Unit = {
    isAppLive = false
    display.message("Process ends!")
  }

  def validateInput(inputType:String, inputValue:Any): Boolean = {
    inputType match {
      case "name" => isValidName(inputValue)
      case "accountNumber" => isValidAccountNumber(inputValue)
      case "pin" => isValidPin(inputValue)
      case "age" => isValidAge(inputValue)
      case _ => true
    }
  }

  def isValidName(data:Any): Boolean = {
    val accountName:String = data.asInstanceOf[String]
    val pattern:Pattern = Pattern.compile("^[a-zA-Z]([._-](?![._-])|[a-zA-Z]){3,18}[a-zA-Z]$")
    val matcher:Matcher = pattern.matcher(accountName)
    matcher.matches()
  }

  def isValidAccountNumber(data:Any): Boolean = {
    val accountNumber:String = data.asInstanceOf[String]
    val pattern:Pattern = Pattern.compile("[0-9]{9,18}")
    val matcher:Matcher = pattern.matcher(accountNumber)
    matcher.matches()
  }

  def isValidPin(data:Any): Boolean = {
    val pin:String = data.asInstanceOf[String]
    val pattern:Pattern = Pattern.compile("[0-9]{4,6}")
    val matcher:Matcher = pattern.matcher(pin)
    matcher.matches()
  }

  def isValidAge(data:Any): Boolean = {
    val age: Int = data.asInstanceOf[String].toInt
    if ((age > 17) && (age < 125)) true
    else false
  }

}