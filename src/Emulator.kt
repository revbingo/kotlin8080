
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUshort
import java.io.File

fun Number?.hex(isWord: Boolean = false) = String.format("$%0${if(isWord) 4 else 2}X", this?.toInt()).toLowerCase()

fun main(args: Array<String>) {
    val file = File("resources/cpudiag.bin")
    val bytes = file.readBytes()

    val emulator = Emulator()
    emulator.load(bytes, 0x100)

    emulator.fiddle()
    //disassemble(bytes)
    emulator.run()
}


fun disassemble(bytes: ByteArray) {
    var pc = Ushort(0)

    do {
        val nextByte = bytes[pc]
        val opCode = opCodeFor(nextByte.toUbyte())
        opCode.consume(pc, bytes)
        println(opCode.toString())
        pc += opCode.operandCount + 1
    } while(pc < bytes.size)
}

class Emulator {
    val state = State()

    var currentOp: OpCode? = null

    var opCount = 0

    fun load(bytes: ByteArray, offset: Int) {
        state.memory = ByteArray(offset) + bytes.copyOf(bytes.size + 0x4000)
    }

    fun run() {

        while(true) {
            loadInst()
            exec()
        }
    }

    fun loadInst() {
        val nextInst = state.memory[state.pc]

        currentOp = opCodeFor(nextInst.toUbyte())
        currentOp!!.consume(state.pc, state.memory)

        debug("Executing")
    }

    fun exec() {
        currentOp!!.execute(state)
        opCount++
        if(currentOp !is JMP && currentOp !is CALL && currentOp !is RET) state.pc += currentOp!!.operandCount + 1
        debug("Executed")
    }

    fun debug(action: String) {
        print("\rOps:${opCount} | Flags: ${state.flags} | ${state} | $action ${currentOp}")
    }

    fun dumpStack() {
        println(state.sp.toInt().hex(true))
        (state.sp.toInt()..0x2400).forEach {
            println("${it.hex(true)}  ${state.memory[it].hex()}")
        }
    }

    fun fiddle() {
        state.memory[0] = 0xc3.toByte()
        state.memory[1] = 0
        state.memory[2] = 0x01.toByte()

        state.memory[368] = 0x7

        state.memory[0x59c] = 0xc3.toByte()
        state.memory[0x59d] = 0xc2.toByte()
        state.memory[0x59e] = 0x05.toByte()

    }
}

class Flags {
    var z: Boolean = false
    var s: Boolean = false
    var p: Boolean = false
    var cy: Boolean = false
    var ac: Boolean = false
    var pad: Boolean = false

    override fun toString(): String {
        return "${ind(z, "z")}\t${ind(s,"s")}\t${ind(p,"p")}\t${ind(cy, "cy")}\t${ind(ac, "ac")}\t${ind(pad, "pad")}"
    }

    fun ind(value: Boolean, letter: String) = if(value) "($letter)" else " $letter "
}

class State {
    var a: Ubyte = Ubyte(0)
    var b: Ubyte = Ubyte(0)
    var c: Ubyte = Ubyte(0)
    var d: Ubyte = Ubyte(0)
    var e: Ubyte = Ubyte(0)
    var h: Ubyte = Ubyte(0)
    var l: Ubyte = Ubyte(0)

    var pc = 0.toUshort()
    var sp = 0.toUshort()

    var memory = ByteArray(0)

    val flags = Flags()

    override fun toString(): String {
        return "a:${a.hex()}\tbc:${b.toUnsignedWord(c).hex(true)}\tde:${d.toUnsignedWord(e).hex(true)}\thl:${h.toUnsignedWord(l).hex(true)}\t\tpc:${pc.hex(true)}\tsp:${sp.hex(true)}"
    }

    fun hl() = h.toUnsignedWord(l)
    fun de() = d.toUnsignedWord(e)
    fun bc() = b.toUnsignedWord(c)

    fun ind(value: Number, label: String) = "label:${value}"
}

