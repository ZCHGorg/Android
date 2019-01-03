package io.charg.chargstation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExampleUnitTest {

    @Test
    public void addition_correct() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addition_isNotCorrect() {
        assertEquals("Numbers isn't equals!", 4, 2 + 2);
    }
}