package intel8080
import unsigned.*

val ZERO = Ubyte(0)
val ONE = Ubyte(1)

fun Number.toWord(loByte: Number) = this.toUshort().shl(8).or(loByte.toUshort())

fun Ushort.hi() = this.shr(8).toUbyte()
fun Ushort.lo() = this.and(0xff).toUbyte()

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

fun opCodeFor(opCode: Ubyte): OpCode = opCodes[opCode.toInt()] ?: throw RuntimeException("Unrecognised opcode ${opCode.toInt()}")

class FlagSet(val flags: String) {

    val s = flags[0] == 'S'
    val z = flags[1] == 'Z'
    val a = flags[2] == 'A'
    val p = flags[3] == 'P'
    val c = flags[4] == 'C'
}

abstract class OpCode(val opCode: Int, val size: Int = 0, val cycles: Int, val flagStr: String = "-----") {

    var offset: Ushort = Ushort(0)
    var state: State = NullState()
    val flags = FlagSet(flagStr)
    var branchTaken: Boolean = false

    fun consume(state: State) {
        this.offset = state.pc
        this.state = state
        this.branchTaken = false
        consumeInternal()
    }

    open fun consumeInternal() {}

    fun execAndAdvance(): Int {
        execute()
        if(!branchTaken) {
            state.pc += this.size
            return cycles
        } else {
            return cycles + 6
        }
    }

    open fun execute() {
        throw RuntimeException("Unimplemented instruction ${this.javaClass.simpleName}")
    }

    fun setFlags(result: Ubyte, lhs: Ubyte? = null, rhs: Ubyte? = null) = setFlags(result.toUshort(), lhs, rhs)
    fun setFlags(result: Ushort, lhs: Ubyte? = null, rhs: Ubyte? = null) {

        if(flags.z)
            state.flags.z = result.toUbyte() == ZERO

        if(flags.s)
            state.flags.s = result.and(0x80).toUbyte() != ZERO

        if(flags.c)
            state.flags.cy = result > 0xff

        if(flags.p)
            state.flags.lastFlaggedValue = result.and(0xff)

        if(lhs != null && rhs != null && flags.a) {
            val halfCarry = (rhs.toUshort().xor(result)).xor(lhs.toUshort()).and(0x10)
            state.flags.ac = (halfCarry > 0)
        }
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

    fun add(lhs: Ubyte, rhs: Ubyte): Ubyte {
        val result = lhs.toUshort() + rhs.toUshort()
        setFlags(result, lhs, rhs)
        return result.toUbyte()
    }

    fun sub(lhs: Ubyte, rhs: Ubyte): Ubyte {
        val result = lhs.toUshort() - rhs.toUshort()
        setFlags(result, lhs, rhs)
        return result.toUbyte()
    }
}

abstract class NoArgOpCode(opCode: Int, cycles: Int, flags: String = "-----"): OpCode(opCode, 1, cycles, flags)

abstract class ByteOpCode(opCode: Int, cycles: Int, flags: String = "-----"): OpCode(opCode, 2, cycles, flags) {
    var value: Ubyte? = null

    override fun consumeInternal() {
        value = state.peek(+1)
    }
}

abstract class WordOpCode(opCode: Int, cycles: Int, flags: String = "-----"): OpCode(opCode, 3, cycles, flags) {
    var value: Ushort? = null
        get() = hi!!.toUshort().shl(8).or(lo!!.toUshort())
    var hi: Ubyte? = null
    var lo: Ubyte? = null

    override fun consumeInternal() {
        hi = state.peek(+2)
        lo = state.peek(+1)
    }
}

// *** JUMPS ***
abstract class JumpOpCode(opCode: Int): WordOpCode(opCode, 10) {

