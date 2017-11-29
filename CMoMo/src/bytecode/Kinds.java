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
			xxx
}
