package simplexer;

import java.util.Stack;

public class UndoStack extends Stack<UndoableAction>{

	private static final long serialVersionUID = 1L;
	private int maxSize;
	
	
	public UndoStack(int maxSize){
		super();
		this.maxSize = maxSize;
	}
	
	public UndoableAction push(UndoType type, Object data){
		
		UndoableAction act = new UndoableAction(type, data);
		
		// Don't save consecutive duplicates
		if(!isEmpty() && peek().equals(act)){
			return peek();
		}
		
		super.push(act);
		
		if(this.size() > maxSize){
			this.remove(0);
		}
		
		return act;
	}
	
}
