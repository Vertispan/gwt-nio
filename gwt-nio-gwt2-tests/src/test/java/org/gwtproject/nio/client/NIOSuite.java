package org.gwtproject.nio.client;

import com.google.gwt.junit.tools.GWTTestSuite;
import junit.framework.Test;

/** @author Dmitrii Tikhomirov Created by treblereel 5/5/20 */
public class NIOSuite {

  public static Test suite() {
    GWTTestSuite suite = new GWTTestSuite("Test suite for all cellview classes");

    suite.addTestSuite(ByteBufferTest.class);
    suite.addTestSuite(CharBufferTest.class);
    suite.addTestSuite(BufferOverflowExceptionTest.class);
    suite.addTestSuite(BufferUnderflowExceptionTest.class);
    suite.addTestSuite(ByteOrderTest.class);

    // suite.addTestSuite(DirectByteBufferTest.class); //TODO
    suite.addTestSuite(DirectCharBufferTest.class);
    suite.addTestSuite(DirectDoubleBufferTest.class);
    // suite.addTestSuite(DirectFloatBufferTest.class); //TODO
    suite.addTestSuite(DirectIntBufferTest.class);
    suite.addTestSuite(DirectIntBufferTest.class);
    suite.addTestSuite(DoubleBufferTest.class);
    suite.addTestSuite(FloatBufferTest.class);
    suite.addTestSuite(IntBufferTest.class);

    suite.addTestSuite(HeapByteBufferTest.class);
    suite.addTestSuite(InvalidMarkExceptionTest.class);
    suite.addTestSuite(LongBufferTest.class);
    suite.addTestSuite(ReadOnlyBufferExceptionTest.class);
    suite.addTestSuite(ReadOnlyCharBufferTest.class);
    suite.addTestSuite(ReadOnlyDoubleBufferTest.class);
    suite.addTestSuite(ReadOnlyFloatBufferTest.class);
    suite.addTestSuite(ReadOnlyHeapByteBufferTest.class);
    suite.addTestSuite(ReadOnlyIntBufferTest.class);
    suite.addTestSuite(ReadOnlyLongBufferTest.class);
    suite.addTestSuite(ReadOnlyShortBufferTest.class);
    suite.addTestSuite(ShortBufferTest.class);
    return suite;
  }
}
