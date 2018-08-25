
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUbyte
import unsigned.toUshort

class Diagnostic: Hardware(title = "Diagnostic test",
                            fileWithOffset = "resources/cpudiag.bin" at 0x100,
                            screenSize = 400.0 by 256.0,
                            memSize = 2.kb()) {

    val screen: Canvas = Canvas(screenSize.first, screenSize.second)

    override fun createInterface(): Scene {
        val root = StackPane()
        root.children.add(screen)
        return Scene(root, screenSize.first, screenSize.second)
    }

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
                    scale(2.0, 2.0)

                    fill = if(stringBuffer.toString().contains("OPERATIONAL")) Color.GREEN else Color.RED
                    fillRect(0.0, 0.0, screenSize.first, screenSize.second)

                    fill = Color.WHITE
                    fillText(stringBuffer.toString().replace("$", ""), 0.0, screenSize.second / 4.0)
                    save()
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