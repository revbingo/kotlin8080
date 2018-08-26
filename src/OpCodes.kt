
import unsigned.*

val ZERO = Ubyte(0)
val ONE = Ubyte(1)

fun Number.toWord(loByte: Number) = this.toUshort().shl(8).or(loByte.toUshort())

fun Ushort.hi() = this.shr(8).toUbyte()
fun Ushort.lo() = this.and(0xff).toUbyte()

operator fun Array<Ubyte>.get(addr: Ushort) = this.get(addr.toInt()).toUbyte()
operator fun Array<Ubyte>.set(addr: Ushort, value: Ubyte) = this.set(addr.toInt(), value) //.also { println("set memory ${addr.toInt().hex(true)} to ${value.hex()}")}.also { Exception().printStackTrace() }

val opCodes = mutableMapOf(0x00 to NOP(),
                                0x01 to LXI_B(),
                                0x02 to STAX_B(),
                                0x03 to INX_B(),
                                0x04 to INR_B(),
                                0x05 to DCR_B(),
                                0x06 to MVI_B(),
                                0x07 to RLC(),
                                0x09 to DAD_B(),
                                0x0a to LDAX_B(),
                                0x0b to DCX_B(),
                                0x0c to INR_C(),
                                0x0d to DCR_C(),
                                0x0e to MVI_C(),
                                0x0f to RRC(),
                                0x11 to LXI_D(),
                                0x12 to STAX_D(),
                                0x13 to INX_D(),
                                0x14 to INR_D(),
                                0x15 to DCR_D(),
                                0x16 to MVI_D(),
                                0x17 to RAL(),
                                0x19 to DAD_D(),
                                0x1a to LDAX_D(),
                                0x1b to DCX_D(),
                                0x1c to INR_E(),
                                0x1d to DCR_E(),
                                0x1e to MVI_E(),
                                0x1f to RAR(),
                                0x20 to RIM(),
                                0x21 to LXI_H(),
                                0x22 to SHLD(),
                                0x23 to INX_H(),
                                0x24 to INR_H(),
                                0x25 to DCR_H(),
                                0x26 to MVI_H(),
                                0x27 to DAA(),
                                0x29 to DAD_H(),
                                0x2a to LHLD(),
                                0x2b to DCX_H(),
                                0x2c to INR_L(),
                                0x2d to DCR_L(),
                                0x2e to MVI_L(),
                                0x2f to CMA(),
                                0x30 to SIM(),
                                0x31 to LXI_SP(),
                                0x32 to STA(),
                                0x33 to INX_SP(),
                                0x34 to INR_M(),
                                0x35 to DCR_M(),
                                0x36 to MVI_M(),
                                0x37 to STC(),
                                0x39 to DAD_SP(),
                                0x3a to LDA(),
                                0x3b to DCX_SP(),
                                0x3c to INR_A(),
                                0x3d to DCR_A(),
                                0x3e to MVI_A(),
                                0x3f to CMC(),
                                0x40 to MOV_B_B(),
                                0x41 to MOV_B_C(),
                                0x42 to MOV_B_D(),
                                0x43 to MOV_B_E(),
                                0x44 to MOV_B_H(),
                                0x45 to MOV_B_L(),
                                0x46 to MOV_B_M(),
                                0x47 to MOV_B_A(),
                                0x48 to MOV_C_B(),
                                0x49 to MOV_C_C(),
                                0x4a to MOV_C_D(),
                                0x4b to MOV_C_E(),
                                0x4c to MOV_C_H(),
                                0x4d to MOV_C_L(),
                                0x4e to MOV_C_M(),
                                0x4f to MOV_C_A(),
                                0x50 to MOV_D_B(),
                                0x51 to MOV_D_C(),
                                0x52 to MOV_D_D(),
                                0x53 to MOV_D_E(),
                                0x54 to MOV_D_H(),
                                0x55 to MOV_D_L(),
                                0x56 to MOV_D_M(),
                                0x57 to MOV_D_A(),
                                0x58 to MOV_E_B(),
                                0x59 to MOV_E_C(),
                                0x5a to MOV_E_D(),
                                0x5b to MOV_E_E(),
                                0x5c to MOV_E_H(),
                                0x5d to MOV_E_L(),
                                0x5e to MOV_E_M(),
                                0x5f to MOV_E_A(),
                                0x60 to MOV_H_B(),
                                0x61 to MOV_H_C(),
                                0x62 to MOV_H_D(),
                                0x63 to MOV_H_E(),
                                0x64 to MOV_H_H(),
                                0x65 to MOV_H_L(),
                                0x66 to MOV_H_M(),
                                0x67 to MOV_H_A(),
                                0x68 to MOV_L_B(),
                                0x69 to MOV_L_C(),
                                0x6a to MOV_L_D(),
                                0x6b to MOV_L_E(),
                                0x6c to MOV_L_H(),
                                0x6d to MOV_L_L(),
                                0x6e to MOV_L_M(),
                                0x6f to MOV_L_A(),
                                0x70 to MOV_M_B(),
                                0x71 to MOV_M_C(),
                                0x72 to MOV_M_D(),
                                0x73 to MOV_M_E(),
                                0x74 to MOV_M_H(),
                                0x75 to MOV_M_L(),
                                0x76 to HLT(),
                                0x77 to MOV_M_A(),
                                0x78 to MOV_A_B(),
                                0x79 to MOV_A_C(),
                                0x7a to MOV_A_D(),
                                0x7b to MOV_A_E(),
                                0x7c to MOV_A_H(),
                                0x7d to MOV_A_L(),
                                0x7e to MOV_A_M(),
                                0x7f to MOV_A_A(),
                                0x80 to ADD_B(),
                                0x81 to ADD_C(),
                                0x82 to ADD_D(),
                                0x83 to ADD_E(),
                                0x84 to ADD_H(),
                                0x85 to ADD_L(),
                                0x86 to ADD_M(),
                                0x87 to ADD_A(),
                                0x88 to ADC_B(),
                                0x89 to ADC_C(),
                                0x8a to ADC_D(),
                                0x8b to ADC_E(),
                                0x8c to ADC_H(),
                                0x8d to ADC_L(),
                                0x8e to ADC_M(),
                                0x8f to ADC_A(),
                                0x90 to SUB_B(),
                                0x91 to SUB_C(),
                                0x92 to SUB_D(),
                                0x93 to SUB_E(),
                                0x94 to SUB_H(),
                                0x95 to SUB_L(),
                                0x96 to SUB_M(),
                                0x97 to SUB_A(),
                                0x98 to SBB_B(),
                                0x99 to SBB_C(),
                                0x9a to SBB_D(),
                                0x9b to SBB_E(),
                                0x9c to SBB_H(),
                                0x9d to SBB_L(),
                                0x9e to SBB_M(),
                                0x9f to SBB_A(),
                                0xa0 to ANA_B(),
                                0xa1 to ANA_C(),
                                0xa2 to ANA_D(),
                                0xa3 to ANA_E(),
                                0xa4 to ANA_H(),
                                0xa5 to ANA_L(),
                                0xa6 to ANA_M(),
                                0xa7 to ANA_A(),
                                0xa8 to XRA_B(),
                                0xa9 to XRA_C(),
                                0xaa to XRA_D(),
                                0xab to XRA_E(),
                                0xac to XRA_H(),
                                0xad to XRA_L(),
                                0xae to XRA_M(),
                                0xaf to XRA_A(),
                                0xb0 to ORA_B(),
                                0xb1 to ORA_C(),
                                0xb2 to ORA_D(),
                                0xb3 to ORA_E(),
                                0xb4 to ORA_H(),
                                0xb5 to ORA_L(),
                                0xb6 to ORA_M(),
                                0xb7 to ORA_A(),
                                0xb8 to CMP_B(),
                                0xb9 to CMP_C(),
                                0xba to CMP_D(),
                                0xbb to CMP_E(),
                                0xbc to CMP_H(),
                                0xbd to CMP_L(),
                                0xbe to CMP_M(),
                                0xbf to CMP_A(),
                                0xc0 to RNZ(),
                                0xc1 to POP_B(),
                                0xc2 to JNZ(),
                                0xc3 to JMP(),
                                0xc4 to CNZ(),
                                0xc5 to PUSH_B(),
                                0xc6 to ADI(),
                                0xc7 to RST_0(),
                                0xc8 to RZ(),
                                0xc9 to RET(),
                                0xca to JZ(),
                                0xcc to CZ(),
                                0xcd to CALL(),
                                0xce to ACI(),
                                0xcf to RST_1(),
                                0xd0 to RNC(),
                                0xd1 to POP_D(),
                                0xd2 to JNC(),
                                0xd3 to OUT(),
                                0xd4 to CNC(),
                                0xd5 to PUSH_D(),
                                0xd6 to SUI(),
                                0xd7 to RST_2(),
                                0xd8 to RC(),
                                0xda to JC(),
                                0xdb to IN(),
                                0xdc to CC(),
                                0xde to SBI(),
                                0xdf to RST_3(),
                                0xe0 to RPO(),
                                0xe1 to POP_H(),
                                0xe2 to JPO(),
                                0xe3 to XTHL(),
                                0xe4 to CPO(),
                                0xe5 to PUSH_H(),
                                0xe6 to ANI(),
                                0xe7 to RST_4(),
                                0xe8 to RPE(),
                                0xe9 to PCHL(),
                                0xea to JPE(),
                                0xeb to XCHG(),
                                0xec to CPE(),
                                0xee to XRI(),
                                0xef to RST_5(),
                                0xf0 to RP(),
                                0xf1 to POP_PSW(),
                                0xf2 to JP(),
                                0xf3 to DI(),
                                0xf4 to CP(),
                                0xf5 to PUSH_PSW(),
                                0xf6 to ORI(),
                                0xf7 to RST_6(),
                                0xf8 to RM(),
                                0xf9 to SPHL(),
                                0xfa to JM(),
                                0xfb to EI(),
                                0xfc to CM(),
                                0xfe to CPI(),
                                0xff to RST_7())

