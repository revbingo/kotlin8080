
import javafx.application.Application
import javafx.scene.Scene
import javafx.stage.Stage
import unsigned.Ubyte
import unsigned.Ushort
import java.io.File
import kotlin.concurrent.thread
import kotlin.system.exitProcess

infix fun Double.by(height: Double) = Pair(this, height)
infix fun String.at(address: Int) = Pair(this, address)
fun Int.kb() = this * 1024

abstract class Hardware(val title: String, val fileWithOffset: Pair<String, Int>, val screenSize: Pair<Double, Double>, val memSize: Int): Application() {

    val emulator: Emulator8080 = Emulator8080(this, memSize)

    val hooks = mutableMapOf<Ushort, (State) -> Unit>()

    open fun initEmulator() {}
    open fun interrupt(num: Int) {}
    open fun inOp(port: Ubyte): Ubyte { return ZERO }
    open fun outOp(port: Ubyte, value: Ubyte) {}

    abstract fun createInterface(): Scene

    fun runEmulator() {
        emulator.reset()
        val file = File(fileWithOffset.first)
        val bytes = file.readBytes()
        emulator.load(bytes, fileWithOffset.second)

        initEmulator()
        if(emulator.debug >= 3) disassemble(emulator, 0x0)
        thread { emulator.run() }
    }

    override fun start(primaryStage: Stage) {
        runEmulator()
        primaryStage.title = title

        primaryStage.setOnCloseRequest { exitProcess(0) }

        primaryStage.scene = createInterface()
        primaryStage.show()
    }
}