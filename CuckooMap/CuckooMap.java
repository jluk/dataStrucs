import java.util.ArrayList;
import java.util.Random;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CuckooMap<K extends Hashable, V> implements Map<K, V> {
	
	private static final double MAX_LOAD_FACTOR = .80;
	private int num1;
	private int num2;
	private Random rand = new Random();
	private int count1;
	private int count2;
	private int size;
	private Bucket[] table1;
	private Bucket[] table2;
	private int countPush = 0;
	
	private class Bucket<K extends Comparable, V> implements Map.Entry<K, V>, Comparable {

		private K key;
		private V value;
		private boolean removed;
		
		public Bucket(K key, V value) {
			this.key = key;
			this.value = value;
			removed = false;
		}
		
		public K getKey() {
			return key;
		}

		public V getValue() {
			return value;
		}

		public V setValue(V value) {
			V temp = this.value;
			this.value = value;
			return temp;
		}
		
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			
			Bucket<K, V> other = (Bucket) obj;
			if (key == null) {
				if (other.key != null)
					return false;
			} else if (!key.equals(other.key)) {
				return false;
			}
			
			if (value == null) {
				if (other.value != null)
					return false;
			} else if (!value.equals(other.value)) {
				return false;
			}
			return true;
		}

		public int compareTo(Object obj){
			return key.compareTo(((Bucket) obj).getKey());
		}
	}
	
	public CuckooMap(int startSize){
		this.table1 = new Bucket[startSize];
		this.table2 = new Bucket[startSize];
		size = 0;
		count1 = startSize;
		num1 = rand.nextInt(100);
		num2 = rand.nextInt(100);
	}

	public V put(K key, V value){
		Bucket<K,V> buck = new Bucket(key,value);
		
		if (key == null || value == null){
			throw new IllegalArgumentException();
		} else if (containsKey(key)){
			V oldValue;
			int index1 = getHashIndex1(key);
			if (table1[index1] != null && table1[index1].getKey().equals(key)){
				oldValue = (V) table1[index1].getValue();
				table1[index1].setValue(value);
				return oldValue;
			}
			int index2 = getHashIndex2(key);
			if (table2[index2] != null && table2[index2].getKey().equals(key)){
				oldValue = (V) table2[index2].getValue();
				table2[index2].setValue(value);
				return oldValue;
			}
		}
		
		if (isFull() || countPush > table1.length) {
			rehash();
		}
		
		int index1 = getHashIndex1(key);
		if (table1[index1] != null){
			countPush++;
			Bucket<K,V> temp = table1[index1];
			table1[index1] = buck;
			int index2 = getHashIndex2(temp.getKey());
			if (table2[index2] != null) {
				countPush++;
				Bucket<K,V> temp2 = table2[index2];
				table2[index2] = temp;
				return put(temp2.getKey(),temp2.getValue());
			} 
			else {
				table2[index2] = temp;
				size++;
				return null;
			}
		}
		else {
			table1[index1] = buck;
			size++;
			return null;
		}
	}

	private int getHashIndex1(K key) {
		int index = Math.abs((num1*key.hashCode()) % table1.length);
		return index;
	}
	
	private int getHashIndex2(K key) {
		int index = Math.abs((num2*key.hashCode()) % table2.length);
		return index;
	}
	
	private boolean isFull(){
		double loadFactor = (double) size / (table1.length*2);
		return (loadFactor > MAX_LOAD_FACTOR) ;
	}
	
	private void rehash(){
		Bucket[] oldTable1 = table1;
		Bucket[] oldTable2 = table2;
		num1 = rand.nextInt(100);
		num2 = rand.nextInt(100);
		int oldSize = table1.length;
		int newSize = (oldSize*2);
		table1 = new Bucket[newSize];
		table2 = new Bucket[newSize];
		size = 0;
		countPush = 0;
		
		for (int i=0 ; i < oldTable1.length ; i++){
			if (oldTable1[i] != null){
				put((K)oldTable1[i].getKey(), (V)oldTable1[i].getValue());
			}
		}
		for (int i=0 ; i < oldTable2.length ; i++){
			if (oldTable2[i] != null){
				put((K) oldTable2[i].getKey(), (V) oldTable2[i].getValue());
			}
		}
	}
	
	@Override
	public V get(Object key){
		int index1 = getHashIndex1((K) key);
		int index2 = getHashIndex2((K) key);
		if (key == null){
			throw new IllegalArgumentException();
		}
		if (containsKey(key)){
			Bucket<K,V> first = table1[index1];
			if (first != null && ((first.getKey()).equals(key))) {
				return first.getValue();
			}
			Bucket<K,V> second = table2[index2];
			if (second != null && ((second.getKey()).equals(key))) {
				return second.getValue();
			}
		}
		return null;
	}
	
	/**
	 * Remove the MapEntry specified by the key from the hash table.
	 * 
 	 * Throw an IllegalArgumentException if input is null.
	 * 
	 * @param key The key of the entry that you are looking to remove.
	 * @return the value that corresponds with the removed key. if null, the tables are empty/unapplicable.
	 */
	public V remove(Object key){
		V oldValue;
		if (key == null){
			throw new IllegalArgumentException();
		}
		if (size() == 0){
			return null;
		}
		int index1 = getHashIndex1((K) key);
		int index2 = getHashIndex2((K) key);
		if (containsKey(key)){
			if (table1[index1].getKey().equals(key)){
				oldValue = (V) table1[index1].getValue();
				table1[index1] = null;
				return oldValue;
			}
			if (table2[index2].getKey().equals(key)){
				oldValue = (V) table2[index2].getValue();
				table2[index2] = null;
				return oldValue;
			}
		}
		return null;
	}
	
	@Override
	public boolean containsValue(Object value){
		V valueCast = (V) value;
		for (int i = 0; i<table1.length; i++){
			if (table1[i] != null){
				if (table1[i].getValue().equals(valueCast)){
					return true;
				}
			}
		}
		for (int i = 0 ; i < table2.length ; i++){
			if (table2[i] != null){
				if (table2[i].getValue().equals(valueCast)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	/**
	 * Check if the index of the HashIndex location is null,
	 * if it is not null check the values of the parameter's key and
	 * the bucket's key.
	 * 
	 * @return true if the key is found, false otherwise.
	 */
	public boolean containsKey(Object key){
		K keyCast = (K) key;
		if (keyCast == null){
			throw new IllegalArgumentException();
		} else {
			 int keyCode1 = getHashIndex1(keyCast);
			 int keyCode2 = getHashIndex2(keyCast);
			 if (table1[keyCode1] != null){
				 if (table1[keyCode1].getKey().equals(keyCast)){
					 return true;
				 }
			 }
			 if (table2[keyCode2] != null){
				 if (table2[keyCode2].getKey().equals(keyCast)) {
					 return true;
				 }
			 }
			 return false;
		}
	}
	
	/**
	 * Return all the values in the hash tables. If there are no values return
	 * an empty collection utilizing ArrayList.
	 * 
	 * @return a collection of all the values in the hash table.
	 */
	public Collection<V> values(){
		ArrayList set = new ArrayList();
		for (int i = 0 ; i<table1.length ; i++){
			if (table1[i] != null){
				set.add(table1[i].getValue());
			}
		}
		for (int i = 0 ; i<table2.length ; i++){
			if (table2[i] != null){
				set.add(table2[i].getValue());
			}
		}
		return set;
	}
	
	public int size(){
		return size;
	}
	
	public boolean isEmpty(){
		return size == 0;
	}
	
	/**
	 * Clear the hash tables. This method should also reset
	 * the backing array to the initial capacity. 
	 */
	public void clear(){
		count1 = 0;
		count2 = 0;
		size = 0;
		table1 = new Bucket[10];
		table2 = new Bucket[10];
	}
	
	@Override
	public Set<K> keySet() {
		Set<K> set = new TreeSet<K>();
		if (isEmpty()) {
			return set;
		} else {
			for (int i = 0; i < table1.length; i++) {
				if (table1[i] != null) {
					set.add((K) table1[i].getKey());
				}
			}
			for (int i = 0; i < table2.length; i++) {
				if (table2[i] != null) {
					set.add((K) table2[i].getKey());
				}
			}
			return set;
		}	
	}
	
	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		Set<Map.Entry<K, V>> set = new TreeSet<Map.Entry<K, V>>();
		if (isEmpty()) {
			return set;
		} else {
			for (int i = 0; i < table1.length; i++) {
				if (table1[i] != null) {
					set.add(table1[i]);
				}
			}
			for (int i = 0; i < table2.length; i++) {
				if (table2[i] != null) {
					set.add(table2[i]);
				}
			}
			return set;
		}
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		if ( m == null) {
			throw new IllegalArgumentException();
		}
		for (Map.Entry<? extends K, ? extends V> item : m.entrySet() ){
			put((K) item.getKey(), (V) item.getValue());
		}
	}
	
}
