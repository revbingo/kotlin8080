
import javafx.application.Application
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
                                memSize = 16.kb()) {
    private val externalShift = ExternalShift()

    private val SIXTY_HERTZ_INTERRUPT = 16L

    override fun inOp(port: Ubyte): Ubyte {
        return when(port.toInt()) {
            0x0 -> ZERO
            0x1 -> ONE
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
        Platform.runLater {
            val gc = screen.graphicsContext2D ?: throw RuntimeException("Cannot get graphics context")
            gc.scale(2.0, 2.0)
            gc.fill = Color.BLACK
            gc.fillRect(0.0,0.0, screenSize.first, screenSize.second)

            val pixelWriter = gc.pixelWriter

            val imageData = emulator.state.memory.slice(0x2400..0x3fff)

            imageData.forEachIndexed { base, b ->
                val yBlock = ((base.and(0x1f)) * 8).and(0xff).inv().and(0xff).toUbyte()
                val x = base.shr(5)
                for (i in 0..7) {
                    val bt = b.shr(i).and(0x1)
                    val y = (yBlock - i).toInt()
                    val color = if(bt == ONE) Color.WHITE else Color.BLACK
                    pixelWriter.setColor(x, y, color)
                }
            }
        }
    }

    override fun initEmulator() {
        emulator.setInterrupt(SIXTY_HERTZ_INTERRUPT) { emulator.interrupt(2) }
        emulator.setInterrupt(SIXTY_HERTZ_INTERRUPT) { emulator.interrupt(1) }

//        debugSymbols.forEach { addr,symbol ->
//            hooks.put(Ushort(addr)) {
//                println(symbol)
//            }
//        }
    }

}

fun main(args: Array<String>) {
    Application.launch(SpaceInvaders::class.java, *args)
}