package app.atm

import java.util.regex.{Matcher, Pattern}

class Validation {

  def isValidInput(inputType:String, inputValue:String): Boolean = {
    inputType match {
      case "name" => isValidName(inputValue)
      case "accountNumber" => isValidAccountNumber(inputValue)
      case "pin" => isValidPin(inputValue)
      case "age" => isValidAge(inputValue.toInt)
      case _ => false
    }
  }

  def isValidAmount(data:String): Boolean = {
    if (isNumber(data)) {
      val amount:Int = data.toInt
      if ((amount % 10) == 0) {
        return true
      }
    }
    false
  }

  def isNumber(data:String, checkIsInt: Boolean = false, checkIsLong: Boolean = false): Boolean = {
    try {
      if (checkIsInt) {
        data.toInt
      } else if (checkIsLong) {
        data.toLong
      } else {
        data.toInt
      }
      true
    } catch {
      case exception: NumberFormatException => false
    }
  }

  def isValidName(data:Any): Boolean = {
    val accountName:String = data.asInstanceOf[String]
    val pattern:Pattern = Pattern.compile("^[a-zA-Z]([._-](?![._-])|[a-zA-Z]){3,18}[a-zA-Z]$")
    val matcher:Matcher = pattern.matcher(accountName)
    matcher.matches()
  }

  def isValidAccountNumber(data:String): Boolean = {
    val accountNumber:String = data.asInstanceOf[String]
    val pattern:Pattern = Pattern.compile("[0-9]{9,18}")
    val matcher:Matcher = pattern.matcher(accountNumber)
    matcher.matches()
  }

  def isValidPin(data:String): Boolean = {
    val pin:String = data.asInstanceOf[String]
    val pattern:Pattern = Pattern.compile("[0-9]{4,6}")
    val matcher:Matcher = pattern.matcher(pin)
    matcher.matches()
  }

  def isValidAge(data:Int): Boolean = {
    val age: Int = data.asInstanceOf[String].toInt
    if ((age > 17) && (age < 125)) {
      return true
    }
    false
  }
}
