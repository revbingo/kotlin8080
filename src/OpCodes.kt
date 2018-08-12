
import unsigned.Ubyte
import unsigned.Ushort
import unsigned.toUbyte
import unsigned.toUshort

val ZERO = Ubyte(0)

fun Number.toUnsignedWord(loByte: Number) = this.toUshort().and(0xff).shl(8).or(loByte.toUshort().and(0xff))

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

abstract class OpCode(val opCode: Int, val operandCount: Int = 0) {

    val operands = emptyArray<String>()
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
    open fun execute(state: State) {
        throw RuntimeException("Unimplemented instruction ${this.javaClass.simpleName}")
    }

    fun setFlags(state: State, result: Ushort) {
        state.flags.z = result.and(0xff).toUbyte() == ZERO

        state.flags.s = result.and(0x80).toUbyte() != ZERO

        state.flags.cy = result > 0xff

        state.flags.p = ((result.and(0xff).toUbyte()) % 2) == ZERO
    }

    fun setFlags(state: State, result: Ubyte) {
        state.flags.z = result.and(0xff).toUbyte() == ZERO

        state.flags.s = result.and(0x80).toUbyte() != ZERO

        state.flags.cy = result > 0xff

        state.flags.p = ((result.and(0xff).toUbyte()) % 2) == ZERO
    }

    fun add(state: State, byte: Ubyte): Ubyte {
        val result = state.a.toUshort() + byte.toUshort()
        setFlags(state, result)
        return result.and(0xff).toUbyte()
    }


}

abstract class NoArgOpCode(opCode: Int): OpCode(opCode, 0) {
    override fun represent(): String = this.javaClass.simpleName.replaceFirst("_", " ").replaceFirst("_", ",")


}

abstract class ByteOpCode(opCode: Int): OpCode(opCode, 1) {
    var value: Ubyte? = null
    override fun represent(): String = "${this.javaClass.simpleName.replaceFirst("_", "\t")}${if(this.javaClass.simpleName.contains("_")) "," else "\t"}#${value.hex()}"

    override fun consumeInternal(bytes: ByteArray) {
        value = bytes[0].toUbyte()
    }
}

abstract class WordOpCode(opCode: Int): OpCode(opCode, 2) {
    var value: Ushort? = null
    var hi: Ubyte? = null
    var lo: Ubyte? = null

    override fun represent(): String = "${this.javaClass.simpleName.replaceFirst("_", "\t")}${if(this.javaClass.simpleName.contains("_")) "," else "\t"}#${value.hex(isWord = true)}"

    override fun consumeInternal(bytes: ByteArray) {
        value = bytes[1].toUnsignedWord(bytes[0])
        hi = bytes[1].toUbyte()
        lo = bytes[0].toUbyte()
    }
}


// ---- ==================================================== --------
// ---- ==================================================== --------

class NOP:NoArgOpCode(0x00) {
    override fun execute(state: State) {}
}
class LXI_B:WordOpCode(0x01) {
    override fun execute(state: State) {
        state.b = hi!!
        state.c = lo!!
    }
}

class STAX_B:NoArgOpCode(0x02)
class INX_B:NoArgOpCode(0x03)
class INR_B:NoArgOpCode(0x04)
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
class RLC:NoArgOpCode(0x07)
class DAD_B:NoArgOpCode(0x09)
class LDAX_B:NoArgOpCode(0x0a)
class DCX_B:NoArgOpCode(0x0b)
class INR_C:NoArgOpCode(0x0c)
class DCR_C:NoArgOpCode(0x0d)
class MVI_C:ByteOpCode(0x0e) {
    override fun execute(state: State) {
        state.c = value!!
    }
}
class RRC:NoArgOpCode(0x0f)
class LXI_D:WordOpCode(0x11) {
    override fun execute(state: State) {
        state.d = hi!!
        state.e = lo!!
    }
}
class STAX_D:NoArgOpCode(0x12)
class INX_D:NoArgOpCode(0x13) {
    override fun execute(state: State) {
        val newValue = state.de() + 1
        state.d = newValue.hi()
        state.e = newValue.lo()
    }
}
class INR_D:NoArgOpCode(0x14)
class DCR_D:NoArgOpCode(0x15)
class MVI_D:ByteOpCode(0x16) {
    override fun execute(state: State) {
        state.d = value!!
    }
}
class RAL:NoArgOpCode(0x17)
class DAD_D:NoArgOpCode(0x19) {
    override fun execute(state: State) {
        val newVal = state.hl() + state.de()
        state.h = newVal.hi()
        state.l = newVal.lo()

    }
}
class LDAX_D:NoArgOpCode(0x1a) {
    override fun execute(state: State) {
        val addr = state.d.toUnsignedWord(state.e).toInt()
        state.a = state.memory[addr].toUbyte()
    }
}
class DCX_D:NoArgOpCode(0x1b)
class INR_E:NoArgOpCode(0x1c)
class DCR_E:NoArgOpCode(0x1d)
class MVI_E:ByteOpCode(0x1e) {
    override fun execute(state: State) {
        state.e = value!!
    }
}
class RAR:NoArgOpCode(0x1f)
class RIM:NoArgOpCode(0x20)
class LXI_H:WordOpCode(0x21) {
    override fun execute(state: State) {
        state.h = hi!!
        state.l = lo!!
    }
}
class SHLD:WordOpCode(0x22)
class INX_H:NoArgOpCode(0x23) {
    override fun execute(state: State) {
        val newValue = state.hl() + 1
        state.h = newValue.hi()
        state.l = newValue.lo()
    }
}

