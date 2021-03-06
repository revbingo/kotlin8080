package intel8080
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUshort
import java.util.*
import kotlin.concurrent.timer

fun Number?.hex(isWord: Boolean = false) = String.format("$%0${if(isWord) 4 else 2}X", this?.toInt()).toLowerCase()
operator fun Array<Ubyte>.get(addr: Ushort) = this.get(addr.toInt()).toUbyte()
operator fun Array<Ubyte>.set(addr: Ushort, value: Ubyte) = this.set(addr.toInt(), value)

fun disassemble(emulator: Emulator8080, offset: Int) {
    var pc = offset.toUshort()

    do {
        val nextByte = emulator.state.memory[pc]
        val opCode = opCodeFor(nextByte.toUbyte())
        opCode.consume(emulator.state)
        val opCodeStr = when(opCode) {
            is NoArgOpCode -> opCode.javaClass.simpleName.replaceFirst("_", " ").replaceFirst("_", ",")
            is ByteOpCode -> "${opCode.javaClass.simpleName.replaceFirst("_", "\t")}${if(opCode.javaClass.simpleName.contains("_")) "," else "\t"}#${opCode.value.hex()}"
            is WordOpCode -> "${opCode.javaClass.simpleName.replaceFirst("_", "\t")}${if(opCode.javaClass.simpleName.contains("_")) "," else "\t"}#${opCode.value.hex(isWord = true)}"
            else -> "Unknown"
        }
        println("${String.format("%04X", offset.toInt())}\t$opCodeStr")
        pc += opCode.size
    } while(pc < emulator.state.memory.size)
}

class Emulator8080(val hardware: Hardware, memSize: Int) {

    private val EMULATOR_CYCLES_PER_SEC = 2000000
    private val nanosPerCycle = (1.0/EMULATOR_CYCLES_PER_SEC) * 1e9

    val state = State(hardware, memSize)
    val cpu = Cpu(state)

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

        runningInterrupts.clear()
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

        state.flags.fromByte(ZERO)

        state.memory.fill(Ubyte(0))

        state.int_enable = false
        state.halted = false
    }

    fun run() {
        //start the interrupts
        interrupts.mapTo(runningInterrupts) { t -> t() }

        val PERIOD = 10L

        while(!state.halted) {
            try {
                var cyclesToProcess = Math.round((PERIOD * 1000000)/(nanosPerCycle))
                val start = System.nanoTime()
                while(cyclesToProcess > 0) {
                    //If there's an interrupt waiting and they are enabled, process the interrupt
                    if (interrupt > 0 && state.int_enable) {
                        hardware.interrupt(interrupt)
                        state.pushStack(state.pc)
                        state.pc = (8 * interrupt).toUshort()
                        interrupt = 0
                        state.int_enable = false
                    }
                    if (hardware.hooks.containsKey(state.pc)) hardware.hooks[state.pc]?.invoke(state)
                    val cycles = cpu.tick()
                    cyclesToProcess -= cycles
                    opCount += cycles
                }
                //How long should we sleep for?
                val diffinMs = (System.nanoTime() - start)/1000000
                Thread.sleep(Math.max(PERIOD - diffinMs, 0))
            } catch(e: Exception) {
                e.printStackTrace()
                break
            }
        }
    }

    fun setInterrupt(period: Long, action: TimerTask.() -> Unit) {
        interrupts.add { timer("Interrupt", period = period, action = action) }
    }

    fun interrupt(num: Int) {
        interrupt = num
    }
}

class Flags(val state: State) {
    var z: Boolean = false
    var s: Boolean = false

    //we lazy evaluate this so it's only calculated when needed
    //setFlags will just set the 'lastFlaggedValue' variable so that we know the last number that parity should have been
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
        //Also need a setter so that POP_PSW (sets flags from a value on the stack) can work.
        set(value) {
            lastFlaggedValue = if(value) Ushort(0) else Ushort(1)
        }
    var cy: Boolean = false
    var ac: Boolean = false

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

    override fun toString(): String {
        return "${ind(z, "z")}\t${ind(s,"s")}\t${ind(p,"p")}\t${ind(cy, "cy")}\t${ind(ac, "ac")}"
    }

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

    fun hl() = h.toWord(l)
    fun de() = d.toWord(e)
    fun bc() = b.toWord(c)

    fun heap() = this.memory[this.hl()]

    fun peek(ahead: Int) = this.memory[this.pc + ahead]

    fun pushStack(value: Ushort) {
        this.memory[this.sp - 2] = value.lo()
        this.memory[this.sp - 1] = value.hi()
        this.sp -= 2
    }

    fun popStack(): Ushort {
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

    override fun toString(): String {
        return "a:${a.hex()}\tbc:${b.toWord(c).hex(true)}\tde:${d.toWord(e).hex(true)}\thl:${h.toWord(l).hex(true)}\t\tpc:${pc.hex(true)}\tsp:${sp.hex(true)}"
    }
}

class NullState: State(NullHardware(), 0)