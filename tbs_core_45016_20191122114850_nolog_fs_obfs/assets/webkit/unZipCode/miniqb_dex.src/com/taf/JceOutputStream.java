package com.taf;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JceOutputStream
{
  protected String a;
  private ByteBuffer a;
  
  public JceOutputStream()
  {
    this(128);
  }
  
  public JceOutputStream(int paramInt)
  {
    this.jdField_a_of_type_JavaLangString = "UTF-8";
    this.jdField_a_of_type_JavaNioByteBuffer = ByteBuffer.allocate(paramInt);
  }
  
  public JceOutputStream(ByteBuffer paramByteBuffer)
  {
    this.jdField_a_of_type_JavaLangString = "UTF-8";
    this.jdField_a_of_type_JavaNioByteBuffer = paramByteBuffer;
  }
  
  private void a(Object[] paramArrayOfObject, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfObject.length, 0);
    int i = paramArrayOfObject.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfObject[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public byte[] copyByteArray()
  {
    byte[] arrayOfByte = new byte[this.jdField_a_of_type_JavaNioByteBuffer.position()];
    System.arraycopy(this.jdField_a_of_type_JavaNioByteBuffer.array(), 0, arrayOfByte, 0, this.jdField_a_of_type_JavaNioByteBuffer.position());
    return arrayOfByte;
  }
  
  public ByteBuffer getByteBuffer()
  {
    return this.jdField_a_of_type_JavaNioByteBuffer;
  }
  
  public void reInit()
  {
    this.jdField_a_of_type_JavaLangString = "UTF-8";
    this.jdField_a_of_type_JavaNioByteBuffer.clear();
  }
  
  public void reserve(int paramInt)
  {
    if (this.jdField_a_of_type_JavaNioByteBuffer.remaining() < paramInt)
    {
      int i = this.jdField_a_of_type_JavaNioByteBuffer.capacity();
      try
      {
        ByteBuffer localByteBuffer = ByteBuffer.allocate((i + paramInt) * 2);
        localByteBuffer.put(this.jdField_a_of_type_JavaNioByteBuffer.array(), 0, this.jdField_a_of_type_JavaNioByteBuffer.position());
        this.jdField_a_of_type_JavaNioByteBuffer = localByteBuffer;
        return;
      }
      catch (IllegalArgumentException localIllegalArgumentException)
      {
        throw localIllegalArgumentException;
      }
    }
  }
  
  public int setServerEncoding(String paramString)
  {
    this.jdField_a_of_type_JavaLangString = paramString;
    return 0;
  }
  
  public byte[] toByteArray()
  {
    byte[] arrayOfByte = new byte[this.jdField_a_of_type_JavaNioByteBuffer.position()];
    System.arraycopy(this.jdField_a_of_type_JavaNioByteBuffer.array(), 0, arrayOfByte, 0, this.jdField_a_of_type_JavaNioByteBuffer.position());
    return arrayOfByte;
  }
  
  public void write(byte paramByte, int paramInt)
  {
    reserve(3);
    if (paramByte == 0)
    {
      writeHead((byte)12, paramInt);
      return;
    }
    writeHead((byte)0, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.put(paramByte);
  }
  
  public void write(double paramDouble, int paramInt)
  {
    reserve(10);
    writeHead((byte)5, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.putDouble(paramDouble);
  }
  
  public void write(float paramFloat, int paramInt)
  {
    reserve(6);
    writeHead((byte)4, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.putFloat(paramFloat);
  }
  
  public void write(int paramInt1, int paramInt2)
  {
    reserve(6);
    if ((paramInt1 >= 32768) && (paramInt1 <= 32767))
    {
      write((short)paramInt1, paramInt2);
      return;
    }
    writeHead((byte)2, paramInt2);
    this.jdField_a_of_type_JavaNioByteBuffer.putInt(paramInt1);
  }
  
  public void write(long paramLong, int paramInt)
  {
    reserve(10);
    if ((paramLong >= -2147483648L) && (paramLong <= 2147483647L))
    {
      write((int)paramLong, paramInt);
      return;
    }
    writeHead((byte)3, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.putLong(paramLong);
  }
  
  public void write(JceStruct paramJceStruct, int paramInt)
  {
    reserve(2);
    writeHead((byte)10, paramInt);
    paramJceStruct.writeTo(this);
    reserve(2);
    writeHead((byte)11, 0);
  }
  
  public void write(Boolean paramBoolean, int paramInt)
  {
    write(paramBoolean.booleanValue(), paramInt);
  }
  
  public void write(Byte paramByte, int paramInt)
  {
    write(paramByte.byteValue(), paramInt);
  }
  
  public void write(Double paramDouble, int paramInt)
  {
    write(paramDouble.doubleValue(), paramInt);
  }
  
  public void write(Float paramFloat, int paramInt)
  {
    write(paramFloat.floatValue(), paramInt);
  }
  
  public void write(Integer paramInteger, int paramInt)
  {
    write(paramInteger.intValue(), paramInt);
  }
  
  public void write(Long paramLong, int paramInt)
  {
    write(paramLong.longValue(), paramInt);
  }
  
  public void write(Object paramObject, int paramInt)
  {
    if ((paramObject instanceof Byte))
    {
      write(((Byte)paramObject).byteValue(), paramInt);
      return;
    }
    if ((paramObject instanceof Boolean))
    {
      write(((Boolean)paramObject).booleanValue(), paramInt);
      return;
    }
    if ((paramObject instanceof Short))
    {
      write(((Short)paramObject).shortValue(), paramInt);
      return;
    }
    if ((paramObject instanceof Integer))
    {
      write(((Integer)paramObject).intValue(), paramInt);
      return;
    }
    if ((paramObject instanceof Long))
    {
      write(((Long)paramObject).longValue(), paramInt);
      return;
    }
    if ((paramObject instanceof Float))
    {
      write(((Float)paramObject).floatValue(), paramInt);
      return;
    }
    if ((paramObject instanceof Double))
    {
      write(((Double)paramObject).doubleValue(), paramInt);
      return;
    }
    if ((paramObject instanceof String))
    {
      write((String)paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof Map))
    {
      write((Map)paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof List))
    {
      write((List)paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof JceStruct))
    {
      write((JceStruct)paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof byte[]))
    {
      write((byte[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof boolean[]))
    {
      write((boolean[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof short[]))
    {
      write((short[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof int[]))
    {
      write((int[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof long[]))
    {
      write((long[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof float[]))
    {
      write((float[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof double[]))
    {
      write((double[])paramObject, paramInt);
      return;
    }
    if (paramObject.getClass().isArray())
    {
      a((Object[])paramObject, paramInt);
      return;
    }
    if ((paramObject instanceof Collection))
    {
      write((Collection)paramObject, paramInt);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("write object error: unsupport type. ");
    localStringBuilder.append(paramObject.getClass());
    throw new JceEncodeException(localStringBuilder.toString());
  }
  
  public void write(Short paramShort, int paramInt)
  {
    write(paramShort.shortValue(), paramInt);
  }
  
  public void write(String paramString, int paramInt)
  {
    try
    {
      byte[] arrayOfByte = paramString.getBytes(this.jdField_a_of_type_JavaLangString);
      paramString = arrayOfByte;
    }
    catch (UnsupportedEncodingException localUnsupportedEncodingException)
    {
      for (;;) {}
    }
    paramString = paramString.getBytes();
    reserve(paramString.length + 10);
    if (paramString.length > 255)
    {
      writeHead((byte)7, paramInt);
      this.jdField_a_of_type_JavaNioByteBuffer.putInt(paramString.length);
      this.jdField_a_of_type_JavaNioByteBuffer.put(paramString);
      return;
    }
    writeHead((byte)6, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.put((byte)paramString.length);
    this.jdField_a_of_type_JavaNioByteBuffer.put(paramString);
  }
  
  public <T> void write(Collection<T> paramCollection, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    if (paramCollection == null) {
      paramInt = 0;
    } else {
      paramInt = paramCollection.size();
    }
    write(paramInt, 0);
    if (paramCollection != null)
    {
      paramCollection = paramCollection.iterator();
      while (paramCollection.hasNext()) {
        write(paramCollection.next(), 0);
      }
    }
  }
  
  public <K, V> void write(Map<K, V> paramMap, int paramInt)
  {
    reserve(8);
    writeHead((byte)8, paramInt);
    if (paramMap == null) {
      paramInt = 0;
    } else {
      paramInt = paramMap.size();
    }
    write(paramInt, 0);
    if (paramMap != null)
    {
      paramMap = paramMap.entrySet().iterator();
      while (paramMap.hasNext())
      {
        Map.Entry localEntry = (Map.Entry)paramMap.next();
        write(localEntry.getKey(), 0);
        write(localEntry.getValue(), 1);
      }
    }
  }
  
  public void write(short paramShort, int paramInt)
  {
    reserve(4);
    if ((paramShort >= -128) && (paramShort <= 127))
    {
      write((byte)paramShort, paramInt);
      return;
    }
    writeHead((byte)1, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.putShort(paramShort);
  }
  
  public void write(boolean paramBoolean, int paramInt)
  {
    write((byte)paramBoolean, paramInt);
  }
  
  public void write(byte[] paramArrayOfByte, int paramInt)
  {
    reserve(paramArrayOfByte.length + 8);
    writeHead((byte)13, paramInt);
    writeHead((byte)0, 0);
    write(paramArrayOfByte.length, 0);
    this.jdField_a_of_type_JavaNioByteBuffer.put(paramArrayOfByte);
  }
  
  public void write(double[] paramArrayOfDouble, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfDouble.length, 0);
    int i = paramArrayOfDouble.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfDouble[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public void write(float[] paramArrayOfFloat, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfFloat.length, 0);
    int i = paramArrayOfFloat.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfFloat[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public void write(int[] paramArrayOfInt, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfInt.length, 0);
    int i = paramArrayOfInt.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfInt[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public void write(long[] paramArrayOfLong, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfLong.length, 0);
    int i = paramArrayOfLong.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfLong[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public <T> void write(T[] paramArrayOfT, int paramInt)
  {
    a(paramArrayOfT, paramInt);
  }
  
  public void write(short[] paramArrayOfShort, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfShort.length, 0);
    int i = paramArrayOfShort.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfShort[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public void write(boolean[] paramArrayOfBoolean, int paramInt)
  {
    reserve(8);
    writeHead((byte)9, paramInt);
    write(paramArrayOfBoolean.length, 0);
    int i = paramArrayOfBoolean.length;
    paramInt = 0;
    while (paramInt < i)
    {
      write(paramArrayOfBoolean[paramInt], 0);
      paramInt += 1;
    }
  }
  
  public void writeByteString(String paramString, int paramInt)
  {
    reserve(paramString.length() + 10);
    paramString = HexUtil.hexStr2Bytes(paramString);
    if (paramString.length > 255)
    {
      writeHead((byte)7, paramInt);
      this.jdField_a_of_type_JavaNioByteBuffer.putInt(paramString.length);
      this.jdField_a_of_type_JavaNioByteBuffer.put(paramString);
      return;
    }
    writeHead((byte)6, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.put((byte)paramString.length);
    this.jdField_a_of_type_JavaNioByteBuffer.put(paramString);
  }
  
  public void writeHead(byte paramByte, int paramInt)
  {
    byte b;
    if (paramInt < 15)
    {
      b = (byte)(paramByte | paramInt << 4);
      this.jdField_a_of_type_JavaNioByteBuffer.put(b);
      return;
    }
    if (paramInt < 256)
    {
      b = (byte)(paramByte | 0xF0);
      this.jdField_a_of_type_JavaNioByteBuffer.put(b);
      this.jdField_a_of_type_JavaNioByteBuffer.put((byte)paramInt);
      return;
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("tag is too large: ");
    localStringBuilder.append(paramInt);
    throw new JceEncodeException(localStringBuilder.toString());
  }
  
  public void writeStringByte(String paramString, int paramInt)
  {
    paramString = HexUtil.hexStr2Bytes(paramString);
    reserve(paramString.length + 10);
    if (paramString.length > 255)
    {
      writeHead((byte)7, paramInt);
      this.jdField_a_of_type_JavaNioByteBuffer.putInt(paramString.length);
      this.jdField_a_of_type_JavaNioByteBuffer.put(paramString);
      return;
    }
    writeHead((byte)6, paramInt);
    this.jdField_a_of_type_JavaNioByteBuffer.put((byte)paramString.length);
    this.jdField_a_of_type_JavaNioByteBuffer.put(paramString);
  }
}


/* Location:              C:\Users\Administrator\Desktop\学习资料\dex2jar\dex2jar-2.0\classes-dex2jar.jar!\com\taf\JceOutputStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */