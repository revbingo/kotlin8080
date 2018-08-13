
import unsigned.*
import kotlin.system.exitProcess

val ZERO = Ubyte(0)
val ONE = Ubyte(1)

fun Number.toWord(loByte: Number) = this.toUshort().and(0xff).shl(8).or(loByte.toUshort().and(0xff))

fun Ushort.hi() = this.and(0xff00).shr(8).toUbyte()
fun Ushort.lo() = this.and(0xff).toUbyte()

operator fun ByteArray.get(addr: Ushort) = this.get(addr.toInt()).toUbyte()
operator fun ByteArray.set(addr: Ushort, value: Ubyte) = this.set(addr.toInt(), value.toByte()) //.also { println("set memory ${addr.toInt().hex(true)} to ${value.hex()}")}.also { Exception().printStackTrace() }

fun opCodeFor(opCode: Ubyte): OpCode {
    return when(opCode.toInt()) {
        0x00 -> NOP()
        0x01 -> LXI_B()
        0x02 -> STAX_B()
        0x03 -> INX_B()
        0x04 -> INR_B()
        0x05 -> DCR_B()
        0x06 -> MVI_B()
        0x07 -> RLC()
        0x09 -> DAD_B()
        0x0a -> LDAX_B()
        0x0b -> DCX_B()
        0x0c -> INR_C()
        0x0d -> DCR_C()
        0x0e -> MVI_C()
        0x0f -> RRC()
        0x11 -> LXI_D()
        0x12 -> STAX_D()
        0x13 -> INX_D()
        0x14 -> INR_D()
        0x15 -> DCR_D()
        0x16 -> MVI_D()
        0x17 -> RAL()
        0x19 -> DAD_D()
        0x1a -> LDAX_D()
        0x1b -> DCX_D()
        0x1c -> INR_E()
        0x1d -> DCR_E()
        0x1e -> MVI_E()
        0x1f -> RAR()
        0x20 -> RIM()
        0x21 -> LXI_H()
        0x22 -> SHLD()
        0x23 -> INX_H()
        0x24 -> INR_H()
        0x25 -> DCR_H()
        0x26 -> MVI_H()
        0x27 -> DAA()
        0x29 -> DAD_H()
        0x2a -> LHLD()
        0x2b -> DCX_H()
        0x2c -> INR_L()
        0x2d -> DCR_L()
        0x2e -> MVI_L()
        0x2f -> CMA()
        0x30 -> SIM()
        0x31 -> LXI_SP()
        0x32 -> STA()
        0x33 -> INX_SP()
        0x34 -> INR_M()
        0x35 -> DCR_M()
        0x36 -> MVI_M()
        0x37 -> STC()
        0x39 -> DAD_SP()
        0x3a -> LDA()
        0x3b -> DCX_SP()
        0x3c -> INR_A()
        0x3d -> DCR_A()
        0x3e -> MVI_A()
        0x3f -> CMC()
        0x40 -> MOV_B_B()
        0x41 -> MOV_B_C()
        0x42 -> MOV_B_D()
        0x43 -> MOV_B_E()
        0x44 -> MOV_B_H()
        0x45 -> MOV_B_L()
        0x46 -> MOV_B_M()
        0x47 -> MOV_B_A()
        0x48 -> MOV_C_B()
        0x49 -> MOV_C_C()
        0x4a -> MOV_C_D()
        0x4b -> MOV_C_E()
        0x4c -> MOV_C_H()
        0x4d -> MOV_C_L()
        0x4e -> MOV_C_M()
        0x4f -> MOV_C_A()
        0x50 -> MOV_D_B()
        0x51 -> MOV_D_C()
        0x52 -> MOV_D_D()
        0x53 -> MOV_D_E()
        0x54 -> MOV_D_H()
        0x55 -> MOV_D_L()
        0x56 -> MOV_D_M()
        0x57 -> MOV_D_A()
        0x58 -> MOV_E_B()
        0x59 -> MOV_E_C()
        0x5a -> MOV_E_D()
        0x5b -> MOV_E_E()
        0x5c -> MOV_E_H()
        0x5d -> MOV_E_L()
        0x5e -> MOV_E_M()
        0x5f -> MOV_E_A()
        0x60 -> MOV_H_B()
        0x61 -> MOV_H_C()
        0x62 -> MOV_H_D()
        0x63 -> MOV_H_E()
        0x64 -> MOV_H_H()
        0x65 -> MOV_H_L()
        0x66 -> MOV_H_M()
        0x67 -> MOV_H_A()
        0x68 -> MOV_L_B()
        0x69 -> MOV_L_C()
        0x6a -> MOV_L_D()
        0x6b -> MOV_L_E()
        0x6c -> MOV_L_H()
        0x6d -> MOV_L_L()
        0x6e -> MOV_L_M()
        0x6f -> MOV_L_A()
        0x70 -> MOV_M_B()
        0x71 -> MOV_M_C()
        0x72 -> MOV_M_D()
        0x73 -> MOV_M_E()
        0x74 -> MOV_M_H()
        0x75 -> MOV_M_L()
        0x76 -> HLT()
        0x77 -> MOV_M_A()
        0x78 -> MOV_A_B()
        0x79 -> MOV_A_C()
        0x7a -> MOV_A_D()
        0x7b -> MOV_A_E()
        0x7c -> MOV_A_H()
        0x7d -> MOV_A_L()
        0x7e -> MOV_A_M()
        0x7f -> MOV_A_A()
        0x80 -> ADD_B()
        0x81 -> ADD_C()
        0x82 -> ADD_D()
        0x83 -> ADD_E()
        0x84 -> ADD_H()
        0x85 -> ADD_L()
        0x86 -> ADD_M()
        0x87 -> ADD_A()
        0x88 -> ADC_B()
        0x89 -> ADC_C()
        0x8a -> ADC_D()
        0x8b -> ADC_E()
        0x8c -> ADC_H()
        0x8d -> ADC_L()
        0x8e -> ADC_M()
        0x8f -> ADC_A()
        0x90 -> SUB_B()
        0x91 -> SUB_C()
        0x92 -> SUB_D()
        0x93 -> SUB_E()
        0x94 -> SUB_H()
        0x95 -> SUB_L()
        0x96 -> SUB_M()
        0x97 -> SUB_A()
        0x98 -> SBB_B()
        0x99 -> SBB_C()
        0x9a -> SBB_D()
        0x9b -> SBB_E()
        0x9c -> SBB_H()
        0x9d -> SBB_L()
        0x9e -> SBB_M()
        0x9f -> SBB_A()
        0xa0 -> ANA_B()
        0xa1 -> ANA_C()
        0xa2 -> ANA_D()
        0xa3 -> ANA_E()
        0xa4 -> ANA_H()
        0xa5 -> ANA_L()
        0xa6 -> ANA_M()
        0xa7 -> ANA_A()
        0xa8 -> XRA_B()
        0xa9 -> XRA_C()
        0xaa -> XRA_D()
        0xab -> XRA_E()
        0xac -> XRA_H()
        0xad -> XRA_L()
        0xae -> XRA_M()
        0xaf -> XRA_A()
        0xb0 -> ORA_B()
        0xb1 -> ORA_C()
        0xb2 -> ORA_D()
        0xb3 -> ORA_E()
        0xb4 -> ORA_H()
        0xb5 -> ORA_L()
        0xb6 -> ORA_M()
        0xb7 -> ORA_A()
        0xb8 -> CMP_B()
        0xb9 -> CMP_C()
        0xba -> CMP_D()
        0xbb -> CMP_E()
        0xbc -> CMP_H()
        0xbd -> CMP_L()
        0xbe -> CMP_M()
        0xbf -> CMP_A()
        0xc0 -> RNZ()
        0xc1 -> POP_B()
        0xc2 -> JNZ()
        0xc3 -> JMP()
        0xc4 -> CNZ()
        0xc5 -> PUSH_B()
        0xc6 -> ADI()
        0xc7 -> RST_0()
        0xc8 -> RZ()
        0xc9 -> RET()
        0xca -> JZ()
        0xcc -> CZ()
        0xcd -> CALL()
        0xce -> ACI()
        0xcf -> RST_1()
        0xd0 -> RNC()
        0xd1 -> POP_D()
        0xd2 -> JNC()
        0xd3 -> OUT()
        0xd4 -> CNC()
        0xd5 -> PUSH_D()
        0xd6 -> SUI()
        0xd7 -> RST_2()
        0xd8 -> RC()
        0xda -> JC()
        0xdb -> IN()
        0xdc -> CC()
        0xde -> SBI()
        0xdf -> RST_3()
        0xe0 -> RPO()
        0xe1 -> POP_H()
        0xe2 -> JPO()
        0xe3 -> XTHL()
        0xe4 -> CPO()
        0xe5 -> PUSH_H()
        0xe6 -> ANI()
        0xe7 -> RST_4()
        0xe8 -> RPE()
        0xe9 -> PCHL()
        0xea -> JPE()
        0xeb -> XCHG()
        0xec -> CPE()
        0xee -> XRI()
        0xef -> RST_5()
        0xf0 -> RP()
        0xf1 -> POP_PSW()
        0xf2 -> JP()
        0xf3 -> DI()
        0xf4 -> CP()
        0xf5 -> PUSH_PSW()
        0xf6 -> ORI()
        0xf7 -> RST_6()
        0xf8 -> RM()
        0xf9 -> SPHL()
        0xfa -> JM()
        0xfb -> EI()
        0xfc -> CM()
        0xfe -> CPI()
        0xff -> RST_7()
        else -> Mystery()
    }
}

