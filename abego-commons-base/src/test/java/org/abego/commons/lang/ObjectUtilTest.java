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

package org.abego.commons.lang;

import org.abego.commons.lang.exception.MustNotInstantiateException;
import org.junit.jupiter.api.Test;

import static org.abego.commons.lang.ObjectUtil.checkType;
import static org.abego.commons.lang.ObjectUtil.ignore;
import static org.abego.commons.lang.ObjectUtil.valueOrElse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ObjectUtilTest {

    @Test
    void constructorFails() {
        assertThrows(MustNotInstantiateException.class, ObjectUtil::new);
    }

    @Test
    void ignoreOk() {
        ignore(null);
        ignore("a");
        ignore(1);
        ignore(true);
    }

    @Test
    void checkTypeOk() {
        String s = checkType("foo", String.class);
        assertEquals("foo", s);
    }

    @Test
    void checkType_failOk() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> checkType(1, String.class));
        assertEquals("Object is not of type java.lang.String (but java.lang.Integer)", e.getMessage());
    }

    @Test
    void checkType_nullFailOk() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> checkType(null, String.class));
        assertEquals("Object is not of type java.lang.String (but null)", e.getMessage());
    }

    @Test
    void valueOrElseOK() {

        assertEquals("foo", valueOrElse("foo", "bar"));
        assertEquals("bar", valueOrElse(null, "bar"));
    }
}