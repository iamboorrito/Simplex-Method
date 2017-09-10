package simplexer;

public class UndoableAction {

	public final UndoType type;
	public final Object data;
	public static final int SRC_UNDO = 0, SRC_REDO = 1;
	
	public UndoableAction(UndoType type, Object data){
		this.type = type;
		this.data = data;
	}

}