abstract class OpCode(val opCode: Int, val operandCount: Int = 0, val noAdvance: Boolean = false) {

    var offset: Ushort = Ushort(0)
    override fun toString(): String {
        return "${String.format("%04X", offset.toInt())}\t${this.represent()}"
    }

    fun consume(pc: Ushort, bytes: ByteArray) {
        offset = pc
        consumeInternal(bytes.sliceArray((pc+1).toInt()..(pc+this.operandCount).toInt()))
    }

    open fun consumeInternal(bytes: ByteArray) {}
    abstract fun represent(): String

    fun execAndAdvance(state:State) {
        execute(state)
        if(!noAdvance) state.pc += this.operandCount + 1
    }

    open fun execute(state: State) {
        throw RuntimeException("Unimplemented instruction ${this.javaClass.simpleName}")
    }

    fun setFlags(state: State, result: Ushort) = setFlags(state, result, 16)
    fun setFlags(state: State, result: Ubyte) = setFlags(state, result.toUshort(), 8)
    fun setFlags(state: State, result: Ushort, size: Int) {
        state.flags.z = result.and(0xff).toUbyte() == ZERO

        state.flags.s = result.and(0x80).toUbyte() != ZERO

        state.flags.cy = result > 0xff

        state.flags.p = parity(result.and(0xff).toUbyte(), size)
    }