class INR_H:NoArgOpCode(0x24)
class DCR_H:NoArgOpCode(0x25)
class MVI_H:ByteOpCode(0x26) {
    override fun execute(state: State) {
        state.h = value!!
    }
}
class DAA:NoArgOpCode(0x27)
class DAD_H:NoArgOpCode(0x29)
class LHLD:WordOpCode(0x2a)
class DCX_H:NoArgOpCode(0x2b)
class INR_L:NoArgOpCode(0x2c)
class DCR_L:NoArgOpCode(0x2d)
class MVI_L:ByteOpCode(0x2e) {
    override fun execute(state: State) {
        state.l = value!!
    }
}
class CMA:NoArgOpCode(0x2f)
class SIM:NoArgOpCode(0x30)
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

class INX_SP:NoArgOpCode(0x33)
class INR_M:NoArgOpCode(0x34)
class DCR_M:NoArgOpCode(0x35)
class MVI_M:ByteOpCode(0x36) {
    override fun execute(state: State) {
        state.memory[state.hl()] = value!!
    }
}
class STC:NoArgOpCode(0x37)
class DAD_SP:NoArgOpCode(0x39)
class LDA:WordOpCode(0x3a) {
    override fun execute(state: State) {
        state.a = state.memory[value!!].toUbyte()
    }
}
class DCX_SP:NoArgOpCode(0x3b)
class INR_A:NoArgOpCode(0x3c)
class DCR_A:NoArgOpCode(0x3d)
class MVI_A:ByteOpCode(0x3e) {
    override fun execute(state: State) {
        state.a = value!!
    }
}
class CMC:NoArgOpCode(0x3f)
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
class ADD_B:NoArgOpCode(0x80) {
    override fun execute(state: State) {
        state.a = add(state, state.b)
    }
}

class ADD_C:NoArgOpCode(0x81) {
    override fun execute(state: State) {
        state.a = add(state, state.c)
    }
}

class ADD_D:NoArgOpCode(0x82) {
    override fun execute(state: State) {
        state.a = add(state, state.d)
    }
}

class ADD_E:NoArgOpCode(0x83) {
    override fun execute(state: State) {
        state.a = add(state, state.e)
    }
}

class ADD_H:NoArgOpCode(0x84) {
    override fun execute(state: State) {
        state.a = add(state, state.h)
    }
}

class ADD_L:NoArgOpCode(0x85) {
    override fun execute(state: State) {
        state.a = add(state, state.l)
    }
}

class ADD_M:NoArgOpCode(0x86)
class ADD_A:NoArgOpCode(0x87) {
    override fun execute(state: State) {
        state.a = add(state, state.a)
    }
}