fun opCodeFor(opCode: Ubyte): OpCode = opCodes[opCode.toInt()]!!

abstract class OpCode(val opCode: Int, val operandCount: Int = 0, val noAdvance: Boolean = false) {

    var offset: Ushort = Ushort(0)
    override fun toString(): String {
        return "${String.format("%04X", offset.toInt())}\t${this.represent()}"
    }

    fun consume(pc: Ushort, bytes: Array<Ubyte>) {
        offset = pc
        consumeInternal(bytes.sliceArray((pc+1).toInt()..(pc+this.operandCount).toInt()))
    }

    open fun consumeInternal(bytes: Array<Ubyte>) {}
    abstract fun represent(): String

    fun execAndAdvance(state:State): Int {
        val cycles = execute(state)
        if(!noAdvance) {
            state.pc += this.operandCount + 1
        }
        return cycles
    }

    open fun execute(state: State): Int {
        throw RuntimeException("Unimplemented instruction ${this.javaClass.simpleName}")
    }

    fun setFlags(state: State, result: Ubyte) = setFlags(state, result.toUshort())
    fun setFlags(state: State, result: Ushort) {
        state.flags.z = result.and(0xff).toUbyte() == ZERO

        state.flags.s = result.and(0x80).toUbyte() != ZERO

        state.flags.cy = result > 0xff

        state.flags.lastFlaggedValue = result.and(0xff)
    }

    fun parity(result: Ubyte): Boolean {
        var p = 0
        var work = result.and(1.shl(8)-1)
        for(i in (0..8)) {
            if(work.and(0x1).toInt() == 0x1) p++
            work = work.shr(1)
        }
        return (0 == (p.and(0x1)))
    }

    fun addA(state: State, byte: Ubyte): Ubyte {
        val result = state.a.toUshort() + byte.toUshort()
        setFlags(state, result)
        val halfCarry = (1.xor(result)).xor(state.a).and(0x10)
        state.flags.ac = (halfCarry > 0)
        state.a = result.and(0xff).toUbyte()
        return result.and(0xff).toUbyte()
    }

    fun subA(state: State, byte: Ubyte): Ubyte {
        val result = state.a.toUshort() - byte.toUshort()
        setFlags(state, result)
        val halfCarry = (1.xor(result)).xor(state.a).and(0x10)
        state.flags.ac = (halfCarry > 0)
        state.a = result.and(0xff).toUbyte()
        return result.and(0xff).toUbyte()
    }


}

abstract class NoArgOpCode(opCode: Int, noAdvance: Boolean = false): OpCode(opCode, 0, noAdvance) {
    override fun represent(): String = this.javaClass.simpleName.replaceFirst("_", " ").replaceFirst("_", ",")
}

abstract class ByteOpCode(opCode: Int, noAdvance: Boolean = false): OpCode(opCode, 1, noAdvance) {
    var value: Ubyte? = null
    override fun represent(): String = "${this.javaClass.simpleName.replaceFirst("_", "\t")}${if(this.javaClass.simpleName.contains("_")) "," else "\t"}#${value.hex()}"

    override fun consumeInternal(bytes: Array<Ubyte>) {
        value = bytes[0].toUbyte()
    }
}