    fun parity(result: Ubyte, size: Int): Boolean {
        var p = 0
        var work = result.and(1.shl(size)-1)
        for(i in (0..size)) {
            if(work.and(0x1).toInt() == 0x1) p++
            work = work.shr(1)
        }
        return (0 == (p.and(0x1)))
    }

    fun addA(state: State, byte: Ubyte): Ubyte {
        val result = state.a.toUshort() + byte.toUshort()
        setFlags(state, result)
        return result.and(0xff).toUbyte()
    }

    fun subA(state: State, byte: Ubyte): Ubyte {
        val result = state.a.toUshort() - byte.toUshort()
        setFlags(state, result)
        return result.and(0xff).toUbyte()
    }


}

abstract class NoArgOpCode(opCode: Int, noAdvance: Boolean = false): OpCode(opCode, 0, noAdvance) {
    override fun represent(): String = this.javaClass.simpleName.replaceFirst("_", " ").replaceFirst("_", ",")
}

abstract class ByteOpCode(opCode: Int, noAdvance: Boolean = false): OpCode(opCode, 1, noAdvance) {
    var value: Ubyte? = null
    override fun represent(): String = "${this.javaClass.simpleName.replaceFirst("_", "\t")}${if(this.javaClass.simpleName.contains("_")) "," else "\t"}#${value.hex()}"

    override fun consumeInternal(bytes: ByteArray) {
        value = bytes[0].toUbyte()
    }
}

abstract class WordOpCode(opCode: Int, noAdvance: Boolean = false): OpCode(opCode, 2, noAdvance) {
    var value: Ushort? = null
    var hi: Ubyte? = null
    var lo: Ubyte? = null

    override fun represent(): String = "${this.javaClass.simpleName.replaceFirst("_", "\t")}${if(this.javaClass.simpleName.contains("_")) "," else "\t"}#${value.hex(isWord = true)}"

    override fun consumeInternal(bytes: ByteArray) {
        value = bytes[1].toWord(bytes[0])
        hi = bytes[1].toUbyte()
        lo = bytes[0].toUbyte()
    }
}

// *** JUMPS ***
abstract class JumpOpCode(opCode: Int): WordOpCode(opCode, true) {
    fun jumpIf(condition: Boolean, state: State) {
        if(condition) {
            state.pc = value!!
        } else {
            state.pc += this.operandCount + 1
        }
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
    fun callIf(condition: Boolean, state: State) {
        if(condition) {
            state.pc += 3
            state.memory[state.sp - 1] = state.pc.hi()
            state.memory[state.sp - 2] = state.pc.lo()
            state.sp -= 2

            state.pc = value!!
        } else {
            state.pc += this.operandCount + 1
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
    override fun execute(state: State) {
        //special diag code
        if(value!! == Ushort(5)) {
            if(state.c == Ubyte(9)) {
                var offset = state.d.toWord(state.e)
                offset += 3
                do {
                    val string = state.memory[offset++]
                    println(string.toChar())
                } while(string.toChar() != '$')
                state.pc = 0x03.toUshort()
            } else if(state.c == Ubyte(2)) {
                exitProcess(0)
            }
        } else {
            callIf(true, state)
        }
    }
}

// *** RETURNS
abstract class ReturnOpCode(opCode:Int):NoArgOpCode(opCode, true) {
    fun returnIf(condition: Boolean, state: State) {
        if(condition) {
            state.pc = state.memory[state.sp + 1].toUbyte().toWord(state.memory[state.sp].toUbyte())
            state.sp += 2
        } else {
            state.pc += this.operandCount + 1
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
    override fun execute(state: State) {
        val popped = state.pop()
        state.c = popped.lo()
        state.b = popped.hi()
    }
}
class PUSH_B:StackOpCode(0xc5) {
    override fun execute(state: State) = state.push(state.bc())
}
class POP_D:StackOpCode(0xd1) {
    override fun execute(state: State) {
        val popped = state.pop()
        state.e = popped.lo()
        state.d = popped.hi()
    }
}

class PUSH_D:StackOpCode(0xd5) {
    override fun execute(state: State) = state.push(state.de())
}
class POP_H:StackOpCode(0xe1) {
    override fun execute(state: State) {
        val popped = state.pop()
        state.l = popped.lo()
        state.h = popped.hi()
    }
}
class PUSH_H:StackOpCode(0xe5) {
    override fun execute(state: State) = state.push(state.hl())
}
class POP_PSW:StackOpCode(0xf1) {
    override fun execute(state: State) {
        val popped = state.pop()
        state.flags.fromByte(popped.lo())
        state.a = popped.hi()
    }
}
class PUSH_PSW:StackOpCode(0xf5) {
    override fun execute(state: State) = state.push(state.a.toWord(state.flags.asByte()))
}

// === ACCUMULATOR OPS
abstract class AccumulatorOpCode(opCode: Int): NoArgOpCode(opCode) {

    fun setAccum(state: State, func: (Ubyte) -> Ushort) {
        val result = func(state.a)
        setFlags(state, result)
        state.a = result.and(0xff).toUbyte()
    }

    fun withAccum(state: State, func: (Ubyte) -> Ushort) {
        val result = func(state.a)
        setFlags(state, result)
    }

    fun withCarry(state: State, byte: Ubyte): Ubyte = byte + if(state.flags.cy) 0x1 else 0x0
}
class ADD_B:AccumulatorOpCode(0x80) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.b.toUshort() }
}
class ADD_C:AccumulatorOpCode(0x81) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.c.toUshort() }
}
class ADD_D:AccumulatorOpCode(0x82) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.d.toUshort() }
}
class ADD_E:AccumulatorOpCode(0x83) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.e.toUshort() }
}
class ADD_H:AccumulatorOpCode(0x84) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.h.toUshort() }
}
class ADD_L:AccumulatorOpCode(0x85) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.l.toUshort() }
}
class ADD_M:AccumulatorOpCode(0x86) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.heap().toUshort() }
}
class ADD_A:AccumulatorOpCode(0x87) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + state.a.toUshort() }
}
class ADC_B:AccumulatorOpCode(0x88) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.b)).toUshort()}
}
class ADC_C:AccumulatorOpCode(0x89) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.c)).toUshort()}
}
class ADC_D:AccumulatorOpCode(0x8a) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.d)).toUshort()}
}
class ADC_E:AccumulatorOpCode(0x8b) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.e)).toUshort()}
}
class ADC_H:AccumulatorOpCode(0x8c) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.h)).toUshort()}
}
class ADC_L:AccumulatorOpCode(0x8d) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.l)).toUshort()}
}
class ADC_M:AccumulatorOpCode(0x8e) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.heap())).toUshort()}
}
class ADC_A:AccumulatorOpCode(0x8f) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() + (withCarry(state, state.a)).toUshort()}
}
class SUB_B:AccumulatorOpCode(0x90) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.b.toUshort() }
}
class SUB_C:AccumulatorOpCode(0x91) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.c.toUshort() }
}
class SUB_D:AccumulatorOpCode(0x92) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.d.toUshort() }
}
class SUB_E:AccumulatorOpCode(0x93) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.e.toUshort() }
}
class SUB_H:AccumulatorOpCode(0x94) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.h.toUshort() }
}
class SUB_L:AccumulatorOpCode(0x95) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.l.toUshort() }
}
class SUB_M:AccumulatorOpCode(0x96) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.heap().toUshort() }
}
class SUB_A:AccumulatorOpCode(0x97) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - state.a.toUshort() }
}
class SBB_B:AccumulatorOpCode(0x98) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.b)).toUshort()}
}
class SBB_C:AccumulatorOpCode(0x99) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.c)).toUshort()}
}
class SBB_D:AccumulatorOpCode(0x9a) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.d)).toUshort()}
}
class SBB_E:AccumulatorOpCode(0x9b) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.e)).toUshort()}
}
class SBB_H:AccumulatorOpCode(0x9c) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.h)).toUshort()}
}
class SBB_L:AccumulatorOpCode(0x9d) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.l)).toUshort()}
}
class SBB_M:AccumulatorOpCode(0x9e) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.heap())).toUshort()}
}
class SBB_A:AccumulatorOpCode(0x9f) {
    override fun execute(state: State) = setAccum(state) { it.toUshort() - (withCarry(state, state.a)).toUshort()}
}

