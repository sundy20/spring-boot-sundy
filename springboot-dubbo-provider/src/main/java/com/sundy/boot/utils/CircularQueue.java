package com.sundy.boot.utils;

/**
 * @author zeng.wang
 * @description 环形队列
 * @date 2020/3/19
 */
public class CircularQueue<E> {
    /**
     * 指针
     */
    private Node<E> node;
    private Node<E> first;
    private Node<E> last;

    private final int MODE_NEXT = 0;
    private final int MODE_PREV = 1;
    /**
     * 最后一次操作，0为next，1为prev
     */
    private int lastMode = MODE_NEXT;
    private int size;

    public CircularQueue() {

    }

    /**
     * 加入队列
     */
    public boolean add(E e) {
        final Node<E> l = last;
        final Node<E> newNode = new Node<>(l, e, first);
        last = newNode;
        if (node == null) {
            //指针
            node = newNode;
        }
        if (l == null) {
            first = newNode;
            first.prev = first;
        } else {
            l.next = newNode;
            first.prev = l.next;
        }
        size++;
        return true;
    }

    /**
     * 返回当前指针元素并把指针指向下一个元素
     */
    public E next() {
        if (node == null) {
            return null;
        }
        E e = node.item;
        node = node.next;
        lastMode = MODE_NEXT;
        return e;
    }

    /**
     * 返回当前元素，并把指针指向上一个元素
     */
    public E prev() {
        if (node == null) {
            return null;
        }
        E e = node.item;
        node = node.prev;
        lastMode = MODE_PREV;
        return e;
    }

    /**
     * 删除队列中某一个元素
     */
    public boolean remove(E e) {
        if (e == null) {
            for (Node<E> x = first; x != null; x = x.next) {
                if (x.item == null) {
                    unlink(x);
                    return true;
                }
            }
        } else {
            for (Node<E> x = first; x != null; x = x.next) {
                if (e.equals(x.item)) {
                    unlink(x);
                    return true;
                }
            }
        }
        size--;
        return true;
    }

    public E peek() {
        return node.item;
    }

    /**
     * 删除节点
     */
    private E unlink(Node<E> x) {
        final E element = x.item;
        final Node<E> next = x.next;
        final Node<E> prev = x.prev;
        if (prev == x || next == x) {
            this.first = null;
            this.last = null;
            this.node = null;
        }
        next.prev = prev;
        prev.next = next;
        if ((element == null && this.node.item == null) || element.equals(this.node.item)) {
            this.node = lastMode == MODE_NEXT ? this.node.next : this.node.prev;
        }
        x.item = null;
        x = null;
        size--;
        return element;
    }

    public int size() {
        return size;
    }

    /**
     * 节点类
     */
    private static class Node<E> {
        E item;
        Node<E> next;
        Node<E> prev;

        Node(Node<E> prev, E element, Node<E> next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }
}