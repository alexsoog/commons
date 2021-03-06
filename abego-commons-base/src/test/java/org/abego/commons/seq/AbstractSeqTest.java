/*
 * MIT License
 *
 * Copyright (c) 2020 Udo Borkowski, (ub@abego.org)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.abego.commons.seq;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.abego.commons.lang.StringUtil.array;
import static org.abego.commons.seq.SeqHelper.emptySeq;
import static org.abego.commons.seq.SeqUtil.newSeq;
import static org.abego.commons.util.ListUtil.list;
import static org.abego.commons.util.ListUtil.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

abstract class AbstractSeqTest {

    static final @NonNull String[] SINGLE_ITEM_ARRAY = array("a");
    static final @NonNull String[] HELLO_ARRAY = array("h", "e", "l", "l", "o");

    private Seq<String> noItemSeq() {
        return emptySeq();
    }

    /**
     * Return a Seq containing the string <code>"a"</code>.
     */
    abstract Seq<String> singleItemSeq();

    /**
     * Return a Seq containing the strings <code>"h","e","l","l","o"</code>.
     */
    abstract Seq<String> helloSeq();

    @Test
    void seq_Array_ok() {
        Seq<String> s = newSeq("a");

        assertNotNull(s);
    }

    @Test
    void seq_List_ok() {
        List<String> list = new ArrayList<>();
        list.add("a");
        Seq<String> s = newSeq(list);

        assertNotNull(s);
    }

    @Test
    void indexOf_OK() {
        Seq<String> seq = helloSeq();

        assertEquals(0, seq.indexOf("h"));
        assertEquals(1, seq.indexOf("e"));
        assertEquals(2, seq.indexOf("l"));
        assertEquals(4, seq.indexOf("o"));
        assertEquals(-1, seq.indexOf("X"));
        assertEquals(-1, seq.indexOf(null));
    }

    @Test
    void stream_ok() {
        Seq<String> seq = helloSeq();

        Stream<String> stream = seq.stream();

        assertEquals(list(HELLO_ARRAY), stream.collect(Collectors.toList()));
    }

    @Test
    void sorted_OK() {
        Seq<String> seq = helloSeq();

        Seq<String> sortedSeq = seq.sorted(Comparator.naturalOrder());

        assertEquals(list("e", "h", "l", "l", "o"), toList(sortedSeq));
    }


    @Test
    void isEmpty_ok() {
        assertTrue(noItemSeq().isEmpty());
        assertFalse(singleItemSeq().isEmpty());
        assertFalse(helloSeq().isEmpty());
    }

    @Test
    void hasItems_ok() {
        assertFalse(noItemSeq().hasItems());
        assertTrue(singleItemSeq().hasItems());
        assertTrue(helloSeq().hasItems());
    }

    @Test
    void hasSingleItem_ok() {
        assertFalse(noItemSeq().hasSingleItem());
        assertTrue(singleItemSeq().hasSingleItem());
        assertFalse(helloSeq().hasSingleItem());
    }

    @Test
    void first_ok() {
        assertThrows(Exception.class, () -> noItemSeq().first());
        assertEquals("a", singleItemSeq().first());
        assertEquals("h", helloSeq().first());
    }

    @Test
    void filter_ok() {
        Seq<String> r1 = noItemSeq().filter(s -> s.equals("l"));
        assertTrue(r1.isEmpty());

        Seq<String> r2 = singleItemSeq().filter(s -> s.equals("l"));
        assertTrue(r2.isEmpty());

        Seq<String> r3 = singleItemSeq().filter(s -> true);
        assertEquals(1, r3.size());
        assertEquals("a", r3.item(0));

        Seq<String> r4 = helloSeq().filter(s -> s.equals("l"));
        assertEquals(2, r4.size());
        assertEquals("l", r4.item(0));
        assertEquals("l", r4.item(1));
    }

    @Test
    void singleItem_ok() {
        assertThrows(NoSuchElementException.class, () -> noItemSeq().singleItem());
        assertEquals("a", singleItemSeq().singleItem());
        assertThrows(NoSuchElementException.class, () -> helloSeq().singleItem());
    }

    @Test
    void singleItemOrNull_ok() {
        assertNull(noItemSeq().singleItemOrNull());
        assertEquals("a", singleItemSeq().singleItemOrNull());
        assertThrows(NoSuchElementException.class, () -> helloSeq().singleItemOrNull());
    }

    @Test
    void anyItem_ok() {
        assertThrows(NoSuchElementException.class, () -> noItemSeq().anyItem());
        assertEquals("a", singleItemSeq().anyItem());

        String r = helloSeq().anyItem();
        assertTrue(r.equals("h") || r.equals("e") || r.equals("l") || r.equals("o"));
    }

    @Test
    void anyItemOrNull_ok() {
        assertNull(noItemSeq().anyItemOrNull());
        assertEquals("a", singleItemSeq().anyItemOrNull());

        @Nullable String r = helloSeq().anyItemOrNull();
        assertTrue(r != null && (r.equals("h") || r.equals("e") || r.equals("l") || r.equals("o")));
    }

    private void checkForEachOrderedForSeq(Seq<String> seq, int seqSize) {
        assertEquals(seqSize, seq.size());

        List<String> r = new ArrayList<>();

        seq.forEach(r::add);

        assertEquals(seqSize, r.size());
        for (int i = 0; i < seqSize; i++) {
            assertEquals(seq.item(i), r.get(i));
        }
    }

    @Test
    void forEachOrdered_ok() {
        checkForEachOrderedForSeq(noItemSeq(), 0);
        checkForEachOrderedForSeq(singleItemSeq(), 1);
        checkForEachOrderedForSeq(helloSeq(), 5);
    }

    private void checkForEachForSeq(Seq<String> seq, int uniqueItemCount) {
        Set<String> r = new HashSet<>();

        seq.forEach(r::add);

        assertEquals(uniqueItemCount, r.size());

        for (String s : seq) {
            assertTrue(r.contains(s));
        }
    }

    @Test
    void forEach_ok() {
        checkForEachForSeq(noItemSeq(), 0);
        checkForEachForSeq(singleItemSeq(), 1);
        checkForEachForSeq(helloSeq(), 4 /* "hello" contains 4 different chars */);
    }

    // Map the strings to the character codes of their first character
    private void checkMapForSeq(Seq<String> seq, Integer... expectedCharCodes) {
        Seq<Integer> result = seq.map(s -> (int) s.charAt(0));

        assertEquals(expectedCharCodes.length, result.size());

        for (int i = 0; i < result.size(); i++) {
            assertEquals(expectedCharCodes[i], result.item(i));
        }
    }

    @Test
    void map_ok() {
        checkMapForSeq(noItemSeq());
        checkMapForSeq(singleItemSeq(), 97);
        checkMapForSeq(helloSeq(), 104, 101, 108, 108, 111);
    }

    @Test
    void equals_ok() {
        assertEquals(noItemSeq(), noItemSeq());
        assertEquals(singleItemSeq(), singleItemSeq());
        assertEquals(helloSeq(), helloSeq());

        assertNotEquals(noItemSeq(), singleItemSeq());
        assertNotEquals(singleItemSeq(), helloSeq());
        assertNotEquals(helloSeq(), noItemSeq());

        // identical check
        Seq<String> n = noItemSeq();
        assertEquals(n, n);
        Seq<String> s = singleItemSeq();
        assertEquals(s, s);
        Seq<String> h = helloSeq();
        assertEquals(h, h);

        // null check
        assertNotEquals(noItemSeq(), null);
        assertNotEquals(null, noItemSeq());
        assertNotEquals(singleItemSeq(), null);
        assertNotEquals(null, singleItemSeq());
        assertNotEquals(helloSeq(), null);
        assertNotEquals(null, helloSeq());

    }

    @Test
    void hashCode_ok() {
        assertEquals(1, noItemSeq().hashCode());
        assertEquals(128, singleItemSeq().hashCode());
        assertEquals(127791473, helloSeq().hashCode());
    }

    @Test
    void toString_OK() {
        assertTrue(noItemSeq().toString().endsWith("[]"));
        assertTrue(singleItemSeq().toString().endsWith("[a]"));
        assertTrue(helloSeq().toString().endsWith("[h, e, l, l, o]"));
    }

    @Test
    void contains_OK() {
        assertFalse(noItemSeq().contains("X"));
        assertTrue(singleItemSeq().contains("a"));
        assertFalse(singleItemSeq().contains("X"));
        assertTrue(helloSeq().contains("l"));
        assertFalse(helloSeq().contains("X"));
    }

    @Test
    void joined_OK() {
        assertEquals("", noItemSeq().joined());
        assertEquals("a", singleItemSeq().joined());
        assertEquals("hello", helloSeq().joined());
    }

    @Test
    void joined_1Arg_OK() {
        assertEquals("", noItemSeq().joined("-"));
        assertEquals("a", singleItemSeq().joined("-"));
        assertEquals("h-e-l-l-o", helloSeq().joined("-"));
    }

    @Test
    void joined_2Args_OK() {
        assertEquals("", noItemSeq().joined("-", s -> "(" + s + ")"));
        assertEquals("(a)", singleItemSeq().joined("-", s -> "(" + s + ")"));
        assertEquals("(h)-(e)-(l)-(l)-(o)", helloSeq().joined("-", s -> "(" + s + ")"));
    }

}