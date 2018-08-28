package gameboy
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUshort
import java.io.File
import java.util.*
import kotlin.concurrent.timer

fun Number?.hex(isWord: Boolean = false) = String.format("$%0${if(isWord) 4 else 2}X", this?.toInt()).toLowerCase()
operator fun Array<Ubyte>.get(addr: Ushort) = this.get(addr.toInt()).toUbyte()
operator fun Array<Ubyte>.set(addr: Ushort, value: Ubyte) = this.set(addr.toInt(), value) //.also { println("intel8080.set memory ${addr.toInt().intel8080.hex(true)} to ${value.intel8080.hex()}")}.also { Exception().printStackTrace() }

fun disassemble(emulator: EmulatorLR35902, offset: Int) {
    var pc = offset.toUshort()

    do {
        val nextByte = emulator.state.memory[pc]
        val opCode = opCodeFor(nextByte.toUbyte())
        opCode.consume(emulator.state)
        println(opCode.toString())
        pc += opCode.operandCount + 1
    } while(pc < emulator.state.memory.size)
}

class EmulatorLR35902(val hardware: Hardware, val memSize: Int) {

    val EMULATOR_CYCLES_PER_SEC = 2000000

    val nanosPerCycle = (1/EMULATOR_CYCLES_PER_SEC) * 1e9
    val state = State(hardware, memSize)

    val debug: Int = 0
    private var log = File("debug.log").printWriter()

    var currentOp: OpCode? = null
    var opCount = 0

    val interrupts = mutableListOf<() -> Timer>()
    private val runningInterrupts = mutableListOf<Timer>()
    var interrupt: Int = 0

    fun load(bytes: ByteArray, offset: Int) {
        bytes.forEachIndexed { i, byte ->
            state.memory[i + offset] = Ubyte(byte)
        }
    }

    fun reset() {
        runningInterrupts.forEach { t -> t.cancel() }

        interrupts.clear()

        state.a = Ubyte(0)
        state.b = Ubyte(0)
        state.c = Ubyte(0)
        state.d = Ubyte(0)
        state.e = Ubyte(0)
        state.h = Ubyte(0)
        state.l = Ubyte(0)

        state.pc = 0.toUshort()
        state.sp = 0.toUshort()

        state.flags.n = false
        state.flags.z = false
        state.flags.c = false
        state.flags.h = false

        state.memory.fill(Ubyte(0))

        state.int_enable = false
        state.halted = false
    }

    fun run() {
        //start the interrupts
        interrupts.mapTo(runningInterrupts) { t -> t() }

        var lastInstruction: Long = 0
        var cyclesToProcess: Long = 0

        while(!state.halted) {
            try {
                if(cyclesToProcess > 0) {
                    //If there's an interrupt waiting and they are enabled, process the interrupt
                    if (interrupt > 0 && state.int_enable) {
                        hardware.interrupt(interrupt)
                        state.push(state.pc)
                        state.pc = (8 * interrupt).toUshort()
                        interrupt = 0
                        state.int_enable = false
                    }
                    if(hardware.hooks.containsKey(state.pc)) hardware.hooks[state.pc]?.invoke(state)
                    readNextInstruction()
                    val processed = execNextInstruction()
                    cyclesToProcess -= processed
                    opCount += processed
                } else {
                    val timeNow = System.nanoTime()
                    if(lastInstruction == 0L) lastInstruction = timeNow
                    cyclesToProcess = Math.round((timeNow - lastInstruction)/nanosPerCycle)
                    lastInstruction = timeNow
                    Thread.sleep(10)
                }
            } catch(e: Exception) {
                e.printStackTrace(log)
                e.printStackTrace()
                break
            }
        }
        log.flush()
        log.close()
    }

    fun setInterrupt(period: Long, action: TimerTask.() -> Unit) {
        interrupts.add { timer("Interrupt", period = period, action = action) }
    }