class ANA_B:AccumulatorOpCode(0xa0) {
    override fun execute(state: State) = setAccum(state) { it.and(state.b).toUshort() }
}
class ANA_C:AccumulatorOpCode(0xa1) {
    override fun execute(state: State) = setAccum(state) { it.and(state.c).toUshort() }
}
class ANA_D:AccumulatorOpCode(0xa2) {
    override fun execute(state: State) = setAccum(state) { it.and(state.d).toUshort() }
}
class ANA_E:AccumulatorOpCode(0xa3) {
    override fun execute(state: State) = setAccum(state) { it.and(state.e).toUshort() }
}
class ANA_H:AccumulatorOpCode(0xa4) {
    override fun execute(state: State) = setAccum(state) { it.and(state.h).toUshort() }
}
class ANA_L:AccumulatorOpCode(0xa5) {
    override fun execute(state: State) = setAccum(state) { it.and(state.l).toUshort() }
}
class ANA_M:AccumulatorOpCode(0xa6) {
    override fun execute(state: State) = setAccum(state) { it.and(state.heap()).toUshort() }
}
class ANA_A:AccumulatorOpCode(0xa7) {
    override fun execute(state: State) = setAccum(state) { it.and(state.a).toUshort() }
}
class XRA_B:AccumulatorOpCode(0xa8) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.b).toUshort() }
}
class XRA_C:AccumulatorOpCode(0xa9) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.c).toUshort() }
}
class XRA_D:AccumulatorOpCode(0xaa) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.d).toUshort() }
}
class XRA_E:AccumulatorOpCode(0xab) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.e).toUshort() }
}
class XRA_H:AccumulatorOpCode(0xac) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.h).toUshort() }
}
class XRA_L:AccumulatorOpCode(0xad) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.l).toUshort() }
}
class XRA_M:AccumulatorOpCode(0xae) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.heap()).toUshort() }
}
class XRA_A:AccumulatorOpCode(0xaf) {
    override fun execute(state: State) = setAccum(state) { it.xor(state.a).toUshort() }
}
class ORA_B:AccumulatorOpCode(0xb0) {
    override fun execute(state: State) = setAccum(state) { it.or(state.b).toUshort() }
}
class ORA_C:AccumulatorOpCode(0xb1) {
    override fun execute(state: State) = setAccum(state) { it.or(state.c).toUshort() }
}
class ORA_D:AccumulatorOpCode(0xb2) {
    override fun execute(state: State) = setAccum(state) { it.or(state.d).toUshort() }
}
class ORA_E:AccumulatorOpCode(0xb3) {
    override fun execute(state: State) = setAccum(state) { it.or(state.e).toUshort() }
}
class ORA_H:AccumulatorOpCode(0xb4) {
    override fun execute(state: State) = setAccum(state) { it.or(state.h).toUshort() }
}
class ORA_L:AccumulatorOpCode(0xb5) {
    override fun execute(state: State) = setAccum(state) { it.or(state.l).toUshort() }
}
class ORA_M:AccumulatorOpCode(0xb6) {
    override fun execute(state: State) = setAccum(state) { it.or(state.heap()).toUshort() }
}
class ORA_A:AccumulatorOpCode(0xb7) {
    override fun execute(state: State) = setAccum(state) { it.or(state.a).toUshort() }
}
class CMP_B:AccumulatorOpCode(0xb8) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.b.toUshort() }
}
class CMP_C:AccumulatorOpCode(0xb9) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.c.toUshort() }
}
class CMP_D:AccumulatorOpCode(0xba) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.d.toUshort() }
}
class CMP_E:AccumulatorOpCode(0xbb) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.e.toUshort() }
}
class CMP_H:AccumulatorOpCode(0xbc) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.h.toUshort() }
}
class CMP_L:AccumulatorOpCode(0xbd) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.l.toUshort() }
}
class CMP_M:AccumulatorOpCode(0xbe) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.heap().toUshort() }
}
class CMP_A:AccumulatorOpCode(0xbf) {
    override fun execute(state: State) = withAccum(state) { it.toUshort() - state.a.toUshort() }
}
// ---- ==================================================== --------
// ---- ======================a============================== --------

