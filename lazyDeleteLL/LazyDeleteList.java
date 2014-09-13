import java.util.Iterator;

/*
 * LazyList.java
 *
 * Version 1.0
 * Copyright 2014 BobSoft Inc
 */

/**
 * Interface for lists that support lazy deletion
 * @author Robert
 * @version 1.0
 *
 */
public interface LazyDeleteList<T> extends Iterable<T>{
     /**
      * Checks whether this list is empty
      * @return true if there are no undeleted elements in the list, false otherwise
      */
      boolean isEmpty();
      
      /**
       * Get the number of undeleted elements in the list
       * @return the count of undeleted elements
       */
      int size();
      
      /**
       * Add a new element to the list.  This element is placed into a deleted node,
       * or if none exists at the end of the list.
       * @param data the data element to add to the list
       */
      void add(T data);
      
      /**
       * Remove all the deleted nodes and ensure list contains only undeleted nodes.
       * @return the number of nodes removed (should count all nodes 
       *              that are removed from the list
       */
      int compress();
      
      /**
       * Remove everything from the list
       */
      void clear();
      
      /**
       * Checks whether the list contains a certain value
       * @param data the item to check for
       * @return true if list has this item and it is undeleted, false otherwise
       */
      boolean contains(T data);
      
      /**
       * Removes an item from the list using lazy deletion (the node is marked deleted, but
       * not actually removed.  If duplicate items are present, this removes the first one found.
       * 
       * @param data the item being deleted
       * @return true if item was in the list and undeleted, false otherwise.
       */
      boolean remove(T data);
      
      /**
       * 
       * @return the number of deleted nodes in the list that are available for use
       */
      int deletedNodeCount();
      
      /**
       * @return the iterator for this collection.  Asking for an iterator causes a compress of the collection.
       */
      Iterator<T> iterator();
}