    inline fun readNextInstruction() {
        val nextInst = state.memory[state.pc]

        currentOp = opCodeFor(nextInst)
        currentOp!!.consume(state)

        if(debug >= 1) debug("Executing")
    }

    fun execNextInstruction(): Int = currentOp!!.execAndAdvance()

    fun debug(action: String) {
        when(state.pc.toInt()) {
            0x01AB -> log.println("TEST JUMP INSTRUCTIONS")
            0x22A -> log.println("TEST ACCUMULATOR IMMEDIATE")
            0x287 -> log.println("TEST CALLS AND RETURNS")
            0x31D -> log.println("TEST \"MOV\",\"INR\",AND \"DCR\" INSTRUCTIONS")
            0x35C -> log.println("TEST ARITHMETIC AND LOGIC INSTRUCTIONS")
            else -> {}
        }
        val statement = "Ops:${opCount} | intel8080.Flags: ${state.flags} | ${state} | $action ${currentOp}"
        print("\r" + statement)
        if(debug >= 2) log.println(statement)
    }

    fun interrupt(num: Int) {
        interrupt = num
    }
}

class Flags(val state: State) {
    var z: Boolean = false  //zero
    var n: Boolean = false  //subtraction
    var c: Boolean = false //carry
    var h: Boolean = false //half-carry

    fun reset() {
        z = false
        n = false
        c = false
        h = false
    }

    fun asByte(): Ubyte {
        var byte = Ubyte(0)
        if(z) byte = byte.or(0x80)
        if(n) byte = byte.or(0x40)
        if(h) byte = byte.or(0x20)
        if(c) byte = byte.or(0x10)
        return byte
    }

    fun fromByte(flags: Ubyte) {
        z = flags.and(0x80) != ZERO
        n = flags.and(0x40) != ZERO
        h = flags.and(0x20) != ZERO
        c = flags.and(0x10) != ZERO
    }

    override fun toString() = "${ind(z, "z")}\t${ind(n,"n")}\t${ind(c, "c")}\t${ind(h, "h")}"
    private fun ind(value: Boolean, letter: String) = if(value) "($letter)" else " $letter "

}

open class State(val hardware: Hardware, memSize: Int) {
    var a: Ubyte = Ubyte(0)
    var b: Ubyte = Ubyte(0)
    var c: Ubyte = Ubyte(0)
    var d: Ubyte = Ubyte(0)
    var e: Ubyte = Ubyte(0)
    var h: Ubyte = Ubyte(0)
    var l: Ubyte = Ubyte(0)

    var pc = 0.toUshort()
    var sp = 0.toUshort()

    var memory = Array(memSize) { Ubyte(0) }

    val flags = Flags(this)

    var int_enable: Boolean = false
    var halted: Boolean = false

    override fun toString(): String {
        return "a:${a.hex()}\tbc:${b.toWord(c).hex(true)}\tde:${d.toWord(e).hex(true)}\thl:${h.toWord(l).hex(true)}\t\tpc:${pc.hex(true)}\tsp:${sp.hex(true)}"
    }

    fun hl() = h.toWord(l)
    fun de() = d.toWord(e)
    fun bc() = b.toWord(c)

    fun heap() = this.memory[this.hl()]

    fun push(value: Ushort) {
        this.memory[this.sp - 2] = value.lo()
        this.memory[this.sp - 1] = value.hi()
        this.sp -= 2
    }

    fun pop(): Ushort {
        val loPop = this.memory[this.sp]
        val hiPop= this.memory[this.sp + 1]
        this.sp += 2
        return hiPop.toWord(loPop)
    }

    fun inOp(port: Ubyte): Ubyte {
        return hardware.inOp(port)
    }

    fun outOp(port: Ubyte, value: Ubyte) {
        hardware.outOp(port, value)
    }

    fun halt() {
        halted = true
    }
}

class NullState: State(NullHardware(), 0)