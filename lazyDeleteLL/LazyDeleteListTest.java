import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Before;
import org.junit.Test;

/*
 * LazyDeleteListTest.java
 *
 * Version 1.0
 * Copyright 2014 BobSoft Inc
 */

/**
 * @author Robert
 * @version 1.0
 *
 */
public class LazyDeleteListTest {
    
    //our test fixture
    LazyDeleteList<String> list2test;
    
    /**
     * This method is called before every individual test.
     * Basically, we put anything in here that we want to have
     * for every test.  In my case, I am just making a "fresh"
     * new list for every test.
     * 
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        list2test = new LazyDeleteLinkedList<String>();
    }

    /**
     * Test method for {@link LazyDeleteList#isEmpty()}.
     */
    @Test
    public void testIsEmptyOnCreate() {
        assertTrue("Your list thinks it has something in it right after it is created", list2test.isEmpty());
 
    }
    
    @Test
    public void testIsEmptyAfterAdd() {
        list2test.add("A");
        assertFalse("Your list thinks it is empty when 1 thing is added", list2test.isEmpty());
    }
    
    @Test
    public void testIsEmptyAfterARemoval() {
        list2test.add("A");
        list2test.remove("A");
        assertTrue("Your list thinks it has something in it after last removal", list2test.isEmpty());
    }
    
    
    @Test
    public void testSizeZeroAtStart() {
        assertEquals("Your list size is not zero right after creation", 0, list2test.size());
    }
    
    @Test
    public void testSizeAfterOneInsert() {
        list2test.add("A");
        assertEquals("Your list size is not one after adding one thing", 1, list2test.size());
    }
    
    @Test
    public void testSizeAfterRemovalOfLastElement() {
        list2test.add("B");
        list2test.add("A");
        list2test.remove("B");
        list2test.remove("A");
        assertEquals("Your list size is not zero after removing the last thing", 0, list2test.size());
    }
    
    @Test
    public void testSizeAfterClear() {
        list2test.add("B");
        list2test.add("C");
        list2test.add("A");
        list2test.clear();
        assertEquals("After clearing the list, the size was not zero", 0, list2test.size());
    }

    /**
     * Test method for {@link LazyDeleteList#add(java.lang.Object)}.
     */
    @Test
    public void testAddOneElement() {
        list2test.add("A");
        assertEquals("after an add, the size didn't change", 1, list2test.size());
        assertTrue("After adding an item, it is not found in the list", list2test.contains("A"));
    }
    
    @Test
    public void testAddMultipleElements() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        assertEquals("Size wrong after many adds", 20, list2test.size());
        
