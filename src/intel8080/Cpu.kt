package intel8080

class Cpu(val state: State) {

    var ticks = 0

    fun tick(): Int {
        ticks++
        val nextOp = readNextInstruction()
        return nextOp.execAndAdvance()
    }

    private fun readNextInstruction(): OpCode {
        val nextInst = state.memory[state.pc]

        val currentOp = opCodeFor(nextInst)
        currentOp.consume(state)

        debug("Executing", currentOp)

        return currentOp
    }

    private fun debug(action: String, currentOp: OpCode) {
        val statement = "Ops:${ticks} | Flags: ${state.flags} | ${state} | $action ${currentOp}"
        Debugger.log(statement)
    }
}
