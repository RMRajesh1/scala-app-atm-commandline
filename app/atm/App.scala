package app.atm

class App {
  def startNewClient(): Unit = {
    val client = new ClientMachine()
    client.connectToUser()
  }
}

object App {
  def main(args: Array[String]): Unit = {
    val app = new App()
    app.startNewClient()
  }
}