class ADC_B:NoArgOpCode(0x88)
class ADC_C:NoArgOpCode(0x89)
class ADC_D:NoArgOpCode(0x8a)
class ADC_E:NoArgOpCode(0x8b)
class ADC_H:NoArgOpCode(0x8c)
class ADC_L:NoArgOpCode(0x8d)
class ADC_M:NoArgOpCode(0x8e)
class ADC_A:NoArgOpCode(0x8f)
class SUB_B:NoArgOpCode(0x90)
class SUB_C:NoArgOpCode(0x91)
class SUB_D:NoArgOpCode(0x92)
class SUB_E:NoArgOpCode(0x93)
class SUB_H:NoArgOpCode(0x94)
class SUB_L:NoArgOpCode(0x95)
class SUB_M:NoArgOpCode(0x96)
class SUB_A:NoArgOpCode(0x97)
class SBB_B:NoArgOpCode(0x98)
class SBB_C:NoArgOpCode(0x99)
class SBB_D:NoArgOpCode(0x9a)
class SBB_E:NoArgOpCode(0x9b)
class SBB_H:NoArgOpCode(0x9c)
class SBB_L:NoArgOpCode(0x9d)
class SBB_M:NoArgOpCode(0x9e)
class SBB_A:NoArgOpCode(0x9f)
class ANA_B:NoArgOpCode(0xa0) {
    override fun execute(state: State) {
        state.a = state.a.and(state.b)
        setFlags(state, state.a)
    }
}
class ANA_C:NoArgOpCode(0xa1) {
    override fun execute(state: State) {
        state.a = state.a.and(state.c)
        setFlags(state, state.a)
    }
}
class ANA_D:NoArgOpCode(0xa2) {
    override fun execute(state: State) {
        state.a = state.a.and(state.d)
        setFlags(state, state.a)
    }
}
class ANA_E:NoArgOpCode(0xa3) {
    override fun execute(state: State) {
        state.a = state.a.and(state.e)
        setFlags(state, state.a)
    }
}
class ANA_H:NoArgOpCode(0xa4) {
    override fun execute(state: State) {
        state.a = state.a.and(state.h)
        setFlags(state, state.a)
    }
}
class ANA_L:NoArgOpCode(0xa5) {
    override fun execute(state: State) {
        state.a = state.a.and(state.l)
        setFlags(state, state.a)
    }
}
class ANA_M:NoArgOpCode(0xa6) {
    override fun execute(state: State) {
        state.a = state.a.and(state.memory[state.hl()])
        setFlags(state, state.a)
    }
}
class ANA_A:NoArgOpCode(0xa7) {
    override fun execute(state: State) {
        state.a = state.a.and(state.a)
        setFlags(state, state.a)
    }
}
class XRA_B:NoArgOpCode(0xa8) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.b)
        setFlags(state, state.a)
    }
}
class XRA_C:NoArgOpCode(0xa9) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.c)
        setFlags(state, state.a)
    }
}
class XRA_D:NoArgOpCode(0xaa) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.d)
        setFlags(state, state.a)
    }
}
class XRA_E:NoArgOpCode(0xab) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.e)
        setFlags(state, state.a)
    }
}
class XRA_H:NoArgOpCode(0xac) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.h)
        setFlags(state, state.a)
    }
}
class XRA_L:NoArgOpCode(0xad) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.l)
        setFlags(state, state.a)
    }
}
class XRA_M:NoArgOpCode(0xae) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.memory[state.hl()])
        setFlags(state, state.a)
    }
}
class XRA_A:NoArgOpCode(0xaf) {
    override fun execute(state: State) {
        state.a = state.a.xor(state.a)
        setFlags(state, state.a)
    }
}
class ORA_B:NoArgOpCode(0xb0) {
    override fun execute(state: State) {
        state.a = state.a.or(state.b)
        setFlags(state, state.a)
    }
}
class ORA_C:NoArgOpCode(0xb1) {
    override fun execute(state: State) {
        state.a = state.a.or(state.c)
        setFlags(state, state.a)
    }
}
class ORA_D:NoArgOpCode(0xb2) {
    override fun execute(state: State) {
        state.a = state.a.or(state.d)
        setFlags(state, state.a)
    }
}
class ORA_E:NoArgOpCode(0xb3) {
    override fun execute(state: State) {
        state.a = state.a.or(state.e)
        setFlags(state, state.a)
    }
}
class ORA_H:NoArgOpCode(0xb4) {
    override fun execute(state: State) {
        state.a = state.a.or(state.h)
        setFlags(state, state.a)
    }
}
class ORA_L:NoArgOpCode(0xb5) {
    override fun execute(state: State) {
        state.a = state.a.or(state.l)
        setFlags(state, state.a)
    }
}
class ORA_M:NoArgOpCode(0xb6) {
    override fun execute(state: State) {
        state.a = state.a.or(state.memory[state.hl()])
        setFlags(state, state.a)
    }
}
class ORA_A:NoArgOpCode(0xb7) {
    override fun execute(state: State) {
        state.a = state.a.or(state.a)
        setFlags(state, state.a)
    }
}
class CMP_B:NoArgOpCode(0xb8) {
    override fun execute(state: State) {
        val result = state.a - state.b
        setFlags(state, result)
    }
}
class CMP_C:NoArgOpCode(0xb9) {
    override fun execute(state: State) {
        val result = state.a - state.c
        setFlags(state, result)
    }
}
class CMP_D:NoArgOpCode(0xba) {
    override fun execute(state: State) {
        val result = state.a - state.d
        setFlags(state, result)
    }
}
class CMP_E:NoArgOpCode(0xbb) {
    override fun execute(state: State) {
        val result = state.a - state.e
        setFlags(state, result)
    }
}
class CMP_H:NoArgOpCode(0xbc) {
    override fun execute(state: State) {
        val result = state.a - state.h
        setFlags(state, result)
    }
}
class CMP_L:NoArgOpCode(0xbd) {
    override fun execute(state: State) {
        val result = state.a - state.l
        setFlags(state, result)
    }
}
class CMP_M:NoArgOpCode(0xbe) {
    override fun execute(state: State) {
        val result = state.a - state.memory[state.hl()]
        setFlags(state, result)
    }
}
class CMP_A:NoArgOpCode(0xbf) {
    override fun execute(state: State) {
        val result = state.a - state.a
        setFlags(state, result)
    }
}
class RNZ:NoArgOpCode(0xc0)
class POP_B:NoArgOpCode(0xc1)
class JNZ:WordOpCode(0xc2) {
    override fun execute(state: State) {
        if(!state.flags.z) {
            state.pc = value!!
        }
    }
}
class JMP:WordOpCode(0xc3) {
    override fun execute(state: State) {
        state.pc = value!!
    }
}
class CNZ:WordOpCode(0xc4)
class PUSH_B:NoArgOpCode(0xc5)
class ADI:ByteOpCode(0xc6) {
    override fun execute(state: State) {
        state.a = add(state, value!!)
    }
}

