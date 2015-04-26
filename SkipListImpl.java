import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.Random;
import java.util.Scanner;

//Driver program and the skeleton for the skip list implementation.

public class SkipListImpl<T extends Comparable<? super T>> implements SkipList<T> {
	int maxLevel = 2;
	Node head;
	Node tail;
	int size = 0;

	public static void main(String[] args) {

		Scanner sc = null;

		if (args.length > 0) {
			File file = new File(args[0]);
			try {
				sc = new Scanner(file);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			sc = new Scanner(System.in);
		}
		String operation = "";
		long operand = 0;
		int modValue = 997;
		long result = 0;
		Long returnValue = null;
		SkipListImpl<Long> skipList = new SkipListImpl<Long>();
		// Initialize the timer
		long startTime = System.currentTimeMillis();

		long k =0;
		while (!((operation = sc.next()).equals("End"))) {
			//System.out.print(".");
			/*if(k++%20000==0)
				System.out.println(k + " time - " + (System.currentTimeMillis() - startTime) + " mSec");
			 */ 
			switch (operation) {
			case "Add": {
				operand = sc.nextLong();
				skipList.add(operand);
				result = (result + 1) % modValue;
				break;
			}
			case "Ceiling": {
				operand = sc.nextLong();
				returnValue = skipList.ceiling(operand);
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "FindIndex": {
				operand = sc.nextLong();
				returnValue = skipList.findIndex((int)operand);
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "First": {
				returnValue = skipList.first();
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Last": {
				returnValue = skipList.last();
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Floor": {
				operand = sc.nextLong();
				returnValue = skipList.floor(operand);
				if (returnValue != null) {
					result = (result + returnValue) % modValue;
				}
				break;
			}
			case "Remove": {
				operand = sc.nextLong();
				if (skipList.remove(operand)) {
					result = (result + 1) % modValue;
				}
				break;
			}
			case "Contains":{
				operand = sc.nextLong();
				if (skipList.contains(operand)) {
					result = (result + 1) % modValue;
				}
				break;
			}
			}
		}

		// End Time
		long endTime = System.currentTimeMillis();
		long elapsedTime = endTime - startTime;

		System.out.print("\t"+result + " " + elapsedTime + " mSec"+ " \t");

		//SkipListImpl<Integer> skipList = new SkipListImpl<Integer>();



	}

	public SkipListImpl() {
		head = new Node("head", maxLevel);
		tail = new Node("tail", maxLevel);

		for(int i = 0;i < maxLevel;i++){
			head.next[i] = tail;
			head.width[i] = 1;
		}
	}

	// --------------------Override methods----------------------------------
	@Override
	public void add(T x) {
		FindBean<T> findBean = find(x);

		//printFind(x);
		Node[] prev = (SkipList.Node[]) findBean.prev;

		if(findBean.p != null){
			//System.out.println("Already Exists");
			return;
		}
		// returns values from 0 to (maxLevel-1) range
		int l = chooseLevel(maxLevel);

		Node temp = (Node)findBean.prev[0];
		// l ranges from 0 to (maxLevel - 1), hence create level l+1;
		Node n = new Node(x,l+1);

		for(int i = 0; i<=l;i++){
			n.next[i] = prev[i].next[i];
			prev[i].next[i] = n;			
		}

		n.width[0] = prev[0].width[0];
		for(int i = 1; i<=l;i++){
			/*
			 *  break width of earlier nodes
			 *  add all the widths of 1 level below this.
			 */
			Node tempBreak = prev[i];
			long oldWidth = prev[i].width[i];
			int count = 0;
			//while(!(tempBreak.value != null && tempBreak.value.equals("tail")) && !tempBreak.equals(n))
			while(!tempBreak.equals(n))
			{
				count+=tempBreak.width[i-1];
				tempBreak = tempBreak.next[i-1];
			}
			// subtract these from old width from new width
			long newWidth = oldWidth - count;
			/*			if(newWidth == 0)
				newWidth = 1;*/
			prev[i].width[i] = count;
			n.width[i] = newWidth+1;
		}

		/*
		 *  if maxLevel = 10. and l is 9 (== maxLevel - 1).
		 *  increment the width of the left over previous nodes by 1.		 *  
		 */
		for(int i = l+1;i<maxLevel;i++){
			if(i == 0)
				continue;
			Node tempOthers = prev[i];
			tempOthers.width[i]++;
		}

		size++;

		// Increase the maxLevel and increase the size of the head and tail
		if(Math.log(size)>maxLevel/2)
			increaseMaxLevel();
	}

	@Override
	public T ceiling(T x) {
		FindBean<Node> findBean = find(x);
		Node n = (Node) findBean.prev[0];
		if(findBean.p!=null)
			return (T) findBean.p.data;
		else if(n.next[0].value!=null)
			return null;
		else{
			return (T) n.next[0].data;
		}			
	}

	@Override
	public boolean contains(T x) {
		return find(x).p!=null;
	}

	@Override
	public T findIndex(int n) {
		// will return null if the element is not found
		if(n > size)
			return null;

		Node p = head;
		long widthTravelled = 0;
		for(int i = maxLevel-1;i>=0;i--){
			while(p.next[i].value==null){
				long temp = p.width[i];
				if(temp+widthTravelled>n)
					break;
				p = p.next[i];
				widthTravelled+=temp;
			}
		}
		return (T) p.next[0].data;
	}

	@Override
	public T first() {
		Node p = head;
		if(p.next[0].value!=null)
			return null;
		else
			return (T) p.next[0].data;
	}

	@Override
	public T floor(T x) {
		FindBean<Node> findBean = find(x);
		Node n = (Node) findBean.prev[0];
		if(findBean.p!=null)
			return (T) findBean.p.data;
		else if(n.value!=null)
			return null;
		else{
			return (T) n.data;
		}	
	}

	@Override
	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public Iterator<T> iterator() {
		return new SkipListIterator<T>(head);
	}

	@Override
	public T last() {
		return findIndex(size-1);
	}

	@Override
	public void rebuild() {

	}

	@Override
	public boolean remove(T x) {

		FindBean<T> findBean = find(x);
		Node p = (SkipList.Node) findBean.p;
		Node[] prev = (SkipList.Node[]) findBean.prev;
		//printFind(x);
		if(findBean.p == null)
			return false;

		size--;
		for(int i = 0;i<maxLevel;i++){
			if(prev[i].next[i] == p){
				prev[i].next[i] = p.next[i];
				prev[i].width[i] = prev[i].width[i] + p.width[i] -1;
			}else{
				prev[i].width[i]--;
			}
		}
		return true;
	}

	@Override
	public int size() {
		return size;
	}
	// --------------------Override methods----------------------------------

	public FindBean find(T x){
		Node p = head;
		Node[] prev = new Node[maxLevel];

		try {
			for(int i = maxLevel-1;i>=0;i--){
				while(p.next[i].value==null && p.next[i].data.compareTo(x)<0){
					p = p.next[i];
				}
				prev[i] = p;
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e){
			System.out.println("size :" + size + " Find value : " + x);
			printList();
			e.printStackTrace();
		}

		if(p.next[0].value==null && p.next[0].data.compareTo(x) == 0)
			return new FindBean<Node>(p.next[0], prev);
		else
			return new FindBean<Node>(null,prev);
	}

	public int chooseLevel(int maxLevel){

		Random rand = new Random();
		// nextInt is normally exclusive of the top value,
		int l = 0;
		while(l<maxLevel-1)
		{
			//int b = rand.nextInt(maxLevel-1);
			int b = rand.nextInt(2);
			if(b == 0)
				return l;
			else
				l++;
		}
		return l;
	}
	public void increaseMaxLevel(){
		maxLevel = 2 * maxLevel;
		Node tempHead = new Node(head.value,maxLevel);

		// do Nothing for tail
		//Node tempTail = new Node<>(tail.value,maxLevel);
		System.out.print("I");
		int i = 0;
		for(i = 0;i<head.next.length;i++)
		{
			tempHead.next[i] = head.next[i];
			tempHead.width[i] = head.width[i];
			//System.out.print("-"+tempHead.width[i]+"("+i+")");
		}
		for(int j = i;j < tempHead.next.length;j++){
			tempHead.next[j] = tail;
			tempHead.width[j] = size + 1;
			//System.out.print("-"+tempHead.width[j]+"("+j+")");
		}
		//System.out.println("");
		head = tempHead;
	}
	public void printList(){
		Node p = head;
		String empty = "----";
		String connector = "--------";
		System.out.println("head next : "+p.next[0].data);
		for(int i = maxLevel-1;i>=0;i--){
			System.out.println("\nLevel "+i);
			p = head;
			while(true)
			{
				System.out.print(p.data + "("+p.width[i]+")"+connector);
				for(int j = 1;j<(p.width[i]);j++)
					System.out.print(empty + connector);
				if(p.value!=null && p.value.equals("tail"))
					break;
				p = p.next[i];
			}
		}
		p = head;
		System.out.println("\nAll---- size : "+ size + " :: \n");
		while(p.value==null || p.value.equals("head"))
		{
			System.out.print(p.data + "("+p.width[0]+")-->");
			p = p.next[0];
		}
		System.out.println("");
	}
	public void printFind(T x){
		FindBean<Node> findBean = find(x);
		System.out.print("\n"+x);
		if(findBean.p==null)
		{
			System.out.print(" Not Found ");
		}else
			System.out.print(" Found ");
		for(int i = maxLevel - 1;i>=0;i--){
			System.out.print(findBean.prev[i].data+"("+findBean.prev[i].width[i]+")"+"-->");
		}
		System.out.println("");
	}
}