package jaci.openrio.toast.lib.log;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Acts as a splitter for {@link java.io.OutputStream} objects, splitting the output
 * between all the given instances in the constructor
 *
 * @author Jaci
 */
public class SplitStream extends OutputStream {

    OutputStream[] outs;

    public SplitStream(OutputStream... streams) {
        this.outs = streams;
    }

    /**
     * Write a single byte to all the output streams created for this SplitStream
     */
    @Override
    public void write(int b) throws IOException {
        for (OutputStream stream : outs)
            stream.write(b);
    }

    /**
     * Write a byte array to all the output streams created for this SplitStream
     */
    @Override
    public void write(byte b[]) throws IOException {
        for (OutputStream stream : outs)
            stream.write(b);
    }

    /**
     * Write a byte array with the offset and length given in this method to all
     * output streams created for this SplitStream
     */
    @Override
    public void write(byte b[], int off, int len) throws IOException {
        for (OutputStream stream : outs)
            stream.write(b, off, len);
    }

    /**
     * Flush the output streams registered for this SplitStream
     */
    @Override
    public void flush() throws IOException {
        for (OutputStream stream : outs)
            stream.flush();
    }

    /**
     * Close this stream and all it's children output streams.
     */
    @Override
    public void close() throws IOException {
        for (OutputStream stream : outs)
            stream.close();
    }
}
