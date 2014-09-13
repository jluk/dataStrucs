import java.util.Iterator;
import java.util.Stack;
import java.util.NoSuchElementException;

public class LazyDeleteLinkedList<T> implements LazyDeleteList<T> {
	
	private Node<T> head;
	private Node<T> tail;
	private int count = 0;
	Stack<Node<T>> myStack = new Stack<Node<T>>();
	
	/**
	 * 
	 * @author Justin Luk
	 *
	 *Inner class that allows to create a Node
	 * @param <T>
	 */
	private class Node<T> {
		private Node<T> prev;
		private Node<T> next;
		private T data;
		private boolean isDeleted;
		
		public Node(T data) {
			this.data = data;
			this.isDeleted = false;
			prev = null;
			next = null;
		}
	}
	
	/**
	 * 
	 * Inner class creating LazyDeleteIterator
	 *
	 * @param <T>
	 */
	private class LazyDeleteIterator<T> implements Iterator<T>{
		private Node<T> current = (Node<T>) head;
		
		public boolean hasNext(){
			return current != null;
		}
		
		public T next(){
			if (hasNext()){
				T previousData = current.data;
				current = current.next;
				return previousData;
			}
			else{
				throw new NoSuchElementException();
			}
		}

		public void remove() {
			return;
		}
		
	}
	
	
    /**
     * Checks whether this list is empty
     * @return true if there are no undeleted elements in the list, false otherwise
     */
     public boolean isEmpty(){
    	 return count == 0;
     }
     
     /**
      * Get the number of undeleted elements in the list
      * @return the count of undeleted elements
      */
     public int size(){
    	 return count;
     }
     
     /**
      * Add a new element to the list.  This element is placed into a deleted node,
      * or if none exists at the end of the list.
      * @param data the data element to add to the list
      */
     public void add(T data){
    	 Node<T> newNode = new Node(data);
    	 if (isEmpty() && myStack.isEmpty()){
    		 head = newNode;
    		 tail = newNode;
    		 count++;
    	 }
    	 else if (!myStack.isEmpty()){
    		 Node<T> latestDeleted = (Node<T>) myStack.pop();
    		 latestDeleted.data = data;
    		 latestDeleted.isDeleted = false;
    		 count++;
    	 }
    	 else {
    		 tail.next = newNode;
    		 newNode.prev = tail;
    		 tail = newNode;
    		 count++;
    	 }

     }
     
     /**
      * Remove all the deleted nodes and ensure list contains only undeleted nodes.
      * @return the number of nodes removed (should count all nodes 
      *              that are removed from the list
      */
     public int compress(){
    	 int sizeStack = myStack.size();
    	 while (myStack.size() != 0){
        	 Node<T> current = myStack.pop();
        	 if (current == head){
        		 head = head.next;
        	 }
        	 else if (current == tail){
        		 current.prev = tail;
        		 tail.next = null;
        	 }
        	 else if (head == tail){
        		 head = null;
        		 tail = null;
        	 }
        	 else {
    		 current.prev.next = current.next;
    		 current.next.prev = current.prev;
        	 }
    		 
    	 }
    	 myStack.clear();
    	 return sizeStack;
     }
     
     /**
      * Remove everything from the list
      */
     public void clear() {
    	 head = null;
    	 tail = null;
    	 count = 0;
     }
     
     /**
      * Checks whether the list contains a certain value
      * @param data the item to check for
      * @return true if list has this item and it is undeleted, false otherwise
      */
     public boolean contains(T data){
    	 Node<T> current = head;
    	 while (current != null){
    		 if (current.data.equals(data) && current.isDeleted == false){
    			 return true;
    		 }
    		 current = current.next;
    	 }
    	 return false;
     }
     
     /**
      * Removes an item from the list using lazy deletion (the node is marked deleted, but
      * not actually removed.  If duplicate items are present, this removes the first one found.
      * 
      * @param data the item being deleted
      * @return true if item was in the list and undeleted, false otherwise.
      */
     @SuppressWarnings("unchecked")
	public boolean remove(T data){
    	 Node<T> current = head;
    	 while (current != null){
	    	 if (current.data.equals(data) && current.isDeleted == false){
	    		 myStack.push((Node<T>) current);
	    		 current.isDeleted = true;
	    		 count--;
	    		 return true;
	    	 }
	    	 current = current.next;
    	 }
    	 return false;
     }
     
     /**
      * 
      * @return the number of deleted nodes in the list that are available for use
      */
     public int deletedNodeCount(){
    	 return myStack.size();
     }
     
     /**
      * @return the iterator for this collection.  Asking for an iterator causes a compress of the collection.
      */
     public Iterator<T> iterator(){
    	 compress();
    	 LazyDeleteIterator<T> iter = new LazyDeleteIterator();
    	 return iter;
     }

}