class NOP:NoArgOpCode(0x00) {
    override fun execute(state: State) {}
}
class LXI_B:WordOpCode(0x01) {
    override fun execute(state: State) {
        state.b = hi!!
        state.c = lo!!
    }
}
class STAX_B:NoArgOpCode(0x02) {
    override fun execute(state: State) {
        state.memory[state.bc()] = state.a
    }
}
class INX_B:NoArgOpCode(0x03) {
    override fun execute(state: State) {
        val newValue = state.bc() + 1
        state.b = newValue.hi()
        state.c = newValue.lo()
    }
}
class INR_B:NoArgOpCode(0x04) {
    override fun execute(state: State) {
        state.b += 1
        setFlags(state, state.b)
    }
}
class DCR_B:NoArgOpCode(0x05) {
    override fun execute(state: State) {
        state.b = state.b - 1
        setFlags(state, state.b)
    }
}
class MVI_B:ByteOpCode(0x06) {
    override fun execute(state: State) {
        state.b = value!!
    }
}
class RLC:NoArgOpCode(0x07) {
    override fun execute(state: State) {
        val highOrder = state.a.and(0x80) != 0x0.toUbyte()
        state.a = state.a.shl(1)

        state.flags.cy = highOrder
        if(highOrder) {
            state.a = state.a.or(ONE)
        }

    }
}
class DAD_B:NoArgOpCode(0x09) {
    override fun execute(state: State) {
        val newVal = state.hl().toUint() + state.bc().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LDAX_B:NoArgOpCode(0x0a) {
    override fun execute(state: State) {
        state.a = state.memory[state.bc()]
    }
}
class DCX_B:NoArgOpCode(0x0b) {
    override fun execute(state: State) {
        val newValue = state.bc() - 1
        state.b = newValue.hi()
        state.c = newValue.lo()
    }
}
class INR_C:NoArgOpCode(0x0c) {
    override fun execute(state: State) {
        state.c += 1
        setFlags(state, state.c)
    }
}
class DCR_C:NoArgOpCode(0x0d) {
    override fun execute(state: State) {
        state.c -= 1
        setFlags(state, state.c)
    }
}
class MVI_C:ByteOpCode(0x0e) {
    override fun execute(state: State) {
        state.c = value!!
    }
}
class RRC:NoArgOpCode(0x0f) {
    override fun execute(state: State) {
        val lowOrder = state.a.and(0x1) != 0x0.toUbyte()
        state.a = state.a.shr(1)

        state.flags.cy = lowOrder
        if(lowOrder) {
            state.a = state.a.or(0x80)
        }

    }
}
class LXI_D:WordOpCode(0x11) {
    override fun execute(state: State) {
        state.d = hi!!
        state.e = lo!!
    }
}
class STAX_D:NoArgOpCode(0x12) {
    override fun execute(state: State) {
        state.memory[state.de()] = state.a
    }
}
class INX_D:NoArgOpCode(0x13) {
    override fun execute(state: State) {
        val newValue = state.de() + 1
        state.d = newValue.hi()
        state.e = newValue.lo()
    }
}
class INR_D:NoArgOpCode(0x14){
    override fun execute(state: State) {
        state.d += 1
        setFlags(state, state.d)
    }
}

class DCR_D:NoArgOpCode(0x15) {
    override fun execute(state: State) {
        state.d -= 1
        setFlags(state, state.d)
    }
}
class MVI_D:ByteOpCode(0x16) {
    override fun execute(state: State) {
        state.d = value!!
    }
}
class RAL:NoArgOpCode(0x17) {
    override fun execute(state: State) {
        val highOrder = state.a.and(0x80) != 0x0.toUbyte()
        state.a = state.a.shl(1).or(if(state.flags.cy) ONE else ZERO)
        state.flags.cy = highOrder
    }
}
class DAD_D:NoArgOpCode(0x19) {
    override fun execute(state: State) {
        val newVal = state.hl().toUint() + state.de().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LDAX_D:NoArgOpCode(0x1a) {
    override fun execute(state: State) {
        state.a = state.memory[state.de()].toUbyte()
    }
}
class DCX_D:NoArgOpCode(0x1b) {
    override fun execute(state: State) {
        val newValue = state.de() - 1
        state.d = newValue.hi()
        state.e = newValue.lo()

    }
}
class INR_E:NoArgOpCode(0x1c) {
    override fun execute(state: State) {
        state.e += 1
        setFlags(state, state.e)
    }
}
class DCR_E:NoArgOpCode(0x1d) {
    override fun execute(state: State) {
        state.e -= 1
        setFlags(state, state.e)
    }
}
class MVI_E:ByteOpCode(0x1e) {
    override fun execute(state: State) {
        state.e = value!!
    }
}
class RAR:NoArgOpCode(0x1f) {
    override fun execute(state: State) {
        val lowOrder = state.a.and(0x1) != 0x0.toUbyte()
        state.a = state.a.shr(1).or(if(state.flags.cy) 0x80.toUbyte() else ZERO)
        state.flags.cy = lowOrder
    }
}

class LXI_H:WordOpCode(0x21) {
    override fun execute(state: State) {
        state.h = hi!!
        state.l = lo!!
    }
}
class SHLD:WordOpCode(0x22) {
    override fun execute(state: State) {
        state.memory[value!!] = state.l
        state.memory[value!! + 1] = state.h
    }
}
class INX_H:NoArgOpCode(0x23) {
    override fun execute(state: State) {
        val newValue = state.hl() + 1
        state.h = newValue.hi()
        state.l = newValue.lo()
    }
}

class INR_H:NoArgOpCode(0x24) {
    override fun execute(state: State) {
        state.h += 1
        setFlags(state, state.h)
    }
}
class DCR_H:NoArgOpCode(0x25)  {
    override fun execute(state: State) {
        state.h -= 1
        setFlags(state, state.h)
    }
}
class MVI_H:ByteOpCode(0x26) {
    override fun execute(state: State) {
        state.h = value!!
    }
}

class DAD_H:NoArgOpCode(0x29) {
    override fun execute(state: State) {
        val newVal = state.hl().toUint() + state.hl().toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LHLD:WordOpCode(0x2a) {
    override fun execute(state: State) {
        state.h = state.memory[value!! + 1]
        state.l = state.memory[value!!]
    }
}
class DCX_H:NoArgOpCode(0x2b) {
    override fun execute(state: State) {
        val newValue = state.hl() - 1
        state.h = newValue.hi()
        state.l = newValue.lo()
    }
}
class INR_L:NoArgOpCode(0x2c) {
    override fun execute(state: State) {
        state.l += 1
        setFlags(state, state.l)
    }
}
class DCR_L:NoArgOpCode(0x2d) {
    override fun execute(state: State) {
        state.l -= 1
        setFlags(state, state.l)
    }
}
class MVI_L:ByteOpCode(0x2e) {
    override fun execute(state: State) {
        state.l = value!!
    }
}
class CMA:NoArgOpCode(0x2f) {
    override fun execute(state: State) {
        state.a = state.a.inv()
    }
}
class LXI_SP:WordOpCode(0x31) {
    override fun execute(state: State) {
        state.sp = value!!
    }
}
class STA:WordOpCode(0x32) {
    override fun execute(state: State) {
        state.memory[value!!] = state.a
    }
}
class INX_SP:NoArgOpCode(0x33) {
    override fun execute(state: State) {
        state.sp += 1
    }
}
class INR_M:NoArgOpCode(0x34) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.memory[state.hl()] + 1
        setFlags(state, state.memory[state.hl()])
    }
}
class DCR_M:NoArgOpCode(0x35) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.memory[state.hl()] - 1
        setFlags(state, state.memory[state.hl()])
    }
}
class MVI_M:ByteOpCode(0x36) {
    override fun execute(state: State) {
        state.memory[state.hl()] = value!!
    }
}
class STC:NoArgOpCode(0x37) {
    override fun execute(state: State) {
        state.flags.cy = true
    }
}
class DAD_SP:NoArgOpCode(0x39) {
    override fun execute(state: State) {
        val newVal = state.hl().toUint() + state.sp.toUint()
        if(newVal > 0xffff) state.flags.cy = true
        state.h = newVal.toUshort().hi()
        state.l = newVal.toUshort().lo()
    }
}
class LDA:WordOpCode(0x3a) {
    override fun execute(state: State) {
        state.a = state.memory[value!!].toUbyte()
    }
}
class DCX_SP:NoArgOpCode(0x3b) {
    override fun execute(state: State) {
        state.sp -= 1
    }
}
class INR_A:NoArgOpCode(0x3c) {
    override fun execute(state: State) {
        state.a += 1
        setFlags(state, state.a)
    }
}
class DCR_A:NoArgOpCode(0x3d) {
    override fun execute(state: State) {
        state.a -= 1
        setFlags(state, state.a)
    }
}
class MVI_A:ByteOpCode(0x3e) {
    override fun execute(state: State) {
        state.a = value!!
    }
}
class CMC:NoArgOpCode(0x3f) {
    override fun execute(state: State) {
        state.flags.cy = !state.flags.cy
    }
}
class MOV_B_B:NoArgOpCode(0x40) {
    override fun execute(state: State) {
        state.b = state.b
    }
}
class MOV_B_C:NoArgOpCode(0x41) {
    override fun execute(state: State) {
        state.b = state.c
    }
}
class MOV_B_D:NoArgOpCode(0x42) {
    override fun execute(state: State) {
        state.b = state.d
    }
}
class MOV_B_E:NoArgOpCode(0x43) {
    override fun execute(state: State) {
        state.b = state.e
    }
}
class MOV_B_H:NoArgOpCode(0x44) {
    override fun execute(state: State) {
        state.b = state.h
    }
}
class MOV_B_L:NoArgOpCode(0x45) {
    override fun execute(state: State) {
        state.b = state.l
    }
}
class MOV_B_M:NoArgOpCode(0x46) {
    override fun execute(state: State) {
        state.b = state.memory[state.hl()]
    }
}
class MOV_B_A:NoArgOpCode(0x47) {
    override fun execute(state: State) {
        state.b = state.a
    }
}
class MOV_C_B:NoArgOpCode(0x48) {
    override fun execute(state: State) {
        state.c = state.b
    }
}
class MOV_C_C:NoArgOpCode(0x49) {
    override fun execute(state: State) {
        state.c = state.c
    }
}
class MOV_C_D:NoArgOpCode(0x4a) {
    override fun execute(state: State) {
        state.c = state.d
    }
}
class MOV_C_E:NoArgOpCode(0x4b) {
    override fun execute(state: State) {
        state.c = state.e
    }
}
class MOV_C_H:NoArgOpCode(0x4c) {
    override fun execute(state: State) {
        state.c = state.h
    }
}
class MOV_C_L:NoArgOpCode(0x4d) {
    override fun execute(state: State) {
        state.c = state.l
    }
}
class MOV_C_M:NoArgOpCode(0x4e) {
    override fun execute(state: State) {
        state.c = state.memory[state.hl()]
    }
}
class MOV_C_A:NoArgOpCode(0x4f) {
    override fun execute(state: State) {
        state.c = state.a
    }
}
class MOV_D_B:NoArgOpCode(0x50) {
    override fun execute(state: State) {
        state.d = state.b
    }
}
class MOV_D_C:NoArgOpCode(0x51) {
    override fun execute(state: State) {
        state.d = state.c
    }
}
class MOV_D_D:NoArgOpCode(0x52) {
    override fun execute(state: State) {
        state.d = state.d
    }
}
class MOV_D_E:NoArgOpCode(0x53) {
    override fun execute(state: State) {
        state.d = state.e
    }
}
class MOV_D_H:NoArgOpCode(0x54) {
    override fun execute(state: State) {
        state.d = state.h
    }
}
class MOV_D_L:NoArgOpCode(0x55) {
    override fun execute(state: State) {
        state.d = state.l
    }
}
class MOV_D_M:NoArgOpCode(0x56) {
    override fun execute(state: State) {
        state.d = state.memory[state.hl()]
    }
}
class MOV_D_A:NoArgOpCode(0x57) {
    override fun execute(state: State) {
        state.d = state.a
    }
}
class MOV_E_B:NoArgOpCode(0x58) {
    override fun execute(state: State) {
        state.e = state.b
    }
}
class MOV_E_C:NoArgOpCode(0x59) {
    override fun execute(state: State) {
        state.e = state.c
    }
}
class MOV_E_D:NoArgOpCode(0x5a) {
    override fun execute(state: State) {
        state.e = state.d
    }
}
class MOV_E_E:NoArgOpCode(0x5b) {
    override fun execute(state: State) {
        state.e = state.e
    }
}
class MOV_E_H:NoArgOpCode(0x5c) {
    override fun execute(state: State) {
        state.e = state.h
    }
}
class MOV_E_L:NoArgOpCode(0x5d) {
    override fun execute(state: State) {
        state.e = state.l
    }
}
class MOV_E_M:NoArgOpCode(0x5e) {
    override fun execute(state: State) {
        state.e = state.memory[state.hl()]
    }
}
class MOV_E_A:NoArgOpCode(0x5f) {
    override fun execute(state: State) {
        state.e = state.a
    }
}
class MOV_H_B:NoArgOpCode(0x60) {
    override fun execute(state: State) {
        state.h = state.b
    }
}
class MOV_H_C:NoArgOpCode(0x61) {
    override fun execute(state: State) {
        state.h = state.c
    }
}
class MOV_H_D:NoArgOpCode(0x62) {
    override fun execute(state: State) {
        state.h = state.d
    }
}
class MOV_H_E:NoArgOpCode(0x63) {
    override fun execute(state: State) {
        state.h = state.e
    }
}
class MOV_H_H:NoArgOpCode(0x64) {
    override fun execute(state: State) {
        state.h = state.h
    }
}
class MOV_H_L:NoArgOpCode(0x65) {
    override fun execute(state: State) {
        state.h = state.l
    }
}
class MOV_H_M:NoArgOpCode(0x66) {
    override fun execute(state: State) {
        state.h = state.memory[state.hl()]
    }
}
class MOV_H_A:NoArgOpCode(0x67) {
    override fun execute(state: State) {
        state.h = state.a
    }
}
class MOV_L_B:NoArgOpCode(0x68) {
    override fun execute(state: State) {
        state.l = state.b
    }
}
class MOV_L_C:NoArgOpCode(0x69) {
    override fun execute(state: State) {
        state.l = state.c
    }
}
class MOV_L_D:NoArgOpCode(0x6a) {
    override fun execute(state: State) {
        state.l = state.d
    }
}
class MOV_L_E:NoArgOpCode(0x6b) {
    override fun execute(state: State) {
        state.l = state.e
    }
}
class MOV_L_H:NoArgOpCode(0x6c) {
    override fun execute(state: State) {
        state.l = state.h
    }
}
class MOV_L_L:NoArgOpCode(0x6d) {
    override fun execute(state: State) {
        state.l = state.l
    }
}
class MOV_L_M:NoArgOpCode(0x6e) {
    override fun execute(state: State) {
        state.l = state.memory[state.hl()]
    }
}
class MOV_L_A:NoArgOpCode(0x6f) {
    override fun execute(state: State) {
        state.l = state.a
    }
}
class MOV_M_B:NoArgOpCode(0x70) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.b
    }
}
class MOV_M_C:NoArgOpCode(0x71) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.c
    }
}
class MOV_M_D:NoArgOpCode(0x72) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.d
    }
}
class MOV_M_E:NoArgOpCode(0x73) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.e
    }
}
class MOV_M_H:NoArgOpCode(0x74) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.h
    }
}
class MOV_M_L:NoArgOpCode(0x75) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.l
    }
}
class HLT:NoArgOpCode(0x76) {
    override fun execute(state: State) {
        throw RuntimeException("Halted")
    }
}
class MOV_M_A:NoArgOpCode(0x77) {
    override fun execute(state: State) {
        state.memory[state.hl()] = state.a
    }
}
class MOV_A_B:NoArgOpCode(0x78) {
    override fun execute(state: State) {
        state.a = state.b
    }
}
class MOV_A_C:NoArgOpCode(0x79) {
    override fun execute(state: State) {
        state.a = state.c
    }
}
class MOV_A_D:NoArgOpCode(0x7a) {
    override fun execute(state: State) {
        state.a = state.d
    }
}
class MOV_A_E:NoArgOpCode(0x7b) {
    override fun execute(state: State) {
        state.a = state.e
    }
}
class MOV_A_H:NoArgOpCode(0x7c) {
    override fun execute(state: State) {
        state.a = state.h
    }
}
class MOV_A_L:NoArgOpCode(0x7d) {
    override fun execute(state: State) {
        state.a = state.l
    }
}
class MOV_A_M:NoArgOpCode(0x7e) {
    override fun execute(state: State) {
        state.a = state.memory[state.hl()]
    }
}
class MOV_A_A:NoArgOpCode(0x7f) {
    override fun execute(state: State) {
        state.a = state.a
    }
}


