package app.atm

import java.util.regex.{Matcher, Pattern}

class Validation {

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
  
}
