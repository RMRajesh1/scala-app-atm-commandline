package app.atm

import scala.collection.immutable.ListMap

class ClientMachine {
  var isAppLive = true
  val amountCategory:ListMap[String, Int] = ListMap("twoThousand" -> 2000, "fiveHundred" -> 500, "TwoHundred" -> 200, "oneHundred" -> 100, "fifty" -> 50, "twenty" -> 20, "ten" -> 10)
  var machineBalance:ListMap[String, Int] = ListMap("twoThousand" -> 2, "fiveHundred" -> 2, "TwoHundred" -> 5, "oneHundred" -> 5, "fifty" -> 5, "twenty" -> 10, "ten" -> 10, "total" -> 7050)
  var cardData: (Long, Int) = null

  def connectToUser() {
    val display = new Display()
    display.welcomeUser()
    val isValidUser = getUserdataAndValidate()
    if (!isValidUser) {
      display.invalid()
      return
    }
    display.message("\n\tOur services")
    while (isAppLive) {
      display.showAvailableServices()
      clientCommandReceiver()
    }
  }

  def clientCommandReceiver() {
    val display = new Display()
    val clientInput = display.getUserInput("\nEnter your choice : ")
    val validate = new Validation()
    if (!validate.isNumber(clientInput)) {
      return
    }
    commandExecutor(clientInput.toInt)
  }

  def commandExecutor(command:Int) {
    val display = new Display()
    command match {
      case 1 => withDrawMoney()
      case 2 => balanceInquiry()
      case 3 => depositCash()
      case 4 => changePin()
      case 5 => display.message("\n\tOur services")
      case 6 => endSession()
      case 0 => display.message(s"Machine balance : $machineBalance")
      case _ => display.invalid("command")
    }
  }

  def balanceInquiry() {
    val display = new Display()
    display.message("\n:- BALANCE INQUIRY -:")
    display.message("Checking your balance...")
    val server = new ServerMachine()
    val balance:Int = server.checkBalance(cardData._1)
    display.message(s"Your account balance : $balance")
  }

  def changePin() {
    val display = new Display()
    display.message("\n:- CHANGE PIN -:")
    val currentPin = display.getUserInput("Enter your current pin : ")
    val validate = new Validation()
    if (!validate.isValidPin(currentPin)){
      display.invalid()
      return
    }
    val server = new ServerMachine()
    val isValidCurrentPin = server.checkCurrentPin(cardData._1, currentPin.toInt)
    if (!isValidCurrentPin) {
      display.message("Wrong pin")
      return
    }
    val newPin = display.getUserInput("Enter new pin : ")
    if (!validate.isValidPin(newPin)) {
      display.invalid()
      return
    }
    val isPinChanged = server.changePin(cardData._1, newPin.toInt)
    if (isPinChanged) {
      display.message("Pin changed")
    } else {
      display.message("Failed!")
    }
  }

  def withDrawMoney() {
    val display = new Display()
    display.message("\n:- CASH WITHDRAW-:")
    val clientRequestedAmount = display.getUserInput("Enter the amount : ")
    val validate = new Validation()
    val isValidAmount = validate.isValidAmount(clientRequestedAmount)
    if (!isValidAmount) {
      display.invalid()
      return
    }
    if (machineBalance("total") < clientRequestedAmount.toInt) {
      display.message("Sorry! not enough money here. \n Try again in next time (after 12 hours)")
      return
    }
    val server = new ServerMachine()
    val isBalanceEnough = server.checkAccountBalance(cardData._1, clientRequestedAmount.toInt)
    if (!isBalanceEnough) {
      balanceInquiry()
      display.message("Not enough balance in your account!")
      return
    }
    display.message(s"get money $clientRequestedAmount")
    divideCashToTotalAmount(clientRequestedAmount.toInt)
  }