class ADI:ByteOpCode(0xc6) {
    override fun execute(state: State) {
        state.a = addA(state, value!!)
    }
}

class ACI:ByteOpCode(0xce) {
    override fun execute(state: State) {
        val valAndCarry = value!! + if(state.flags.cy) ONE else ZERO
        state.a = addA(state, valAndCarry!!)
    }
}

class SUI:ByteOpCode(0xd6) {
    override fun execute(state: State) {
        state.a = subA(state, value!!)
    }
}

class SBI:ByteOpCode(0xde) {
    override fun execute(state: State) {
        val valAndCarry = value!! + if(state.flags.cy) 0x1 else 0x0
        state.a = subA(state, valAndCarry)
    }
}

class XTHL:NoArgOpCode(0xe3) {
    override fun execute(state: State) {
        val tmpLo = state.l
        state.l = state.memory[state.sp]
        state.memory[state.sp] = tmpLo

        val tmpHi = state.h
        state.h = state.memory[state.sp + 1]
        state.memory[state.sp + 1] = tmpHi
    }
}


class ANI:ByteOpCode(0xe6) {
    override fun execute(state: State) {
        state.a = state.a.and(value!!)
        setFlags(state, state.a)
    }
}

class PCHL:NoArgOpCode(0xe9, true) {
    override fun execute(state: State) {
        state.pc = state.hl()
    }
}

