
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.canvas.GraphicsContext
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage
import unsigned.Ubyte
import unsigned.toUbyte
import java.io.File
import kotlin.concurrent.thread

class ExternalShift {
    var shift0: Ubyte = ZERO
    var shift1: Ubyte = ONE
    var shiftOffset: Ubyte = ZERO

    fun doShift(): Ubyte {
        val word = shift1.toWord(shift0)
        return word.shr(8-shiftOffset.toInt()).and(0xff).toUbyte()
    }
}

class Hardware: Application() {
    var canvas: Canvas? = null
    var gc: GraphicsContext? = null

    var emulator: Emulator8080? = null

    val externalShift = ExternalShift()
    fun inOp(port: Ubyte): Ubyte {
        return when(port.toInt()) {
            0x0 -> ZERO
            0x1 -> ONE
            0x3 -> externalShift.doShift()
            else -> ZERO
        }
    }

    fun outOp(port: Ubyte, value: Ubyte) {
        when(port.toInt()) {
            0x2 -> externalShift.shiftOffset = value.and(0x7)
            0x4 -> {
                externalShift.shift0 = externalShift.shift1
                externalShift.shift1 = value
            }
        }
    }

    override fun start(primaryStage: Stage?) {
        if(primaryStage == null) return
        emulator = initEmulator()
        primaryStage.title = "Space Invaders!"

        val root = StackPane()
        canvas = Canvas(512.0, 488.0)
        root.children.add(canvas)
        primaryStage.scene = Scene(root, 512.0, 488.0)
        primaryStage.show()
    }

    fun interrupt(num: Int) {
        if(canvas == null) return
        Platform.runLater {
            val gc = canvas?.graphicsContext2D ?: throw RuntimeException("Cannot get graphics context")
            gc.scale(2.0, 2.0)
            gc.fill = Color.BLACK
            gc.fillRect(0.0,0.0,256.0,224.0)

            val pixelWriter = gc.pixelWriter

            val imageData = emulator?.state?.memory?.slice(0x2400..0x3fff) ?: throw RuntimeException("No image data to work with")

            for(y in 0..223) {
                for(x in 0..31) {
                    val byte = imageData[(y*32) + x].toInt()
                    for (b in 7 downTo 0) {
                        val color = if (byte.shr(b) == 0x1) Color.WHITE else Color.BLACK
                        if(byte != 0) {
                            if(color == Color.WHITE) print("*") else print(".")
                        }
                        pixelWriter.setColor((x * 8) + (7-b), y, color)
                    }
                }
                println("")
            }


        }
    }

    fun initEmulator(): Emulator8080 {
        val diag = true
        return if(diag) {
            val file = File("resources/cpudiag.bin")
            val bytes = file.readBytes()

            val emulator = Emulator8080(this)
            emulator.load(bytes, 0x100)

            emulator.state.memory[0] = 0xc3.toUbyte()
            emulator.state.memory[1] = 0.toUbyte()
            emulator.state.memory[2] = 0x01.toUbyte()

            emulator.state.memory[3] = 0x76.toUbyte()

            emulator.state.memory[368] = 0x7.toUbyte()

            emulator.state.memory[0x59c] = 0xc3.toUbyte()
            emulator.state.memory[0x59d] = 0xc2.toUbyte()
            emulator.state.memory[0x59e] = 0x05.toUbyte()

            if(emulator.debug >= 3) disassemble(emulator, 0x100)
            thread { emulator.run() }
            emulator
        } else {
            val file = File("resources/invaders")
            val bytes = file.readBytes()

            val emulator = Emulator8080(this)
            emulator.setInterrupt(SIXTY_HERTZ_INTERRUPT) { emulator.interrupt(2) }
            emulator.load(bytes, 0x0)
            if(emulator.debug >= 3) disassemble(emulator, 0x0)
            thread { emulator.run() }
            emulator
        }
    }

}
