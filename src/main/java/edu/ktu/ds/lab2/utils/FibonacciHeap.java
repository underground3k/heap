package edu.ktu.ds.lab2.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class FibonacciHeap<T extends Comparable<T>> {

    public static class Node<T> {
        T value;
        int degree;
        boolean marked;
        Node<T> parent;
        Node<T> child;
        Node<T> next;
        Node<T> prev;

        Node(T value) {
            this.value = value;
            this.next = this;
            this.prev = this;
        }
    }

    private Node<T> min;
    private int size;

    public Node<T> insert(T value) {
        Node<T> node = new Node<>(value);
        min = mergeLists(min, node);
        size++;
        return node;
    }

    public T findMin() {
        if (isEmpty()) throw new NoSuchElementException();
        return min.value;
    }

    public T extractMin() {
        if (isEmpty()) throw new NoSuchElementException();

        Node<T> minNode = min;

        //add children to root list
        if (minNode.child != null) {
            Node<T> child = minNode.child;

            do {
                child.parent = null;
                child = child.next;
            } while (child != minNode.child);
            min = mergeLists(min, minNode.child);
        }

        //remove min from root list
        if (minNode.next == minNode) {
            min = null;
        } else {
            min = minNode.next;
            minNode.prev.next = minNode.next;
            minNode.next.prev = minNode.prev;
            consolidate();
        }

        size--;
        return minNode.value;
    }

    public void decreaseKey(Node<T> node, T newValue) {
        if (newValue.compareTo(node.value) > 0)
            throw new IllegalArgumentException("New value cant be greater than current value");

        node.value = newValue;

        if (node.parent != null && node.value.compareTo(node.parent.value) < 0) {
            cut(node);
        }

        if (node.value.compareTo(min.value) < 0) {
            min = node;
        }
    }

    public boolean isEmpty() {
        return min == null;
    }

    public int size() {
        return size;
    }

    private void consolidate() {
        List<Node<T>> trees = new ArrayList<>();

        //collect all roots
        List<Node<T>> roots = new ArrayList<>();
        Node<T> curr = min;
        do {
            roots.add(curr);
            curr = curr.next;
        } while (curr != min);

        //merge trees of same degree
        for (Node<T> node : roots) {
            int degree = node.degree;

            while (degree >= trees.size()) trees.add(null);

            while (trees.get(degree) != null) {
                Node<T> other = trees.get(degree);
                trees.set(degree, null);

                // Make smaller root the parent
                if (node.value.compareTo(other.value) > 0) {
                    Node<T> temp = node;
                    node = other;
                    other = temp;
                }

                link(other, node);
                degree++;

                while (degree >= trees.size()) trees.add(null);
            }

            trees.set(degree, node);
        }

        // Rebuild root list and find new min
        min = null;
        for (Node<T> node : trees) {
            if (node != null) {
                node.next = node.prev = node;
                min = mergeLists(min, node);
            }
        }
    }

    private void link(Node<T> child, Node<T> parent) {
        // Remove child from root list
        child.prev.next = child.next;
        child.next.prev = child.prev;

        // Make child a child of parent
        child.parent = parent;
        child.next = child.prev = child;
        parent.child = mergeLists(parent.child, child);
        parent.degree++;
        child.marked = false;
    }

    private void cut(Node<T> node) {
        Node<T> parent = node.parent;

        // Remove from sibling list
        if (node.next == node) {
            parent.child = null;
        } else {
            if (parent.child == node) {
                parent.child = node.next;
            }
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        parent.degree--;

        // Add to root list
        node.parent = null;
        node.marked = false;
        node.next = node.prev = node;
        min = mergeLists(min, node);

        // Cascading cut
        if (parent.parent != null) {
            if (parent.marked) {
                cut(parent);
            } else {
                parent.marked = true;
            }
        }
    }

    private static <T extends Comparable<T>> Node<T> mergeLists(Node<T> a, Node<T> b) {
        if (a == null) return b;
        if (b == null) return a;

        // Splice lists together
        Node<T> aNext = a.next;
        a.next = b.next;
        a.next.prev = a;
        b.next = aNext;
        b.next.prev = b;

        return a.value.compareTo(b.value) < 0 ? a : b;
    }
}
