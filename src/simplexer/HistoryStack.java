package simplexer;

import java.util.Stack;

public class HistoryStack<T> extends Stack<T>{

	private static final long serialVersionUID = 1L;
	private int maxSize;
	
	
	public HistoryStack(int maxSize){
		super();
		this.maxSize = maxSize;
	}
	
	@Override
	public T push(T item){
		super.push(item);
		
		if(this.size() > maxSize){
			this.remove(0);
		}
		
		return item;
	}
	
}