abstract class WordOpCode(opCode: Int, noAdvance: Boolean = false): OpCode(opCode, 2, noAdvance) {
    var value: Ushort? = null
        get() = hi!!.toUshort().shl(8).or(lo!!.toUshort())
    var hi: Ubyte? = null
    var lo: Ubyte? = null

    override fun represent(): String = "${this.javaClass.simpleName.replaceFirst("_", "\t")}${if(this.javaClass.simpleName.contains("_")) "," else "\t"}#${value.hex(isWord = true)}"

    override fun consumeInternal(bytes: Array<Ubyte>) {
        hi = bytes[1].toUbyte()
        lo = bytes[0].toUbyte()
    }
}

// *** JUMPS ***
abstract class JumpOpCode(opCode: Int): WordOpCode(opCode, true) {
    val cycles: Int = 10

    fun jumpIf(condition: Boolean, state: State): Int {
        if(condition) {
            state.pc = value!!
        } else {
            state.pc += this.operandCount + 1
        }
        return cycles
    }
}

class JMP:JumpOpCode(0xc3) {
    override fun execute(state: State) = jumpIf(true, state)
}
class JM:JumpOpCode(0xfa) {
    override fun execute(state: State) = jumpIf(state.flags.s, state)
}
class JP:JumpOpCode(0xf2) {
    override fun execute(state: State) = jumpIf(!state.flags.s, state)
}
class JPE:JumpOpCode(0xea) {
    override fun execute(state: State) = jumpIf(state.flags.p, state)
}
class JPO:JumpOpCode(0xe2) {
    override fun execute(state: State) = jumpIf(!state.flags.p, state)
}
class JC:JumpOpCode(0xda) {
    override fun execute(state: State) = jumpIf(state.flags.cy, state)
}
class JNC:JumpOpCode(0xd2) {
    override fun execute(state: State) = jumpIf(!state.flags.cy, state)
}
class JZ:JumpOpCode(0xca) {
    override fun execute(state: State) = jumpIf(state.flags.z, state)
}
class JNZ:JumpOpCode(0xc2) {
    override fun execute(state: State) = jumpIf(!state.flags.z, state)
}

// **** CALLS

abstract class CallOpCode(opCode: Int): WordOpCode(opCode, true) {
    val ACTION_CYCLES: Int = 17
    val NO_ACTION_CYCLES: Int = 11
    fun callIf(condition: Boolean, state: State): Int {
        return if(condition) {
            state.pc += 3
            state.memory[state.sp - 1] = state.pc.hi()
            state.memory[state.sp - 2] = state.pc.lo()
            state.sp -= 2

            state.pc = value!!
            ACTION_CYCLES
        } else {
            state.pc += this.operandCount + 1
            NO_ACTION_CYCLES
        }
    }
}
class CM:CallOpCode(0xfc) {
    override fun execute(state: State) = callIf(state.flags.s, state)
}
class CP:CallOpCode(0xf4) {
    override fun execute(state: State) = callIf(!state.flags.s, state)
}
class CPE:CallOpCode(0xec) {
    override fun execute(state: State) = callIf(state.flags.p, state)
}
class CPO:CallOpCode(0xe4) {
    override fun execute(state: State) = callIf(!state.flags.p, state)
}
class CC:CallOpCode(0xdc) {
    override fun execute(state: State) = callIf(state.flags.cy, state)
}
class CNC:CallOpCode(0xd4) {
    override fun execute(state: State) = callIf(!state.flags.cy, state)
}
class CZ:CallOpCode(0xcc) {
    override fun execute(state: State) = callIf(state.flags.z, state)
}
class CNZ:CallOpCode(0xc4) {
    override fun execute(state: State) = callIf(!state.flags.z, state)
}
class CALL:CallOpCode(0xcd) {
    override fun execute(state: State) = callIf(true, state)
}

// *** RETURNS
abstract class ReturnOpCode(opCode:Int):NoArgOpCode(opCode, true) {
    val ACTION_CYCLES: Int = 11
    val NO_ACTION_CYCLES: Int = 5
    fun returnIf(condition: Boolean, state: State): Int {
        return if(condition) {
            state.pc = state.memory[state.sp + 1].toWord(state.memory[state.sp])
            state.sp += 2
            ACTION_CYCLES
        } else {
            state.pc += this.operandCount + 1
            NO_ACTION_CYCLES
        }
    }
}
class RM:ReturnOpCode(0xf8) {
    override fun execute(state: State) = returnIf(state.flags.s, state)
}
class RP:ReturnOpCode(0xf0) {
    override fun execute(state: State) = returnIf(!state.flags.s, state)
}
class RPE:ReturnOpCode(0xe8) {
    override fun execute(state: State) = returnIf(state.flags.p, state)
}
class RPO:ReturnOpCode(0xe0) {
    override fun execute(state: State) = returnIf(!state.flags.p, state)
}
class RC:ReturnOpCode(0xd8) {
    override fun execute(state: State) = returnIf(state.flags.cy, state)
}
class RNC:ReturnOpCode(0xd0) {
    override fun execute(state: State) = returnIf(!state.flags.cy, state)
}
class RZ:ReturnOpCode(0xc8) {
    override fun execute(state: State) = returnIf(state.flags.z, state)
}
class RNZ:ReturnOpCode(0xc0) {
    override fun execute(state: State) = returnIf(!state.flags.z, state)
}
class RET:ReturnOpCode(0xc9) {
    override fun execute(state: State) = returnIf(true, state)
}

// ***** STACK OPS
abstract class StackOpCode(opCode: Int): NoArgOpCode(opCode)

class POP_B:StackOpCode(0xc1) {
    override fun execute(state: State): Int {
        val popped = state.pop()
        state.c = popped.lo()
        state.b = popped.hi()
        return 10
    }
}
class PUSH_B:StackOpCode(0xc5) {
    override fun execute(state: State): Int {
        state.push(state.bc())
        return 11
    }
}
class POP_D:StackOpCode(0xd1) {
    override fun execute(state: State): Int {
        val popped = state.pop()
        state.e = popped.lo()
        state.d = popped.hi()
        return 10
    }
}

class PUSH_D:StackOpCode(0xd5) {
    override fun execute(state: State): Int {
        state.push(state.de())
        return 11
    }
}
class POP_H:StackOpCode(0xe1) {
    override fun execute(state: State): Int {
        val popped = state.pop()
        state.l = popped.lo()
        state.h = popped.hi()
        return 10
    }
}
class PUSH_H:StackOpCode(0xe5) {
    override fun execute(state: State): Int {
        state.push(state.hl())
        return 11
    }
}
class POP_PSW:StackOpCode(0xf1) {
    override fun execute(state: State): Int {
        val popped = state.pop()
        state.flags.fromByte(popped.lo())
        state.a = popped.hi()
        return 10
    }
}
class PUSH_PSW:StackOpCode(0xf5) {
    override fun execute(state: State): Int {
        state.push(state.a.toWord(state.flags.asByte()))
        return 11
    }
}

