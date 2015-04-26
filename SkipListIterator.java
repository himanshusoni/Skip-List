import java.util.Iterator;


public class SkipListIterator<T extends Comparable<? super T>> implements Iterator<T>{

	SkipList.Node head;
	SkipList.Node current;

	public SkipListIterator(SkipList.Node head){
		this.head = head;
		this.current = this.head;
	}

	@Override
	public boolean hasNext() {
		return current.next[0].value==null;
	}

	@Override
	public T next() {
	T data = (T) current.next[0].data;
	current = current.next[0];
		return data;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
}
