package intel8080

import javafx.application.Application
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.canvas.Canvas
import javafx.scene.control.Button
import javafx.scene.layout.BorderPane
import javafx.scene.layout.HBox
import javafx.scene.media.AudioClip
import javafx.scene.paint.Color
import unsigned.Ubyte
import unsigned.toUbyte
import java.io.File
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

    //Actually 120Hz, there are 2 interrupts per 60Hz refresh
    //As the game waits for interrupts, make this lower to speed the game up
    private val SIXTY_HERTZ_INTERRUPT = 8L

    private var port0 = Port()
    private var port1 = Port()
    private var port2 = Port()
    private var port3 = Port()
    private var port5 = Port()

    override fun inOp(port: Ubyte): Ubyte {
        return when(port.toInt()) {
            0x0 -> port0.value
            0x1 -> port1.value
            0x2 -> port2.value
            0x3 -> externalShift.doShift()
            else -> ZERO
        }
    }

    override fun outOp(port: Ubyte, value: Ubyte) {
        when(port.toInt()) {
            0x2 -> externalShift.shiftOffset = value.and(0x7)
            0x3 -> {
                        val oldValue = port3.value
                        if(oldValue != value) {
                            port3.value = value
                            playSound(port, oldValue, value)
                        }
                    }
            0x4 -> {
                externalShift.shift0 = externalShift.shift1
                externalShift.shift1 = value
            }
            0x5 -> {
                        val oldValue = port5.value
                        if(oldValue != value) {
                            port5.value = value
                            playSound(port, oldValue, value)
                        }
                    }
        }
    }

    override fun interrupt(num: Int) {
        //num == 1 means "scanline in the middle" so ignore it
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
    }

    private fun reset() {
        emulator.state.halt()
        emulator.reset()
        Thread.sleep(100)
        runEmulator()
    }

    override fun createInterface(): Scene {
        val root = BorderPane()
        root.center = screen

        val hbox = HBox()
        val reset = Button("Reset")
        hbox.children.add(reset)
        root.bottom = hbox

        reset.setOnAction {
            this.reset()
        }
        val scene = Scene(root, screenSize.first, screenSize.second + 40)
        bindKeys(scene)
        return scene

    }

    private val soundFileNames = mapOf(
            (port3 bit 0) to "ufo_highpitch.wav",
            (port3 bit 1) to "shoot.wav",
            (port3 bit 2) to "basehit.wav",
            (port3 bit 3) to "invaderkilled.wav",
            (port5 bit 0) to "fastinvader1.wav",
            (port5 bit 1) to "fastinvader2.wav",
            (port5 bit 2) to "fastinvader3.wav",
            (port5 bit 3) to "fastinvader4.wav",
            (port5 bit 4) to "ufohit.wav"
            )

    private val sounds = soundFileNames.mapValues { v ->
        val ac = AudioClip(File("resources/sounds/${v.value}").toURI().toString())
        if(v.value == "ufo_highpitch.wav") {
            ac.cycleCount = AudioClip.INDEFINITE
        }
        ac
    }

    private fun playSound(port: Ubyte, oldValue: Ubyte, newValue: Ubyte) {
        //No sound if amp disabled
        if((port2 bit 5).isUnset()) return

        val changedBits = oldValue.xor(newValue)
        val setBits = changedBits.and(newValue)
        val unsetBits = changedBits.and(oldValue)

        (0..7).forEach { i ->
            val bitIsSet = setBits.shr(i) == ONE
            val bitIsUnset = unsetBits.shr(i) == ONE
            val thePort = if(port == Ubyte(3)) port3 else port5
            if(bitIsSet) {
                sounds[(thePort bit i)]?.play()
            } else if(bitIsUnset && port == Ubyte(3) && i == 0) {
                //ufo
                sounds[(thePort bit i)]?.stop()
            }

        }
    }

    private fun bindKeys(scene: Scene) {
        val keyPressBindings = mapOf(
                "c" to insertCoin,
                "2" to start2Player,
                "1" to start1Player,
                "z" to player1Left,
                "x" to player1Right,
                "m" to player1Shot,
                "q" to player2Left,
                "w" to player2Right,
                "p" to player2Shot,
                "t" to tilt
        )

        val toggleBindings = mapOf(
                "3" to dip3,
                "4" to dip4,
                "5" to dip5,
                "6" to dip6,
                "7" to dip7
        )

        scene.setOnKeyPressed { e ->
            if(keyPressBindings.containsKey(e.text)) {
                val switch = keyPressBindings[e.text]!!
                switch.first.value = switch.first.value.or(switch.second)
            } else if(toggleBindings.containsKey(e.text)) {
                val switch = toggleBindings[e.text]!!
                switch.toggle()
            }
        }
        scene.setOnKeyReleased { e ->
            if(keyPressBindings.containsKey(e.text)) {
                val switch = keyPressBindings[e.text]!!
                switch.first.value = switch.first.value.and(switch.second.inv())
            }
        }
    }
    private val insertCoin = port1 bit 0
    private val start2Player = port1 bit 1
    private val start1Player = port1 bit 2
    private val player1Shot = port1 bit 4
    private val player1Left = port1 bit 5
    private val player1Right = port1 bit 6

    private val player2Shot = port2 bit 4
    private val player2Left = port2 bit 5
    private val player2Right = port2 bit 6

    private val tilt = port2 bit 2

    private val dip3 = port2 bit 0
    private val dip5 = port2 bit 1

    private val dip4 = port0 bit 0

    private val dip6 = port2 bit 3
    private val dip7 = port2 bit 7

    private fun Int.pow(exp: Int) = this.toDouble().pow(exp).toInt()
    private infix fun Port.bit(bit: Int) = Pair(this, 2.pow(bit))

    private fun Pair<Port, Int>.toggle() {
        if(this.first.value.and(this.second) == ZERO) {
            this.first.value = this.first.value.or(this.second)
        } else {
            this.first.value = this.first.value.and(this.second.inv())
        }
    }

    private fun Pair<Port, Int>.isSet(): Boolean {
        return this.first.value.and(this.second) == ZERO
    }

    private fun Pair<Port, Int>.isUnset() = !this.isSet()
}

fun main(args: Array<String>) {
    Application.launch(SpaceInvaders::class.java, *args)
}