// === ACCUMULATOR OPS
abstract class AccumulatorOpCode(opCode: Int): NoArgOpCode(opCode) {

    val CYCLES = 4

    fun setAccum(state: State, other: Ubyte, func: Ushort.(Ushort) -> Ushort): Int {
        val rhs = other.toUshort()
        val lhs = state.a.toUshort()
        val result = func.invoke(lhs, rhs)

        setFlags(state, result)

        val halfCarry = (rhs.xor(result)).xor(lhs).and(0x10)
        state.flags.ac = (halfCarry > 0)
        state.a = result.and(0xff).toUbyte()
        return CYCLES
    }

    fun withAccum(state: State, other: Ubyte, func: Ushort.(Ushort) -> Ushort): Int {
        val rhs = other.toUshort()
        val lhs = state.a.toUshort()
        val result = func.invoke(lhs, rhs)
        setFlags(state, result)
        val halfCarry = (rhs.xor(result)).xor(lhs).and(0x10)
        state.flags.ac = (halfCarry > 0)
        return CYCLES
    }

    fun withCarry(state: State, byte: Ubyte): Ubyte = byte + if(state.flags.cy) 0x1 else 0x0
}

class ADD_B:AccumulatorOpCode(0x80) {
    override fun execute(state: State) = setAccum(state, state.b, Ushort::plus)
}
class ADD_C:AccumulatorOpCode(0x81) {
    override fun execute(state: State) = setAccum(state, state.c, Ushort::plus)
}
class ADD_D:AccumulatorOpCode(0x82) {
    override fun execute(state: State) = setAccum(state, state.d, Ushort::plus)
}
class ADD_E:AccumulatorOpCode(0x83) {
    override fun execute(state: State) = setAccum(state, state.e, Ushort::plus)
}
class ADD_H:AccumulatorOpCode(0x84) {
    override fun execute(state: State) = setAccum(state, state.h, Ushort::plus)
}
class ADD_L:AccumulatorOpCode(0x85) {
    override fun execute(state: State) = setAccum(state, state.l, Ushort::plus)
}
class ADD_M:AccumulatorOpCode(0x86) {
    override fun execute(state: State): Int {
        setAccum(state, state.heap(), Ushort::plus)
        return 7
    }
}
class ADD_A:AccumulatorOpCode(0x87) {
    override fun execute(state: State) = setAccum(state, state.a, Ushort::plus)
}
class ADC_B:AccumulatorOpCode(0x88) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.b), Ushort::plus)
}
class ADC_C:AccumulatorOpCode(0x89) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.c), Ushort::plus)
}
class ADC_D:AccumulatorOpCode(0x8a) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.d), Ushort::plus)
}
class ADC_E:AccumulatorOpCode(0x8b) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.e), Ushort::plus)
}
class ADC_H:AccumulatorOpCode(0x8c) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.h), Ushort::plus)
}
class ADC_L:AccumulatorOpCode(0x8d) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.l), Ushort::plus)
}
class ADC_M:AccumulatorOpCode(0x8e) {
    override fun execute(state: State): Int {
        setAccum(state, withCarry(state, state.heap()), Ushort::plus)
        return 7
    }
}
class ADC_A:AccumulatorOpCode(0x8f) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.a), Ushort::plus)
}
class SUB_B:AccumulatorOpCode(0x90) {
    override fun execute(state: State) = setAccum(state, state.b, Ushort::minus)
}
class SUB_C:AccumulatorOpCode(0x91) {
    override fun execute(state: State) = setAccum(state, state.c, Ushort::minus)
}
class SUB_D:AccumulatorOpCode(0x92) {
    override fun execute(state: State) = setAccum(state, state.d, Ushort::minus)
}
class SUB_E:AccumulatorOpCode(0x93) {
    override fun execute(state: State) = setAccum(state, state.e, Ushort::minus)
}
class SUB_H:AccumulatorOpCode(0x94) {
    override fun execute(state: State) = setAccum(state, state.h, Ushort::minus)
}
class SUB_L:AccumulatorOpCode(0x95) {
    override fun execute(state: State) = setAccum(state, state.l, Ushort::minus)
}
class SUB_M:AccumulatorOpCode(0x96) {
    override fun execute(state: State): Int {
        setAccum(state, state.heap(), Ushort::minus)
        return 7
    }
}
class SUB_A:AccumulatorOpCode(0x97) {
    override fun execute(state: State) = setAccum(state, state.a, Ushort::minus)
}
class SBB_B:AccumulatorOpCode(0x98) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.b), Ushort::minus)
}
class SBB_C:AccumulatorOpCode(0x99) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.c), Ushort::minus)
}
class SBB_D:AccumulatorOpCode(0x9a) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.d), Ushort::minus)
}
class SBB_E:AccumulatorOpCode(0x9b) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.e), Ushort::minus)
}
class SBB_H:AccumulatorOpCode(0x9c) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.h), Ushort::minus)
}
class SBB_L:AccumulatorOpCode(0x9d) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.l), Ushort::minus)
}
class SBB_M:AccumulatorOpCode(0x9e) {
    override fun execute(state: State): Int {
        setAccum(state, withCarry(state, state.heap()), Ushort::minus)
        return 7
    }
}
class SBB_A:AccumulatorOpCode(0x9f) {
    override fun execute(state: State) = setAccum(state, withCarry(state, state.a), Ushort::minus)
}