class RST_0:NoArgOpCode(0xc7)
class RZ:NoArgOpCode(0xc8)
class RET:NoArgOpCode(0xc9) {
    override fun execute(state: State) {
        state.pc = state.memory[state.sp + 1].toUbyte().toUnsignedWord(state.memory[state.sp].toUbyte())
        state.sp += 2
    }
}
class JZ:WordOpCode(0xca) {
    override fun execute(state: State) {
        if(state.flags.z) state.pc = value!!
    }
}
class CZ:WordOpCode(0xcc)
class CALL:WordOpCode(0xcd) {
    override fun execute(state: State) {
        state.pc += 2
        state.memory[state.sp - 1] = state.pc.hi()
        state.memory[state.sp - 2] = state.pc.lo()

        state.sp -= 2
        state.pc = value!!

    }
}
class ACI:ByteOpCode(0xce)
class RST_1:NoArgOpCode(0xcf)
class RNC:NoArgOpCode(0xd0)
class POP_D:NoArgOpCode(0xd1)
class JNC:WordOpCode(0xd2)
class OUT:ByteOpCode(0xd3) {
    override fun execute(state: State) {
        //no op at the moment
    }
}
class CNC:WordOpCode(0xd4)
class PUSH_D:NoArgOpCode(0xd5) {
    override fun execute(state: State) {
        state.memory[state.sp - 2] = state.e
        state.memory[state.sp - 1] = state.d
        state.sp -= 2
    }
}
class SUI:ByteOpCode(0xd6)
class RST_2:NoArgOpCode(0xd7)
class RC:NoArgOpCode(0xd8)
class JC:WordOpCode(0xda)
class IN:ByteOpCode(0xdb)
class CC:WordOpCode(0xdc)
class SBI:ByteOpCode(0xde)
class RST_3:NoArgOpCode(0xdf)
class RPO:NoArgOpCode(0xe0)
class POP_H:NoArgOpCode(0xe1)
class JPO:WordOpCode(0xe2)
class XTHL:NoArgOpCode(0xe3)
class CPO:WordOpCode(0xe4)
class PUSH_H:NoArgOpCode(0xe5)
class ANI:ByteOpCode(0xe6) {
    override fun execute(state: State) {
        state.a = state.a.and(value!!)
        setFlags(state, state.a)
    }
}
class RST_4:NoArgOpCode(0xe7)
class RPE:NoArgOpCode(0xe8)
class PCHL:NoArgOpCode(0xe9)
class JPE:WordOpCode(0xea)
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
class CPE:WordOpCode(0xec)
class XRI:ByteOpCode(0xee)
class RST_5:NoArgOpCode(0xef)
class RP:NoArgOpCode(0xf0)
class POP_PSW:NoArgOpCode(0xf1)
class JP:WordOpCode(0xf2)
class DI:NoArgOpCode(0xf3)
class CP:WordOpCode(0xf4)
class PUSH_PSW:NoArgOpCode(0xf5)
class ORI:ByteOpCode(0xf6)
class RST_6:NoArgOpCode(0xf7)
class RM:NoArgOpCode(0xf8)
class SPHL:NoArgOpCode(0xf9)
class JM:WordOpCode(0xfa)
class EI:NoArgOpCode(0xfb) {
    override fun execute(state: State) {
        //no op at the moment
    }
}

class CM:WordOpCode(0xfc)
class CPI:ByteOpCode(0xfe) {
    override fun execute(state: State) {
        val result = state.a - value!!
        setFlags(state, result)
    }
}
class RST_7:NoArgOpCode(0xff)
class Mystery:NoArgOpCode(999) {
    override fun represent(): String = "Unknown!"
}