    fun jumpIf(condition: Boolean) {
        if(condition) {
            state.pc = value!!
            branchTaken = true
        }
    }
}

class JMP: JumpOpCode(0xc3) {
    override fun execute() = jumpIf(true)
}
class JM: JumpOpCode(0xfa) {
    override fun execute() = jumpIf(state.flags.s)
}
class JP: JumpOpCode(0xf2) {
    override fun execute() = jumpIf(!state.flags.s)
}
class JPE: JumpOpCode(0xea) {
    override fun execute() = jumpIf(state.flags.p)
}
class JPO: JumpOpCode(0xe2) {
    override fun execute() = jumpIf(!state.flags.p)
}
class JC: JumpOpCode(0xda) {
    override fun execute() = jumpIf(state.flags.cy)
}
class JNC: JumpOpCode(0xd2) {
    override fun execute() = jumpIf(!state.flags.cy)
}
class JZ: JumpOpCode(0xca) {
    override fun execute() = jumpIf(state.flags.z)
}
class JNZ: JumpOpCode(0xc2) {
    override fun execute() = jumpIf(!state.flags.z)
}

// **** CALLS

abstract class CallOpCode(opCode: Int): WordOpCode(opCode, 11) {
    fun callIf(condition: Boolean) {
        if(condition) {
            state.pc += 3
            state.pushStack(state.pc)

            state.pc = value!!
            branchTaken = true
        }
    }
}
class CM: CallOpCode(0xfc) {
    override fun execute() = callIf(state.flags.s)
}
class CP: CallOpCode(0xf4) {
    override fun execute() = callIf(!state.flags.s)
}
class CPE: CallOpCode(0xec) {
    override fun execute() = callIf(state.flags.p)
}
class CPO: CallOpCode(0xe4) {
    override fun execute() = callIf(!state.flags.p)
}
class CC: CallOpCode(0xdc) {
    override fun execute() = callIf(state.flags.cy)
}
class CNC: CallOpCode(0xd4) {
    override fun execute() = callIf(!state.flags.cy)
}
class CZ: CallOpCode(0xcc) {
    override fun execute() = callIf(state.flags.z)
}
class CNZ: CallOpCode(0xc4) {
    override fun execute() = callIf(!state.flags.z)
}
class CALL: CallOpCode(0xcd) {
    override fun execute() = callIf(true)
}

// *** RETURNS
abstract class ReturnOpCode(opCode:Int): NoArgOpCode(opCode, 5) {
    fun returnIf(condition: Boolean) {
        if(condition) {
            state.pc = state.popStack()
            branchTaken = true
        }
    }
}
class RM: ReturnOpCode(0xf8) {
    override fun execute() = returnIf(state.flags.s)
}
class RP: ReturnOpCode(0xf0) {
    override fun execute() = returnIf(!state.flags.s)
}
class RPE: ReturnOpCode(0xe8) {
    override fun execute() = returnIf(state.flags.p)
}
class RPO: ReturnOpCode(0xe0) {
    override fun execute() = returnIf(!state.flags.p)
}
class RC: ReturnOpCode(0xd8) {
    override fun execute() = returnIf(state.flags.cy)
}
class RNC: ReturnOpCode(0xd0) {
    override fun execute() = returnIf(!state.flags.cy)
}
class RZ: ReturnOpCode(0xc8) {
    override fun execute() = returnIf(state.flags.z)
}
class RNZ: ReturnOpCode(0xc0) {
    override fun execute() = returnIf(!state.flags.z)
}
class RET: ReturnOpCode(0xc9) {
    override fun execute() = returnIf(true)
}

// ***** STACK OPS
abstract class StackOpCode(opCode: Int, cycles: Int): NoArgOpCode(opCode, cycles)

class POP_B: StackOpCode(0xc1, 10) {
    override fun execute() {
        val popped = state.popStack()
        state.c = popped.lo()
        state.b = popped.hi()
    }
}
class PUSH_B: StackOpCode(0xc5, 11) {
    override fun execute() {
        state.pushStack(state.bc())
    }
}
class POP_D: StackOpCode(0xd1, 10) {
    override fun execute() {
        val popped = state.popStack()
        state.e = popped.lo()
        state.d = popped.hi()
    }
}

class PUSH_D: StackOpCode(0xd5, 11) {
    override fun execute() {
        state.pushStack(state.de())
    }
}
class POP_H: StackOpCode(0xe1, 10) {
    override fun execute() {
        val popped = state.popStack()
        state.l = popped.lo()
        state.h = popped.hi()
    }
}
class PUSH_H: StackOpCode(0xe5, 11) {
    override fun execute() {
        state.pushStack(state.hl())
    }
}
class POP_PSW: StackOpCode(0xf1, 10) {
    override fun execute() {
        val popped = state.popStack()
        state.flags.fromByte(popped.lo())
        state.a = popped.hi()
    }
}
class PUSH_PSW: StackOpCode(0xf5, 11) {
    override fun execute() {
        state.pushStack(state.a.toWord(state.flags.asByte()))
    }
}

// === ACCUMULATOR OPS
abstract class AccumulatorOpCode(opCode: Int, cycles: Int = 4): NoArgOpCode(opCode, cycles, flags = "SZAPC") {

    fun setAccum(other: Ubyte, func: Ushort.(Ushort) -> Ushort) {
        val rhs = other.toUshort()
        val lhs = state.a.toUshort()
        val result = func.invoke(lhs, rhs)

        setFlags(result, state.a, other)
        state.a = result.and(0xff).toUbyte()
    }

    fun withAccum(state: State, other: Ubyte, func: Ushort.(Ushort) -> Ushort) {
        val rhs = other.toUshort()
        val lhs = state.a.toUshort()
        val result = func.invoke(lhs, rhs)
        setFlags(result, state.a, other)
    }