class ANA_B:AccumulatorOpCode(0xa0) {
    override fun execute(state: State) = setAccum(state, state.b, Ushort::and)
}
class ANA_C:AccumulatorOpCode(0xa1) {
    override fun execute(state: State) = setAccum(state, state.c, Ushort::and)
}
class ANA_D:AccumulatorOpCode(0xa2) {
    override fun execute(state: State) = setAccum(state, state.d, Ushort::and)
}
class ANA_E:AccumulatorOpCode(0xa3) {
    override fun execute(state: State) = setAccum(state, state.e, Ushort::and)
}
class ANA_H:AccumulatorOpCode(0xa4) {
    override fun execute(state: State) = setAccum(state, state.h, Ushort::and)
}
class ANA_L:AccumulatorOpCode(0xa5) {
    override fun execute(state: State) = setAccum(state, state.l, Ushort::and)
}
class ANA_M:AccumulatorOpCode(0xa6) {
    override fun execute(state: State): Int {
        setAccum(state, state.heap(), Ushort::and)
        return 7
    }
}
class ANA_A:AccumulatorOpCode(0xa7) {
    override fun execute(state: State) = setAccum(state, state.a, Ushort::and)
}
class XRA_B:AccumulatorOpCode(0xa8) {
    override fun execute(state: State) = setAccum(state, state.b, Ushort::xor)
}
class XRA_C:AccumulatorOpCode(0xa9) {
    override fun execute(state: State) = setAccum(state, state.c, Ushort::xor)
}
class XRA_D:AccumulatorOpCode(0xaa) {
    override fun execute(state: State) = setAccum(state, state.d, Ushort::xor)
}
class XRA_E:AccumulatorOpCode(0xab) {
    override fun execute(state: State) = setAccum(state, state.e, Ushort::xor)
}
class XRA_H:AccumulatorOpCode(0xac) {
    override fun execute(state: State) = setAccum(state, state.h, Ushort::xor)
}
class XRA_L:AccumulatorOpCode(0xad) {
    override fun execute(state: State) = setAccum(state, state.l, Ushort::xor)
}
class XRA_M:AccumulatorOpCode(0xae) {
    override fun execute(state: State): Int {
        setAccum(state, state.heap(), Ushort::xor)
        return 7
    }
}
class XRA_A:AccumulatorOpCode(0xaf) {
    override fun execute(state: State) = setAccum(state, state.a, Ushort::xor)
}
class ORA_B:AccumulatorOpCode(0xb0) {
    override fun execute(state: State) = setAccum(state, state.b, Ushort::or)
}
class ORA_C:AccumulatorOpCode(0xb1) {
    override fun execute(state: State) = setAccum(state, state.c, Ushort::or)
}
class ORA_D:AccumulatorOpCode(0xb2) {
    override fun execute(state: State) = setAccum(state, state.d, Ushort::or)
}
class ORA_E:AccumulatorOpCode(0xb3) {
    override fun execute(state: State) = setAccum(state, state.e, Ushort::or)
}
class ORA_H:AccumulatorOpCode(0xb4) {
    override fun execute(state: State) = setAccum(state, state.h, Ushort::or)
}
class ORA_L:AccumulatorOpCode(0xb5) {
    override fun execute(state: State) = setAccum(state, state.l, Ushort::or)
}
class ORA_M:AccumulatorOpCode(0xb6) {
    override fun execute(state: State): Int {
        setAccum(state, state.heap(), Ushort::or)
        return 7
    }
}
class ORA_A:AccumulatorOpCode(0xb7) {
    override fun execute(state: State) = setAccum(state, state.a, Ushort::or)
}
class CMP_B:AccumulatorOpCode(0xb8) {
    override fun execute(state: State) = withAccum(state, state.b, Ushort::minus)
}
class CMP_C:AccumulatorOpCode(0xb9) {
    override fun execute(state: State) = withAccum(state, state.c, Ushort::minus)
}
class CMP_D:AccumulatorOpCode(0xba) {
    override fun execute(state: State) = withAccum(state, state.d, Ushort::minus)
}
class CMP_E:AccumulatorOpCode(0xbb) {
    override fun execute(state: State) = withAccum(state, state.e, Ushort::minus)
}
class CMP_H:AccumulatorOpCode(0xbc) {
    override fun execute(state: State) = withAccum(state, state.h, Ushort::minus)
}
class CMP_L:AccumulatorOpCode(0xbd) {
    override fun execute(state: State) = withAccum(state, state.l, Ushort::minus)
}
class CMP_M:AccumulatorOpCode(0xbe) {
    override fun execute(state: State): Int {
        withAccum(state, state.heap(), Ushort::minus)
        return 7
    }
}
class CMP_A:AccumulatorOpCode(0xbf) {
    override fun execute(state: State) = withAccum(state, state.a, Ushort::minus)
}
// ---- ==================================================== --------
// ---- ======================a============================== --------

class NOP:NoArgOpCode(0x00) {
    override fun execute(state: State): Int { return 4 }
}
class LXI_B:WordOpCode(0x01) {
    override fun execute(state: State): Int {
        state.b = hi!!
        state.c = lo!!
        return 10
    }
}
class STAX_B:NoArgOpCode(0x02) {
    override fun execute(state: State): Int {
        state.memory[state.bc()] = state.a
        return 7
    }
}
class INX_B:NoArgOpCode(0x03) {
    override fun execute(state: State): Int {
        val newValue = state.bc() + 1
        state.b = newValue.hi()
        state.c = newValue.lo()
        return 5
    }
}
class INR_B:NoArgOpCode(0x04) {
    override fun execute(state: State): Int {
        state.b += 1
        setFlags(state, state.b)
        return 5
    }
}
class DCR_B:NoArgOpCode(0x05) {
    override fun execute(state: State): Int {
        state.b = state.b - 1
        setFlags(state, state.b)
        return 5
    }
}
class MVI_B:ByteOpCode(0x06) {
    override fun execute(state: State): Int {
        state.b = value!!
        return 7
    }
}
class RLC:NoArgOpCode(0x07) {
    override fun execute(state: State): Int {
        val highOrder = state.a.and(0x80) != 0x0.toUbyte()
        state.a = state.a.shl(1)

        state.flags.cy = highOrder
        if(highOrder) {
            state.a = state.a.or(ONE)
        }
        return 4
    }
}
class DAD_B:NoArgOpCode(0x09) {
    override fun execute(state: State): Int {
        val newVal = state.hl().toUint() + state.bc().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
        return 10
    }
}
class LDAX_B:NoArgOpCode(0x0a) {
    override fun execute(state: State): Int {
        state.a = state.memory[state.bc()]
        return 7
    }
}
class DCX_B:NoArgOpCode(0x0b) {
    override fun execute(state: State): Int {
        val newValue = state.bc() - 1
        state.b = newValue.hi()
        state.c = newValue.lo()
        return 5
    }
}
class INR_C:NoArgOpCode(0x0c) {
    override fun execute(state: State): Int {
        state.c += 1
        setFlags(state, state.c)
        return 5
    }
}
class DCR_C:NoArgOpCode(0x0d) {
    override fun execute(state: State): Int {
        state.c -= 1
        setFlags(state, state.c)
        return 5
    }
}
class MVI_C:ByteOpCode(0x0e) {
    override fun execute(state: State): Int {
        state.c = value!!
        return 7
    }
}
class RRC:NoArgOpCode(0x0f) {
    override fun execute(state: State): Int {
        val lowOrder = state.a.and(0x1) != 0x0.toUbyte()
        state.a = state.a.shr(1)

        state.flags.cy = lowOrder
        if(lowOrder) {
            state.a = state.a.or(0x80)
        }
        return 4
    }
}
class LXI_D:WordOpCode(0x11) {
    override fun execute(state: State): Int {
        state.d = hi!!
        state.e = lo!!
        return 10
    }
}
class STAX_D:NoArgOpCode(0x12) {
    override fun execute(state: State): Int {
        state.memory[state.de()] = state.a
        return 7
    }
}
class INX_D:NoArgOpCode(0x13) {
    override fun execute(state: State): Int {
        val newValue = state.de() + 1
        state.d = newValue.hi()
        state.e = newValue.lo()
        return 5
    }
}
class INR_D:NoArgOpCode(0x14){
    override fun execute(state: State): Int {
        state.d += 1
        setFlags(state, state.d)
        return 5
    }
}