        for (int i = 0; i < 20; i++) {
            assertTrue("Can't find items in list after many adds", list2test.contains("A" + i));
        }
    }
    
    @Test
    public void testAddAfterSomeRemovals() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        list2test.remove("A12");
        list2test.remove("A16");
        list2test.remove("A9");
        
        assertEquals(17,list2test.size());
        list2test.add("B");
        list2test.add("C");
        list2test.add("D");
        assertTrue("Overwritten deleted node not found", list2test.contains("D"));
        assertEquals("Size of list wrong after overwriting deleted nodes", 20, list2test.size());
        assertEquals("Should not be any deleted nodes after adding back", 0, list2test.deletedNodeCount());
        
    }
    
    @Test
    public void testOneRemoval() {
        list2test.add("A");
        boolean result = list2test.remove("A");
        assertTrue("List not showing empty after last item removed", list2test.isEmpty());
        assertEquals("Size not zero after last item removed", 0, list2test.size());
        assertTrue("Remove failed to return true after delete", result);
        assertFalse("List still thinks it contains a deleted item", list2test.contains("A"));
        assertEquals("Deleted node count wrong after removal", 1, list2test.deletedNodeCount());
    }
    
    @Test
    public void testRemoveLastItemThenAddStuff() {
        list2test.add("A");
        list2test.add("B");
        list2test.remove("A");
        list2test.remove("B");
        list2test.add("C");
        list2test.add("D");
        list2test.add("E");
        
        assertEquals("Size wrong after remove and add", 3, list2test.size());
        assertEquals("Deleted node count wrong after remove and add", 0, list2test.deletedNodeCount());
        assertFalse(list2test.contains("A"));
        assertTrue(list2test.contains("C"));
        list2test.contains("D");
        assertTrue(list2test.contains("D"));
        assertTrue(list2test.contains("E"));
    }
    
    @Test
    public void testTryToRemoveFromEmptyList() {
        boolean result = list2test.remove("A");
        assertFalse("Remove from empty list returned true", result);
    }
    
    @Test
    public void testRemoveNonExistentElement() {
        list2test.add("A");
        list2test.add("C");
        list2test.add("E");
        
        boolean result = list2test.remove("FF");
        assertFalse("Remove non-exist element returned true", result);
    }
    
    @Test
    public void testRemoveSameElementTwice() {
        list2test.add("A");
        list2test.add("E");
        
        list2test.remove("E");
        boolean result = list2test.remove("E");
        assertFalse("Remove of same item a second time returned true", result);     
    }
    
    @Test
    public void testRemoveDuplicates() {
        list2test.add("A");
        list2test.add("B");
        list2test.add("C");
        list2test.add("B");
        
        assertTrue("Failed to find first duplicate item for removal", list2test.remove("B"));
        assertTrue("Should have only deleted one copy", list2test.contains("B"));
        assertTrue("Failed to find second duplicate item for removal", list2test.remove("B"));
        assertFalse("Should have deleted last copy", list2test.contains("B"));
        
    }
    
    @Test
    public void testDeleteCountOnCreate() {
        assertEquals("Your list thinks there are deleted nodes when it is new", 0, list2test.deletedNodeCount());
    }
    
    @Test
    public void testDeleteCountAfterSomeRemovals() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        list2test.remove("A12");
        list2test.remove("A16");
        list2test.remove("A9");
        
        assertEquals("Your deletedCount is wrong afer some removals", 3, list2test.deletedNodeCount());
        
    }
    
    @Test
    public void testContainsOnEmptyList() {
        assertFalse("Your empty list thinks it contains something", list2test.contains("A"));
    }
    
    @Test
    public void testContainsOnValidElement() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        assertTrue("You could not find head", list2test.contains("A0"));
        assertTrue("You could not find interior item", list2test.contains("A11"));
        assertTrue("You could not find tail", list2test.contains("A19"));
    }
    
    @Test
    public void testContainsOnRemovedElement() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        list2test.remove("A0");
        list2test.remove("A19");
        list2test.remove("A10");
        
        assertFalse("You think head is still valid after remove", list2test.contains("A0"));
        assertFalse("You think an interior node is still valid after remove", list2test.contains("A10"));
        assertFalse("You think tail is still valid after remove", list2test.contains("A19"));
    }
    
    @Test
    public void testCompressEmptyList() {
        int result = list2test.compress();
        assertEquals("Compressing empty list thinks it removed something", 0 , result);
    }
    
    @Test
    public void testCompressOneDeletedNodeList() {
    	list2test.add("A");
    	list2test.remove("A");
    	int result = list2test.compress();
    	assertEquals("Compressing list with only 1 deleted node fails", 1, result);
    }
    
    
    @Test
    public void testCompressListWithNoDeletes() {
        list2test.add("A");
        list2test.add("B");
        list2test.add("C");
        
        int result = list2test.compress();
        assertEquals("Compressing a list with no removes thinks it removed something", 0 , result);
        
    }
    
    @Test
    public void testCompressWithRemovalAtHead() {
        list2test.add("A");
        list2test.add("B");
        list2test.add("C");
        
        list2test.remove("A");
        list2test.remove("B");
        
        int result = list2test.compress();
        assertEquals("Compression at head caused wrong count", 2, result);
        assertEquals(1, list2test.size());
        assertTrue("Contains can't find item after compression at head", list2test.contains("C"));
    }
 
    @Test
    public void testCompressListWithMultipleDeletesInMiddle() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        list2test.remove("A12");
        list2test.remove("A16");
        list2test.remove("A9");
        
        int result = list2test.compress();
        assertEquals("Compressing list returned wrong count", 3, result);
        assertEquals("Compressing list didn't empty the stack", 0 , list2test.deletedNodeCount());
        
    }
    
    @Test
    public void testCompressWithTailDeleted() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        list2test.remove("A19");
        list2test.remove("A18");
        list2test.remove("A17");
        
        int result = list2test.compress();
        assertEquals("Compressing list returned wrong count", 3, result);
        assertEquals("Compressing list didn't empty the stack", 0 , list2test.deletedNodeCount());
        
    }
    
    @Test
    public void testCompressAndThenAddSomeStuff() {
        for (int i=0; i < 20 ; i++){
            list2test.add("A" + i);
        }
        
        list2test.remove("A19");
        list2test.remove("A16");
        list2test.remove("A0");
        
        int result = list2test.compress();
        assertEquals("Compressing list returned wrong count", 3, result);
        assertEquals("Compressing list didn't empty the stack", 0 , list2test.deletedNodeCount());
        
        list2test.add("C");
        list2test.add("E");
        
        assertTrue("Could not find an add after compression", list2test.contains("C"));
        assertTrue("Could not find an add after compression", list2test.contains("E"));
        
    }
    
    @Test
    public void testIteratorEmptyList() {
    	Iterator<String> iter = list2test.iterator();
    	assertFalse("Should not have any items in an empty list", iter.hasNext());
    }
    
    @Test
    public void testIteratorElementException() {
    	 Iterator<String> iter = list2test.iterator();
    	 try {
    		 iter.next();
    		 fail("Should have thrown an exception for bad next call");
    	 } catch (NoSuchElementException nse) {
    		 assertTrue(true);
    	 }
    }
    
    @Test 
    public void testIteratorForOneElement() {
    	list2test.add("A");
    	Iterator<String> iter = list2test.iterator();
    	assertTrue("Iterator did not find first element", iter.hasNext());
    	String a = iter.next();
    	assertEquals("Element from iterator doesn't match list", "A", a);
    	assertFalse("Iterator should have been empty", iter.hasNext());
    }
    
    @Test
    public void testIteratorMultipleItems() {
    	list2test.add("A");
    	list2test.add("B");
    	list2test.add("C");
    	Iterator<String> iter = list2test.iterator();
    	assertTrue("Iterator did not find first element", iter.hasNext());
    	String a = iter.next();
    	System.out.println(a);
    	assertEquals("Element 1 from iterator doesn't match list", "A", a);
    	assertTrue("Iterator did not find second element", iter.hasNext());
    	a = iter.next();
    	assertEquals("Element 2 from iterator doesn't match list", "B", a);
    	assertTrue("Iterator did not find third element", iter.hasNext());
    	a = iter.next();
    	assertEquals("Element 3 from iterator doesn't match list", "C", a);
    	assertFalse("Iterator did detect it was empty", iter.hasNext());
    	
    }
}
