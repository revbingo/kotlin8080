package intel8080

import java.io.File

class Debugger {
    companion object {

        val debug: Int = 0
        private var log = File("debug.log").printWriter()

        fun log(statement: String) {
            if(debug >= 2) log.println(statement)
        }


    }
}