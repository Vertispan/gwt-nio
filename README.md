# GWT/J2CL emulation for `java.nio` packages

This project provides emulation for the `java.nio` packages, forked
from the [quake2](https://code.google.com/archive/p/quake2-gwt-port) project. There is 
also a utility class included to allow wrapping and unwrapping JS
Typed Arrays. 

To use in GWT2, depend on this Jar in your project, and add this to
your `.gwt.xml` module file:

    <inherits name="org.gwtproject.nio.GwtNioSupport" />

The `org.gwtproject.nio.TypedArrayHelper` class contains to helper 
methods, to turn `java.nio.ByteBuffer`s into JavaScript `ArrayBuffer` 
(technically `ArrayBufferView`, but you can get an `ArrayBuffer` from 
there) and back again.

#### Take care in forking this repository, we may rewrite history to remove unrelated PlayN commits.