package cr.ac.ucr.sga;

import cr.ac.ucr.sga.model.structures.stacks.ArrayStack;
import cr.ac.ucr.sga.model.structures.stacks.LinkedStack;
import cr.ac.ucr.sga.model.structures.stacks.StackException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class StacksTest {

    @Test
    void testArrayStackPushAndPop() throws Exception {
        ArrayStack<Integer> stack = new ArrayStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        Assertions.assertEquals(3, stack.pop());
        Assertions.assertEquals(2, stack.pop());
        Assertions.assertEquals(1, stack.pop());
    }

    @Test
    void testArrayStackPeekNoRemove() throws Exception {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("A");
        Assertions.assertEquals("A", stack.peek());
        Assertions.assertEquals(1, stack.size());
    }

    @Test
    void testArrayStackEmptyThrowsException() {
        ArrayStack<Integer> stack = new ArrayStack<>();
        Assertions.assertThrows(StackException.class, stack::pop);
    }

    @Test
    void testArrayStackResize() throws Exception {
        ArrayStack<Integer> stack = new ArrayStack<>(10);
        for (int i = 0; i < 15; i++) stack.push(i);
        for (int i = 14; i >= 0; i--) Assertions.assertEquals(i, stack.pop());
        Assertions.assertTrue(stack.isEmpty());
    }

    @Test
    void testArrayStackOneElement() throws Exception {
        ArrayStack<String> stack = new ArrayStack<>();
        stack.push("X");
        Assertions.assertEquals("X", stack.pop());
        Assertions.assertTrue(stack.isEmpty());
    }

    @Test
    void testLinkedStackPushAndPop() throws Exception {
        LinkedStack<Integer> stack = new LinkedStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        Assertions.assertEquals(3, stack.pop());
        Assertions.assertEquals(2, stack.pop());
        Assertions.assertEquals(1, stack.pop());
    }

    @Test
    void testLinkedStackPeekNoRemove() throws Exception {
        LinkedStack<String> stack = new LinkedStack<>();
        stack.push("A");
        Assertions.assertEquals("A", stack.peek());
        Assertions.assertEquals(1, stack.size());
    }

    @Test
    void testLinkedStackEmptyThrowsException() {
        LinkedStack<Integer> stack = new LinkedStack<>();
        Assertions.assertThrows(StackException.class, stack::pop);
    }

    @Test
    void testLinkedStackOneElement() throws Exception {
        LinkedStack<String> stack = new LinkedStack<>();
        stack.push("X");
        Assertions.assertEquals("X", stack.pop());
        Assertions.assertTrue(stack.isEmpty());
    }
}

