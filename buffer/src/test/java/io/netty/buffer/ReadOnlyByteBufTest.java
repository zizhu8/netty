/*
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package io.netty.buffer;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;

import static io.netty.buffer.Unpooled.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests read-only channel buffers
 */
public class ReadOnlyByteBufTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullInConstructor() {
        new ReadOnlyByteBuf(null);
    }

    @Test
    public void testUnmodifiableBuffer() {
        assertTrue(unmodifiableBuffer(buffer(1)) instanceof ReadOnlyByteBuf);
    }

    @Test
    public void testUnwrap() {
        ByteBuf buf = buffer(1);
        assertSame(buf, unmodifiableBuffer(buf).unwrap());
    }

    @Test
    public void shouldHaveSameByteOrder() {
        ByteBuf buf = buffer(1);
        assertSame(BIG_ENDIAN, unmodifiableBuffer(buf).order());
        buf = buf.order(LITTLE_ENDIAN);
        assertSame(LITTLE_ENDIAN, unmodifiableBuffer(buf).order());
    }

    @Test
    public void shouldReturnReadOnlyDerivedBuffer() {
        ByteBuf buf = unmodifiableBuffer(buffer(1));
        assertTrue(buf.duplicate() instanceof ReadOnlyByteBuf);
        assertTrue(buf.slice() instanceof ReadOnlyByteBuf);
        assertTrue(buf.slice(0, 1) instanceof ReadOnlyByteBuf);
        assertTrue(buf.duplicate() instanceof ReadOnlyByteBuf);
    }

    @Test
    public void shouldReturnWritableCopy() {
        ByteBuf buf = unmodifiableBuffer(buffer(1));
        assertFalse(buf.copy() instanceof ReadOnlyByteBuf);
    }

    @Test
    public void shouldForwardReadCallsBlindly() throws Exception {
        ByteBuf buf = mock(ByteBuf.class);
        when(buf.order()).thenReturn(BIG_ENDIAN);
        when(buf.maxCapacity()).thenReturn(65536);
        when(buf.readerIndex()).thenReturn(0);
        when(buf.writerIndex()).thenReturn(0);
        when(buf.capacity()).thenReturn(0);

        when(buf.getBytes(1, (GatheringByteChannel) null, 2)).thenReturn(3);
        when(buf.getBytes(4, (OutputStream) null, 5)).thenReturn(buf);
        when(buf.getBytes(6, (byte[]) null, 7, 8)).thenReturn(buf);
        when(buf.getBytes(9, (ByteBuf) null, 10, 11)).thenReturn(buf);
        when(buf.getBytes(12, (ByteBuffer) null)).thenReturn(buf);
        when(buf.getByte(13)).thenReturn(Byte.valueOf((byte) 14));
        when(buf.getShort(15)).thenReturn(Short.valueOf((short) 16));
        when(buf.getUnsignedMedium(17)).thenReturn(18);
        when(buf.getInt(19)).thenReturn(20);
        when(buf.getLong(21)).thenReturn(22L);

        ByteBuffer bb = ByteBuffer.allocate(100);

        when(buf.nioBuffer(23, 24)).thenReturn(bb);
        when(buf.capacity()).thenReturn(27);

        ByteBuf roBuf = unmodifiableBuffer(buf);
        assertEquals(3, roBuf.getBytes(1, (GatheringByteChannel) null, 2));
        roBuf.getBytes(4, (OutputStream) null, 5);
        roBuf.getBytes(6, (byte[]) null, 7, 8);
        roBuf.getBytes(9, (ByteBuf) null, 10, 11);
        roBuf.getBytes(12, (ByteBuffer) null);
        assertEquals((byte) 14, roBuf.getByte(13));
        assertEquals((short) 16, roBuf.getShort(15));
        assertEquals(18, roBuf.getUnsignedMedium(17));
        assertEquals(20, roBuf.getInt(19));
        assertEquals(22L, roBuf.getLong(21));

        ByteBuffer roBB = roBuf.nioBuffer(23, 24);
        assertEquals(100, roBB.capacity());
        assertTrue(roBB.isReadOnly());

        assertEquals(27, roBuf.capacity());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectDiscardReadBytes() {
        unmodifiableBuffer(EMPTY_BUFFER).discardReadBytes();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetByte() {
        unmodifiableBuffer(EMPTY_BUFFER).setByte(0, (byte) 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetShort() {
        unmodifiableBuffer(EMPTY_BUFFER).setShort(0, (short) 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetMedium() {
        unmodifiableBuffer(EMPTY_BUFFER).setMedium(0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetInt() {
        unmodifiableBuffer(EMPTY_BUFFER).setInt(0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetLong() {
        unmodifiableBuffer(EMPTY_BUFFER).setLong(0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes1() throws IOException {
        unmodifiableBuffer(EMPTY_BUFFER).setBytes(0, (InputStream) null, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes2() throws IOException {
        unmodifiableBuffer(EMPTY_BUFFER).setBytes(0, (ScatteringByteChannel) null, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes3() {
        unmodifiableBuffer(EMPTY_BUFFER).setBytes(0, (byte[]) null, 0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes4() {
        unmodifiableBuffer(EMPTY_BUFFER).setBytes(0, (ByteBuf) null, 0, 0);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldRejectSetBytes5() {
        unmodifiableBuffer(EMPTY_BUFFER).setBytes(0, (ByteBuffer) null);
    }

    public void shouldIndicateNotWriteable() {
        assertFalse(unmodifiableBuffer(buffer(1)).isWritable());
    }

    public void shouldIndicteNotWritableAnyNumber() {
        assertFalse(unmodifiableBuffer(buffer(1)).isWritable(1));
    }
}