class DCR_D:NoArgOpCode(0x15) {
    override fun execute(state: State): Int {
        state.d -= 1
        setFlags(state, state.d)
        return 5
    }
}
class MVI_D:ByteOpCode(0x16) {
    override fun execute(state: State): Int {
        state.d = value!!
        return 7
    }
}
class RAL:NoArgOpCode(0x17) {
    override fun execute(state: State): Int {
        val highOrder = state.a.and(0x80) != 0x0.toUbyte()
        state.a = state.a.shl(1).or(if(state.flags.cy) ONE else ZERO)
        state.flags.cy = highOrder
        return 4
    }
}
class DAD_D:NoArgOpCode(0x19) {
    override fun execute(state: State): Int {
        val newVal = state.hl().toUint() + state.de().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
        return 10
    }
}
class LDAX_D:NoArgOpCode(0x1a) {
    override fun execute(state: State): Int {
        state.a = state.memory[state.de()].toUbyte()
        return 7
    }
}
class DCX_D:NoArgOpCode(0x1b) {
    override fun execute(state: State): Int {
        val newValue = state.de() - 1
        state.d = newValue.hi()
        state.e = newValue.lo()
        return 5
    }
}
class INR_E:NoArgOpCode(0x1c) {
    override fun execute(state: State): Int {
        state.e += 1
        setFlags(state, state.e)
        return 5
    }
}
class DCR_E:NoArgOpCode(0x1d) {
    override fun execute(state: State): Int {
        state.e -= 1
        setFlags(state, state.e)
        return 5
    }
}
class MVI_E:ByteOpCode(0x1e) {
    override fun execute(state: State): Int {
        state.e = value!!
        return 7
    }
}
class RAR:NoArgOpCode(0x1f) {
    override fun execute(state: State): Int {
        val lowOrder = state.a.and(0x1) != 0x0.toUbyte()
        state.a = state.a.shr(1).or(if(state.flags.cy) 0x80.toUbyte() else ZERO)
        state.flags.cy = lowOrder
        return 4
    }
}

class LXI_H:WordOpCode(0x21) {
    override fun execute(state: State): Int {
        state.h = hi!!
        state.l = lo!!
        return 10
    }
}
class SHLD:WordOpCode(0x22) {
    override fun execute(state: State): Int {
        state.memory[value!!] = state.l
        state.memory[value!! + 1] = state.h
        return 16
    }
}
class INX_H:NoArgOpCode(0x23) {
    override fun execute(state: State): Int {
        val newValue = state.hl() + 1
        state.h = newValue.hi()
        state.l = newValue.lo()
        return 5
    }
}

class INR_H:NoArgOpCode(0x24) {
    override fun execute(state: State): Int {
        state.h += 1
        setFlags(state, state.h)
        return 5
    }
}
class DCR_H:NoArgOpCode(0x25)  {
    override fun execute(state: State): Int {
        state.h -= 1
        setFlags(state, state.h)
        return 5
    }
}
class MVI_H:ByteOpCode(0x26) {
    override fun execute(state: State): Int {
        state.h = value!!
        return 7
    }
}

