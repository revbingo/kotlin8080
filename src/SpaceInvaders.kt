
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import unsigned.Ubyte
import unsigned.toUbyte

class ExternalShift {
    var shift0: Ubyte = ZERO
    var shift1: Ubyte = ONE
    var shiftOffset: Ubyte = ZERO

    fun doShift(): Ubyte {
        val word = shift1.toWord(shift0)
        return word.shr(8-shiftOffset.toInt()).and(0xff).toUbyte()
    }
}

var debugSymbols = mapOf(0x00 to "Reset",
                        0x08 to "ScanLine96",
        0x10 to "ScanLine224",
        0xB1 to "InitRack",
        0x100 to "DrawAlien",
        0x141 to "CursorNextAlien",
        0x17a to "GetAlienCoords",
        0x1a1 to "MoveRefAlien",
        0x1c0 to "InitAliens",
        0x1cd to "ReturnTwo",
        0x1cf to "DrawBottomLine",
        0x1d9 to "AddDelta",
        0x1e4 to "CopyRAMMirror"
)
class SpaceInvaders: Hardware(title = "Space Invaders!",
                                fileWithOffset = "resources/invaders" at 0x0,
                                screenSize = 224.0 by 256.0,
                                memSize = 32.kb()) {
    private val externalShift = ExternalShift()

    private val screen: Canvas = Canvas(screenSize.first, screenSize.second)

    private var nextInterrupt = 1
    private val SIXTY_HERTZ_INTERRUPT = 8L

    private var port0: Ubyte = ZERO
    private var port1: Ubyte = ZERO

    override fun inOp(port: Ubyte): Ubyte {
        return when(port.toInt()) {
            0x0 -> port0
            0x1 -> port1
            0x3 -> externalShift.doShift()
            else -> ZERO
        }
    }

    override fun outOp(port: Ubyte, value: Ubyte) {
        when(port.toInt()) {
            0x2 -> externalShift.shiftOffset = value.and(0x7)
            0x4 -> {
                externalShift.shift0 = externalShift.shift1
                externalShift.shift1 = value
            }
        }
    }

    override fun interrupt(num: Int) {
        //num == 1 means "scanline in the middle"
        //otherwise we draw twice as fast
        if(num == 1) return

        Platform.runLater {
            val gc = screen.graphicsContext2D

            //Black background
            gc.fill = Color.BLACK
            gc.fillRect(0.0, 0.0, screenSize.first, screenSize.second)
            gc.save()

            val pixelWriter = gc.pixelWriter

            val imageData = emulator.state.memory.sliceArray(0x2400..0x3fff)

            imageData.forEachIndexed { base, b ->
                //if it's zero, it's all background and we don't need to do anything
                if(b != ZERO) {
                    val yBlock = ((base.and(0x1f)) * 8).and(0xff).inv().and(0xff).toUbyte()
                    val x = base.shr(5)
                    for (j in 0..7) {
                        val bt = b.shr(j).and(0x1)
                        if (bt == ONE) {
                            val y = (yBlock - j).toInt()
                            pixelWriter.setColor(x, y, Color.WHITE)
                        }

                    }
                }
            }
        }
    }

    override fun initEmulator() {
        emulator.setInterrupt(SIXTY_HERTZ_INTERRUPT) {
            emulator.interrupt(nextInterrupt)
            nextInterrupt = if(nextInterrupt == 1) 2 else 1


        }

//        debugSymbols.forEach { addr,symbol ->
//            hooks.put(Ushort(addr)) {
//                println(symbol)
//            }
//        }
    }

    override fun createInterface(): Scene {
        val root = StackPane()
        root.children.add(screen)
        val scene = Scene(root, screenSize.first, screenSize.second)

        val keyPressBindings = mapOf(
                "c" to this::insertCoin,
                "1" to this::start1Player,
                "2" to this::start2Player

        )

        scene.setOnKeyPressed { e ->
            if(keyPressBindings.containsKey(e.text)) {
                keyPressBindings[e.text]?.invoke()
            }
        }
        return scene

    }

    private fun flipPort1(bit: Int) {
        port1 = port1.or(bit)

        Thread.sleep(50)

        port1 = port1.and(bit.inv())
    }
    private fun insertCoin() = flipPort1(0x1)
    private fun start1Player() = flipPort1(0x4)
    private fun start2Player() = flipPort1(0x8)

}

fun main(args: Array<String>) {
    Application.launch(SpaceInvaders::class.java, *args)
}