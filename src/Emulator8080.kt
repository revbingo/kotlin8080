
import javafx.application.Application
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUshort
import java.io.File
import java.util.*
import kotlin.concurrent.timer
import kotlin.system.exitProcess

fun Number?.hex(isWord: Boolean = false) = String.format("$%0${if(isWord) 4 else 2}X", this?.toInt()).toLowerCase()

val SIXTY_HERTZ_INTERRUPT = 100L

fun main(args: Array<String>) {
    Application.launch(Hardware::class.java, *args)
}

fun disassemble(emulator: Emulator8080, offset: Int) {
    var pc = offset.toUshort()

    do {
        val nextByte = emulator.state.memory[pc]
        val opCode = opCodeFor(nextByte.toUbyte())
        opCode.consume(pc, emulator.state.memory)
        println(opCode.toString())
        pc += opCode.operandCount + 1
    } while(pc < emulator.state.memory.size)
}

class Emulator8080(val hardware: Hardware) {
    val state = State(hardware)

    val debug: Int = 0

    var currentOp: OpCode? = null

    var opCount = 0

    var log = File("debug.log").printWriter()

    val interrupts = mutableListOf<() -> Timer>()

    var interrupt: Int = 0

    fun load(bytes: ByteArray, offset: Int) {
        val ubytes = bytes.map { Ubyte(it) }
        state.memory = Array(offset, { Ubyte(0)}) + ubytes + Array(8192, { Ubyte(0)})
        println("Loaded with mem size " + state.memory.size)
    }

    fun run() {
        //start the interrupts
        interrupts.forEach { t -> t() }
        var start: Long = 0
        var lastOpCount: Int = 0
        var lastInstruction : Long = 0
        var cyclesToProcess: Long = 0
        while(true) {
            try {
//                if(cyclesToProcess > 0) {
                    if (interrupt > 0 && state.int_enable) {
                        hardware.interrupt(interrupt)
                        state.push(state.pc)
                        state.pc = (8 * interrupt).toUshort()
                        interrupt = 0
                        state.int_enable = false
                        log.print("**")
                    }
                    loadInst()
                    if(currentOp is IN && (currentOp as IN).value!! == ZERO) {
                        println("Waiting for IN ${(currentOp as IN).value}")
                    }
                    val processed = exec()
//                    cyclesToProcess -= processed
//                } else {
//                    val timeNow = System.nanoTime()
//                    if(start == 0L) start = timeNow
//                    cyclesToProcess = if(lastInstruction == 0L) 4 else Math.round((timeNow - lastInstruction)/500.0)
//                    //println("Time now: $timeNow Last Inst: $lastInstruction Diff: ${timeNow - lastInstruction} - Got ${cyclesToProcess} to process")
//                    lastInstruction = timeNow
//
//                    if((lastInstruction - start)/1000000000.0 > 1.0) {
//                        val opCountPerSec = opCount - lastOpCount
//                        print("\rProcessed $opCountPerSec cycles in the last second")
//                        start = 0
//                        lastOpCount = opCount
//                    }
//
//                }
            } catch(e: Exception) {
                e.printStackTrace(log)
                log.close()
                exitProcess(99)
            }
        }
    }

    fun setInterrupt(period: Long, action: TimerTask.() -> Unit) {
        interrupts.add { timer("Interrupt", period = period, action = action) }
    }

    fun loadInst() {
        val nextInst = state.memory[state.pc]

        currentOp = opCodeFor(nextInst.toUbyte())
        currentOp!!.consume(state.pc, state.memory)

        if(debug >= 1) debug("Executing")
    }

    fun exec(): Int {
        val cyclesProcessed = currentOp!!.execAndAdvance(state)
        opCount += cyclesProcessed

        return cyclesProcessed
    }

    fun debug(action: String) {
        when(state.pc.toInt()) {
            0x01AB -> log.println("TEST JUMP INSTRUCTIONS")
            0x22A -> log.println("TEST ACCUMULATOR IMMEDIATE")
            0x287 -> log.println("TEST CALLS AND RETURNS")
            0x31D -> log.println("TEST \"MOV\",\"INR\",AND \"DCR\" INSTRUCTIONS")
            0x35C -> log.println("TEST ARITHMETIC AND LOGIC INSTRUCTIONS")
            else -> {}
        }
        val statement = "Ops:${opCount} | Flags: ${state.flags} | ${state} | $action ${currentOp}"
        print("\r" + statement)
        if(debug >= 2) log.println(statement)
    }

    fun interrupt(num: Int) {
        interrupt = num
    }
}

class Flags {
    var z: Boolean = false
    var s: Boolean = false
    var p: Boolean = false
    var cy: Boolean = false
    var ac: Boolean = false

    override fun toString(): String {
        return "${ind(z, "z")}\t${ind(s,"s")}\t${ind(p,"p")}\t${ind(cy, "cy")}\t${ind(ac, "ac")}"
    }

    fun ind(value: Boolean, letter: String) = if(value) "($letter)" else " $letter "

    fun asByte(): Ubyte {
        var byte = Ubyte(0)
        if(z) byte = byte.or(0x1)
        if(s) byte = byte.or(0x2)
        if(p) byte = byte.or(0x4)
        if(cy) byte = byte.or(0x8)
        if(ac) byte = byte.or(0x10)
        return byte
    }

    fun fromByte(flags: Ubyte) {
        z = flags.and(0x1) != ZERO
        s = flags.and(0x2) != ZERO
        p = flags.and(0x4) != ZERO
        cy = flags.and(0x8) != ZERO
        ac = flags.and(0x10) != ZERO
    }
}

class State(val hardware: Hardware) {
    var a: Ubyte = Ubyte(0)
    var b: Ubyte = Ubyte(0)
    var c: Ubyte = Ubyte(0)
    var d: Ubyte = Ubyte(0)
    var e: Ubyte = Ubyte(0)
    var h: Ubyte = Ubyte(0)
    var l: Ubyte = Ubyte(0)

    var pc = 0.toUshort()
    var sp = 0.toUshort()

    var memory = Array<Ubyte>(4000, { Ubyte(0) })

    val flags = Flags()

    var int_enable: Boolean = false

    override fun toString(): String {
        return "a:${a.hex()}\tbc:${b.toWord(c).hex(true)}\tde:${d.toWord(e).hex(true)}\thl:${h.toWord(l).hex(true)}\t\tpc:${pc.hex(true)}\tsp:${sp.hex(true)}"
    }

    fun hl() = h.toWord(l)
    fun de() = d.toWord(e)
    fun bc() = b.toWord(c)

    fun heap() = this.memory[this.hl()]

    fun writeMem(address: Ushort, value: Ubyte) {
        this.memory[address.toInt()] = value
    }
    fun stack() = this.memory[this.sp]

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
}
