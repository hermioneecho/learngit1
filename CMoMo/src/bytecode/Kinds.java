package bytecode;

public enum Kinds {

	// stack operations
			vload,
			aload,
			vstore,
			astore,
			push,
			fpush,
			pop,
			//arithmetic
			iadd,
			fadd,
			isub,
			fsub,
			imul,
			fmul,
			idiv,
			fdiv,
			//jump
			gto,
			icmpg,
			fcmpg,
			icmp,
			fcmp,
			//function
			invoke,
			rtn,
			//IO
			iread,
			fread,
			swrite,
			iwrite,
			fwrite,
			// others
			xxx,
			// use for debugBytecode only
			debugBytecodeGetAddress, /*put the address of the symbol to the stack */
			debugBytecodeGetArray /*the symbol in the DebugBytecode is the name of the array's symbol*/
			                      /*the op in Bytecode is the index of the array */
}