class XCHG:NoArgOpCode(0xeb) {
    override fun execute(state: State) {
        val tmp = state.h
        state.h = state.d
        state.d = tmp

        val tmp2 = state.l
        state.l = state.e
        state.e = tmp2
    }
}

class XRI:ByteOpCode(0xee) {
    override fun execute(state: State) {
        state.a = state.a.xor(value!!)
        setFlags(state, state.a)
    }
}

class ORI:ByteOpCode(0xf6) {
    override fun execute(state: State) {
        state.a = state.a.or(value!!)
        setFlags(state, state.a)
    }
}

class SPHL:NoArgOpCode(0xf9) {
    override fun execute(state: State) {
        state.sp = state.hl()
    }
}

class CPI:ByteOpCode(0xfe) {
    override fun execute(state: State) {
        val result = state.a.toUshort() - value!!.toUshort()
        setFlags(state, result)
    }
}

// **** Unimplemented ***
class DI:NoArgOpCode(0xf3) {
    override fun execute(state: State) {}
}
class EI:NoArgOpCode(0xfb) {
    override fun execute(state: State) {}
}
class OUT:ByteOpCode(0xd3) {
    override fun execute(state: State) {}
}
class IN:ByteOpCode(0xdb) {
    override fun execute(state: State) {}
}
class SIM:NoArgOpCode(0x30)
class RIM:NoArgOpCode(0x20)
class DAA:NoArgOpCode(0x27)
class RST_0:NoArgOpCode(0xc7)
class RST_1:NoArgOpCode(0xcf)
class RST_2:NoArgOpCode(0xd7)
class RST_3:NoArgOpCode(0xdf)
class RST_4:NoArgOpCode(0xe7)
class RST_5:NoArgOpCode(0xef)
class RST_6:NoArgOpCode(0xf7)
class RST_7:NoArgOpCode(0xff)
class Mystery:NoArgOpCode(999) {
    override fun represent(): String = "Unknown!"
}