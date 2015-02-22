import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class AVLTree<E extends Comparable> implements BinaryTree<E>{
	
	private Node<E> root;
	private int count = 0;
	private E oldNode;
	private boolean dup = false;
	private boolean removed = false;
	
	/**
	 * 
	 * @author Justin Luk
	 * Inner private Node class
	 * 
	 */
	private class Node<E> {

		private Node<E> left;
		private Node<E> right;
		private int balanceFactor;
		private E data;
		private int height;
		
		public Node(E data) {
			this.data = data;
			left = null;
			right = null;
		}
		
	    public E getData() {
	        return data;
	    }
	    
	    public void setData(E data) {
	        this.data = data;
	    }
	    
	    public Node<E> getLeft() {
	        return left;
	    }

	    public void setLeft(Node<E> left) {
	        this.left = left;
	    }

	    public Node<E> getRight() {
	        return right;
	    }

	    public void setRight(Node<E> right) {
	        this.right = right;
	    }
	    
	    public void setBalanceFactor(int balanceFactor) {
	    	this.balanceFactor = balanceFactor;
	    }

	    public int getBalanceFactor() {
	    	return balanceFactor;
	    }

	    public void setHeight(int height) {
	    	this.height = height;
	    }

	    public int getHeight() {
	    	return height;
	    }

	}
	
	/**
	 * 
	 * Private inner iterator class
	 * @param <E>
	 */
	private class AVLIterator<E> implements Iterator<E>{
		private List<E> myList = (ArrayList<E>) getInOrder();
		private int current = 0;

		public boolean hasNext(){
			return current < myList.size();
		}
		
		public E next(){
			if (hasNext()){
				E previous = myList.get(current);
				current++;
				return previous;
			}
			else { 
				throw new NoSuchElementException();
				}
		}

		public void remove() {
			return;
		}
		
	}
	
	/**
	 * Adds the item to the tree.  Duplicate items and null items should not be added.
	 * 
	 * @param item the item to add
	 * @return true if item added, false if it was not
	 */
	public boolean add(E item){
		if (item == null) {
			return false;
		} else {
			root = addHelper(root, item);
			if (dup){
				dup = false;
				return false;
			} else {
				update(root);
				root = rotate(root);
				return true;
			}
		}	
	}
	
	private Node<E> addHelper(Node<E> curr, E item){
		if (curr == null) {
			count++;
			return new Node<E>(item);
		} else if (item.compareTo(curr.getData()) == 0) {
			dup = true;
			return curr;
		} else if (item.compareTo(curr.getData()) < 0) {
			Node<E> left = curr.getLeft();
			curr.setLeft(addHelper(left, item));
			update(curr.getLeft());
			update(curr);
			curr = rotate(curr);
			return curr;
		} else if (item.compareTo(curr.getData()) > 0) {
			curr.setRight(addHelper(curr.getRight(), item));
			update(curr.getRight());
			update(curr);
			curr = rotate(curr);
			return curr;
		}
		return curr;
	}
	
	/**
	 * returns the maximum element held in the tree.  null if tree is empty.
	 * @return maximum item or null if empty
	 */
	public E max(){
		if (root == null){
			return null;
		}
		Node<E> current = root;
        while(current.getRight() != null) {
            current = current.getRight();
        }
        return current.getData();
	}
	
	/**
	 * returns the number of items in the tree
	 * @return 
	 */
	public int size(){
		return count;
	}
	
	/**
	 * 
	 * @return true if tree has no elements, false if tree has anything in it.
	 */
	public boolean isEmpty(){
		return count == 0;
	}
	
	/**
	 * @return the minimum element in the tree or null if empty
	 */
	public E min(){
		if (root == null){
			return null;
		}
		Node<E> current = root;
        while(current.getLeft() != null) {
            current = current.getLeft();
        }
        return current.getData();
	}

	private E get(E item){
		if (item == null){
			throw new IllegalArgumentException();
		}
		return getHelper(root, item);
	}
	
	
	private E getHelper(Node<E> current, E item){
		E gotNode = null;
		if (current == null){
			return gotNode;
		} else if (item.compareTo(current.getData()) == 0) {
			gotNode = current.getData();
		} else if (item.compareTo(current.getData()) < 0) {
			gotNode = getHelper(current.getLeft(), item);
		} else if (item.compareTo(current.getData()) > 0) {
			gotNode = getHelper(current.getRight(), item);
		}
		return gotNode;
	}
	
	/**
	 * Checks for the given item in the tree.
	 * @param item the item to look for
	 * @return true if item is in tree, false otherwise
	 */
	public boolean contains(E item){
		return get(item) != null;
	}
	
	/**
	 * removes the given item from the tree
	 * @param item the item to remove
	 * @return true if item removed, false if item not found
	 */
	public boolean remove(E item){
		if (item == null) {
			return false;
		}
		root = removeHelper(root, item);
		if (removed){
			removed = false;
			update(root);
			root = rotate(root);
			return true;
		} else {
			return false;
		}
	}
	
	private Node<E> removeHelper(Node<E> curr, E item) {
		oldNode = null;
		if (curr == null) {
			return null;
		} else if (root.getData() == item && root.getLeft() == null && root.getRight() == null) {
			root = null;
			count = 0;
			removed = true;
		} else if (item.compareTo(curr.getData()) < 0) {
			curr.setLeft(removeHelper(curr.getLeft(), item));
			update(curr);
			curr = rotate(curr);
		} else if (item.compareTo(curr.getData()) > 0) {
			curr.setRight(removeHelper(curr.getRight(), item));
			update(curr);
			curr = rotate(curr);
		} else if (item.compareTo(curr.getData()) == 0){
			removed = true;
			if (curr.getRight() == null) {
				count--;
				oldNode = curr.getData();
				return curr.getLeft();
			} else if (curr.getLeft() == null) {
				count--;
				oldNode = curr.getData();
				return curr.getRight();
			}
			E temp = curr.getData();
			oldNode = findSuccessor(curr.getRight());
			curr.setData(oldNode);
			curr.setRight(removeHelper(curr.getRight(), oldNode));
			oldNode = temp;
		} else {
			return null;
		}
		return curr;
	}
	
	private E findSuccessor(Node<E> curr){
		if (curr.getLeft() == null){
			return curr.getData();
		} else {
			return findSuccessor(curr.getLeft());
		}
	}
	
	private Node<E> rotate(Node<E> unbalanced){
		int bf = unbalanced.getBalanceFactor();
		if (bf > 1) {
			if (unbalanced.getLeft().getBalanceFactor() > 0) {
				unbalanced = rotateRight(unbalanced);
			} else {
			    unbalanced = rotateLeftRight(unbalanced);
			}
		} else if (bf < -1) {
			if (unbalanced.getRight().getBalanceFactor() < 0) {
				unbalanced = rotateLeft(unbalanced);
			} else {
				unbalanced = rotateRightLeft(unbalanced);			
			}
		}
		return unbalanced;
		
	}

	private Node<E> rotateRight(Node<E> node) {
		Node<E> left = node.getLeft();
		node.setLeft(left.getRight());
		left.setRight(node);
		update(left);
		update(left.getRight());
		return left;
	}
	
	private Node<E> rotateLeft(Node<E> node) {
		Node<E> right = node.getRight();
		node.setRight(right.getLeft());
		right.setLeft(node);
		update(right);
		update(right.getLeft());
		return right;
	}
	
	private Node<E> rotateLeftRight(Node<E> node) {
		Node<E> left = node.getLeft();
		node.setLeft(rotateLeft(left));
		return rotateRight(node);
	}
	
	private Node<E> rotateRightLeft(Node<E> node) {
		Node<E> right = node.getRight();
		node.setRight(rotateRight(right));
		return rotateLeft(node);
	}
	
	/**
	 * A helper method to update balance factors and heights for AVL adding/removing
	 * @param node a node that needs to be updated
	 */
	private void update(Node<E> node) {
		int max = 0;
		if (node == null) {
			return;
		} else if ((node.getLeft() == null) && (node.getRight() == null)) {
			node.setHeight(1);
			node.setBalanceFactor(0);
		} else if (node.getLeft() == null) {
			node.setHeight(node.getRight().getHeight() + 1);
			node.setBalanceFactor(-node.getRight().getHeight());
		} else if (node.getRight() == null) {
			node.setHeight(node.getLeft().getHeight() + 1);
			node.setBalanceFactor(node.getLeft().getHeight());
		} else {
			max = Math.max(node.getLeft().getHeight(), node.getRight().getHeight());
			node.setHeight(max + 1);
			node.setBalanceFactor(node.getLeft().getHeight() - node.getRight().getHeight());
		}		
	}
	
    /**
     * returns an iterator over this collection
     * iterator is based on an in-order traversal
     */
	public Iterator<E> iterator(){
		AVLIterator<E> iter = new AVLIterator();
		return iter;
	}
	/**
	 * @return a list of the data in in-order traversal order
	 */
    public List<E> getInOrder(){
		List<E> myList = new ArrayList<E>();
		return inOrderHelper(root, myList);
    }
    
	private List<E> inOrderHelper(Node<E> node, List<E> myList){
		if (node != null){
			inOrderHelper(node.getLeft(), myList);
			myList.add(node.getData());
			inOrderHelper(node.getRight(), myList);
		}
		return myList;
	}
	
	/**
	 * @return a list of the data in post-order traversal order
	 */
    public List<E> getPostOrder(){
		List<E> myList = new ArrayList<E>();
		return postOrderHelper(root, myList);
    }
    
	private List<E> postOrderHelper(Node<E> node, List<E> myList){
		if (node != null){
			postOrderHelper(node.getLeft(), myList);
			postOrderHelper(node.getRight(), myList);
			myList.add(node.getData());
		}
		return myList;
	}
      
   /**
    * 
    * @return a list of the data in level-order traversal order
    */
    public List<E> getLevelOrder(){
        List<Node<E>> myNodeList = new ArrayList<Node<E>>();
        List<E> list = new ArrayList<E>();
        Node<E> current;
        if(root == null){
            return list;
        }
        myNodeList.add(root);
        while(!myNodeList.isEmpty()) {
            current = myNodeList.remove(0);
            if (current.getLeft() != null) {
                myNodeList.add(current.getLeft());
            }
            if (current.getRight() != null) {
                myNodeList.add(current.getRight());
            }
            list.add(current.getData());
        }
        return list;
	}
    
    /**
     * @return a list of the data in pre-order traversal order
     */
    public List<E> getPreOrder(){
		List<E> myList = new ArrayList<E>();
		return preOrderHelper(root, myList);
    }
    
	private List<E> preOrderHelper(Node<E> node, List<E> myList){
		if (node != null){
			myList.add(node.getData());
			preOrderHelper(node.getLeft(), myList);
			preOrderHelper(node.getRight(), myList);
		}
		return myList;
	}
	
    /**
     * Removes all the elements from this tree
     */
    public void clear(){
    	root = null;
    	count = 0;
    }

}
