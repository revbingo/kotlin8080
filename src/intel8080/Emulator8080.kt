package intel8080
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUshort
import java.io.File
import java.util.*
import kotlin.concurrent.timer

fun Number?.hex(isWord: Boolean = false) = String.format("$%0${if(isWord) 4 else 2}X", this?.toInt()).toLowerCase()
operator fun Array<Ubyte>.get(addr: Ushort) = this.get(addr.toInt()).toUbyte()
operator fun Array<Ubyte>.set(addr: Ushort, value: Ubyte) = this.set(addr.toInt(), value) //.also { println("intel8080.set memory ${addr.toInt().intel8080.hex(true)} to ${value.intel8080.hex()}")}.also { Exception().printStackTrace() }

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

class Emulator8080(val hardware: Hardware, val memSize: Int) {

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

        state.flags.s = false
        state.flags.z = false
        state.flags.cy = false
        state.flags.p = false

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
        currentOp!!.consume(state.pc, state.memory)

        if(debug >= 1) debug("Executing")
    }

    fun execNextInstruction(): Int = currentOp!!.execAndAdvance(state)

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
    var z: Boolean = false
    var s: Boolean = false

    //we lazy evaluate this so it's only calculated when needed
    //setFlags will just intel8080.set the 'lastFlaggedValue' variable so that we know the last number that parity should have been
    //calculated for
    var lastFlaggedValue: Ushort = Ushort(0)
    var p: Boolean
        get() {
            var p = 0
            var work = lastFlaggedValue.and(1.shl(8)-1)
            for(i in (0..8)) {
                if(work.and(0x1).toInt() == 0x1) p++
                work = work.shr(1)
            }
            return (0 == (p.and(0x1)))
        }
        //Also need a setter so that intel8080.POP_PSW (sets flags from a value on the stack) can work.
        set(value: Boolean) {
            lastFlaggedValue = if(value) Ushort(0) else Ushort(1)
        }
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

class State(val hardware: Hardware, memSize: Int) {
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
