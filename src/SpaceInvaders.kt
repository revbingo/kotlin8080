
import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import unsigned.Ubyte
import unsigned.toUbyte
import kotlin.math.pow

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

class Port {
    var value: Ubyte = ZERO
}

class SpaceInvaders: Hardware(title = "Space Invaders!",
                                fileWithOffset = "resources/invaders" at 0x0,
                                screenSize = 224.0 by 256.0,
                                memSize = 32.kb()) {
    private val externalShift = ExternalShift()

    private val screen: Canvas = Canvas(screenSize.first, screenSize.second)

    private var nextInterrupt = 1
    private val SIXTY_HERTZ_INTERRUPT = 8L

    private var port0: Port = Port()
    private var port1: Port = Port()

    override fun inOp(port: Ubyte): Ubyte {
//        println("port1 is now ${port1.value}")
        return when(port.toInt()) {
            0x0 -> port0.value
            0x1 -> port1.value
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

    fun Int.pow(exp: Int) = this.toDouble().pow(exp).toInt()
    infix fun Port.bit(bit: Int) = Pair(this, 2.pow(bit))

    override fun createInterface(): Scene {
        val root = StackPane()
        root.children.add(screen)
        val scene = Scene(root, screenSize.first, screenSize.second)

        val keyPressBindings = mapOf(
                "c" to insertCoin,
                "2" to start2Player,
                "1" to start1Player,
                "z" to player1Left,
                "x" to player1Right,
                "m" to player1Shot
        )

        scene.setOnKeyPressed { e ->
            if(keyPressBindings.containsKey(e.text)) {
                val switch = keyPressBindings[e.text]!!
                switch.first.value = switch.first.value.or(switch.second)
            }
        }
        scene.setOnKeyReleased { e ->
            if(keyPressBindings.containsKey(e.text)) {
                val switch = keyPressBindings[e.text]!!
                switch.first.value = switch.first.value.and(switch.second.inv())
            }
        }
        return scene

    }

    private val insertCoin = port1 bit 0
    private val start2Player = port1 bit 1
    private val start1Player = port1 bit 2
    private val player1Shot = port1 bit 4
    private val player1Left = port1 bit 5
    private val player1Right = port1 bit 6
}

fun main(args: Array<String>) {
    Application.launch(SpaceInvaders::class.java, *args)
}