    fun Ubyte.withCarry(): Ubyte = this + if(state.flags.cy) 0x1 else 0x0
}

class ADD_B: AccumulatorOpCode(0x80) {
    override fun execute() = setAccum(state.b, Ushort::plus)
}
class ADD_C: AccumulatorOpCode(0x81) {
    override fun execute() = setAccum(state.c, Ushort::plus)
}
class ADD_D: AccumulatorOpCode(0x82) {
    override fun execute() = setAccum(state.d, Ushort::plus)
}
class ADD_E: AccumulatorOpCode(0x83) {
    override fun execute() = setAccum(state.e, Ushort::plus)
}
class ADD_H: AccumulatorOpCode(0x84) {
    override fun execute() = setAccum(state.h, Ushort::plus)
}
class ADD_L: AccumulatorOpCode(0x85) {
    override fun execute() = setAccum(state.l, Ushort::plus)
}
class ADD_M: AccumulatorOpCode(0x86, 7) {
    override fun execute() = setAccum(state.heap(), Ushort::plus)
}
class ADD_A: AccumulatorOpCode(0x87) {
    override fun execute() = setAccum(state.a, Ushort::plus)
}
class ADC_B: AccumulatorOpCode(0x88) {
    override fun execute() = setAccum(state.b.withCarry(), Ushort::plus)
}
class ADC_C: AccumulatorOpCode(0x89) {
    override fun execute() = setAccum(state.c.withCarry(), Ushort::plus)
}
class ADC_D: AccumulatorOpCode(0x8a) {
    override fun execute() = setAccum(state.d.withCarry(), Ushort::plus)
}
class ADC_E: AccumulatorOpCode(0x8b) {
    override fun execute() = setAccum(state.e.withCarry(), Ushort::plus)
}
class ADC_H: AccumulatorOpCode(0x8c) {
    override fun execute() = setAccum(state.h.withCarry(), Ushort::plus)
}
class ADC_L: AccumulatorOpCode(0x8d) {
    override fun execute() = setAccum(state.l.withCarry(), Ushort::plus)
}
class ADC_M: AccumulatorOpCode(0x8e, 7) {
    override fun execute() = setAccum(state.heap().withCarry(), Ushort::plus)
}
class ADC_A: AccumulatorOpCode(0x8f) {
    override fun execute() = setAccum(state.a.withCarry(), Ushort::plus)
}
class SUB_B: AccumulatorOpCode(0x90) {
    override fun execute() = setAccum(state.b, Ushort::minus)
}
class SUB_C: AccumulatorOpCode(0x91) {
    override fun execute() = setAccum(state.c, Ushort::minus)
}
class SUB_D: AccumulatorOpCode(0x92) {
    override fun execute() = setAccum(state.d, Ushort::minus)
}
class SUB_E: AccumulatorOpCode(0x93) {
    override fun execute() = setAccum(state.e, Ushort::minus)
}
class SUB_H: AccumulatorOpCode(0x94) {
    override fun execute() = setAccum(state.h, Ushort::minus)
}
class SUB_L: AccumulatorOpCode(0x95) {
    override fun execute() = setAccum(state.l, Ushort::minus)
}
class SUB_M: AccumulatorOpCode(0x96, 7) {
    override fun execute() = setAccum(state.heap(), Ushort::minus)
}
class SUB_A: AccumulatorOpCode(0x97) {
    override fun execute() = setAccum(state.a, Ushort::minus)
}
class SBB_B: AccumulatorOpCode(0x98) {
    override fun execute() = setAccum(state.b.withCarry(), Ushort::minus)
}
class SBB_C: AccumulatorOpCode(0x99) {
    override fun execute() = setAccum(state.c.withCarry(), Ushort::minus)
}
class SBB_D: AccumulatorOpCode(0x9a) {
    override fun execute() = setAccum(state.d.withCarry(), Ushort::minus)
}
class SBB_E: AccumulatorOpCode(0x9b) {
    override fun execute() = setAccum(state.e.withCarry(), Ushort::minus)
}
class SBB_H: AccumulatorOpCode(0x9c) {
    override fun execute() = setAccum(state.h.withCarry(), Ushort::minus)
}
class SBB_L: AccumulatorOpCode(0x9d) {
    override fun execute() = setAccum(state.l.withCarry(), Ushort::minus)
}
class SBB_M: AccumulatorOpCode(0x9e, 7) {
    override fun execute() = setAccum(state.heap().withCarry(), Ushort::minus)
}
class SBB_A: AccumulatorOpCode(0x9f) {
    override fun execute() = setAccum(state.a.withCarry(), Ushort::minus)
}
class ANA_B: AccumulatorOpCode(0xa0) {
    override fun execute() = setAccum(state.b, Ushort::and)
}
class ANA_C: AccumulatorOpCode(0xa1) {
    override fun execute() = setAccum(state.c, Ushort::and)
}
class ANA_D: AccumulatorOpCode(0xa2) {
    override fun execute() = setAccum(state.d, Ushort::and)
}
class ANA_E: AccumulatorOpCode(0xa3) {
    override fun execute() = setAccum(state.e, Ushort::and)
}
class ANA_H: AccumulatorOpCode(0xa4) {
    override fun execute() = setAccum(state.h, Ushort::and)
}
class ANA_L: AccumulatorOpCode(0xa5) {
    override fun execute() = setAccum(state.l, Ushort::and)
}
class ANA_M: AccumulatorOpCode(0xa6, 7) {
    override fun execute() = setAccum(state.heap(), Ushort::and)
}
class ANA_A: AccumulatorOpCode(0xa7) {
    override fun execute() = setAccum(state.a, Ushort::and)
}
class XRA_B: AccumulatorOpCode(0xa8) {
    override fun execute() = setAccum(state.b, Ushort::xor)
}
class XRA_C: AccumulatorOpCode(0xa9) {
    override fun execute() = setAccum(state.c, Ushort::xor)
}
class XRA_D: AccumulatorOpCode(0xaa) {
    override fun execute() = setAccum(state.d, Ushort::xor)
}
class XRA_E: AccumulatorOpCode(0xab) {
    override fun execute() = setAccum(state.e, Ushort::xor)
}
class XRA_H: AccumulatorOpCode(0xac) {
    override fun execute() = setAccum(state.h, Ushort::xor)
}
class XRA_L: AccumulatorOpCode(0xad) {
    override fun execute() = setAccum(state.l, Ushort::xor)
}
class XRA_M: AccumulatorOpCode(0xae, 7) {
    override fun execute() = setAccum(state.heap(), Ushort::xor)
}
class XRA_A: AccumulatorOpCode(0xaf) {
    override fun execute() = setAccum(state.a, Ushort::xor)
}
class ORA_B: AccumulatorOpCode(0xb0) {
    override fun execute() = setAccum(state.b, Ushort::or)
}
class ORA_C: AccumulatorOpCode(0xb1) {
    override fun execute() = setAccum(state.c, Ushort::or)
}
class ORA_D: AccumulatorOpCode(0xb2) {
    override fun execute() = setAccum(state.d, Ushort::or)
}
class ORA_E: AccumulatorOpCode(0xb3) {
    override fun execute() = setAccum(state.e, Ushort::or)
}
class ORA_H: AccumulatorOpCode(0xb4) {
    override fun execute() = setAccum(state.h, Ushort::or)
}
class ORA_L: AccumulatorOpCode(0xb5) {
    override fun execute() = setAccum(state.l, Ushort::or)
}
class ORA_M: AccumulatorOpCode(0xb6, 7) {
    override fun execute() = setAccum(state.heap(), Ushort::or)
}
class ORA_A: AccumulatorOpCode(0xb7) {
    override fun execute() = setAccum(state.a, Ushort::or)
}
class CMP_B: AccumulatorOpCode(0xb8) {
    override fun execute() = withAccum(state, state.b, Ushort::minus)
}
class CMP_C: AccumulatorOpCode(0xb9) {
    override fun execute() = withAccum(state, state.c, Ushort::minus)
}
class CMP_D: AccumulatorOpCode(0xba) {
    override fun execute() = withAccum(state, state.d, Ushort::minus)
}
class CMP_E: AccumulatorOpCode(0xbb) {
    override fun execute() = withAccum(state, state.e, Ushort::minus)
}
class CMP_H: AccumulatorOpCode(0xbc) {
    override fun execute() = withAccum(state, state.h, Ushort::minus)
}
class CMP_L: AccumulatorOpCode(0xbd) {
    override fun execute() = withAccum(state, state.l, Ushort::minus)
}
class CMP_M: AccumulatorOpCode(0xbe, 7) {
    override fun execute() = withAccum(state, state.heap(), Ushort::minus)
}
class CMP_A: AccumulatorOpCode(0xbf) {
    override fun execute() = withAccum(state, state.a, Ushort::minus)
}
// ---- ==================================================== --------
// ---- ======================a============================== --------

class NOP: NoArgOpCode(0x00, 4) {
    override fun execute() { }
}
class LXI_B: WordOpCode(0x01, 10) {
    override fun execute() {
        state.b = hi!!
        state.c = lo!!
    }
}
class STAX_B: NoArgOpCode(0x02, 7) {
    override fun execute() {
        state.memory[state.bc()] = state.a
    }
}
class INX_B: NoArgOpCode(0x03, 5) {
    override fun execute() {
        val newValue = state.bc() + 1
        state.b = newValue.hi()
        state.c = newValue.lo()
    }
}
class INR_B: NoArgOpCode(0x04, 5, flags = "SZAP-") {
    override fun execute() {
        state.b = add(state.b, ONE)
    }
}
class DCR_B: NoArgOpCode(0x05, 5, flags = "SZAP-") {
    override fun execute() {
        state.b = sub(state.b, ONE)
    }
}
class MVI_B: ByteOpCode(0x06, 7) {
    override fun execute() {
        state.b = value!!
    }
}
class RLC: NoArgOpCode(0x07, 4, flags = "----C") {
    override fun execute() {
        val highOrder = state.a.and(0x80) != 0x0.toUbyte()
        state.a = state.a.shl(1)

        state.flags.cy = highOrder
        if(highOrder) {
            state.a = state.a.or(ONE)
        }
    }
}
class DAD_B: NoArgOpCode(0x09, 10, flags = "----C") {
    override fun execute() {
        val newVal = state.hl().toUint() + state.bc().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LDAX_B: NoArgOpCode(0x0a, 7) {
    override fun execute() {
        state.a = state.memory[state.bc()]
    }
}
class DCX_B: NoArgOpCode(0x0b, 5) {
    override fun execute() {
        val newValue = state.bc() - 1
        state.b = newValue.hi()
        state.c = newValue.lo()
    }
}
class INR_C: NoArgOpCode(0x0c, 5, flags = "SZAP-") {
    override fun execute() {
        state.c = add(state.c, ONE)
    }
}
class DCR_C: NoArgOpCode(0x0d, 5, flags = "SZAP-") {
    override fun execute() {
        state.c = sub(state.c, ONE)
    }
}
class MVI_C: ByteOpCode(0x0e, 7) {
    override fun execute() {
        state.c = value!!
    }
}
class RRC: NoArgOpCode(0x0f, 4, flags = "----C") {
    override fun execute() {
        val lowOrder = state.a.and(0x1) != 0x0.toUbyte()
        state.a = state.a.shr(1)

        state.flags.cy = lowOrder
        if(lowOrder) {
            state.a = state.a.or(0x80)
        }
    }
}
class LXI_D: WordOpCode(0x11, 10) {
    override fun execute() {
        state.d = hi!!
        state.e = lo!!
    }
}
class STAX_D: NoArgOpCode(0x12, 7) {
    override fun execute() {
        state.memory[state.de()] = state.a
    }
}
class INX_D: NoArgOpCode(0x13, 5) {
    override fun execute() {
        val newValue = state.de() + 1
        state.d = newValue.hi()
        state.e = newValue.lo()
    }
}
class INR_D: NoArgOpCode(0x14, 5, flags = "SZAP-"){
    override fun execute() {
        state.d = add(state.d, ONE)
    }
}

class DCR_D: NoArgOpCode(0x15, 5, flags = "SZAP-") {
    override fun execute() {
        state.d = sub(state.d, ONE)
    }
}
class MVI_D: ByteOpCode(0x16, 7) {
    override fun execute() {
        state.d = value!!
    }
}
class RAL: NoArgOpCode(0x17, 4, flags = "----C") {
    override fun execute() {
        val highOrder = state.a.and(0x80) != 0x0.toUbyte()
        state.a = state.a.shl(1).or(if(state.flags.cy) ONE else ZERO)
        state.flags.cy = highOrder
    }
}
class DAD_D: NoArgOpCode(0x19, 10, flags = "----C") {
    override fun execute() {
        val newVal = state.hl().toUint() + state.de().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LDAX_D: NoArgOpCode(0x1a, 7) {
    override fun execute() {
        state.a = state.memory[state.de()].toUbyte()
    }
}
class DCX_D: NoArgOpCode(0x1b, 5) {
    override fun execute() {
        val newValue = state.de() - 1
        state.d = newValue.hi()
        state.e = newValue.lo()
    }
}
class INR_E: NoArgOpCode(0x1c, 5, flags = "SZAP-") {
    override fun execute() {
        state.e = add(state.e, ONE)
    }
}
class DCR_E: NoArgOpCode(0x1d, 5, flags = "SZAP-") {
    override fun execute() {
        state.e = sub(state.e, ONE)
    }
}
class MVI_E: ByteOpCode(0x1e, 7) {
    override fun execute() {
        state.e = value!!
    }
}
class RAR: NoArgOpCode(0x1f, 4, flags = "----C") {
    override fun execute() {
        val lowOrder = state.a.and(0x1) != 0x0.toUbyte()
        state.a = state.a.shr(1).or(if(state.flags.cy) 0x80.toUbyte() else ZERO)
        state.flags.cy = lowOrder
    }
}

class LXI_H: WordOpCode(0x21, 10) {
    override fun execute() {
        state.h = hi!!
        state.l = lo!!
    }
}
class SHLD: WordOpCode(0x22, 16) {
    override fun execute() {
        state.memory[value!!] = state.l
        state.memory[value!! + 1] = state.h
    }
}
class INX_H: NoArgOpCode(0x23, 5) {
    override fun execute() {
        val newValue = state.hl() + 1
        state.h = newValue.hi()
        state.l = newValue.lo()
    }
}

class INR_H: NoArgOpCode(0x24, 5, flags = "SZAP-") {
    override fun execute() {
        state.h = add(state.h, ONE)
    }
}
class DCR_H: NoArgOpCode(0x25, 5, flags = "SZAP-")  {
    override fun execute() {
        state.h = sub(state.h, ONE)
    }
}
class MVI_H: ByteOpCode(0x26, 7) {
    override fun execute() {
        state.h = value!!
    }
}
class DAD_H: NoArgOpCode(0x29, 10, flags = "----C") {
    override fun execute() {
        val newVal = state.hl().toUint() + state.hl().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LHLD: WordOpCode(0x2a, 16) {
    override fun execute() {
        state.h = state.memory[value!! + 1]
        state.l = state.memory[value!!]
    }
}
class DCX_H: NoArgOpCode(0x2b, 5) {
    override fun execute() {
        val newValue = state.hl() - 1
        state.h = newValue.hi()
        state.l = newValue.lo()
    }
}
class INR_L: NoArgOpCode(0x2c, 5, flags = "SZAP-") {
    override fun execute() {
        state.l = add(state.l, ONE)
    }
}
class DCR_L: NoArgOpCode(0x2d, 5, flags = "SZAP-") {
    override fun execute() {
        state.l = sub(state.l, ONE)
    }
}
class MVI_L: ByteOpCode(0x2e, 7) {
    override fun execute() {
        state.l = value!!
    }
}
class CMA: NoArgOpCode(0x2f, 4) {
    override fun execute() {
        state.a = state.a.inv()
    }
}
class LXI_SP: WordOpCode(0x31, 10) {
    override fun execute() {
        state.sp = value!!
    }
}
class STA: WordOpCode(0x32, 13) {
    override fun execute() {
        state.memory[value!!] = state.a
    }
}
class INX_SP: NoArgOpCode(0x33, 5) {
    override fun execute() {
        state.sp += 1
    }
}
class INR_M: NoArgOpCode(0x34, 10, flags = "SZAP-") {
    override fun execute() {
        state.memory[state.hl()] = state.memory[state.hl()] + 1
        setFlags(state.memory[state.hl()])
    }
}
class DCR_M: NoArgOpCode(0x35, 10, flags = "SZAP-") {
    override fun execute() {
        state.memory[state.hl()] = state.memory[state.hl()] - 1
        setFlags(state.memory[state.hl()])
    }
}
class MVI_M: ByteOpCode(0x36, 10) {
    override fun execute() {
        state.memory[state.hl()] = value!!
    }
}
class STC: NoArgOpCode(0x37, 4, flags = "----C") {
    override fun execute() {
        state.flags.cy = true
    }
}
class DAD_SP: NoArgOpCode(0x39, 10, flags = "----C") {
    override fun execute() {
        val newVal = state.hl().toUint() + state.sp.toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LDA: WordOpCode(0x3a, 13) {
    override fun execute() {
        state.a = state.memory[value!!]
    }
}
class DCX_SP: NoArgOpCode(0x3b, 5) {
    override fun execute() {
        state.sp -= 1
    }
}
class INR_A: NoArgOpCode(0x3c, 5, flags = "SZAP-") {
    override fun execute() {
        state.a = add(state.a, ONE)
    }
}
class DCR_A: NoArgOpCode(0x3d, 5, flags = "SZAP-") {
    override fun execute() {
        state.a = sub(state.a, ONE)
    }
}
class MVI_A: ByteOpCode(0x3e, 7) {
    override fun execute() {
        state.a = value!!
    }
}
class CMC: NoArgOpCode(0x3f, 4, flags = "----C") {
    override fun execute() {
        state.flags.cy = !state.flags.cy
    }
}
class MOV_B_B: NoArgOpCode(0x40, 5) {
    override fun execute() {
        state.b = state.b
    }
}
class MOV_B_C: NoArgOpCode(0x41, 5) {
    override fun execute() {
        state.b = state.c
    }
}
class MOV_B_D: NoArgOpCode(0x42, 5) {
    override fun execute() {
        state.b = state.d
    }
}
class MOV_B_E: NoArgOpCode(0x43, 5) {
    override fun execute() {
        state.b = state.e
    }
}
class MOV_B_H: NoArgOpCode(0x44, 5) {
    override fun execute() {
        state.b = state.h
    }
}
class MOV_B_L: NoArgOpCode(0x45, 5) {
    override fun execute() {
        state.b = state.l
    }
}
class MOV_B_M: NoArgOpCode(0x46, 7) {
    override fun execute() {
        state.b = state.heap()
    }
}
class MOV_B_A: NoArgOpCode(0x47, 5) {
    override fun execute() {
        state.b = state.a
    }
}
class MOV_C_B: NoArgOpCode(0x48, 5) {
    override fun execute() {
        state.c = state.b
    }
}
class MOV_C_C: NoArgOpCode(0x49, 5) {
    override fun execute() {
        state.c = state.c
    }
}
class MOV_C_D: NoArgOpCode(0x4a, 5) {
    override fun execute() {
        state.c = state.d
    }
}
class MOV_C_E: NoArgOpCode(0x4b, 5) {
    override fun execute() {
        state.c = state.e
    }
}
class MOV_C_H: NoArgOpCode(0x4c, 5) {
    override fun execute() {
        state.c = state.h
    }
}
class MOV_C_L: NoArgOpCode(0x4d, 5) {
    override fun execute() {
        state.c = state.l
    }
}
class MOV_C_M: NoArgOpCode(0x4e, 7) {
    override fun execute() {
        state.c = state.heap()
    }
}
class MOV_C_A: NoArgOpCode(0x4f, 5) {
    override fun execute() {
        state.c = state.a
    }
}
class MOV_D_B: NoArgOpCode(0x50, 5) {
    override fun execute() {
        state.d = state.b
    }
}
class MOV_D_C: NoArgOpCode(0x51, 5) {
    override fun execute() {
        state.d = state.c
    }
}
class MOV_D_D: NoArgOpCode(0x52, 5) {
    override fun execute() {
        state.d = state.d
    }
}
class MOV_D_E: NoArgOpCode(0x53, 5) {
    override fun execute() {
        state.d = state.e
    }
}
class MOV_D_H: NoArgOpCode(0x54, 5) {
    override fun execute() {
        state.d = state.h
    }
}
class MOV_D_L: NoArgOpCode(0x55, 5) {
    override fun execute() {
        state.d = state.l
    }
}
class MOV_D_M: NoArgOpCode(0x56, 7) {
    override fun execute() {
        state.d = state.heap()
    }
}
class MOV_D_A: NoArgOpCode(0x57, 5) {
    override fun execute() {
        state.d = state.a
    }
}
class MOV_E_B: NoArgOpCode(0x58, 5) {
    override fun execute() {
        state.e = state.b
    }
}
class MOV_E_C: NoArgOpCode(0x59, 5) {
    override fun execute() {
        state.e = state.c
    }
}
class MOV_E_D: NoArgOpCode(0x5a, 5) {
    override fun execute() {
        state.e = state.d
    }
}
class MOV_E_E: NoArgOpCode(0x5b, 5) {
    override fun execute() {
        state.e = state.e
    }
}
class MOV_E_H: NoArgOpCode(0x5c, 5) {
    override fun execute() {
        state.e = state.h
    }
}
class MOV_E_L: NoArgOpCode(0x5d, 5) {
    override fun execute() {
        state.e = state.l
    }
}
class MOV_E_M: NoArgOpCode(0x5e, 7) {
    override fun execute() {
        state.e = state.heap()
    }
}
class MOV_E_A: NoArgOpCode(0x5f, 5) {
    override fun execute() {
        state.e = state.a
    }
}
class MOV_H_B: NoArgOpCode(0x60, 5) {
    override fun execute() {
        state.h = state.b
    }
}
class MOV_H_C: NoArgOpCode(0x61, 5) {
    override fun execute() {
        state.h = state.c
    }
}
class MOV_H_D: NoArgOpCode(0x62, 5) {
    override fun execute() {
        state.h = state.d
    }
}
class MOV_H_E: NoArgOpCode(0x63, 5) {
    override fun execute() {
        state.h = state.e
    }
}
class MOV_H_H: NoArgOpCode(0x64, 5) {
    override fun execute() {
        state.h = state.h
    }
}
class MOV_H_L: NoArgOpCode(0x65, 5) {
    override fun execute() {
        state.h = state.l
    }
}
class MOV_H_M: NoArgOpCode(0x66, 7) {
    override fun execute() {
        state.h = state.heap()
    }
}
class MOV_H_A: NoArgOpCode(0x67, 5) {
    override fun execute() {
        state.h = state.a
    }
}
class MOV_L_B: NoArgOpCode(0x68, 5) {
    override fun execute() {
        state.l = state.b
    }
}
class MOV_L_C: NoArgOpCode(0x69, 5) {
    override fun execute() {
        state.l = state.c
    }
}
class MOV_L_D: NoArgOpCode(0x6a, 5) {
    override fun execute() {
        state.l = state.d
    }
}
class MOV_L_E: NoArgOpCode(0x6b, 5) {
    override fun execute() {
        state.l = state.e
    }
}
class MOV_L_H: NoArgOpCode(0x6c, 5) {
    override fun execute() {
        state.l = state.h
    }
}
class MOV_L_L: NoArgOpCode(0x6d, 5) {
    override fun execute() {
        state.l = state.l
    }
}
class MOV_L_M: NoArgOpCode(0x6e, 7) {
    override fun execute() {
        state.l = state.heap()
    }
}
class MOV_L_A: NoArgOpCode(0x6f, 5) {
    override fun execute() {
        state.l = state.a
    }
}
class MOV_M_B: NoArgOpCode(0x70, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.b
    }
}
class MOV_M_C: NoArgOpCode(0x71, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.c
    }
}
class MOV_M_D: NoArgOpCode(0x72, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.d
    }
}
class MOV_M_E: NoArgOpCode(0x73, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.e
    }
}
class MOV_M_H: NoArgOpCode(0x74, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.h
    }
}
class MOV_M_L: NoArgOpCode(0x75, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.l
    }
}
class HLT: NoArgOpCode(0x76, 0) {
    override fun execute() = state.halt()
}
class MOV_M_A: NoArgOpCode(0x77, 7) {
    override fun execute() {
        state.memory[state.hl()] = state.a
    }
}
class MOV_A_B: NoArgOpCode(0x78, 5) {
    override fun execute() {
        state.a = state.b
    }
}
class MOV_A_C: NoArgOpCode(0x79, 5) {
    override fun execute() {
        state.a = state.c
    }
}
class MOV_A_D: NoArgOpCode(0x7a, 5) {
    override fun execute() {
        state.a = state.d
    }
}
class MOV_A_E: NoArgOpCode(0x7b, 5) {
    override fun execute() {
        state.a = state.e
    }
}
class MOV_A_H: NoArgOpCode(0x7c, 5) {
    override fun execute() {
        state.a = state.h
    }
}
class MOV_A_L: NoArgOpCode(0x7d, 5) {
    override fun execute() {
        state.a = state.l
    }
}
class MOV_A_M: NoArgOpCode(0x7e, 7) {
    override fun execute() {
        state.a = state.memory[state.hl()]
    }
}
class MOV_A_A: NoArgOpCode(0x7f, 5) {
    override fun execute() {
        state.a = state.a
    }
}
class ADI: ByteOpCode(0xc6, 7, flags = "SZAPC") {
    override fun execute() {
        state.a = add(state.a, value!!)
    }
}

class ACI: ByteOpCode(0xce, 7, flags = "SZAPC") {
    override fun execute() {
        val valAndCarry = value!! + if(state.flags.cy) ONE else ZERO
        state.a = add(state.a, valAndCarry)
    }
}

class SUI: ByteOpCode(0xd6, 7, flags = "SZAPC") {
    override fun execute() {
        state.a = sub(state.a, value!!)
    }
}

class SBI: ByteOpCode(0xde, 7, flags = "SZAPC") {
    override fun execute() {
        val valAndCarry = value!! + if(state.flags.cy) 0x1 else 0x0
        state.a = sub(state.a, valAndCarry)
    }
}

class XTHL: NoArgOpCode(0xe3, 18) {
    override fun execute() {
        val tmpLo = state.l
        state.l = state.memory[state.sp]
        state.memory[state.sp] = tmpLo

        val tmpHi = state.h
        state.h = state.memory[state.sp + 1]
        state.memory[state.sp + 1] = tmpHi
    }
}


class ANI: ByteOpCode(0xe6, 7, flags = "SZAPC") {
    override fun execute() {
        state.a = state.a.and(value!!)
        setFlags(state.a)
    }
}

class PCHL: NoArgOpCode(0xe9, 5) {
    override fun execute() {
        branchTaken = true
        state.pc = state.hl()
    }
}

class XCHG: NoArgOpCode(0xeb, 5) {
    override fun execute() {
        val tmp = state.h
        state.h = state.d
        state.d = tmp

        val tmp2 = state.l
        state.l = state.e
        state.e = tmp2
    }
}

class XRI: ByteOpCode(0xee, 7, flags = "SZAPC") {
    override fun execute() {
        state.a = state.a.xor(value!!)
        setFlags(state.a)
    }
}

class ORI: ByteOpCode(0xf6, 7, flags = "SZAPC") {
    override fun execute() {
        state.a = state.a.or(value!!)
        setFlags(state.a)
    }
}

class SPHL: NoArgOpCode(0xf9, 5) {
    override fun execute() {
        state.sp = state.hl()
    }
}

class CPI: ByteOpCode(0xfe, 7, flags = "SZAPC") {
    override fun execute() {
        sub(state.a, value!!)
    }
}

class DI: NoArgOpCode(0xf3, 4) {
    override fun execute() {
        state.int_enable = false
    }
}
class EI: NoArgOpCode(0xfb, 4) {
    override fun execute() {
        state.int_enable = true
    }
}
class OUT: ByteOpCode(0xd3, 10) {
    override fun execute() {
        state.outOp(value!!, state.a)
    }
}
class IN: ByteOpCode(0xdb, 10) {
    override fun execute() {
        state.a = state.inOp(value!!)
    }
}

class DAA: NoArgOpCode(0x27, 4, flags = "SZAPC") {
    override fun execute() {
        if(state.a.and(0xf) > 9 || state.flags.ac) {
            state.a = add(state.a, Ubyte(0x6))
        }

        if(state.a.and(0xf0).shr(4) > 9 || state.flags.cy) {
            state.a = add(state.a, Ubyte(0x60))
        }
    }
}

// **** Unimplemented ***
class SIM: NoArgOpCode(0x30, 0)
class RIM: NoArgOpCode(0x20, 0)

class RST_0: NoArgOpCode(0xc7, 11)
class RST_1: NoArgOpCode(0xcf, 11)
class RST_2: NoArgOpCode(0xd7, 11)
class RST_3: NoArgOpCode(0xdf, 11)
class RST_4: NoArgOpCode(0xe7, 11)
class RST_5: NoArgOpCode(0xef, 11)
class RST_6: NoArgOpCode(0xf7, 11)
class RST_7: NoArgOpCode(0xff, 11)