  def divideCashToTotalAmount(amount:Int) {
    val display = new Display()
    if ( amount > machineBalance("total")) {
      display.message("Not enough amount available! try again after some time ")
      return
    }
    var dividedCash:ListMap[String, Int] = ListMap.empty
    var targetAmount:Int = 0
    var isIterationOver = false
    amountCategory.foreach {
      case (key, value) => {
        if (isIterationOver) {
          dividedCash += key -> 0
        } else {
            var divided = (amount - targetAmount) / value
            val cash = machineBalance(key)
            if (divided > cash) {
              divided = cash
            }
            targetAmount += value * divided
            dividedCash += key -> divided
            if (targetAmount >= amount) {
              isIterationOver = true
            }
        }
      }
    }
    val totalInDividedCash:Int = calculateTotalAmount(dividedCash)
    if (totalInDividedCash != amount) {
      display.message("Not available cashes for this amount!")
      return
    }
    var updatedCash:ListMap[String, Int] = ListMap.empty
    dividedCash.foreach {
      case (key, value) => {
        updatedCash += key -> (machineBalance(key) - value)
      }
    }
    updatedCash += "total" -> calculateTotalAmount(updatedCash)
    val server = new ServerMachine()
    server.updateMoneyAfterWithdraw(amount, cardData._1)
    giveMoneyToUser(dividedCash)
    machineBalance = updatedCash
  }

  def giveMoneyToUser(money:ListMap[String, Int]) {
    val display = new Display()
    display.message("Collect your money!")
    money.foreach {
      case (key, value) =>
        if (value > 0) {
          val cash = amountCategory(key)
          display.message(s"\t₹$cash * $value\t = ${cash * value}")
        }
    }
    display.message(s"\tTotal amount = ${calculateTotalAmount(money)}")
    val server = new ServerMachine()
    val balance = server.checkBalance(cardData._1)
    display.message(s"your account balance = $balance")
  }

  def depositCash() {
    val display = new Display()
    display.message("\n:- DEPOSIT CASH -:")
    collectMoneyFromUser()
  }

  def getUserdataAndValidate(): Boolean = {
    val display = new Display()
    val validate = new Validation()
    val accountNumber = display.getUserInput("Enter your card number : ")
    if (!validate.isValidAccountNumber(accountNumber)) {
      return false
    }
    val pin = display.getUserInput("Enter your pin : ")
    if (!validate.isValidPin(pin)) {
      return false
    }
    cardData = (accountNumber.toLong, pin.toInt)
    val server = new ServerMachine()
    server.isValidPinAndAccount(cardData)
  }

  def collectMoneyFromUser() {
    val display = new Display()
    val validate = new Validation()
    var clientMoney:ListMap[String, Int] = ListMap.empty
    amountCategory.foreach {
      case (key, value) => {
        val count = display.getUserInput(s"Enter the count of $value : ")
        val isValidInput = validate.isNumber(count)
        if (!isValidInput) {
          display.message("Invalid input")
          return
        } else {
          clientMoney += key -> count.toInt
        }
      }
    }
    val depositAmount = calculateTotalAmount(clientMoney)
    clientMoney += "total" -> depositAmount
    display.message(s"\tTotal : $depositAmount")
    depositMoneyToMachine(clientMoney)
    val server = new ServerMachine()
    server.depositMoney(depositAmount, cardData._1)
    display.message("deposited!")
  }

  def depositMoneyToMachine(data:ListMap[String, Int]) {
    var updatedBalance:ListMap[String, Int] = ListMap.empty
    machineBalance.foreach {
      case (key, value) => {
        updatedBalance += key -> (data(key) + value)
      }
    }
    machineBalance = updatedBalance
  }

  def calculateTotalAmount(amount:ListMap[String, Int]): Int = {
    var total:Int = 0
    amount.foreach {
      case (key, value) => total += amountCategory(key) * value
    }
    total
  }

  def endSession() {
    isAppLive = false
    val display = new Display()
    display.message("Process ends!")
  }

}