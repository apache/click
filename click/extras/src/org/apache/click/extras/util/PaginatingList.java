/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.click.extras.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Provides a paginating list for displaying a sliding window into a much larger
 * data set. This list object is typically provided to the Table control to
 * support table paging while also displaying a page of data.
 *
 * <h3>Example</h3>
 *
 * The example below uses a PaginatingList object in a DataProvider for a table.
 * When the table is rendered it will call the DataProvider to load the data.
 * The DataProvider in term calls the getPaginatingList() method which performs
 * the database query and returns a PaginatingList to the table.
 *
 * <pre class="prettyprint">
 * public class CustomerSearach extends Page {
 *
 *    private Table table = new Table("table");
 *
 *    public CustomerSearch() {
 *        // Setup table
 *        table.setPageSize(5);
 *        table.setSortable(true);
 *        table.setSorted(true);
 *
 *        table.addColumn(new Column("id"));
 *        ..
 *        addControl(table);
 *
 *
 *        table.setDataProvider(new DataProvider<Customer>() {
 *            public List<Customer> getData() {
 *                return getPaginatingList();
 *            }
 *        });
 *    }
 *
 *    private PaginatingList<Customer> getPaginatingList() {
 *
 *        // Below we retrieve only those customers between:
 *        //     first row .. (first row + page size)
 *        List<Customer> customerList =
 *            customerService.getCustomersForPage(table.getFirstRow(),
 *                                                table.getPageSize(),
 *                                                table.getSortedColumn(),
 *                                                table.isSortedAscending());
 *
 *        int customerCount = customerService.getNumberOfCustomers();
 *
 *        return new PaginatingList<Customer>(customerList,
 *                                            table.getFirstRow(),
 *                                            table.getPageSize(),
 *                                            customerCount);
 *    }
 * } </pre>
 */
public class PaginatingList<E> implements List<E> {

    private static final long serialVersionUID = 1L;

    /** The first index to access. */
    protected final int firstIndex;

    /** The page data list. */
    protected final List<E> pageData;

    /** The page size. */
    protected final int pageSize;

    /** The total size of the data set. */
    protected final int totalSize;

    // Constructors -----------------------------------------------------------

    /**
     * Create a paginating list with the given pageData, first page item index,
     * page size and the total data set size.
     *
     * @param pageData the page of data to display
     * @param pageSize the number of items read from the pageData and display
     * @param firstIndex the index of the first item to display
     * @param totalSize the total size of the paginated data set
     */
    public PaginatingList(Iterator<E> pageData, int firstIndex, int pageSize, int totalSize) {
        if (pageData == null) {
            throw new IllegalArgumentException("Null pageData parameter");
        }
        if (pageSize < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + pageSize);
        }

        // Populate the internal page data list
        this.pageData = new ArrayList<E>(pageSize);
        for (int i = 0; i < pageSize; i++) {
            if (pageData.hasNext()) {
                this.pageData.add(pageData.next());
            } else {
                break;
            }
        }

        this.firstIndex = firstIndex;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
    }

    /**
     * Create a paginating list with the given pageData, first page item index,
     * page size and the total data set size.
     *
     * @param pageData the page of data to display
     * @param pageSize the number of items read from the pageData and display
     * @param firstIndex the index of the first item to display
     * @param totalSize the total size of the paginated data set
     */
    public PaginatingList(List<E> pageData, int firstIndex, int pageSize, int totalSize) {
        if (pageData == null) {
            throw new IllegalArgumentException("Null pageData parameter");
        }
        if (pageSize < 0) {
            throw new IllegalArgumentException("Illegal Capacity: " + pageSize);
        }

        // Populate the internal page data list
        this.pageData = new ArrayList<E>(pageSize);
        int size = Math.min(pageSize, pageData.size());
        for (int i = 0; i < size; i++) {
            this.pageData.add(pageData.get(i));
        }

        this.firstIndex = firstIndex;
        this.pageSize = pageSize;
        this.totalSize = totalSize;
    }

    // Public Methods ---------------------------------------------------------

    /**
     * Returns the row at the specified index, offsetted by the current
     * table first row value.
     *
     * @param index the index of the row as viewed in the Table
     * @return the the row at the specified index, offsetted by the
     * current table first row value.
     */
    public E get(int index) {
        int realIndex = index - firstIndex;
        return pageData.get(realIndex);
    }

    /**
     * Return true if the total size of the data set is greater than zero.
     *
     * @see Collection#isEmpty()
     *
     * @return true if the total size of the data set is greater than 0
     */
    public boolean isEmpty() {
        return totalSize <= 0;
    }

    /**
     * Return total size of the paginated data set.
     *
     * @see java.util.List#size()
     *
     * @return total size of the paginated data set.
     */
    public int size() {
        return totalSize;
    }

    /**
     * Return the string representation of this object.
     *
     * @see String#toString()
     *
     * @return the string representation of this object
     */
    public String toString() {
        return getClass().getSimpleName()
            + "[pageData.size=" + pageData.size()
            + ",firstIndex=" + firstIndex
            + ",pageSize=" + pageSize
            + ",totalSize=" + totalSize + "]";
    }

    // Unsupported Methods ----------------------------------------------------

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#add(Object)
     *
     * @throws UnsupportedOperationException
     */
    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#addAll(int, Collection)a
     *
     * @throws UnsupportedOperationException
     */
    public void add(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#addAll(Collection)
     *
     * @throws UnsupportedOperationException
     */
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#addAll(int, Collection)
     *
     * @throws UnsupportedOperationException
     */
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#clear()
     *
     * @throws UnsupportedOperationException
     */
    public void clear() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#contains(Object)
     *
     * @throws UnsupportedOperationException
     */
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#containsAll(Collection)
     *
     * @throws UnsupportedOperationException
     */
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#indexOf(Object)
     *
     * @throws UnsupportedOperationException
     */
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#iterator()
     *
     * @throws UnsupportedOperationException
     */
    public Iterator<E> iterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#lastIndexOf(Object)
     *
     * @throws UnsupportedOperationException
     */
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#listIterator()
     *
     * @throws UnsupportedOperationException
     */
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#listIterator(int)
     */
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#remove(Object)
     *
     * @throws UnsupportedOperationException
     */
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#remove(int)
     *
     * @throws UnsupportedOperationException
     */
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#removeAll(Collection)
     *
     * @throws UnsupportedOperationException
     */
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#retainAll(Collection)
     *
     * @throws UnsupportedOperationException
     */
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#set(int, Object)
     *
     * @throws UnsupportedOperationException
     */
    public E set(int index, E element) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#subList(int, int)
     *
     * @throws UnsupportedOperationException
     */
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#toArray()
     *
     * @throws UnsupportedOperationException
     */
    public Object[] toArray() {
        throw new UnsupportedOperationException();

    }

    /**
     * This method is not supported and will throw
     * <tt>UnsupportedOperationException</tt> if invoked.
     *
     * @see List#toArray(Object[])
     *
     * @throws UnsupportedOperationException
     */
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

}
