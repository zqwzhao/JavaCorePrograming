
package reflection.action.example2;

import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.BoundedCollection;
import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;

import org.apache.commons.collections4.collection.UnmodifiableBoundedCollection;
import org.apache.commons.collections4.collection.UnmodifiableCollection;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;


public class CollectionUtils {
  private static Integer INTEGER_ONE = new Integer(1);
  public static final Collection EMPTY_COLLECTION = UnmodifiableCollection.unmodifiableCollection(new ArrayList());

  public CollectionUtils() {
  }

  public static Collection union(Collection a, Collection b) {
    ArrayList list = new ArrayList();
    Map mapa = getCardinalityMap(a);
    Map mapb = getCardinalityMap(b);
    Set elts = new HashSet(a);
    elts.addAll(b);
    Iterator it = elts.iterator();

    while(it.hasNext()) {
      Object obj = it.next();
      int i = 0;

      for(int m = Math.max(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
        list.add(obj);
      }
    }

    return list;
  }

  public static Collection intersection(Collection a, Collection b) {
    ArrayList list = new ArrayList();
    Map mapa = getCardinalityMap(a);
    Map mapb = getCardinalityMap(b);
    Set elts = new HashSet(a);
    elts.addAll(b);
    Iterator it = elts.iterator();

    while(it.hasNext()) {
      Object obj = it.next();
      int i = 0;

      for(int m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
        list.add(obj);
      }
    }

    return list;
  }

  public static Collection disjunction(Collection a, Collection b) {
    ArrayList list = new ArrayList();
    Map mapa = getCardinalityMap(a);
    Map mapb = getCardinalityMap(b);
    Set elts = new HashSet(a);
    elts.addAll(b);
    Iterator it = elts.iterator();

    while(it.hasNext()) {
      Object obj = it.next();
      int i = 0;

      for(int m = Math.max(getFreq(obj, mapa), getFreq(obj, mapb)) - Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; ++i) {
        list.add(obj);
      }
    }

    return list;
  }

  public static Collection subtract(Collection a, Collection b) {
    ArrayList list = new ArrayList(a);
    Iterator it = b.iterator();

    while(it.hasNext()) {
      list.remove(it.next());
    }

    return list;
  }

  public static boolean containsAny(Collection coll1, Collection coll2) {
    Iterator it;
    if (coll1.size() < coll2.size()) {
      it = coll1.iterator();

      while(it.hasNext()) {
        if (coll2.contains(it.next())) {
          return true;
        }
      }
    } else {
      it = coll2.iterator();

      while(it.hasNext()) {
        if (coll1.contains(it.next())) {
          return true;
        }
      }
    }

    return false;
  }

  public static Map getCardinalityMap(Collection coll) {
    Map count = new HashMap();
    Iterator it = coll.iterator();

    while(it.hasNext()) {
      Object obj = it.next();
      Integer c = (Integer)((Integer)count.get(obj));
      if (c == null) {
        count.put(obj, INTEGER_ONE);
      } else {
        count.put(obj, new Integer(c + 1));
      }
    }

    return count;
  }

  public static boolean isSubCollection(Collection a, Collection b) {
    Map mapa = getCardinalityMap(a);
    Map mapb = getCardinalityMap(b);
    Iterator it = a.iterator();

    Object obj;
    do {
      if (!it.hasNext()) {
        return true;
      }

      obj = it.next();
    } while(getFreq(obj, mapa) <= getFreq(obj, mapb));

    return false;
  }

  public static boolean isProperSubCollection(Collection a, Collection b) {
    return a.size() < b.size() && isSubCollection(a, b);
  }

  public static boolean isEqualCollection(Collection a, Collection b) {
    if (a.size() != b.size()) {
      return false;
    } else {
      Map mapa = getCardinalityMap(a);
      Map mapb = getCardinalityMap(b);
      if (mapa.size() != mapb.size()) {
        return false;
      } else {
        Iterator it = mapa.keySet().iterator();

        Object obj;
        do {
          if (!it.hasNext()) {
            return true;
          }

          obj = it.next();
        } while(getFreq(obj, mapa) == getFreq(obj, mapb));

        return false;
      }
    }
  }

  public static int cardinality(Object obj, Collection coll) {
    if (coll instanceof Set) {
      return coll.contains(obj) ? 1 : 0;
    } else if (coll instanceof Bag) {
      return ((Bag)coll).getCount(obj);
    } else {
      int count = 0;
      Iterator it;
      if (obj == null) {
        it = coll.iterator();

        while(it.hasNext()) {
          if (it.next() == null) {
            ++count;
          }
        }
      } else {
        it = coll.iterator();

        while(it.hasNext()) {
          if (obj.equals(it.next())) {
            ++count;
          }
        }
      }

      return count;
    }
  }

  public static Object find(Collection collection, Predicate predicate) {
    if (collection != null && predicate != null) {
      Iterator iter = collection.iterator();

      while(iter.hasNext()) {
        Object item = iter.next();
        if (predicate.evaluate(item)) {
          return item;
        }
      }
    }

    return null;
  }

  public static void forAllDo(Collection collection, Closure closure) {
    if (collection != null && closure != null) {
      Iterator it = collection.iterator();

      while(it.hasNext()) {
        closure.execute(it.next());
      }
    }

  }

  public static void filter(Collection collection, Predicate predicate) {
    if (collection != null && predicate != null) {
      Iterator it = collection.iterator();

      while(it.hasNext()) {
        if (!predicate.evaluate(it.next())) {
          it.remove();
        }
      }
    }

  }

  public static void transform(Collection collection, Transformer transformer) {
    if (collection != null && transformer != null) {
      if (collection instanceof List) {
        List list = (List)collection;
        ListIterator it = list.listIterator();

        while(it.hasNext()) {
          it.set(transformer.transform(it.next()));
        }
      } else {
        Collection resultCollection = collect(collection, transformer);
        collection.clear();
        collection.addAll(resultCollection);
      }
    }

  }

  public static int countMatches(Collection inputCollection, Predicate predicate) {
    int count = 0;
    if (inputCollection != null && predicate != null) {
      Iterator it = inputCollection.iterator();

      while(it.hasNext()) {
        if (predicate.evaluate(it.next())) {
          ++count;
        }
      }
    }

    return count;
  }

  public static boolean exists(Collection collection, Predicate predicate) {
    if (collection != null && predicate != null) {
      Iterator it = collection.iterator();

      while(it.hasNext()) {
        if (predicate.evaluate(it.next())) {
          return true;
        }
      }
    }

    return false;
  }

  public static Collection select(Collection inputCollection, Predicate predicate) {
    ArrayList answer = new ArrayList(inputCollection.size());
    select(inputCollection, predicate, answer);
    return answer;
  }

  public static void select(Collection inputCollection, Predicate predicate, Collection outputCollection) {
    if (inputCollection != null && predicate != null) {
      Iterator iter = inputCollection.iterator();

      while(iter.hasNext()) {
        Object item = iter.next();
        if (predicate.evaluate(item)) {
          outputCollection.add(item);
        }
      }
    }

  }

  public static Collection selectRejected(Collection inputCollection, Predicate predicate) {
    ArrayList answer = new ArrayList(inputCollection.size());
    selectRejected(inputCollection, predicate, answer);
    return answer;
  }

  public static void selectRejected(Collection inputCollection, Predicate predicate, Collection outputCollection) {
    if (inputCollection != null && predicate != null) {
      Iterator iter = inputCollection.iterator();

      while(iter.hasNext()) {
        Object item = iter.next();
        if (!predicate.evaluate(item)) {
          outputCollection.add(item);
        }
      }
    }

  }

  public static Collection collect(Collection inputCollection, Transformer transformer) {
    ArrayList answer = new ArrayList(inputCollection.size());
    collect((Collection)inputCollection, transformer, answer);
    return answer;
  }

  public static Collection collect(Iterator inputIterator, Transformer transformer) {
    ArrayList answer = new ArrayList();
    collect((Iterator)inputIterator, transformer, answer);
    return answer;
  }

  public static Collection collect(Collection inputCollection, Transformer transformer, Collection outputCollection) {
    return inputCollection != null ? collect(inputCollection.iterator(), transformer, outputCollection) : outputCollection;
  }

  public static Collection collect(Iterator inputIterator, Transformer transformer, Collection outputCollection) {
    if (inputIterator != null && transformer != null) {
      while(inputIterator.hasNext()) {
        Object item = inputIterator.next();
        Object value = transformer.transform(item);
        outputCollection.add(value);
      }
    }

    return outputCollection;
  }

  public static boolean addIgnoreNull(Collection collection, Object object) {
    return object == null ? false : collection.add(object);
  }

  public static void addAll(Collection collection, Iterator iterator) {
    while(iterator.hasNext()) {
      collection.add(iterator.next());
    }

  }

  public static void addAll(Collection collection, Enumeration enumeration) {
    while(enumeration.hasMoreElements()) {
      collection.add(enumeration.nextElement());
    }

  }

  public static void addAll(Collection collection, Object[] elements) {
    int i = 0;

    for(int size = elements.length; i < size; ++i) {
      collection.add(elements[i]);
    }

  }

  /** @deprecated */
  public static Object index(Object obj, int idx) {
    return index(obj, new Integer(idx));
  }

  /** @deprecated */
  public static Object index(Object obj, Object index) {
    if (obj instanceof Map) {
      Map map = (Map)obj;
      if (map.containsKey(index)) {
        return map.get(index);
      }
    }

    int idx = -1;
    if (index instanceof Integer) {
      idx = (Integer)index;
    }

    if (idx < 0) {
      return obj;
    } else if (obj instanceof Map) {
      Map map = (Map)obj;
      Iterator iterator = map.keySet().iterator();
      return index(iterator, idx);
    } else if (obj instanceof List) {
      return ((List)obj).get(idx);
    } else if (obj instanceof Object[]) {
      return ((Object[])((Object[])obj))[idx];
    } else {
      if (obj instanceof Enumeration) {
        Enumeration it = (Enumeration)obj;

        while(it.hasMoreElements()) {
          --idx;
          if (idx == -1) {
            return it.nextElement();
          }

          it.nextElement();
        }
      } else {
        if (obj instanceof Iterator) {
          return index((Iterator)obj, idx);
        }

        if (obj instanceof Collection) {
          Iterator iterator = ((Collection)obj).iterator();
          return index(iterator, idx);
        }
      }

      return obj;
    }
  }

  private static Object index(Iterator iterator, int idx) {
    while(iterator.hasNext()) {
      --idx;
      if (idx == -1) {
        return iterator.next();
      }

      iterator.next();
    }

    return iterator;
  }

  public static Object get(Object object, int index) {
    if (index < 0) {
      throw new IndexOutOfBoundsException("Index cannot be negative: " + index);
    } else if (object instanceof Map) {
      Map map = (Map)object;
      Iterator iterator = map.entrySet().iterator();
      return get(iterator, index);
    } else if (object instanceof List) {
      return ((List)object).get(index);
    } else if (object instanceof Object[]) {
      return ((Object[])((Object[])object))[index];
    } else {
      Iterator it;
      if (object instanceof Iterator) {
        it = (Iterator)object;

        while(it.hasNext()) {
          --index;
          if (index == -1) {
            return it.next();
          }

          it.next();
        }

        throw new IndexOutOfBoundsException("Entry does not exist: " + index);
      } else if (object instanceof Collection) {
        it = ((Collection)object).iterator();
        return get(it, index);
      } else if (object instanceof Enumeration) {
        Enumeration enumeIt = (Enumeration)object;

        while(enumeIt.hasMoreElements()) {
          --index;
          if (index == -1) {
            return enumeIt.nextElement();
          }

          enumeIt.nextElement();
        }

        throw new IndexOutOfBoundsException("Entry does not exist: " + index);
      } else if (object == null) {
        throw new IllegalArgumentException("Unsupported object type: null");
      } else {
        try {
          return Array.get(object, index);
        } catch (IllegalArgumentException var4) {
          throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
        }
      }
    }
  }

  public static int size(Object object) {
    int total = 0;
    if (object instanceof Map) {
      total = ((Map)object).size();
    } else if (object instanceof Collection) {
      total = ((Collection)object).size();
    } else if (object instanceof Object[]) {
      total = ((Object[])((Object[])object)).length;
    } else if (object instanceof Iterator) {
      Iterator it = (Iterator)object;

      while(it.hasNext()) {
        ++total;
        it.next();
      }
    } else if (object instanceof Enumeration) {
      Enumeration it = (Enumeration)object;

      while(it.hasMoreElements()) {
        ++total;
        it.nextElement();
      }
    } else {
      if (object == null) {
        throw new IllegalArgumentException("Unsupported object type: null");
      }

      try {
        total = Array.getLength(object);
      } catch (IllegalArgumentException var3) {
        throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
      }
    }

    return total;
  }

  public static boolean sizeIsEmpty(Object object) {
    if (object instanceof Collection) {
      return ((Collection)object).isEmpty();
    } else if (object instanceof Map) {
      return ((Map)object).isEmpty();
    } else if (object instanceof Object[]) {
      return ((Object[])((Object[])object)).length == 0;
    } else if (object instanceof Iterator) {
      return !((Iterator)object).hasNext();
    } else if (object instanceof Enumeration) {
      return !((Enumeration)object).hasMoreElements();
    } else if (object == null) {
      throw new IllegalArgumentException("Unsupported object type: null");
    } else {
      try {
        return Array.getLength(object) == 0;
      } catch (IllegalArgumentException var2) {
        throw new IllegalArgumentException("Unsupported object type: " + object.getClass().getName());
      }
    }
  }

  public static boolean isEmpty(Collection coll) {
    return coll == null || coll.isEmpty();
  }

  public static boolean isNotEmpty(Collection coll) {
    return !isEmpty(coll);
  }

  public static void reverseArray(Object[] array) {
    int i = 0;

    for(int j = array.length - 1; j > i; ++i) {
      Object tmp = array[j];
      array[j] = array[i];
      array[i] = tmp;
      --j;
    }

  }

  private static final int getFreq(Object obj, Map freqMap) {
    Integer count = (Integer)freqMap.get(obj);
    return count != null ? count : 0;
  }

  public static boolean isFull(Collection coll) {
    if (coll == null) {
      throw new NullPointerException("The collection must not be null");
    } else if (coll instanceof BoundedCollection) {
      return ((BoundedCollection)coll).isFull();
    } else {
      try {
        BoundedCollection bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
        return bcoll.isFull();
      } catch (IllegalArgumentException var2) {
        return false;
      }
    }
  }

  public static int maxSize(Collection coll) {
    if (coll == null) {
      throw new NullPointerException("The collection must not be null");
    } else if (coll instanceof BoundedCollection) {
      return ((BoundedCollection)coll).maxSize();
    } else {
      try {
        BoundedCollection bcoll = UnmodifiableBoundedCollection.unmodifiableBoundedCollection(coll);
        return bcoll.maxSize();
      } catch (IllegalArgumentException var2) {
        return -1;
      }
    }
  }

/*  public static Collection retainAll(Collection collection, Collection retain) {
    return ListUtils.retainAll(collection, retain);
  }

  public static Collection removeAll(Collection collection, Collection remove) {
    return ListUtils.removeAll(collection, remove);
  }

  public static Collection synchronizedCollection(Collection collection) {
    return SynchronizedCollection.decorate(collection);
  }

  public static Collection unmodifiableCollection(Collection collection) {
    return UnmodifiableCollection.decorate(collection);
  }

  public static Collection predicatedCollection(Collection collection, Predicate predicate) {
    return PredicatedCollection.decorate(collection, predicate);
  }

  public static Collection typedCollection(Collection collection, Class type) {
    return TypedCollection.decorate(collection, type);
  }

  public static Collection transformedCollection(Collection collection, Transformer transformer) {
    return TransformedCollection.decorate(collection, transformer);
  }*/
}