class DAD_H:NoArgOpCode(0x29) {
    override fun execute(state: State): Int {
        val newVal = state.hl().toUint() + state.hl().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
        return 10
    }
}
class LHLD:WordOpCode(0x2a) {
    override fun execute(state: State): Int {
        state.h = state.memory[value!! + 1]
        state.l = state.memory[value!!]
        return 16
    }
}
class DCX_H:NoArgOpCode(0x2b) {
    override fun execute(state: State): Int {
        val newValue = state.hl() - 1
        state.h = newValue.hi()
        state.l = newValue.lo()
        return 5
    }
}
class INR_L:NoArgOpCode(0x2c) {
    override fun execute(state: State): Int {
        state.l += 1
        setFlags(state, state.l)
        return 5
    }
}
class DCR_L:NoArgOpCode(0x2d) {
    override fun execute(state: State): Int {
        state.l -= 1
        setFlags(state, state.l)
        return 5
    }
}
class MVI_L:ByteOpCode(0x2e) {
    override fun execute(state: State): Int {
        state.l = value!!
        return 7
    }
}
class CMA:NoArgOpCode(0x2f) {
    override fun execute(state: State): Int {
        state.a = state.a.inv()
        return 4
    }
}
class LXI_SP:WordOpCode(0x31) {
    override fun execute(state: State): Int {
        state.sp = value!!
        return 10
    }
}
class STA:WordOpCode(0x32) {
    override fun execute(state: State): Int {
        state.memory[value!!] = state.a
        return 13
    }
}
class INX_SP:NoArgOpCode(0x33) {
    override fun execute(state: State): Int {
        state.sp += 1
        return 5
    }
}
class INR_M:NoArgOpCode(0x34) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.memory[state.hl()] + 1
        setFlags(state, state.memory[state.hl()])
        return 10
    }
}
class DCR_M:NoArgOpCode(0x35) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.memory[state.hl()] - 1
        setFlags(state, state.memory[state.hl()])
        return 10
    }
}
class MVI_M:ByteOpCode(0x36) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = value!!
        return 10
    }
}
class STC:NoArgOpCode(0x37) {
    override fun execute(state: State): Int {
        state.flags.cy = true
        return 4
    }
}
class DAD_SP:NoArgOpCode(0x39) {
    override fun execute(state: State): Int {
        val newVal = state.hl().toUint() + state.sp.toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
        return 10
    }
}
class LDA:WordOpCode(0x3a) {
    override fun execute(state: State): Int {
        state.a = state.memory[value!!]
        return 13
    }
}
class DCX_SP:NoArgOpCode(0x3b) {
    override fun execute(state: State): Int {
        state.sp -= 1
        return 5
    }
}
class INR_A:NoArgOpCode(0x3c) {
    override fun execute(state: State): Int {
        state.a += 1
        setFlags(state, state.a)
        return 5
    }
}
class DCR_A:NoArgOpCode(0x3d) {
    override fun execute(state: State): Int {
        state.a -= 1
        setFlags(state, state.a)
        return 5
    }
}
class MVI_A:ByteOpCode(0x3e) {
    override fun execute(state: State): Int {
        state.a = value!!
        return 7
    }
}
class CMC:NoArgOpCode(0x3f) {
    override fun execute(state: State): Int {
        state.flags.cy = !state.flags.cy
        return 4
    }
}
class MOV_B_B:NoArgOpCode(0x40) {
    override fun execute(state: State): Int {
        state.b = state.b
        return 5
    }
}
class MOV_B_C:NoArgOpCode(0x41) {
    override fun execute(state: State): Int {
        state.b = state.c
        return 5
    }
}
class MOV_B_D:NoArgOpCode(0x42) {
    override fun execute(state: State): Int {
        state.b = state.d
        return 5
    }
}
class MOV_B_E:NoArgOpCode(0x43) {
    override fun execute(state: State): Int {
        state.b = state.e
        return 5
    }
}
class MOV_B_H:NoArgOpCode(0x44) {
    override fun execute(state: State): Int {
        state.b = state.h
        return 5
    }
}
class MOV_B_L:NoArgOpCode(0x45) {
    override fun execute(state: State): Int {
        state.b = state.l
        return 5
    }
}
class MOV_B_M:NoArgOpCode(0x46) {
    override fun execute(state: State): Int {
        state.b = state.memory[state.hl()]
        return 7
    }
}
class MOV_B_A:NoArgOpCode(0x47) {
    override fun execute(state: State): Int {
        state.b = state.a
        return 5
    }
}
class MOV_C_B:NoArgOpCode(0x48) {
    override fun execute(state: State): Int {
        state.c = state.b
        return 5
    }
}
class MOV_C_C:NoArgOpCode(0x49) {
    override fun execute(state: State): Int {
        state.c = state.c
        return 5
    }
}
class MOV_C_D:NoArgOpCode(0x4a) {
    override fun execute(state: State): Int {
        state.c = state.d
        return 5
    }
}
class MOV_C_E:NoArgOpCode(0x4b) {
    override fun execute(state: State): Int {
        state.c = state.e
        return 5
    }
}
class MOV_C_H:NoArgOpCode(0x4c) {
    override fun execute(state: State): Int {
        state.c = state.h
        return 5
    }
}
class MOV_C_L:NoArgOpCode(0x4d) {
    override fun execute(state: State): Int {
        state.c = state.l
        return 5
    }
}
class MOV_C_M:NoArgOpCode(0x4e) {
    override fun execute(state: State): Int {
        state.c = state.memory[state.hl()]
        return 7
    }
}
class MOV_C_A:NoArgOpCode(0x4f) {
    override fun execute(state: State): Int {
        state.c = state.a
        return 5
    }
}
class MOV_D_B:NoArgOpCode(0x50) {
    override fun execute(state: State): Int {
        state.d = state.b
        return 5
    }
}
class MOV_D_C:NoArgOpCode(0x51) {
    override fun execute(state: State): Int {
        state.d = state.c
        return 5
    }
}
class MOV_D_D:NoArgOpCode(0x52) {
    override fun execute(state: State): Int {
        state.d = state.d
        return 5
    }
}
class MOV_D_E:NoArgOpCode(0x53) {
    override fun execute(state: State): Int {
        state.d = state.e
        return 5
    }
}
class MOV_D_H:NoArgOpCode(0x54) {
    override fun execute(state: State): Int {
        state.d = state.h
        return 5
    }
}
class MOV_D_L:NoArgOpCode(0x55) {
    override fun execute(state: State): Int {
        state.d = state.l
        return 5
    }
}
class MOV_D_M:NoArgOpCode(0x56) {
    override fun execute(state: State): Int {
        state.d = state.memory[state.hl()]
        return 7
    }
}
class MOV_D_A:NoArgOpCode(0x57) {
    override fun execute(state: State): Int {
        state.d = state.a
        return 5
    }
}
class MOV_E_B:NoArgOpCode(0x58) {
    override fun execute(state: State): Int {
        state.e = state.b
        return 5
    }
}
class MOV_E_C:NoArgOpCode(0x59) {
    override fun execute(state: State): Int {
        state.e = state.c
        return 5
    }
}
class MOV_E_D:NoArgOpCode(0x5a) {
    override fun execute(state: State): Int {
        state.e = state.d
        return 5
    }
}
class MOV_E_E:NoArgOpCode(0x5b) {
    override fun execute(state: State): Int {
        state.e = state.e
        return 5
    }
}
class MOV_E_H:NoArgOpCode(0x5c) {
    override fun execute(state: State): Int {
        state.e = state.h
        return 5
    }
}
class MOV_E_L:NoArgOpCode(0x5d) {
    override fun execute(state: State): Int {
        state.e = state.l
        return 5
    }
}
class MOV_E_M:NoArgOpCode(0x5e) {
    override fun execute(state: State): Int {
        state.e = state.memory[state.hl()]
        return 7
    }
}
class MOV_E_A:NoArgOpCode(0x5f) {
    override fun execute(state: State): Int {
        state.e = state.a
        return 5
    }
}
class MOV_H_B:NoArgOpCode(0x60) {
    override fun execute(state: State): Int {
        state.h = state.b
        return 5
    }
}
class MOV_H_C:NoArgOpCode(0x61) {
    override fun execute(state: State): Int {
        state.h = state.c
        return 5
    }
}
class MOV_H_D:NoArgOpCode(0x62) {
    override fun execute(state: State): Int {
        state.h = state.d
        return 5
    }
}
class MOV_H_E:NoArgOpCode(0x63) {
    override fun execute(state: State): Int {
        state.h = state.e
        return 5
    }
}
class MOV_H_H:NoArgOpCode(0x64) {
    override fun execute(state: State): Int {
        state.h = state.h
        return 5
    }
}
class MOV_H_L:NoArgOpCode(0x65) {
    override fun execute(state: State): Int {
        state.h = state.l
        return 5
    }
}
class MOV_H_M:NoArgOpCode(0x66) {
    override fun execute(state: State): Int {
        state.h = state.memory[state.hl()]
        return 7
    }
}
class MOV_H_A:NoArgOpCode(0x67) {
    override fun execute(state: State): Int {
        state.h = state.a
        return 5
    }
}
class MOV_L_B:NoArgOpCode(0x68) {
    override fun execute(state: State): Int {
        state.l = state.b
        return 5
    }
}
class MOV_L_C:NoArgOpCode(0x69) {
    override fun execute(state: State): Int {
        state.l = state.c
        return 5
    }
}
class MOV_L_D:NoArgOpCode(0x6a) {
    override fun execute(state: State): Int {
        state.l = state.d
        return 5
    }
}
class MOV_L_E:NoArgOpCode(0x6b) {
    override fun execute(state: State): Int {
        state.l = state.e
        return 5
    }
}
class MOV_L_H:NoArgOpCode(0x6c) {
    override fun execute(state: State): Int {
        state.l = state.h
        return 5
    }
}
class MOV_L_L:NoArgOpCode(0x6d) {
    override fun execute(state: State): Int {
        state.l = state.l
        return 5
    }
}
class MOV_L_M:NoArgOpCode(0x6e) {
    override fun execute(state: State): Int {
        state.l = state.memory[state.hl()]
        return 7
    }
}
class MOV_L_A:NoArgOpCode(0x6f) {
    override fun execute(state: State): Int {
        state.l = state.a
        return 5
    }
}
class MOV_M_B:NoArgOpCode(0x70) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.b
        return 7
    }
}
class MOV_M_C:NoArgOpCode(0x71) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.c
        return 7
    }
}
class MOV_M_D:NoArgOpCode(0x72) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.d
        return 7
    }
}
class MOV_M_E:NoArgOpCode(0x73) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.e
        return 7
    }
}
class MOV_M_H:NoArgOpCode(0x74) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.h
        return 7
    }
}
class MOV_M_L:NoArgOpCode(0x75) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.l
        return 7
    }
}
class HLT:NoArgOpCode(0x76) {
    override fun execute(state: State): Int {
        state.halt()
        return 0
    }
}
class MOV_M_A:NoArgOpCode(0x77) {
    override fun execute(state: State): Int {
        state.memory[state.hl()] = state.a
        return 7
    }
}
class MOV_A_B:NoArgOpCode(0x78) {
    override fun execute(state: State): Int {
        state.a = state.b
        return 5
    }
}
class MOV_A_C:NoArgOpCode(0x79) {
    override fun execute(state: State): Int {
        state.a = state.c
        return 5
    }
}
class MOV_A_D:NoArgOpCode(0x7a) {
    override fun execute(state: State): Int {
        state.a = state.d
        return 5
    }
}
class MOV_A_E:NoArgOpCode(0x7b) {
    override fun execute(state: State): Int {
        state.a = state.e
        return 5
    }
}
class MOV_A_H:NoArgOpCode(0x7c) {
    override fun execute(state: State): Int {
        state.a = state.h
        return 5
    }
}
class MOV_A_L:NoArgOpCode(0x7d) {
    override fun execute(state: State): Int {
        state.a = state.l
        return 5
    }
}
class MOV_A_M:NoArgOpCode(0x7e) {
    override fun execute(state: State): Int {
        state.a = state.memory[state.hl()]
        return 7
    }
}
class MOV_A_A:NoArgOpCode(0x7f) {
    override fun execute(state: State): Int {
        state.a = state.a
        return 5
    }
}


