
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.paint.Color
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUbyte
import unsigned.toUshort

class Diagnostic: Hardware(title = "Diagnostic test",
                            fileWithOffset = "resources/cpudiag.bin" at 0x100,
                            screenSize = 224.0 by 256.0,
                            memSize = 2.kb()) {

    override fun initEmulator() {
        //Some hackery to make the diagnostics work
        emulator.state.memory[0] = 0xc3.toUbyte()
        emulator.state.memory[1] = 0.toUbyte()
        emulator.state.memory[2] = 0x01.toUbyte()

        emulator.state.memory[3] = 0x76.toUbyte()

        emulator.state.memory[368] = 0x7.toUbyte()

        emulator.state.memory[0x59c] = 0xc3.toUbyte()
        emulator.state.memory[0x59d] = 0xc2.toUbyte()
        emulator.state.memory[0x59e] = 0x05.toUbyte()

        hooks[Ushort(0x5)] = this::printResult
    }

    private fun printResult(state: State) {
        if (state.c == Ubyte(9)) {
            var offset = state.d.toWord(state.e)
            offset += 3
            val stringBuffer = StringBuffer()
            do {
                val string = state.memory[offset++]
                stringBuffer.append(string.toChar())
            } while (string.toChar() != '$')
            state.pc = 0x03.toUshort()
            Platform.runLater {
                with(screen.graphicsContext2D) {
                    fill = if(stringBuffer.toString().contains("OPERATIONAL")) Color.GREEN else Color.RED
                    fillRect(0.0, 0.0, screenSize.first, screenSize.second)

                    fill = Color.WHITE
                    fillText(stringBuffer.toString(), 0.0, screenSize.second / 2.0)
                }
            }
        } else if (state.c == Ubyte(2)) {
            state.halt()
        }
    }
}

fun main(args: Array<String>) {
    Application.launch(Diagnostic::class.java, *args)
}