class ADI:ByteOpCode(0xc6) {
    override fun execute(state: State): Int {
        state.a = addA(state, value!!)
        return 7
    }
}

class ACI:ByteOpCode(0xce) {
    override fun execute(state: State): Int {
        val valAndCarry = value!! + if(state.flags.cy) ONE else ZERO
        state.a = addA(state, valAndCarry)
        return 7
    }
}

class SUI:ByteOpCode(0xd6) {
    override fun execute(state: State): Int {
        state.a = subA(state, value!!)
        return 7
    }
}

class SBI:ByteOpCode(0xde) {
    override fun execute(state: State): Int {
        val valAndCarry = value!! + if(state.flags.cy) 0x1 else 0x0
        state.a = subA(state, valAndCarry)
        return 7
    }
}

class XTHL:NoArgOpCode(0xe3) {
    override fun execute(state: State): Int {
        val tmpLo = state.l
        state.l = state.memory[state.sp]
        state.memory[state.sp] = tmpLo

        val tmpHi = state.h
        state.h = state.memory[state.sp + 1]
        state.memory[state.sp + 1] = tmpHi
        return 18
    }
}


class ANI:ByteOpCode(0xe6) {
    override fun execute(state: State): Int {
        state.a = state.a.and(value!!)
        setFlags(state, state.a)
        return 7
    }
}

class PCHL:NoArgOpCode(0xe9, true) {
    override fun execute(state: State): Int {
        state.pc = state.hl()
        return 5
    }
}

class XCHG:NoArgOpCode(0xeb) {
    override fun execute(state: State): Int {
        val tmp = state.h
        state.h = state.d
        state.d = tmp

        val tmp2 = state.l
        state.l = state.e
        state.e = tmp2
        return 5
    }
}

class XRI:ByteOpCode(0xee) {
    override fun execute(state: State): Int {
        state.a = state.a.xor(value!!)
        setFlags(state, state.a)
        return 7
    }
}

class ORI:ByteOpCode(0xf6) {
    override fun execute(state: State): Int {
        state.a = state.a.or(value!!)
        setFlags(state, state.a)
        return 7
    }
}

class SPHL:NoArgOpCode(0xf9) {
    override fun execute(state: State): Int {
        state.sp = state.hl()
        return 5
    }
}

class CPI:ByteOpCode(0xfe) {
    override fun execute(state: State): Int {
        val result = state.a.toUshort() - value!!.toUshort()
        setFlags(state, result)
        return 7
    }
}

class DI:NoArgOpCode(0xf3) {
    override fun execute(state: State): Int {
        state.int_enable = false
        return 4
    }
}
class EI:NoArgOpCode(0xfb) {
    override fun execute(state: State): Int {
        state.int_enable = true
        return 4
    }
}
class OUT:ByteOpCode(0xd3) {
    override fun execute(state: State): Int {
        state.outOp(value!!, state.a)
        return 10
    }
}
class IN:ByteOpCode(0xdb) {
    override fun execute(state: State): Int {
        state.a = state.inOp(value!!)
        return 10
    }
}

class DAA:NoArgOpCode(0x27) {
    override fun execute(state: State): Int {
        if(state.a.and(0xf) > 9 || state.flags.ac) {
            addA(state, Ubyte(0x6))
        }

        if(state.a.and(0xf0).shr(4) > 9 || state.flags.cy) {
            addA(state, Ubyte(0x60))
        }
        return 4
    }
}

// **** Unimplemented ***
class SIM:NoArgOpCode(0x30)
class RIM:NoArgOpCode(0x20)

class RST_0:NoArgOpCode(0xc7)
class RST_1:NoArgOpCode(0xcf)
class RST_2:NoArgOpCode(0xd7)
class RST_3:NoArgOpCode(0xdf)
class RST_4:NoArgOpCode(0xe7)
class RST_5:NoArgOpCode(0xef)
class RST_6:NoArgOpCode(0xf7)
class RST_7:NoArgOpCode(0xff)