package com.tencent.common.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build.VERSION;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import com.tencent.basesupport.FLogger;
import com.tencent.common.http.ContentType;
import com.tencent.mtt.ContextHolder;
import com.tencent.mtt.browser.engine.AppBroadcastObserver;
import com.tencent.mtt.browser.engine.AppBroadcastReceiver;
import com.tencent.tbs.common.internal.service.StatServerHolder;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressLint({"NewApi"})
public class FileUtils
{
  public static final String APP_DEFAULT_FOLDER_NAME = ".Application";
  public static final String DIR_DATA = "data";
  public static final String DIR_DYNAMIC_JAR_OUTPUT = "dynamic_jar_output";
  public static final String DIR_DYNAMIC_SO_OUTPUT = "dynamic_so_output";
  public static String DIR_EXT_MAIN = "QQBrowser";
  public static final String DL_FILE_SUFFIX = ".qbdltmp";
  public static int ERR_BASE = -1000;
  public static int ERR_SAVE_IMAGE_FAILED = 0;
  public static int ERR_SDCARD_NOT_AVAILABLE = 0;
  public static final byte QQBROWSER_POSITION_DATA = 2;
  public static final byte QQBROWSER_POSITION_NONE = 0;
  public static final byte QQBROWSER_POSITION_ROOT = 1;
  public static int SUCCESS = 0;
  public static final int TYPE_DATA = 2;
  public static final int TYPE_DEFAUT = 0;
  public static final int TYPE_SDCARD = 1;
  static byte jdField_a_of_type_Byte = 0;
  private static Context jdField_a_of_type_AndroidContentContext;
  private static AppBroadcastObserver jdField_a_of_type_ComTencentMttBrowserEngineAppBroadcastObserver = new AppBroadcastObserver()
  {
    public void onBroadcastReceiver(Intent paramAnonymousIntent)
    {
      if (paramAnonymousIntent == null) {
        return;
      }
      paramAnonymousIntent = paramAnonymousIntent.getAction();
      if (("android.intent.action.MEDIA_UNMOUNTED".equalsIgnoreCase(paramAnonymousIntent)) || ("android.intent.action.MEDIA_MOUNTED".equalsIgnoreCase(paramAnonymousIntent)) || ("android.intent.action.MEDIA_EJECT".equalsIgnoreCase(paramAnonymousIntent)) || ("android.intent.action.MEDIA_REMOVED".equalsIgnoreCase(paramAnonymousIntent)) || ("android.intent.action.MEDIA_BAD_REMOVAL".equalsIgnoreCase(paramAnonymousIntent)))
      {
        FLogger.d("FileUtils", "sdcardChanged");
        FileUtils.jdField_a_of_type_Boolean = false;
        FileUtils.jdField_b_of_type_Boolean = false;
        FileUtils.jdField_a_of_type_JavaIoFile = null;
        FileUtils.jdField_b_of_type_JavaIoFile = null;
        FileUtils.c = null;
        FileUtils.jdField_a_of_type_Byte = 0;
      }
    }
  };
  static File jdField_a_of_type_JavaIoFile;
  private static Object jdField_a_of_type_JavaLangObject;
  private static HashMap<String, ContentType> jdField_a_of_type_JavaUtilHashMap;
  private static Pattern jdField_a_of_type_JavaUtilRegexPattern;
  static boolean jdField_a_of_type_Boolean;
  static File jdField_b_of_type_JavaIoFile;
  private static Object jdField_b_of_type_JavaLangObject;
  private static Pattern jdField_b_of_type_JavaUtilRegexPattern;
  static boolean jdField_b_of_type_Boolean;
  static File jdField_c_of_type_JavaIoFile;
  public static FileUtils sInstance;
  private int jdField_a_of_type_Int = 0;
  private byte[] jdField_a_of_type_ArrayOfByte = new byte['က'];
  private final ByteBuffer[] jdField_a_of_type_ArrayOfJavaNioByteBuffer;
  private int jdField_b_of_type_Int = 0;
  private int jdField_c_of_type_Int = 0;
  private boolean jdField_c_of_type_Boolean = false;
  private int d = 0;
  public int mByteBufferPoolAvailableSize;
  
  static
  {
    int i = ERR_BASE;
    ERR_SDCARD_NOT_AVAILABLE = i - 1;
    ERR_SAVE_IMAGE_FAILED = i - 2;
    jdField_a_of_type_JavaUtilHashMap = null;
    jdField_a_of_type_JavaLangObject = new Object();
    jdField_a_of_type_AndroidContentContext = null;
    sInstance = null;
    jdField_b_of_type_JavaLangObject = new Object();
    jdField_a_of_type_Boolean = false;
    jdField_b_of_type_Boolean = false;
    jdField_a_of_type_JavaIoFile = null;
    jdField_b_of_type_JavaIoFile = null;
    jdField_c_of_type_JavaIoFile = null;
  }
  
  private FileUtils(int paramInt)
  {
    if (paramInt > 0)
    {
      this.jdField_b_of_type_Int = paramInt;
      this.jdField_a_of_type_ArrayOfJavaNioByteBuffer = new ByteBuffer[paramInt];
      return;
    }
    throw new IllegalArgumentException("The max pool size must be > 0");
  }
  
  private ByteBuffer a()
  {
    synchronized (this.jdField_a_of_type_ArrayOfJavaNioByteBuffer)
    {
      if (this.jdField_c_of_type_Int != 0) {
        int i = this.jdField_c_of_type_Int;
      }
      this.jdField_c_of_type_Int += 1;
      if (this.mByteBufferPoolAvailableSize > 0)
      {
        this.d += 1;
        localByteBuffer = b();
        return localByteBuffer;
      }
      if (this.jdField_a_of_type_Int < this.jdField_b_of_type_Int)
      {
        this.d += 1;
        localByteBuffer = ByteBuffer.allocate(4096);
        this.jdField_a_of_type_ArrayOfJavaNioByteBuffer[this.mByteBufferPoolAvailableSize] = localByteBuffer;
        this.mByteBufferPoolAvailableSize += 1;
        this.jdField_a_of_type_Int += 1;
        localByteBuffer = b();
        return localByteBuffer;
      }
      ByteBuffer localByteBuffer = ByteBuffer.allocate(4096);
      return localByteBuffer;
    }
  }
  
  private static ByteBuffer a(ByteBuffer paramByteBuffer, int paramInt)
  {
    int i;
    if (paramByteBuffer.remaining() < paramInt)
    {
      i = paramByteBuffer.capacity();
      paramInt /= 2048;
    }
    try
    {
      ByteBuffer localByteBuffer = ByteBuffer.allocate(i + (paramInt + 1) * 2048);
      localByteBuffer.put(paramByteBuffer.array(), 0, paramByteBuffer.position());
      return localByteBuffer;
    }
    catch (Throwable localThrowable) {}
    return paramByteBuffer;
    return paramByteBuffer;
  }
  
  private static HashMap<String, ContentType> a()
  {
    synchronized (jdField_a_of_type_JavaLangObject)
    {
      Object localObject2;
      if (jdField_a_of_type_JavaUtilHashMap != null)
      {
        localObject2 = jdField_a_of_type_JavaUtilHashMap;
        return (HashMap<String, ContentType>)localObject2;
      }
      if (jdField_a_of_type_JavaUtilHashMap == null)
      {
        jdField_a_of_type_JavaUtilHashMap = new HashMap();
        localObject2 = new ContentType("text", "html", "utf-8");
        jdField_a_of_type_JavaUtilHashMap.put("html", localObject2);
        jdField_a_of_type_JavaUtilHashMap.put("htm", localObject2);
        jdField_a_of_type_JavaUtilHashMap.put("txt", new ContentType("text", "plain", "utf-8"));
        jdField_a_of_type_JavaUtilHashMap.put("css", new ContentType("text", "css", "utf-8"));
        jdField_a_of_type_JavaUtilHashMap.put("js", new ContentType("text", "javascript", "utf-8"));
        jdField_a_of_type_JavaUtilHashMap.put("png", new ContentType("image", "png", "binary"));
        localObject2 = new ContentType("image", "jpeg", "binary");
        jdField_a_of_type_JavaUtilHashMap.put("jpg", localObject2);
        jdField_a_of_type_JavaUtilHashMap.put("jpeg", localObject2);
        jdField_a_of_type_JavaUtilHashMap.put("gif", new ContentType("image", "gif", "binary"));
      }
      return jdField_a_of_type_JavaUtilHashMap;
    }
  }
  
  private static Pattern a()
  {
    if (jdField_a_of_type_JavaUtilRegexPattern == null) {
      jdField_a_of_type_JavaUtilRegexPattern = Pattern.compile("^(.*)\\((\\d+)\\)$", 2);
    }
    return jdField_a_of_type_JavaUtilRegexPattern;
  }
  
  private static void a()
  {
    synchronized (jdField_b_of_type_JavaLangObject)
    {
      if (!jdField_b_of_type_Boolean)
      {
        FLogger.d("FileUtils", "initDataDir called here", new Throwable("initDataDir called here"));
        Object localObject1 = SdCardInfo.Utils.getSDcardInfo(ContextHolder.getAppContext());
        int i;
        Object localObject6;
        if ((jdField_b_of_type_JavaIoFile == null) && (((SdCardInfo)localObject1).hasInternalSD()))
        {
          Object localObject5 = new File(((SdCardInfo)localObject1).internalStorage.path, DIR_EXT_MAIN);
          i = Integer.parseInt(Build.VERSION.SDK);
          if (isFolderWritable((File)localObject5)) {
            jdField_b_of_type_JavaIoFile = (File)localObject5;
          } else if (i >= 19) {
            try
            {
              localObject5 = ContextHolder.getAppContext().getExternalFilesDir(null);
              if ((localObject5 != null) && (((File)localObject5).exists()))
              {
                localObject5 = ((File)localObject5).getAbsolutePath();
                if (((String)localObject5).startsWith(((SdCardInfo)localObject1).internalStorage.path))
                {
                  localObject6 = new StringBuilder();
                  ((StringBuilder)localObject6).append((String)localObject5);
                  ((StringBuilder)localObject6).append(File.separator);
                  ((StringBuilder)localObject6).append(DIR_EXT_MAIN);
                  localObject5 = new File(((StringBuilder)localObject6).toString());
                  if (isFolderWritable((File)localObject5)) {
                    jdField_b_of_type_JavaIoFile = (File)localObject5;
                  }
                }
              }
            }
            catch (Throwable localThrowable2)
            {
              localThrowable2.printStackTrace();
            }
          }
        }
        Iterator localIterator = ((SdCardInfo)localObject1).extStorage.iterator();
        while (localIterator.hasNext())
        {
          localObject6 = (StorageInfo)localIterator.next();
          localObject1 = new File(((StorageInfo)localObject6).path, DIR_EXT_MAIN);
          i = Integer.parseInt(Build.VERSION.SDK);
          if (((StorageInfo)localObject6).isWritable)
          {
            jdField_c_of_type_JavaIoFile = (File)localObject1;
            jdField_a_of_type_Byte = 1;
            break;
          }
          if (i >= 19)
          {
            try
            {
              localObject1 = ContextHolder.getAppContext().getExternalFilesDir(null);
              if (localObject1 != null) {
                localObject1 = ((File)localObject1).getAbsolutePath();
              } else {
                localObject1 = null;
              }
            }
            catch (Throwable localThrowable1)
            {
              localThrowable1.printStackTrace();
              localObject2 = null;
            }
            Object localObject7 = new StringBuilder();
            ((StringBuilder)localObject7).append(((StorageInfo)localObject6).path);
            ((StringBuilder)localObject7).append(File.separator);
            ((StringBuilder)localObject7).append("Android");
            ((StringBuilder)localObject7).append(File.separator);
            ((StringBuilder)localObject7).append("data");
            ((StringBuilder)localObject7).append(File.separator);
            ((StringBuilder)localObject7).append(ContextHolder.getAppContext().getPackageName());
            ((StringBuilder)localObject7).append(File.separator);
            ((StringBuilder)localObject7).append("files");
            ((StringBuilder)localObject7).append(File.separator);
            ((StringBuilder)localObject7).append(DIR_EXT_MAIN);
            localObject6 = ((StringBuilder)localObject7).toString();
            localObject7 = new File((String)localObject6);
            if (isFolderWritable((File)localObject7))
            {
              jdField_c_of_type_JavaIoFile = (File)localObject7;
              jdField_a_of_type_Byte = 2;
              break;
            }
            if (!TextUtils.isEmpty((CharSequence)localObject2))
            {
              StatServerHolder.userBehaviorStatistics("AHNG708_1");
              localObject7 = new StringBuilder();
              ((StringBuilder)localObject7).append((String)localObject2);
              ((StringBuilder)localObject7).append(";");
              ((StringBuilder)localObject7).append((String)localObject6);
              FLogger.d("SDCardInfo", ((StringBuilder)localObject7).toString());
            }
          }
        }
        if (hasSDcard())
        {
          localObject2 = getSDcardDir();
          if ((localObject2 != null) && (((File)localObject2).exists()))
          {
            localObject2 = createDir((File)localObject2, DIR_EXT_MAIN);
            if (localObject2 != null) {
              if (((File)localObject2).equals(jdField_b_of_type_JavaIoFile)) {
                jdField_a_of_type_JavaIoFile = jdField_b_of_type_JavaIoFile;
              } else if (((File)localObject2).equals(jdField_c_of_type_JavaIoFile)) {
                jdField_a_of_type_JavaIoFile = jdField_c_of_type_JavaIoFile;
              } else if (isFolderWritable((File)localObject2)) {
                jdField_a_of_type_JavaIoFile = (File)localObject2;
              }
            }
          }
        }
        if (jdField_a_of_type_JavaIoFile == null)
        {
          if (jdField_b_of_type_JavaIoFile != null) {
            localObject2 = jdField_b_of_type_JavaIoFile;
          } else {
            localObject2 = jdField_c_of_type_JavaIoFile;
          }
          jdField_a_of_type_JavaIoFile = (File)localObject2;
        }
        AppBroadcastReceiver.getInstance().addBroadcastObserver(jdField_a_of_type_ComTencentMttBrowserEngineAppBroadcastObserver);
        jdField_b_of_type_Boolean = true;
        Object localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append("mAvailableQQBrowser=");
        ((StringBuilder)localObject2).append(jdField_a_of_type_JavaIoFile);
        FLogger.i("FileUtils", ((StringBuilder)localObject2).toString());
        if (jdField_a_of_type_JavaIoFile != null)
        {
          localObject2 = new File(jdField_a_of_type_JavaIoFile, ".nomedia");
          if (((File)localObject2).exists()) {
            deleteQuietly((File)localObject2);
          }
        }
      }
      return;
    }
  }
  
  private boolean a(ByteBuffer paramByteBuffer)
  {
    int i = 0;
    while (i < this.mByteBufferPoolAvailableSize)
    {
      if (this.jdField_a_of_type_ArrayOfJavaNioByteBuffer[i] == paramByteBuffer) {
        return true;
      }
      i += 1;
    }
    return false;
  }
  
  private ByteBuffer b()
  {
    int i = this.mByteBufferPoolAvailableSize;
    int j = i - 1;
    ByteBuffer[] arrayOfByteBuffer = this.jdField_a_of_type_ArrayOfJavaNioByteBuffer;
    ByteBuffer localByteBuffer = arrayOfByteBuffer[j];
    arrayOfByteBuffer[j] = null;
    this.mByteBufferPoolAvailableSize = (i - 1);
    return localByteBuffer;
  }
  
  private static Pattern b()
  {
    if (jdField_b_of_type_JavaUtilRegexPattern == null) {
      jdField_b_of_type_JavaUtilRegexPattern = Pattern.compile("[\\\\\\/\\:\\*\\?\\\"\\|\\<\\>]", 2);
    }
    return jdField_b_of_type_JavaUtilRegexPattern;
  }
  
  public static boolean checkFileName(String paramString)
  {
    return b().matcher(paramString).find() ^ true;
  }
  
  public static void cleanDirectory(File paramFile)
    throws IOException, IllegalArgumentException
  {
    if (paramFile == null) {
      return;
    }
    if (paramFile.exists())
    {
      if (paramFile.isDirectory())
      {
        File localFile = null;
        try
        {
          File[] arrayOfFile = paramFile.listFiles();
        }
        catch (Error localError)
        {
          localError.printStackTrace();
          localStringBuilder = null;
        }
        if (localStringBuilder != null)
        {
          int j = localStringBuilder.length;
          int i = 0;
          paramFile = localFile;
          while (i < j)
          {
            localFile = localStringBuilder[i];
            try
            {
              delete(localFile);
            }
            catch (IOException paramFile) {}
            i += 1;
          }
          if (paramFile == null) {
            return;
          }
          throw paramFile;
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("Failed to list contents of ");
        localStringBuilder.append(paramFile);
        throw new IOException(localStringBuilder.toString());
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramFile);
      localStringBuilder.append(" is not a directory");
      throw new IllegalArgumentException(localStringBuilder.toString());
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(paramFile);
    localStringBuilder.append(" does not exist");
    throw new IllegalArgumentException(localStringBuilder.toString());
  }
  
  public static void closeQuietly(Closeable paramCloseable)
  {
    if (paramCloseable == null) {
      return;
    }
    try
    {
      paramCloseable.close();
      return;
    }
    catch (Exception paramCloseable)
    {
      paramCloseable.printStackTrace();
    }
  }
  
  public static int copy(InputStream paramInputStream, OutputStream paramOutputStream)
    throws IOException, OutOfMemoryError
  {
    if (paramInputStream == null) {
      return -1;
    }
    byte[] arrayOfByte = getInstance().accqureByteArray();
    int i;
    for (long l = 0L;; l += i)
    {
      i = paramInputStream.read(arrayOfByte);
      if (-1 == i) {
        break;
      }
      paramOutputStream.write(arrayOfByte, 0, i);
    }
    getInstance().releaseByteArray(arrayOfByte);
    if (l > 2147483647L) {
      return -1;
    }
    return (int)l;
  }
  
  /* Error */
  public static boolean copyAssetsFileTo(Context paramContext, String paramString, File paramFile)
    throws IOException
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 5
    //   3: iconst_0
    //   4: istore 6
    //   6: aload_0
    //   7: ifnull +249 -> 256
    //   10: aload_1
    //   11: invokestatic 343	android/text/TextUtils:isEmpty	(Ljava/lang/CharSequence;)Z
    //   14: ifne +242 -> 256
    //   17: aload_2
    //   18: ifnonnull +5 -> 23
    //   21: iconst_0
    //   22: ireturn
    //   23: aconst_null
    //   24: astore 8
    //   26: aconst_null
    //   27: astore 7
    //   29: aconst_null
    //   30: astore 9
    //   32: aload_0
    //   33: invokevirtual 482	android/content/Context:getApplicationContext	()Landroid/content/Context;
    //   36: invokevirtual 486	android/content/Context:getAssets	()Landroid/content/res/AssetManager;
    //   39: aload_1
    //   40: invokevirtual 492	android/content/res/AssetManager:open	(Ljava/lang/String;)Ljava/io/InputStream;
    //   43: astore_0
    //   44: aload 8
    //   46: astore_1
    //   47: aload_0
    //   48: astore 7
    //   50: new 494	java/io/FileOutputStream
    //   53: dup
    //   54: aload_2
    //   55: invokespecial 496	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   58: astore_2
    //   59: ldc_w 497
    //   62: newarray <illegal type>
    //   64: astore_1
    //   65: aload_0
    //   66: aload_1
    //   67: invokevirtual 465	java/io/InputStream:read	([B)I
    //   70: istore_3
    //   71: iload_3
    //   72: iconst_m1
    //   73: if_icmpeq +13 -> 86
    //   76: aload_2
    //   77: aload_1
    //   78: iconst_0
    //   79: iload_3
    //   80: invokevirtual 498	java/io/FileOutputStream:write	([BII)V
    //   83: goto -18 -> 65
    //   86: aload_2
    //   87: invokevirtual 501	java/io/FileOutputStream:flush	()V
    //   90: iconst_1
    //   91: istore 4
    //   93: iconst_1
    //   94: istore 5
    //   96: aload_2
    //   97: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   100: goto +8 -> 108
    //   103: astore_1
    //   104: aload_1
    //   105: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   108: aload_0
    //   109: ifnull +106 -> 215
    //   112: iload 5
    //   114: istore 4
    //   116: aload_0
    //   117: invokevirtual 504	java/io/InputStream:close	()V
    //   120: iconst_1
    //   121: ireturn
    //   122: astore_0
    //   123: aload_0
    //   124: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   127: iload 4
    //   129: ireturn
    //   130: astore 7
    //   132: aload_2
    //   133: astore_1
    //   134: aload 7
    //   136: astore_2
    //   137: goto +85 -> 222
    //   140: astore 8
    //   142: goto +27 -> 169
    //   145: astore 8
    //   147: aload 9
    //   149: astore_2
    //   150: goto +19 -> 169
    //   153: astore_2
    //   154: aconst_null
    //   155: astore_0
    //   156: aload 7
    //   158: astore_1
    //   159: goto +63 -> 222
    //   162: astore 8
    //   164: aconst_null
    //   165: astore_0
    //   166: aload 9
    //   168: astore_2
    //   169: aload_2
    //   170: astore_1
    //   171: aload_0
    //   172: astore 7
    //   174: aload 8
    //   176: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   179: aload_2
    //   180: ifnull +15 -> 195
    //   183: aload_2
    //   184: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   187: goto +8 -> 195
    //   190: astore_1
    //   191: aload_1
    //   192: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   195: iload 5
    //   197: istore 4
    //   199: aload_0
    //   200: ifnull +15 -> 215
    //   203: iload 6
    //   205: istore 4
    //   207: aload_0
    //   208: invokevirtual 504	java/io/InputStream:close	()V
    //   211: iload 5
    //   213: istore 4
    //   215: iload 4
    //   217: ireturn
    //   218: astore_2
    //   219: aload 7
    //   221: astore_0
    //   222: aload_1
    //   223: ifnull +15 -> 238
    //   226: aload_1
    //   227: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   230: goto +8 -> 238
    //   233: astore_1
    //   234: aload_1
    //   235: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   238: aload_0
    //   239: ifnull +15 -> 254
    //   242: aload_0
    //   243: invokevirtual 504	java/io/InputStream:close	()V
    //   246: goto +8 -> 254
    //   249: astore_0
    //   250: aload_0
    //   251: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   254: aload_2
    //   255: athrow
    //   256: iconst_0
    //   257: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	258	0	paramContext	Context
    //   0	258	1	paramString	String
    //   0	258	2	paramFile	File
    //   70	10	3	i	int
    //   91	125	4	bool1	boolean
    //   1	211	5	bool2	boolean
    //   4	200	6	bool3	boolean
    //   27	22	7	localContext1	Context
    //   130	27	7	localObject1	Object
    //   172	48	7	localContext2	Context
    //   24	21	8	localObject2	Object
    //   140	1	8	localException1	Exception
    //   145	1	8	localException2	Exception
    //   162	13	8	localException3	Exception
    //   30	137	9	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   96	100	103	java/io/IOException
    //   116	120	122	java/io/IOException
    //   207	211	122	java/io/IOException
    //   59	65	130	finally
    //   65	71	130	finally
    //   76	83	130	finally
    //   86	90	130	finally
    //   59	65	140	java/lang/Exception
    //   65	71	140	java/lang/Exception
    //   76	83	140	java/lang/Exception
    //   86	90	140	java/lang/Exception
    //   50	59	145	java/lang/Exception
    //   32	44	153	finally
    //   32	44	162	java/lang/Exception
    //   183	187	190	java/io/IOException
    //   50	59	218	finally
    //   174	179	218	finally
    //   226	230	233	java/io/IOException
    //   242	246	249	java/io/IOException
  }
  
  public static void copyAssetsFileToEx(String paramString, File paramFile, boolean paramBoolean)
    throws IOException
  {
    if (paramBoolean) {
      deleteQuietly(paramFile);
    }
    copyAssetsFileTo(ContextHolder.getAppContext(), paramString, paramFile);
  }
  
  /* Error */
  public static boolean copyFile(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: ldc 2
    //   2: monitorenter
    //   3: aconst_null
    //   4: astore 6
    //   6: aconst_null
    //   7: astore 9
    //   9: aconst_null
    //   10: astore 7
    //   12: aconst_null
    //   13: astore 8
    //   15: iconst_0
    //   16: istore_3
    //   17: iconst_0
    //   18: istore 5
    //   20: iconst_0
    //   21: istore 4
    //   23: new 243	java/io/File
    //   26: dup
    //   27: aload_0
    //   28: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   31: invokevirtual 281	java/io/File:exists	()Z
    //   34: ifeq +129 -> 163
    //   37: new 512	java/io/FileInputStream
    //   40: dup
    //   41: aload_0
    //   42: invokespecial 513	java/io/FileInputStream:<init>	(Ljava/lang/String;)V
    //   45: astore_0
    //   46: new 494	java/io/FileOutputStream
    //   49: dup
    //   50: aload_1
    //   51: invokespecial 514	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   54: astore_1
    //   55: ldc_w 515
    //   58: newarray <illegal type>
    //   60: astore 6
    //   62: aload_0
    //   63: aload 6
    //   65: invokevirtual 465	java/io/InputStream:read	([B)I
    //   68: istore_2
    //   69: iload_2
    //   70: iconst_m1
    //   71: if_icmpeq +14 -> 85
    //   74: aload_1
    //   75: aload 6
    //   77: iconst_0
    //   78: iload_2
    //   79: invokevirtual 498	java/io/FileOutputStream:write	([BII)V
    //   82: goto -20 -> 62
    //   85: aload_1
    //   86: invokevirtual 501	java/io/FileOutputStream:flush	()V
    //   89: iconst_1
    //   90: istore_3
    //   91: goto +80 -> 171
    //   94: astore 7
    //   96: aload_1
    //   97: astore 6
    //   99: aload 7
    //   101: astore_1
    //   102: goto +29 -> 131
    //   105: astore 6
    //   107: aload_1
    //   108: astore 7
    //   110: aload 6
    //   112: astore_1
    //   113: goto +28 -> 141
    //   116: astore 6
    //   118: aload_1
    //   119: astore 7
    //   121: aload 6
    //   123: astore_1
    //   124: goto +30 -> 154
    //   127: astore_1
    //   128: aconst_null
    //   129: astore 6
    //   131: aload_1
    //   132: astore 7
    //   134: goto +200 -> 334
    //   137: astore_1
    //   138: aconst_null
    //   139: astore 7
    //   141: aload_0
    //   142: astore 6
    //   144: aload_1
    //   145: astore 8
    //   147: goto +80 -> 227
    //   150: astore_1
    //   151: aconst_null
    //   152: astore 7
    //   154: aload_0
    //   155: astore 6
    //   157: aload_1
    //   158: astore 8
    //   160: goto +121 -> 281
    //   163: aconst_null
    //   164: astore_1
    //   165: iload 4
    //   167: istore_3
    //   168: aload 8
    //   170: astore_0
    //   171: aload_0
    //   172: ifnull +10 -> 182
    //   175: aload_0
    //   176: invokevirtual 504	java/io/InputStream:close	()V
    //   179: goto +3 -> 182
    //   182: iload_3
    //   183: istore 4
    //   185: aload_1
    //   186: ifnull +137 -> 323
    //   189: aload_1
    //   190: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   193: iload_3
    //   194: istore 4
    //   196: goto +127 -> 323
    //   199: aload_0
    //   200: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   203: iload_3
    //   204: istore 4
    //   206: goto +117 -> 323
    //   209: astore_1
    //   210: aconst_null
    //   211: astore 6
    //   213: aload 7
    //   215: astore_0
    //   216: aload_1
    //   217: astore 7
    //   219: goto +115 -> 334
    //   222: astore 8
    //   224: aconst_null
    //   225: astore 7
    //   227: aload 6
    //   229: astore_0
    //   230: aload 7
    //   232: astore_1
    //   233: aload 8
    //   235: invokevirtual 516	java/lang/OutOfMemoryError:printStackTrace	()V
    //   238: aload 6
    //   240: ifnull +11 -> 251
    //   243: aload 6
    //   245: invokevirtual 504	java/io/InputStream:close	()V
    //   248: goto +3 -> 251
    //   251: iload 5
    //   253: istore 4
    //   255: aload 7
    //   257: ifnull +66 -> 323
    //   260: aload 7
    //   262: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   265: iload 5
    //   267: istore 4
    //   269: goto +54 -> 323
    //   272: astore 8
    //   274: aconst_null
    //   275: astore 7
    //   277: aload 9
    //   279: astore 6
    //   281: aload 6
    //   283: astore_0
    //   284: aload 7
    //   286: astore_1
    //   287: aload 8
    //   289: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   292: aload 6
    //   294: ifnull +11 -> 305
    //   297: aload 6
    //   299: invokevirtual 504	java/io/InputStream:close	()V
    //   302: goto +3 -> 305
    //   305: iload 5
    //   307: istore 4
    //   309: aload 7
    //   311: ifnull +12 -> 323
    //   314: aload 7
    //   316: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   319: iload 5
    //   321: istore 4
    //   323: ldc 2
    //   325: monitorexit
    //   326: iload 4
    //   328: ireturn
    //   329: astore 7
    //   331: aload_1
    //   332: astore 6
    //   334: aload_0
    //   335: ifnull +10 -> 345
    //   338: aload_0
    //   339: invokevirtual 504	java/io/InputStream:close	()V
    //   342: goto +3 -> 345
    //   345: aload 6
    //   347: ifnull +15 -> 362
    //   350: aload 6
    //   352: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   355: goto +7 -> 362
    //   358: aload_0
    //   359: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   362: aload 7
    //   364: athrow
    //   365: ldc 2
    //   367: monitorexit
    //   368: aload_0
    //   369: athrow
    //   370: astore_0
    //   371: goto -172 -> 199
    //   374: astore_0
    //   375: goto -176 -> 199
    //   378: astore_0
    //   379: goto -14 -> 365
    //   382: astore_0
    //   383: goto -184 -> 199
    //   386: astore_0
    //   387: goto -29 -> 358
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	390	0	paramString1	String
    //   0	390	1	paramString2	String
    //   68	11	2	i	int
    //   16	188	3	bool1	boolean
    //   21	306	4	bool2	boolean
    //   18	302	5	bool3	boolean
    //   4	94	6	localObject1	Object
    //   105	6	6	localOutOfMemoryError1	OutOfMemoryError
    //   116	6	6	localException1	Exception
    //   129	222	6	localObject2	Object
    //   10	1	7	localObject3	Object
    //   94	6	7	localObject4	Object
    //   108	207	7	str1	String
    //   329	34	7	localObject5	Object
    //   13	156	8	str2	String
    //   222	12	8	localOutOfMemoryError2	OutOfMemoryError
    //   272	16	8	localException2	Exception
    //   7	271	9	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   55	62	94	finally
    //   62	69	94	finally
    //   74	82	94	finally
    //   85	89	94	finally
    //   55	62	105	java/lang/OutOfMemoryError
    //   62	69	105	java/lang/OutOfMemoryError
    //   74	82	105	java/lang/OutOfMemoryError
    //   85	89	105	java/lang/OutOfMemoryError
    //   55	62	116	java/lang/Exception
    //   62	69	116	java/lang/Exception
    //   74	82	116	java/lang/Exception
    //   85	89	116	java/lang/Exception
    //   46	55	127	finally
    //   46	55	137	java/lang/OutOfMemoryError
    //   46	55	150	java/lang/Exception
    //   23	46	209	finally
    //   23	46	222	java/lang/OutOfMemoryError
    //   23	46	272	java/lang/Exception
    //   233	238	329	finally
    //   287	292	329	finally
    //   175	179	370	java/lang/Exception
    //   189	193	370	java/lang/Exception
    //   243	248	374	java/lang/Exception
    //   260	265	374	java/lang/Exception
    //   175	179	378	finally
    //   189	193	378	finally
    //   199	203	378	finally
    //   243	248	378	finally
    //   260	265	378	finally
    //   297	302	378	finally
    //   314	319	378	finally
    //   338	342	378	finally
    //   350	355	378	finally
    //   358	362	378	finally
    //   362	365	378	finally
    //   297	302	382	java/lang/Exception
    //   314	319	382	java/lang/Exception
    //   338	342	386	java/lang/Exception
    //   350	355	386	java/lang/Exception
  }
  
  public static void copyFileToSharePrefsDir(Context paramContext, File paramFile1, File paramFile2)
  {
    if (!paramFile1.exists()) {
      return;
    }
    long l = System.currentTimeMillis();
    paramContext = new File(paramContext.getDir("shared_prefs", 0).getParentFile(), "shared_prefs");
    if (!paramContext.exists()) {
      paramContext.mkdirs();
    }
    paramContext = paramFile1.getAbsolutePath();
    paramFile1 = paramFile2.getAbsolutePath();
    copyFile(paramContext, paramFile1);
    paramFile2 = new StringBuilder();
    paramFile2.append("copyFileToSharePrefsDir used time: ");
    paramFile2.append(System.currentTimeMillis() - l);
    paramFile2.append(", ");
    paramFile2.append(paramContext);
    paramFile2.append("->");
    paramFile2.append(paramFile1);
    FLogger.d("FileUtils", paramFile2.toString());
  }
  
  /* Error */
  public static boolean copyFolder(String paramString1, String paramString2)
  {
    // Byte code:
    //   0: new 243	java/io/File
    //   3: dup
    //   4: aload_1
    //   5: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   8: invokevirtual 536	java/io/File:mkdirs	()Z
    //   11: pop
    //   12: new 243	java/io/File
    //   15: dup
    //   16: aload_0
    //   17: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   20: invokevirtual 552	java/io/File:list	()[Ljava/lang/String;
    //   23: astore 13
    //   25: aload 13
    //   27: ifnonnull +5 -> 32
    //   30: iconst_0
    //   31: ireturn
    //   32: aconst_null
    //   33: astore 9
    //   35: aload 9
    //   37: astore 7
    //   39: iconst_0
    //   40: istore_2
    //   41: iconst_1
    //   42: istore 4
    //   44: iload_2
    //   45: aload 13
    //   47: arraylength
    //   48: if_icmpge +673 -> 721
    //   51: aload_0
    //   52: getstatic 301	java/io/File:separator	Ljava/lang/String;
    //   55: invokevirtual 555	java/lang/String:endsWith	(Ljava/lang/String;)Z
    //   58: ifeq +46 -> 104
    //   61: new 293	java/lang/StringBuilder
    //   64: dup
    //   65: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   68: astore 8
    //   70: aload 8
    //   72: aload_0
    //   73: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   76: pop
    //   77: aload 8
    //   79: aload 13
    //   81: iload_2
    //   82: aaload
    //   83: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: pop
    //   87: new 243	java/io/File
    //   90: dup
    //   91: aload 8
    //   93: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   96: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   99: astore 12
    //   101: goto +52 -> 153
    //   104: new 293	java/lang/StringBuilder
    //   107: dup
    //   108: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   111: astore 8
    //   113: aload 8
    //   115: aload_0
    //   116: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   119: pop
    //   120: aload 8
    //   122: getstatic 301	java/io/File:separator	Ljava/lang/String;
    //   125: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   128: pop
    //   129: aload 8
    //   131: aload 13
    //   133: iload_2
    //   134: aaload
    //   135: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   138: pop
    //   139: new 243	java/io/File
    //   142: dup
    //   143: aload 8
    //   145: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   148: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   151: astore 12
    //   153: iload 4
    //   155: istore 5
    //   157: aload 9
    //   159: astore 8
    //   161: aload 7
    //   163: astore 10
    //   165: aload 12
    //   167: invokevirtual 558	java/io/File:isFile	()Z
    //   170: ifeq +395 -> 565
    //   173: new 512	java/io/FileInputStream
    //   176: dup
    //   177: aload 12
    //   179: invokespecial 559	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   182: astore 8
    //   184: aload 7
    //   186: astore 11
    //   188: aload 8
    //   190: astore 10
    //   192: new 293	java/lang/StringBuilder
    //   195: dup
    //   196: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   199: astore 9
    //   201: aload 7
    //   203: astore 11
    //   205: aload 8
    //   207: astore 10
    //   209: aload 9
    //   211: aload_1
    //   212: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   215: pop
    //   216: aload 7
    //   218: astore 11
    //   220: aload 8
    //   222: astore 10
    //   224: aload 9
    //   226: ldc_w 561
    //   229: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   232: pop
    //   233: aload 7
    //   235: astore 11
    //   237: aload 8
    //   239: astore 10
    //   241: aload 9
    //   243: aload 12
    //   245: invokevirtual 564	java/io/File:getName	()Ljava/lang/String;
    //   248: invokevirtual 565	java/lang/String:toString	()Ljava/lang/String;
    //   251: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   254: pop
    //   255: aload 7
    //   257: astore 11
    //   259: aload 8
    //   261: astore 10
    //   263: new 494	java/io/FileOutputStream
    //   266: dup
    //   267: aload 9
    //   269: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   272: invokespecial 514	java/io/FileOutputStream:<init>	(Ljava/lang/String;)V
    //   275: astore 9
    //   277: sipush 5120
    //   280: newarray <illegal type>
    //   282: astore 7
    //   284: aload 8
    //   286: aload 7
    //   288: invokevirtual 566	java/io/FileInputStream:read	([B)I
    //   291: istore_3
    //   292: iload_3
    //   293: iconst_m1
    //   294: if_icmpeq +15 -> 309
    //   297: aload 9
    //   299: aload 7
    //   301: iconst_0
    //   302: iload_3
    //   303: invokevirtual 498	java/io/FileOutputStream:write	([BII)V
    //   306: goto -22 -> 284
    //   309: aload 9
    //   311: invokevirtual 501	java/io/FileOutputStream:flush	()V
    //   314: aload 9
    //   316: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   319: goto +10 -> 329
    //   322: astore 7
    //   324: aload 7
    //   326: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   329: aload 8
    //   331: invokevirtual 567	java/io/FileInputStream:close	()V
    //   334: goto +10 -> 344
    //   337: astore 7
    //   339: aload 7
    //   341: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   344: aload 8
    //   346: astore 10
    //   348: goto +164 -> 512
    //   351: astore_0
    //   352: aload 9
    //   354: astore 7
    //   356: goto +171 -> 527
    //   359: astore 10
    //   361: aload 9
    //   363: astore 7
    //   365: aload 10
    //   367: astore 9
    //   369: goto +42 -> 411
    //   372: astore_0
    //   373: aload 11
    //   375: astore 7
    //   377: aload 10
    //   379: astore 8
    //   381: goto +146 -> 527
    //   384: astore 9
    //   386: goto +25 -> 411
    //   389: astore_0
    //   390: aload 9
    //   392: astore 8
    //   394: goto +133 -> 527
    //   397: astore 8
    //   399: aload 9
    //   401: astore 10
    //   403: aload 8
    //   405: astore 9
    //   407: aload 10
    //   409: astore 8
    //   411: iload 4
    //   413: istore 5
    //   415: iload 4
    //   417: ifeq +6 -> 423
    //   420: iconst_0
    //   421: istore 5
    //   423: aload 7
    //   425: astore 11
    //   427: aload 8
    //   429: astore 10
    //   431: aload 9
    //   433: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   436: aload 7
    //   438: ifnull +18 -> 456
    //   441: aload 7
    //   443: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   446: goto +10 -> 456
    //   449: astore 9
    //   451: aload 9
    //   453: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   456: iload 5
    //   458: istore 4
    //   460: aload 7
    //   462: astore 9
    //   464: aload 8
    //   466: astore 10
    //   468: aload 8
    //   470: ifnull +42 -> 512
    //   473: aload 8
    //   475: invokevirtual 567	java/io/FileInputStream:close	()V
    //   478: iload 5
    //   480: istore 4
    //   482: aload 7
    //   484: astore 9
    //   486: aload 8
    //   488: astore 10
    //   490: goto +22 -> 512
    //   493: astore 9
    //   495: aload 9
    //   497: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   500: aload 8
    //   502: astore 10
    //   504: aload 7
    //   506: astore 9
    //   508: iload 5
    //   510: istore 4
    //   512: aload 10
    //   514: astore 8
    //   516: iload 4
    //   518: istore 5
    //   520: aload 9
    //   522: astore 10
    //   524: goto +41 -> 565
    //   527: aload 7
    //   529: ifnull +16 -> 545
    //   532: aload 7
    //   534: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   537: goto +8 -> 545
    //   540: astore_1
    //   541: aload_1
    //   542: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   545: aload 8
    //   547: ifnull +16 -> 563
    //   550: aload 8
    //   552: invokevirtual 567	java/io/FileInputStream:close	()V
    //   555: goto +8 -> 563
    //   558: astore_1
    //   559: aload_1
    //   560: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   563: aload_0
    //   564: athrow
    //   565: iload 5
    //   567: istore 4
    //   569: aload 12
    //   571: invokevirtual 423	java/io/File:isDirectory	()Z
    //   574: ifeq +132 -> 706
    //   577: new 293	java/lang/StringBuilder
    //   580: dup
    //   581: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   584: astore 7
    //   586: aload 7
    //   588: aload_0
    //   589: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   592: pop
    //   593: aload 7
    //   595: ldc_w 561
    //   598: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   601: pop
    //   602: aload 7
    //   604: aload 13
    //   606: iload_2
    //   607: aaload
    //   608: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   611: pop
    //   612: aload 7
    //   614: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   617: astore 7
    //   619: new 293	java/lang/StringBuilder
    //   622: dup
    //   623: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   626: astore 9
    //   628: aload 9
    //   630: aload_1
    //   631: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   634: pop
    //   635: aload 9
    //   637: ldc_w 561
    //   640: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   643: pop
    //   644: aload 9
    //   646: aload 13
    //   648: iload_2
    //   649: aaload
    //   650: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   653: pop
    //   654: aload 7
    //   656: aload 9
    //   658: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   661: invokestatic 569	com/tencent/common/utils/FileUtils:copyFolder	(Ljava/lang/String;Ljava/lang/String;)Z
    //   664: istore 6
    //   666: iload 5
    //   668: istore 4
    //   670: iload 6
    //   672: ifne +34 -> 706
    //   675: iconst_0
    //   676: istore 4
    //   678: goto +28 -> 706
    //   681: astore 7
    //   683: iload 5
    //   685: istore 4
    //   687: iload 5
    //   689: ifeq +6 -> 695
    //   692: iconst_0
    //   693: istore 4
    //   695: ldc -41
    //   697: aload 7
    //   699: invokevirtual 570	java/lang/Throwable:toString	()Ljava/lang/String;
    //   702: invokestatic 576	android/util/Log:e	(Ljava/lang/String;Ljava/lang/String;)I
    //   705: pop
    //   706: iload_2
    //   707: iconst_1
    //   708: iadd
    //   709: istore_2
    //   710: aload 8
    //   712: astore 9
    //   714: aload 10
    //   716: astore 7
    //   718: goto -674 -> 44
    //   721: iload 4
    //   723: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	724	0	paramString1	String
    //   0	724	1	paramString2	String
    //   40	670	2	i	int
    //   291	12	3	j	int
    //   42	680	4	bool1	boolean
    //   155	533	5	bool2	boolean
    //   664	7	6	bool3	boolean
    //   37	263	7	localObject1	Object
    //   322	3	7	localIOException1	IOException
    //   337	3	7	localIOException2	IOException
    //   354	301	7	localObject2	Object
    //   681	17	7	localThrowable	Throwable
    //   716	1	7	localObject3	Object
    //   68	325	8	localObject4	Object
    //   397	7	8	localException1	Exception
    //   409	302	8	localObject5	Object
    //   33	335	9	localObject6	Object
    //   384	16	9	localException2	Exception
    //   405	27	9	localObject7	Object
    //   449	3	9	localIOException3	IOException
    //   462	23	9	localObject8	Object
    //   493	3	9	localIOException4	IOException
    //   506	207	9	localObject9	Object
    //   163	184	10	localObject10	Object
    //   359	19	10	localException3	Exception
    //   401	314	10	localObject11	Object
    //   186	240	11	localObject12	Object
    //   99	471	12	localFile	File
    //   23	624	13	arrayOfString	String[]
    // Exception table:
    //   from	to	target	type
    //   314	319	322	java/io/IOException
    //   329	334	337	java/io/IOException
    //   277	284	351	finally
    //   284	292	351	finally
    //   297	306	351	finally
    //   309	314	351	finally
    //   277	284	359	java/lang/Exception
    //   284	292	359	java/lang/Exception
    //   297	306	359	java/lang/Exception
    //   309	314	359	java/lang/Exception
    //   192	201	372	finally
    //   209	216	372	finally
    //   224	233	372	finally
    //   241	255	372	finally
    //   263	277	372	finally
    //   431	436	372	finally
    //   192	201	384	java/lang/Exception
    //   209	216	384	java/lang/Exception
    //   224	233	384	java/lang/Exception
    //   241	255	384	java/lang/Exception
    //   263	277	384	java/lang/Exception
    //   173	184	389	finally
    //   173	184	397	java/lang/Exception
    //   441	446	449	java/io/IOException
    //   473	478	493	java/io/IOException
    //   532	537	540	java/io/IOException
    //   550	555	558	java/io/IOException
    //   577	666	681	java/lang/Throwable
  }
  
  public static boolean copyMoveFile(String paramString1, String paramString2)
  {
    boolean bool2 = renameTo(new File(paramString1), new File(paramString2));
    boolean bool1 = bool2;
    if (!bool2)
    {
      bool2 = copyFile(paramString1, paramString2);
      bool1 = bool2;
      if (bool2) {
        try
        {
          delete(new File(paramString1));
          return bool2;
        }
        catch (Exception paramString1)
        {
          paramString1.printStackTrace();
          bool1 = bool2;
        }
      }
    }
    return bool1;
  }
  
  public static File createDir(File paramFile, String paramString)
  {
    if ((paramFile != null) && (paramString != null) && (paramString.length() != 0))
    {
      paramFile = new File(paramFile, paramString);
      if (!paramFile.exists()) {
        paramFile.mkdirs();
      }
      return paramFile;
    }
    return null;
  }
  
  public static void delete(File paramFile)
    throws IOException, IllegalArgumentException, FileNotFoundException
  {
    if (paramFile == null) {
      return;
    }
    if ((paramFile.isDirectory()) && (paramFile.getCanonicalPath().equals(paramFile.getAbsolutePath()))) {
      cleanDirectory(paramFile);
    }
    boolean bool = paramFile.exists();
    if (!paramFile.delete())
    {
      if (!bool)
      {
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("File does not exist: ");
        localStringBuilder.append(paramFile);
        throw new FileNotFoundException(localStringBuilder.toString());
      }
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to delete file: ");
      localStringBuilder.append(paramFile);
      throw new IOException(localStringBuilder.toString());
    }
  }
  
  public static void deleteFileOnThread(File paramFile)
  {
    if (paramFile == null) {
      return;
    }
    Object localObject = paramFile.getName();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append(System.currentTimeMillis());
    localStringBuilder.append((String)localObject);
    localObject = localStringBuilder.toString();
    localObject = new File(paramFile.getParentFile(), (String)localObject);
    if (paramFile.renameTo((File)localObject)) {
      try
      {
        new Thread()
        {
          public void run()
          {
            if (this.a.isDirectory()) {
              try
              {
                FileUtils.delete(this.a);
                return;
              }
              catch (IOException localIOException)
              {
                localIOException.printStackTrace();
                return;
              }
            }
            this.a.delete();
          }
        }.start();
        return;
      }
      catch (OutOfMemoryError paramFile)
      {
        paramFile.printStackTrace();
        return;
      }
    }
    if (paramFile.isDirectory()) {
      try
      {
        delete(paramFile);
        return;
      }
      catch (IOException paramFile)
      {
        paramFile.printStackTrace();
        return;
      }
    }
    paramFile.delete();
  }
  
  public static boolean deleteQuietly(File paramFile)
  {
    if (paramFile == null) {
      return false;
    }
    if (!paramFile.exists()) {
      return true;
    }
    try
    {
      if (paramFile.isDirectory()) {
        cleanDirectory(paramFile);
      }
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
    try
    {
      boolean bool = paramFile.delete();
      return bool;
    }
    catch (Exception paramFile) {}
    return false;
  }
  
  public static void deleteQuietlyIfEmpty(File paramFile, FilenameFilter paramFilenameFilter)
  {
    if (paramFile.exists())
    {
      if (!paramFile.isDirectory()) {
        return;
      }
      if (paramFilenameFilter != null) {
        paramFilenameFilter = paramFile.list(paramFilenameFilter);
      } else {
        paramFilenameFilter = paramFile.list();
      }
      if ((paramFilenameFilter != null) && (paramFilenameFilter.length > 0)) {
        return;
      }
      deleteQuietly(paramFile);
      return;
    }
  }
  
  public static String fixIllegalPath(String paramString)
  {
    Object localObject = paramString;
    if (!checkFileName(paramString))
    {
      paramString = b().split(paramString);
      localObject = new StringBuilder();
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        ((StringBuilder)localObject).append(paramString[i]);
        i += 1;
      }
      localObject = ((StringBuilder)localObject).toString();
    }
    return (String)localObject;
  }
  
  public static void forceMkdir(File paramFile)
    throws IOException
  {
    StringBuilder localStringBuilder;
    if (paramFile.exists())
    {
      if (paramFile.isDirectory()) {
        return;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("File ");
      localStringBuilder.append(paramFile);
      localStringBuilder.append(" exists and is not a directory. Unable to create directory.");
      throw new IOException(localStringBuilder.toString());
    }
    if (!paramFile.mkdirs())
    {
      if (paramFile.isDirectory()) {
        return;
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("Unable to create directory ");
      localStringBuilder.append(paramFile);
      throw new IOException(localStringBuilder.toString());
    }
  }
  
  public static String generateFileOrigNameFromUrl(String paramString)
  {
    int i = paramString.lastIndexOf('/');
    int j = paramString.lastIndexOf('.');
    if ((i < j) && (i != -1)) {
      return paramString.substring(i + 1, j);
    }
    return null;
  }
  
  public static File getAppCacheDir(Context paramContext)
  {
    if (hasSDcard())
    {
      paramContext.getExternalCacheDir();
      return createDir(getQQBrowserDir(), ".cache");
    }
    return getCacheDir(paramContext);
  }
  
  public static AssetManager getAssets()
  {
    return ContextHolder.getAppContext().getAssets();
  }
  
  public static File getAvailableQQBrowserDir(long paramLong)
  {
    a();
    File localFile = jdField_a_of_type_JavaIoFile;
    if ((localFile != null) && (SdCardInfo.Utils.getSdcardSpace(localFile.getAbsolutePath(), ContextHolder.getAppContext()).rest >= paramLong)) {
      return jdField_a_of_type_JavaIoFile;
    }
    localFile = jdField_b_of_type_JavaIoFile;
    if ((localFile != null) && (localFile != jdField_a_of_type_JavaIoFile) && (SdCardInfo.Utils.getSdcardSpace(localFile.getAbsolutePath(), ContextHolder.getAppContext()).rest >= paramLong)) {
      return jdField_b_of_type_JavaIoFile;
    }
    localFile = jdField_c_of_type_JavaIoFile;
    if ((localFile != null) && (localFile != jdField_a_of_type_JavaIoFile) && (SdCardInfo.Utils.getSdcardSpace(localFile.getAbsolutePath(), ContextHolder.getAppContext()).rest >= paramLong)) {
      return jdField_c_of_type_JavaIoFile;
    }
    return null;
  }
  
  public static File getBusinessDir(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    paramString = createDir(getBusinessRootDir(), paramString);
    if (!paramString.exists()) {
      paramString.mkdirs();
    }
    return paramString;
  }
  
  public static File getBusinessRootDir()
  {
    File localFile = createDir(getDataDir(), "business");
    if (!localFile.exists()) {
      localFile.mkdirs();
    }
    return localFile;
  }
  
  public static File getCacheDir()
  {
    if (SdCardInfo.Utils.hasSdcard(ContextHolder.getAppContext())) {
      return createDir(getQQBrowserDir(), ".cache");
    }
    return createDir(getCacheDir(ContextHolder.getAppContext()), ".cache");
  }
  
  public static File getCacheDir(Context paramContext)
  {
    try
    {
      paramContext = paramContext.getApplicationContext().getCacheDir();
      return paramContext;
    }
    catch (IllegalArgumentException paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static File getCommonDataShareDir(Context paramContext)
  {
    paramContext = paramContext.getDir("tbs_common_share", 0);
    if (((!paramContext.exists()) || (!paramContext.isDirectory())) && (!paramContext.mkdir())) {
      return null;
    }
    LinuxToolsJni localLinuxToolsJni = new LinuxToolsJni();
    try
    {
      if (LinuxToolsJni.gJniloaded == true) {
        localLinuxToolsJni.Chmod(paramContext.getAbsolutePath(), "755");
      }
      return paramContext;
    }
    catch (Throwable paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static File getCommonDataShareDirWithoutChmod(Context paramContext)
  {
    paramContext = paramContext.getDir("tbs_common_share", 0);
    if ((paramContext.exists()) && (paramContext.isDirectory())) {
      return paramContext;
    }
    return null;
  }
  
  public static ContentType getContentType(String paramString)
  {
    paramString = getFileExt(paramString);
    if (paramString != null) {
      paramString = (ContentType)a().get(paramString.toLowerCase());
    } else {
      paramString = null;
    }
    Object localObject = paramString;
    if (paramString == null) {
      localObject = new ContentType("application", "octet-stream", "binary");
    }
    return (ContentType)localObject;
  }
  
  public static File getDataDir()
  {
    return createDir(getFilesDir(ContextHolder.getAppContext()), "data");
  }
  
  public static File getDataDir(Context paramContext)
  {
    return createDir(paramContext.getFilesDir(), "data");
  }
  
  public static long getDataFreeSpace(Context paramContext)
  {
    return getSdcardFreeSpace(getDataDir(paramContext).getAbsolutePath());
  }
  
  public static File getDatabaseBase(Context paramContext, String paramString)
  {
    if ((paramContext != null) && (!TextUtils.isEmpty(paramString))) {
      return new File(paramContext.getApplicationContext().getDatabasePath(paramString).getParent());
    }
    return null;
  }
  
  public static File getDirFromRelativeName(String paramString, int paramInt)
  {
    Object localObject3 = null;
    Object localObject1;
    Object localObject2;
    switch (paramInt)
    {
    default: 
      localObject1 = localObject3;
      break;
    case 2: 
      localObject1 = getFilesDir(ContextHolder.getAppContext());
      if (localObject1 == null) {
        return null;
      }
      localObject1 = ((File)localObject1).getAbsolutePath();
      if (paramString == null)
      {
        localObject1 = new File((String)localObject1);
      }
      else if (paramString.startsWith("/"))
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append((String)localObject1);
        ((StringBuilder)localObject2).append(paramString);
        localObject1 = new File(((StringBuilder)localObject2).toString());
      }
      else
      {
        localObject2 = new StringBuilder();
        ((StringBuilder)localObject2).append((String)localObject1);
        ((StringBuilder)localObject2).append("/");
        ((StringBuilder)localObject2).append(paramString);
        localObject1 = new File(((StringBuilder)localObject2).toString());
      }
      break;
    case 1: 
      localObject1 = localObject3;
      if (SdCardInfo.Utils.hasSdcard(ContextHolder.getAppContext()))
      {
        localObject1 = getQQBrowserDir();
        if ((localObject1 != null) && (((File)localObject1).exists())) {
          localObject2 = ((File)localObject1).getAbsolutePath();
        } else {
          localObject2 = null;
        }
        if (paramString == null)
        {
          localObject1 = localObject3;
          if (localObject2 != null) {
            localObject1 = new File((String)localObject2);
          }
        }
        else if (paramString.startsWith("/"))
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append((String)localObject2);
          ((StringBuilder)localObject1).append(paramString);
          localObject1 = new File(((StringBuilder)localObject1).toString());
        }
        else
        {
          localObject1 = new StringBuilder();
          ((StringBuilder)localObject1).append((String)localObject2);
          ((StringBuilder)localObject1).append("/");
          ((StringBuilder)localObject1).append(paramString);
          localObject1 = new File(((StringBuilder)localObject1).toString());
        }
      }
      break;
    case 0: 
      if (SdCardInfo.Utils.hasSdcard(ContextHolder.getAppContext()))
      {
        localObject1 = getQQBrowserDir();
        if ((localObject1 != null) && (((File)localObject1).exists())) {
          localObject2 = ((File)localObject1).getAbsolutePath();
        } else {
          localObject2 = null;
        }
      }
      else
      {
        localObject1 = getFilesDir(ContextHolder.getAppContext());
        if (localObject1 == null) {
          return null;
        }
        localObject2 = ((File)localObject1).getAbsolutePath();
      }
      if (paramString == null)
      {
        localObject1 = localObject3;
        if (localObject2 != null) {
          localObject1 = new File((String)localObject2);
        }
      }
      else if (paramString.startsWith("/"))
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append((String)localObject2);
        ((StringBuilder)localObject1).append(paramString);
        localObject1 = new File(((StringBuilder)localObject1).toString());
      }
      else
      {
        localObject1 = new StringBuilder();
        ((StringBuilder)localObject1).append((String)localObject2);
        ((StringBuilder)localObject1).append("/");
        ((StringBuilder)localObject1).append(paramString);
        localObject1 = new File(((StringBuilder)localObject1).toString());
      }
      break;
    }
    if ((localObject1 != null) && (!((File)localObject1).exists())) {
      ((File)localObject1).mkdirs();
    }
    return (File)localObject1;
  }
  
  public static File getExternalAvailableQQBrowserDir()
  {
    a();
    return jdField_c_of_type_JavaIoFile;
  }
  
  public static File getExternalAvailableQQBrowserDirForDownload()
  {
    if (!jdField_b_of_type_Boolean) {
      a();
    }
    Object localObject1 = jdField_c_of_type_JavaIoFile;
    if (localObject1 != null) {
      return (File)localObject1;
    }
    Iterator localIterator = SdCardInfo.Utils.getSDcardInfo(ContextHolder.getAppContext()).extStorage.iterator();
    for (;;)
    {
      boolean bool = localIterator.hasNext();
      ??? = null;
      if (bool)
      {
        Object localObject5 = (StorageInfo)localIterator.next();
        localObject1 = new File(((StorageInfo)localObject5).path, DIR_EXT_MAIN);
        int i = Integer.parseInt(Build.VERSION.SDK);
        if (((StorageInfo)localObject5).isWritable) {
          return (File)localObject1;
        }
        if (i < 19) {
          continue;
        }
        Object localObject2;
        try
        {
          localObject6 = ContextHolder.getAppContext().getExternalFilesDir(null);
          localObject1 = ???;
          if (localObject6 != null) {
            localObject1 = ((File)localObject6).getAbsolutePath();
          }
        }
        catch (Throwable localThrowable)
        {
          localThrowable.printStackTrace();
          localObject2 = ???;
        }
        ??? = new StringBuilder();
        ((StringBuilder)???).append(((StorageInfo)localObject5).path);
        ((StringBuilder)???).append(File.separator);
        ((StringBuilder)???).append("Android");
        ((StringBuilder)???).append(File.separator);
        ((StringBuilder)???).append("data");
        ((StringBuilder)???).append(File.separator);
        ((StringBuilder)???).append(ContextHolder.getAppContext().getPackageName());
        ((StringBuilder)???).append(File.separator);
        ((StringBuilder)???).append("files");
        ((StringBuilder)???).append(File.separator);
        ((StringBuilder)???).append(DIR_EXT_MAIN);
        localObject5 = ((StringBuilder)???).toString();
        Object localObject6 = new File((String)localObject5);
        synchronized (jdField_b_of_type_JavaLangObject)
        {
          if ((!((File)localObject6).exists()) && ((((File)localObject6).exists()) || (((File)localObject6).mkdirs() != true)))
          {
            if (!TextUtils.isEmpty((CharSequence)localObject2))
            {
              StatServerHolder.userBehaviorStatistics("AHNG708_2");
              localObject6 = new StringBuilder();
              ((StringBuilder)localObject6).append((String)localObject2);
              ((StringBuilder)localObject6).append(";");
              ((StringBuilder)localObject6).append((String)localObject5);
              FLogger.d("SDCardInfo", ((StringBuilder)localObject6).toString());
            }
          }
          else {
            return (File)localObject6;
          }
        }
      }
    }
    return null;
  }
  
  public static File getExternalFilesDir(Context paramContext, String paramString)
  {
    Object localObject = paramContext.getExternalFilesDir(paramString);
    if (localObject != null) {
      return (File)localObject;
    }
    if (paramString == null)
    {
      paramString = getSDcardDir();
      localObject = new StringBuilder();
      ((StringBuilder)localObject).append("/Android/data/");
      ((StringBuilder)localObject).append(paramContext.getPackageName());
      ((StringBuilder)localObject).append("/files");
      return createDir(paramString, ((StringBuilder)localObject).toString());
    }
    localObject = getSDcardDir();
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("/Android/data/");
    localStringBuilder.append(paramContext.getPackageName());
    localStringBuilder.append("/files/");
    localStringBuilder.append(paramString);
    return createDir((File)localObject, localStringBuilder.toString());
  }
  
  public static int getExternalQQBrowserPos()
  {
    if (!jdField_b_of_type_Boolean) {
      a();
    }
    return jdField_a_of_type_Byte;
  }
  
  public static String getFileExt(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    int i = paramString.lastIndexOf('.');
    if ((i > -1) && (i < paramString.length() - 1))
    {
      String str = paramString.substring(i + 1);
      paramString = str;
      if (str.indexOf(File.separatorChar) > -1) {
        return null;
      }
    }
    else
    {
      paramString = null;
    }
    return paramString;
  }
  
  public static String getFileName(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    int i = paramString.lastIndexOf(File.separator);
    if (i < 0) {
      return paramString;
    }
    return paramString.substring(i + 1);
  }
  
  public static long getFileOrDirectorySize(File paramFile)
  {
    long l = 0L;
    if (paramFile == null) {
      return 0L;
    }
    if (!paramFile.isDirectory()) {
      return paramFile.length();
    }
    paramFile = paramFile.listFiles();
    if (paramFile == null) {
      return 0L;
    }
    int j = paramFile.length;
    int i = 0;
    while (i < j)
    {
      l += getFileOrDirectorySize(paramFile[i]);
      i += 1;
    }
    return l;
  }
  
  public static String getFileParentPath(String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    return new File(paramString).getParent();
  }
  
  public static List<File> getFiles(String paramString)
  {
    ArrayList localArrayList = new ArrayList();
    paramString = new File(paramString).listFiles();
    if (paramString != null)
    {
      int j = paramString.length;
      int i = 0;
      while (i < j)
      {
        Object localObject = paramString[i];
        if (((File)localObject).isFile()) {
          localArrayList.add(localObject);
        } else {
          localArrayList.addAll(getFiles(((File)localObject).getPath()));
        }
        i += 1;
      }
    }
    return localArrayList;
  }
  
  public static File getFilesDir(Context paramContext)
  {
    if (paramContext.getApplicationContext() != null) {
      return paramContext.getApplicationContext().getFilesDir();
    }
    return paramContext.getFilesDir();
  }
  
  public static Bitmap getImage(File paramFile)
    throws OutOfMemoryError
  {
    return getImage(paramFile, -1, -1);
  }
  
  /* Error */
  public static Bitmap getImage(File paramFile, int paramInt1, int paramInt2)
    throws OutOfMemoryError
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 5
    //   3: aconst_null
    //   4: astore 7
    //   6: aconst_null
    //   7: astore 8
    //   9: aconst_null
    //   10: astore 6
    //   12: aconst_null
    //   13: astore_3
    //   14: aload_0
    //   15: ifnull +171 -> 186
    //   18: aload_3
    //   19: astore 4
    //   21: aload_0
    //   22: invokevirtual 281	java/io/File:exists	()Z
    //   25: ifeq +161 -> 186
    //   28: aload_3
    //   29: astore 4
    //   31: aload_0
    //   32: invokestatic 794	com/tencent/common/utils/FileUtils:openInputStream	(Ljava/io/File;)Ljava/io/FileInputStream;
    //   35: astore_3
    //   36: iload_1
    //   37: ifle +54 -> 91
    //   40: iload_2
    //   41: ifgt +6 -> 47
    //   44: goto +47 -> 91
    //   47: aload_3
    //   48: astore_0
    //   49: new 796	android/graphics/BitmapFactory$Options
    //   52: dup
    //   53: invokespecial 797	android/graphics/BitmapFactory$Options:<init>	()V
    //   56: astore 4
    //   58: aload_3
    //   59: astore_0
    //   60: aload 4
    //   62: iload_1
    //   63: putfield 800	android/graphics/BitmapFactory$Options:outWidth	I
    //   66: aload_3
    //   67: astore_0
    //   68: aload 4
    //   70: iload_2
    //   71: putfield 803	android/graphics/BitmapFactory$Options:outHeight	I
    //   74: aload_3
    //   75: astore_0
    //   76: aload_3
    //   77: aconst_null
    //   78: aload 4
    //   80: invokestatic 809	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;Landroid/graphics/Rect;Landroid/graphics/BitmapFactory$Options;)Landroid/graphics/Bitmap;
    //   83: astore 4
    //   85: aload 4
    //   87: astore_0
    //   88: goto +103 -> 191
    //   91: aload_3
    //   92: astore_0
    //   93: aload_3
    //   94: invokestatic 812	android/graphics/BitmapFactory:decodeStream	(Ljava/io/InputStream;)Landroid/graphics/Bitmap;
    //   97: astore 4
    //   99: aload 4
    //   101: astore_0
    //   102: goto +89 -> 191
    //   105: astore_0
    //   106: aload_3
    //   107: astore 4
    //   109: goto +57 -> 166
    //   112: astore_3
    //   113: aload 4
    //   115: astore_0
    //   116: goto +52 -> 168
    //   119: aconst_null
    //   120: astore_3
    //   121: aload_3
    //   122: astore_0
    //   123: ldc -41
    //   125: ldc_w 814
    //   128: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   131: aload 6
    //   133: astore 4
    //   135: aload_3
    //   136: ifnull +72 -> 208
    //   139: aload 5
    //   141: astore 4
    //   143: aload_3
    //   144: invokevirtual 504	java/io/InputStream:close	()V
    //   147: aconst_null
    //   148: areturn
    //   149: astore_0
    //   150: aload_0
    //   151: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   154: aload 4
    //   156: areturn
    //   157: astore_3
    //   158: goto +10 -> 168
    //   161: astore_0
    //   162: aload 7
    //   164: astore 4
    //   166: aload_0
    //   167: athrow
    //   168: aload_0
    //   169: ifnull +15 -> 184
    //   172: aload_0
    //   173: invokevirtual 504	java/io/InputStream:close	()V
    //   176: goto +8 -> 184
    //   179: astore_0
    //   180: aload_0
    //   181: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   184: aload_3
    //   185: athrow
    //   186: aconst_null
    //   187: astore_3
    //   188: aload 8
    //   190: astore_0
    //   191: aload_0
    //   192: astore 4
    //   194: aload_3
    //   195: ifnull +13 -> 208
    //   198: aload_0
    //   199: astore 4
    //   201: aload_3
    //   202: invokevirtual 504	java/io/InputStream:close	()V
    //   205: aload_0
    //   206: astore 4
    //   208: aload 4
    //   210: areturn
    //   211: astore_0
    //   212: goto -93 -> 119
    //   215: astore_0
    //   216: goto -95 -> 121
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	219	0	paramFile	File
    //   0	219	1	paramInt1	int
    //   0	219	2	paramInt2	int
    //   13	94	3	localFileInputStream	FileInputStream
    //   112	1	3	localObject1	Object
    //   120	24	3	localObject2	Object
    //   157	28	3	localObject3	Object
    //   187	15	3	localObject4	Object
    //   19	190	4	localObject5	Object
    //   1	139	5	localObject6	Object
    //   10	122	6	localObject7	Object
    //   4	159	7	localObject8	Object
    //   7	182	8	localObject9	Object
    // Exception table:
    //   from	to	target	type
    //   49	58	105	java/lang/OutOfMemoryError
    //   60	66	105	java/lang/OutOfMemoryError
    //   68	74	105	java/lang/OutOfMemoryError
    //   76	85	105	java/lang/OutOfMemoryError
    //   93	99	105	java/lang/OutOfMemoryError
    //   21	28	112	finally
    //   31	36	112	finally
    //   166	168	112	finally
    //   143	147	149	java/io/IOException
    //   201	205	149	java/io/IOException
    //   49	58	157	finally
    //   60	66	157	finally
    //   68	74	157	finally
    //   76	85	157	finally
    //   93	99	157	finally
    //   123	131	157	finally
    //   21	28	161	java/lang/OutOfMemoryError
    //   31	36	161	java/lang/OutOfMemoryError
    //   172	176	179	java/io/IOException
    //   21	28	211	java/lang/Exception
    //   31	36	211	java/lang/Exception
    //   49	58	215	java/lang/Exception
    //   60	66	215	java/lang/Exception
    //   68	74	215	java/lang/Exception
    //   76	85	215	java/lang/Exception
    //   93	99	215	java/lang/Exception
  }
  
  public static FileUtils getInstance()
  {
    try
    {
      if (sInstance == null) {
        sInstance = new FileUtils(4);
      }
      FileUtils localFileUtils = sInstance;
      return localFileUtils;
    }
    finally {}
  }
  
  public static File getInternalAvailableQQBrowserDir()
  {
    a();
    return jdField_b_of_type_JavaIoFile;
  }
  
  public static String getLocalAssets(String paramString)
  {
    boolean bool = TextUtils.isEmpty(paramString);
    Object localObject2 = null;
    if (bool) {
      return null;
    }
    try
    {
      paramString = getLocalAssetsInput(paramString);
      if (paramString == null) {
        return null;
      }
      try
      {
        BufferedReader localBufferedReader = new BufferedReader(new InputStreamReader(paramString, "utf-8"));
        StringBuilder localStringBuilder = new StringBuilder();
        for (String str = localBufferedReader.readLine(); str != null; str = localBufferedReader.readLine())
        {
          localStringBuilder.append("\n");
          localStringBuilder.append(str);
        }
        str = localStringBuilder.toString();
      }
      catch (Exception localException1) {}
      localException2.printStackTrace();
    }
    catch (Exception localException2)
    {
      paramString = null;
    }
    Object localObject1 = localObject2;
    if (paramString != null) {
      try
      {
        paramString.close();
        return (String)localObject1;
      }
      catch (IOException paramString)
      {
        paramString.printStackTrace();
      }
    }
    return (String)localObject1;
  }
  
  public static InputStream getLocalAssetsInput(String paramString)
  {
    Context localContext = ContextHolder.getAppContext();
    if (localContext != null) {}
    try
    {
      Object localObject = new StringBuilder();
      ((StringBuilder)localObject).append("zh/");
      ((StringBuilder)localObject).append(paramString);
      localObject = ((StringBuilder)localObject).toString();
      localObject = localContext.getAssets().open((String)localObject);
      return (InputStream)localObject;
    }
    catch (Exception localException)
    {
      for (;;) {}
    }
    try
    {
      paramString = localContext.getAssets().open(paramString);
      return paramString;
    }
    catch (Exception paramString) {}
    return null;
    return null;
  }
  
  public static String getNativeLibraryDir(Context paramContext)
  {
    int i = Integer.parseInt(Build.VERSION.SDK);
    if (i >= 9) {
      return paramContext.getApplicationInfo().nativeLibraryDir;
    }
    if (i >= 4)
    {
      paramContext = paramContext.getApplicationInfo().dataDir;
      localStringBuilder = new StringBuilder();
      localStringBuilder.append(paramContext);
      localStringBuilder.append("/lib");
      return localStringBuilder.toString();
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("/data/data/");
    localStringBuilder.append(paramContext.getPackageName());
    localStringBuilder.append("/lib");
    return localStringBuilder.toString();
  }
  
  public static File getPublicFilesDir()
  {
    return createDir(getFilesDir(ContextHolder.getAppContext()), "public");
  }
  
  public static String getQBNativeLibPath(Context paramContext)
  {
    StringBuilder localStringBuilder = new StringBuilder();
    if (paramContext != null)
    {
      localStringBuilder.append(getNativeLibraryDir(paramContext));
      localStringBuilder.append(File.pathSeparator);
      localStringBuilder.append(paramContext.getDir("dynamic_so_output", 0));
    }
    return localStringBuilder.toString();
  }
  
  public static File getQBSdcardGuidDir()
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(getSDcardDir().getAbsolutePath());
    ((StringBuilder)localObject).append("/QQBrowser/.Application");
    localObject = new File(((StringBuilder)localObject).toString());
    if (!((File)localObject).exists()) {
      ((File)localObject).mkdirs();
    }
    return (File)localObject;
  }
  
  public static File getQQBrowserDir()
  {
    File localFile2 = getAvailableQQBrowserDir(0L);
    File localFile1;
    if (localFile2 != null)
    {
      localFile1 = localFile2;
      if (localFile2.exists()) {}
    }
    else
    {
      localFile1 = createDir(getSDcardDir(), DIR_EXT_MAIN);
    }
    return localFile1;
  }
  
  public static File getSDcardDir()
  {
    for (;;)
    {
      try
      {
        if (jdField_a_of_type_AndroidContentContext != null)
        {
          File localFile1 = jdField_a_of_type_AndroidContentContext.getExternalFilesDir(null);
          File localFile3 = localFile1;
          if (localFile1 == null) {
            localFile3 = Environment.getExternalStorageDirectory();
          }
          return localFile3;
        }
      }
      catch (Exception localException)
      {
        localException.printStackTrace();
        localFile2 = new File("/mnt/sdcard");
        if (localFile2.exists()) {
          return localFile2;
        }
        localFile2 = new File("/storage/sdcard0");
        if (localFile2.exists()) {
          return localFile2;
        }
        return null;
      }
      File localFile2 = null;
    }
  }
  
  @SuppressLint({"NewApi"})
  public static File getSDcardDir(Context paramContext)
  {
    int j = 1;
    int i = j;
    Context localContext = paramContext;
    if (paramContext != null)
    {
      i = j;
      localContext = paramContext;
      if (Build.VERSION.SDK_INT >= 23)
      {
        localContext = paramContext.getApplicationContext();
        if (localContext.getApplicationContext().checkCallingOrSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == 0) {
          i = j;
        } else {
          i = 0;
        }
      }
    }
    if (i != 0)
    {
      try
      {
        paramContext = Environment.getExternalStorageDirectory();
        if (paramContext != null)
        {
          boolean bool = paramContext.canWrite();
          if (bool) {
            return paramContext;
          }
        }
      }
      catch (Exception paramContext)
      {
        paramContext.printStackTrace();
        paramContext = new File("/mnt/sdcard");
        if ((paramContext.exists()) && (paramContext.canWrite())) {
          return paramContext;
        }
        paramContext = new File("/storage/sdcard0");
        if (!paramContext.exists()) {
          break label278;
        }
      }
      if (paramContext.canWrite()) {
        return paramContext;
      }
    }
    else
    {
      try
      {
        paramContext = localContext.getExternalFilesDir(null);
      }
      catch (Exception paramContext)
      {
        paramContext.printStackTrace();
      }
    }
    try
    {
      paramContext = new StringBuilder();
      paramContext.append(Environment.getExternalStorageDirectory());
      paramContext.append(File.separator);
      paramContext.append("Android");
      paramContext.append(File.separator);
      paramContext.append("data");
      paramContext.append(File.separator);
      paramContext.append(localContext.getApplicationInfo().packageName);
      paramContext.append(File.separator);
      paramContext.append("files");
      paramContext = paramContext.toString();
      paramContext = new File(paramContext);
      if (paramContext != null)
      {
        if (!paramContext.exists()) {
          paramContext.mkdirs();
        }
        if ((paramContext.exists()) && (paramContext.canWrite())) {
          return paramContext;
        }
      }
      label278:
      return null;
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
    return null;
  }
  
  public static long getSdcardFreeSpace()
  {
    File localFile = getSDcardDir();
    if ((localFile != null) && (localFile.exists())) {
      return getSdcardFreeSpace(localFile.getAbsolutePath());
    }
    return 0L;
  }
  
  public static long getSdcardFreeSpace(String paramString)
  {
    try
    {
      StatFs localStatFs = new StatFs(paramString);
      localStatFs.restat(paramString);
      long l = localStatFs.getBlockSize();
      int i = localStatFs.getAvailableBlocks();
      return l * i;
    }
    catch (IllegalArgumentException paramString)
    {
      for (;;) {}
    }
    return -1L;
  }
  
  public static long getSdcardTotalSpace()
  {
    File localFile = getSDcardDir();
    if ((localFile != null) && (localFile.exists())) {
      return getSdcardTotalSpace(localFile.getAbsolutePath());
    }
    return 0L;
  }
  
  public static long getSdcardTotalSpace(String paramString)
  {
    try
    {
      StatFs localStatFs = new StatFs(paramString);
      localStatFs.restat(paramString);
      long l = localStatFs.getBlockSize();
      int i = localStatFs.getBlockCount();
      return l * i;
    }
    catch (IllegalArgumentException paramString)
    {
      for (;;) {}
    }
    return -1L;
  }
  
  public static File getSharedPrefsFile(Context paramContext, String paramString)
  {
    if (TextUtils.isEmpty(paramString)) {
      return null;
    }
    return new File(new File(paramContext.getDir("shared_prefs", 0).getParentFile(), "shared_prefs"), paramString);
  }
  
  public static File getSystemPhotoDir()
  {
    File localFile = getSDcardDir();
    if ((localFile != null) && (localFile.exists())) {
      return createDir(getSDcardDir(), "DCIM");
    }
    return null;
  }
  
  public static File getTbsDataShareDirWithoutChmod(Context paramContext)
  {
    paramContext = new File(paramContext.getDir("tbs", 0), "share");
    if ((!paramContext.isDirectory()) && (!paramContext.mkdir())) {
      return null;
    }
    return paramContext;
  }
  
  public static File getTesCorePrivateDir(Context paramContext)
  {
    paramContext = new File(paramContext.getDir("tbs", 0), "core_private");
    if ((!paramContext.isDirectory()) && (!paramContext.mkdir())) {
      return null;
    }
    return paramContext;
  }
  
  public static File getTesCoreShareDir(Context paramContext)
  {
    paramContext = new File(paramContext.getDir("tbs", 0), "core_share");
    if ((!paramContext.isDirectory()) && (!paramContext.mkdir())) {
      return null;
    }
    return paramContext;
  }
  
  public static File getTesDataShareDir(Context paramContext)
  {
    File localFile2 = paramContext.getDir("tbs", 0);
    File localFile1 = new File(localFile2, "share");
    LinuxToolsJni localLinuxToolsJni = new LinuxToolsJni();
    try
    {
      if (LinuxToolsJni.gJniloaded == true)
      {
        paramContext.getPackageName();
        if (TbsMode.TBSISQB()) {
          localLinuxToolsJni.Chmod(localFile2.getAbsolutePath(), "701");
        } else {
          localLinuxToolsJni.Chmod(localFile2.getAbsolutePath(), "755");
        }
        localLinuxToolsJni.Chmod(localFile1.getAbsolutePath(), "755");
      }
    }
    catch (Exception paramContext)
    {
      paramContext.printStackTrace();
    }
    if ((!localFile1.isDirectory()) && (!localFile1.mkdir())) {
      return null;
    }
    return localFile1;
  }
  
  public static File getTesSdcardShareDir()
  {
    Object localObject = new StringBuilder();
    ((StringBuilder)localObject).append(getSDcardDir().getAbsolutePath());
    ((StringBuilder)localObject).append("/");
    ((StringBuilder)localObject).append(".tbs");
    localObject = new File(((StringBuilder)localObject).toString());
    if (!((File)localObject).exists()) {
      ((File)localObject).mkdirs();
    }
    return (File)localObject;
  }
  
  public static File getTmpTesCoreShareDir(Context paramContext)
  {
    paramContext = new File(paramContext.getDir("tbs", 0), "core_share_tmp");
    if ((!paramContext.isDirectory()) && (!paramContext.mkdir())) {
      return null;
    }
    return paramContext;
  }
  
  public static File getWebViewCacheDir()
  {
    return createDir(getCacheDir(ContextHolder.getAppContext()), "webviewCache");
  }
  
  public static boolean hasSDcard()
  {
    try
    {
      bool1 = "mounted".equals(Environment.getExternalStorageState());
    }
    catch (Throwable localThrowable2)
    {
      boolean bool1;
      for (;;) {}
    }
    bool1 = false;
    if (!bool1) {
      try
      {
        if (getSDcardDir() != null)
        {
          boolean bool2 = getSDcardDir().exists();
          if (bool2) {
            return true;
          }
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable1.printStackTrace();
        return false;
      }
    }
    return bool1;
  }
  
  public static boolean isFolderWritable(File paramFile)
  {
    try
    {
      if (!paramFile.exists()) {
        paramFile.mkdirs();
      }
      Object localObject = File.createTempFile("tmppp", null, paramFile);
      if (!((File)localObject).exists())
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("isFolderWritable returns false: ");
        ((StringBuilder)localObject).append(paramFile.getAbsolutePath());
        ((StringBuilder)localObject).append(": not exist");
        FLogger.d("FileUtils", ((StringBuilder)localObject).toString());
        return false;
      }
      ((File)localObject).delete();
      return true;
    }
    catch (Exception localException)
    {
      StringBuilder localStringBuilder = new StringBuilder();
      localStringBuilder.append("isFolderWritable returns false: ");
      localStringBuilder.append(paramFile.getAbsolutePath());
      FLogger.d("FileUtils", localStringBuilder.toString(), localException);
    }
    return false;
  }
  
  public static boolean isLocalFile(String paramString)
  {
    return (paramString != null) && ((paramString.startsWith("/")) || (paramString.startsWith("file:///")) || (paramString.startsWith("FILE:///")));
  }
  
  public static boolean isSameFileName(String paramString1, String paramString2)
  {
    if (!TextUtils.isEmpty(paramString1)) {
      if (TextUtils.isEmpty(paramString2)) {
        return false;
      }
    }
    for (;;)
    {
      try
      {
        int i = paramString1.lastIndexOf('.');
        if (i <= -1) {
          break label179;
        }
        localObject1 = paramString1.substring(0, i);
        localObject2 = paramString1.substring(i);
        paramString1 = (String)localObject1;
        localObject1 = localObject2;
        i = paramString2.lastIndexOf('.');
        if (i <= -1) {
          break label187;
        }
        localObject2 = paramString2.substring(0, i);
        String str = paramString2.substring(i);
        paramString2 = (String)localObject2;
        localObject2 = str;
        if ((((String)localObject1).equalsIgnoreCase((String)localObject2)) && (paramString1.startsWith(paramString2)))
        {
          paramString1 = paramString1.substring(paramString2.length());
          if ((paramString1 != null) && (paramString1.length() > 2) && (paramString1.startsWith("(")) && (paramString1.endsWith(")")))
          {
            boolean bool = StringUtils.isNumeric(paramString1.substring(1, paramString1.length() - 1));
            if (bool) {
              return true;
            }
          }
        }
      }
      catch (Exception paramString1)
      {
        paramString1.printStackTrace();
      }
      return false;
      return false;
      label179:
      Object localObject1 = "";
      continue;
      label187:
      Object localObject2 = "";
    }
  }
  
  public static boolean isUsingExternalDataDir()
  {
    return jdField_a_of_type_Byte == 2;
  }
  
  /* Error */
  public static java.util.Properties loadPropertiesFromAsset(String paramString)
  {
    // Byte code:
    //   0: aload_0
    //   1: invokestatic 987	com/tencent/common/utils/FileUtils:openAssetsInput	(Ljava/lang/String;)Ljava/io/InputStream;
    //   4: astore_1
    //   5: aload_1
    //   6: astore_0
    //   7: new 989	java/util/Properties
    //   10: dup
    //   11: invokespecial 990	java/util/Properties:<init>	()V
    //   14: astore_2
    //   15: aload_1
    //   16: astore_0
    //   17: aload_2
    //   18: aload_1
    //   19: invokevirtual 994	java/util/Properties:load	(Ljava/io/InputStream;)V
    //   22: aload_1
    //   23: ifnull +7 -> 30
    //   26: aload_1
    //   27: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   30: aload_2
    //   31: areturn
    //   32: astore_1
    //   33: goto +32 -> 65
    //   36: astore_2
    //   37: goto +12 -> 49
    //   40: astore_1
    //   41: aconst_null
    //   42: astore_0
    //   43: goto +22 -> 65
    //   46: astore_2
    //   47: aconst_null
    //   48: astore_1
    //   49: aload_1
    //   50: astore_0
    //   51: aload_2
    //   52: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   55: aload_1
    //   56: ifnull +7 -> 63
    //   59: aload_1
    //   60: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   63: aconst_null
    //   64: areturn
    //   65: aload_0
    //   66: ifnull +7 -> 73
    //   69: aload_0
    //   70: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   73: aload_1
    //   74: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	75	0	paramString	String
    //   4	23	1	localInputStream	InputStream
    //   32	1	1	localObject1	Object
    //   40	1	1	localObject2	Object
    //   48	26	1	localCloseable	Closeable
    //   14	17	2	localProperties	java.util.Properties
    //   36	1	2	localIOException1	IOException
    //   46	6	2	localIOException2	IOException
    // Exception table:
    //   from	to	target	type
    //   7	15	32	finally
    //   17	22	32	finally
    //   51	55	32	finally
    //   7	15	36	java/io/IOException
    //   17	22	36	java/io/IOException
    //   0	5	40	finally
    //   0	5	46	java/io/IOException
  }
  
  public static InputStream openAssetsInput(String paramString)
    throws IOException
  {
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("assetsName:");
    localStringBuilder.append(paramString);
    FLogger.d("FileUtils", localStringBuilder.toString());
    return ContextHolder.getAppContext().getAssets().open(paramString);
  }
  
  public static FileInputStream openInputStream(File paramFile)
    throws IOException
  {
    if (paramFile.exists())
    {
      if (!paramFile.isDirectory())
      {
        if (paramFile.canRead()) {
          return new FileInputStream(paramFile);
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("File '");
        localStringBuilder.append(paramFile);
        localStringBuilder.append("' cannot be read");
        throw new IOException(localStringBuilder.toString());
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("File '");
      localStringBuilder.append(paramFile);
      localStringBuilder.append("' exists but is a directory");
      throw new IOException(localStringBuilder.toString());
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("File '");
    localStringBuilder.append(paramFile);
    localStringBuilder.append("' does not exist");
    throw new FileNotFoundException(localStringBuilder.toString());
  }
  
  public static FileOutputStream openOutputStream(File paramFile)
    throws IOException
  {
    Object localObject;
    if (paramFile.exists())
    {
      if (!paramFile.isDirectory())
      {
        if (!paramFile.canWrite())
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("File '");
          ((StringBuilder)localObject).append(paramFile);
          ((StringBuilder)localObject).append("' cannot be written to");
          throw new IOException(((StringBuilder)localObject).toString());
        }
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("File '");
        ((StringBuilder)localObject).append(paramFile);
        ((StringBuilder)localObject).append("' exists but is a directory");
        throw new IOException(((StringBuilder)localObject).toString());
      }
    }
    else
    {
      localObject = paramFile.getParentFile();
      if ((localObject != null) && (!((File)localObject).exists()) && (!((File)localObject).mkdirs()))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("File '");
        ((StringBuilder)localObject).append(paramFile);
        ((StringBuilder)localObject).append("' could not be created");
        throw new IOException(((StringBuilder)localObject).toString());
      }
    }
    return new FileOutputStream(paramFile);
  }
  
  public static FileOutputStream openOutputStreamAppend(File paramFile)
    throws IOException
  {
    Object localObject;
    if (paramFile.exists())
    {
      if (!paramFile.isDirectory())
      {
        if (!paramFile.canWrite())
        {
          localObject = new StringBuilder();
          ((StringBuilder)localObject).append("File '");
          ((StringBuilder)localObject).append(paramFile);
          ((StringBuilder)localObject).append("' cannot be written to");
          throw new IOException(((StringBuilder)localObject).toString());
        }
      }
      else
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("File '");
        ((StringBuilder)localObject).append(paramFile);
        ((StringBuilder)localObject).append("' exists but is a directory");
        throw new IOException(((StringBuilder)localObject).toString());
      }
    }
    else
    {
      localObject = paramFile.getParentFile();
      if ((localObject != null) && (!((File)localObject).exists()) && (!((File)localObject).mkdirs()))
      {
        localObject = new StringBuilder();
        ((StringBuilder)localObject).append("File '");
        ((StringBuilder)localObject).append(paramFile);
        ((StringBuilder)localObject).append("' could not be created");
        throw new IOException(((StringBuilder)localObject).toString());
      }
    }
    return new FileOutputStream(paramFile, true);
  }
  
  public static RandomAccessFile openRandomAccessFile(File paramFile)
    throws IOException
  {
    if (paramFile.exists())
    {
      if (!paramFile.isDirectory())
      {
        if (paramFile.canRead()) {
          return new RandomAccessFile(paramFile, "r");
        }
        localStringBuilder = new StringBuilder();
        localStringBuilder.append("File '");
        localStringBuilder.append(paramFile);
        localStringBuilder.append("' cannot be read");
        throw new IOException(localStringBuilder.toString());
      }
      localStringBuilder = new StringBuilder();
      localStringBuilder.append("File '");
      localStringBuilder.append(paramFile);
      localStringBuilder.append("' exists but is a directory");
      throw new IOException(localStringBuilder.toString());
    }
    StringBuilder localStringBuilder = new StringBuilder();
    localStringBuilder.append("File '");
    localStringBuilder.append(paramFile);
    localStringBuilder.append("' does not exist");
    throw new FileNotFoundException(localStringBuilder.toString());
  }
  
  /* Error */
  public static ByteBuffer read(File paramFile)
    throws OutOfMemoryError
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore 8
    //   3: aconst_null
    //   4: astore 9
    //   6: aconst_null
    //   7: astore 7
    //   9: aload_0
    //   10: ifnonnull +5 -> 15
    //   13: aconst_null
    //   14: areturn
    //   15: invokestatic 456	com/tencent/common/utils/FileUtils:getInstance	()Lcom/tencent/common/utils/FileUtils;
    //   18: invokespecial 1029	com/tencent/common/utils/FileUtils:a	()Ljava/nio/ByteBuffer;
    //   21: astore 6
    //   23: aload 6
    //   25: astore 4
    //   27: aload 7
    //   29: astore_1
    //   30: aload 8
    //   32: astore_2
    //   33: aload 9
    //   35: astore_3
    //   36: aload 6
    //   38: astore 5
    //   40: aload_0
    //   41: invokevirtual 281	java/io/File:exists	()Z
    //   44: ifeq +28 -> 72
    //   47: aload 7
    //   49: astore_1
    //   50: aload 8
    //   52: astore_2
    //   53: aload 9
    //   55: astore_3
    //   56: aload 6
    //   58: astore 5
    //   60: aload 6
    //   62: aload_0
    //   63: invokevirtual 764	java/io/File:length	()J
    //   66: l2i
    //   67: invokestatic 1031	com/tencent/common/utils/FileUtils:a	(Ljava/nio/ByteBuffer;I)Ljava/nio/ByteBuffer;
    //   70: astore 4
    //   72: aload 7
    //   74: astore_1
    //   75: aload 8
    //   77: astore_2
    //   78: aload 9
    //   80: astore_3
    //   81: aload 4
    //   83: astore 5
    //   85: aload_0
    //   86: invokestatic 794	com/tencent/common/utils/FileUtils:openInputStream	(Ljava/io/File;)Ljava/io/FileInputStream;
    //   89: astore_0
    //   90: aload_0
    //   91: astore_1
    //   92: aload_0
    //   93: astore_2
    //   94: aload_0
    //   95: astore_3
    //   96: aload 4
    //   98: astore 5
    //   100: aload_0
    //   101: aload 4
    //   103: lconst_0
    //   104: iconst_m1
    //   105: invokestatic 1035	com/tencent/common/utils/FileUtils:readInputStreamToByteBuffer	(Ljava/io/InputStream;Ljava/nio/ByteBuffer;JI)Ljava/nio/ByteBuffer;
    //   108: astore 4
    //   110: aload_0
    //   111: ifnull +7 -> 118
    //   114: aload_0
    //   115: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   118: aload 4
    //   120: areturn
    //   121: astore_0
    //   122: goto +32 -> 154
    //   125: astore_0
    //   126: aload_2
    //   127: astore_1
    //   128: aload_0
    //   129: invokevirtual 516	java/lang/OutOfMemoryError:printStackTrace	()V
    //   132: aload_2
    //   133: astore_1
    //   134: aload_0
    //   135: athrow
    //   136: astore_0
    //   137: aload_3
    //   138: astore_1
    //   139: aload_0
    //   140: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   143: aload_3
    //   144: ifnull +7 -> 151
    //   147: aload_3
    //   148: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   151: aload 5
    //   153: areturn
    //   154: aload_1
    //   155: ifnull +7 -> 162
    //   158: aload_1
    //   159: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   162: aload_0
    //   163: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	164	0	paramFile	File
    //   29	130	1	localObject1	Object
    //   32	101	2	localObject2	Object
    //   35	113	3	localObject3	Object
    //   25	94	4	localByteBuffer1	ByteBuffer
    //   38	114	5	localByteBuffer2	ByteBuffer
    //   21	40	6	localByteBuffer3	ByteBuffer
    //   7	66	7	localObject4	Object
    //   1	75	8	localObject5	Object
    //   4	75	9	localObject6	Object
    // Exception table:
    //   from	to	target	type
    //   40	47	121	finally
    //   60	72	121	finally
    //   85	90	121	finally
    //   100	110	121	finally
    //   128	132	121	finally
    //   134	136	121	finally
    //   139	143	121	finally
    //   40	47	125	java/lang/OutOfMemoryError
    //   60	72	125	java/lang/OutOfMemoryError
    //   85	90	125	java/lang/OutOfMemoryError
    //   100	110	125	java/lang/OutOfMemoryError
    //   40	47	136	java/lang/Exception
    //   60	72	136	java/lang/Exception
    //   85	90	136	java/lang/Exception
    //   100	110	136	java/lang/Exception
  }
  
  public static ByteBuffer read(InputStream paramInputStream, int paramInt)
    throws IOException
  {
    return readInputStreamToByteBuffer(paramInputStream, getInstance().a(), 0L, paramInt);
  }
  
  public static ByteBuffer read(InputStream paramInputStream, int paramInt1, int paramInt2)
    throws IOException
  {
    return readInputStreamToByteBuffer(paramInputStream, getInstance().a(), paramInt1, paramInt2);
  }
  
  /* Error */
  public static ByteBuffer read(String paramString, long paramLong, int paramInt)
    throws OutOfMemoryError
  {
    // Byte code:
    //   0: invokestatic 456	com/tencent/common/utils/FileUtils:getInstance	()Lcom/tencent/common/utils/FileUtils;
    //   3: invokespecial 1029	com/tencent/common/utils/FileUtils:a	()Ljava/nio/ByteBuffer;
    //   6: astore 8
    //   8: new 243	java/io/File
    //   11: dup
    //   12: aload_0
    //   13: invokespecial 305	java/io/File:<init>	(Ljava/lang/String;)V
    //   16: astore 13
    //   18: aconst_null
    //   19: astore 11
    //   21: aconst_null
    //   22: astore 12
    //   24: aconst_null
    //   25: astore 10
    //   27: iload_3
    //   28: iflt +59 -> 87
    //   31: aload 8
    //   33: astore 5
    //   35: aload 10
    //   37: astore 4
    //   39: aload 11
    //   41: astore 6
    //   43: aload 8
    //   45: astore 9
    //   47: aload 12
    //   49: astore 7
    //   51: aload 8
    //   53: invokevirtual 147	java/nio/ByteBuffer:capacity	()I
    //   56: iload_3
    //   57: if_icmpge +87 -> 144
    //   60: aload 10
    //   62: astore 4
    //   64: aload 11
    //   66: astore 6
    //   68: aload 8
    //   70: astore 9
    //   72: aload 12
    //   74: astore 7
    //   76: aload 8
    //   78: iload_3
    //   79: invokestatic 1031	com/tencent/common/utils/FileUtils:a	(Ljava/nio/ByteBuffer;I)Ljava/nio/ByteBuffer;
    //   82: astore 5
    //   84: goto +60 -> 144
    //   87: aload 8
    //   89: astore 5
    //   91: aload 10
    //   93: astore 4
    //   95: aload 11
    //   97: astore 6
    //   99: aload 8
    //   101: astore 9
    //   103: aload 12
    //   105: astore 7
    //   107: aload 13
    //   109: invokevirtual 281	java/io/File:exists	()Z
    //   112: ifeq +32 -> 144
    //   115: aload 10
    //   117: astore 4
    //   119: aload 11
    //   121: astore 6
    //   123: aload 8
    //   125: astore 9
    //   127: aload 12
    //   129: astore 7
    //   131: aload 8
    //   133: aload 13
    //   135: invokevirtual 764	java/io/File:length	()J
    //   138: l2i
    //   139: invokestatic 1031	com/tencent/common/utils/FileUtils:a	(Ljava/nio/ByteBuffer;I)Ljava/nio/ByteBuffer;
    //   142: astore 5
    //   144: aload 10
    //   146: astore 4
    //   148: aload 11
    //   150: astore 6
    //   152: aload 5
    //   154: astore 9
    //   156: aload 12
    //   158: astore 7
    //   160: aload 13
    //   162: invokestatic 794	com/tencent/common/utils/FileUtils:openInputStream	(Ljava/io/File;)Ljava/io/FileInputStream;
    //   165: astore 8
    //   167: aload 8
    //   169: astore 4
    //   171: aload 8
    //   173: astore 6
    //   175: aload 5
    //   177: astore 9
    //   179: aload 8
    //   181: astore 7
    //   183: aload 8
    //   185: aload 5
    //   187: lload_1
    //   188: iload_3
    //   189: invokestatic 1035	com/tencent/common/utils/FileUtils:readInputStreamToByteBuffer	(Ljava/io/InputStream;Ljava/nio/ByteBuffer;JI)Ljava/nio/ByteBuffer;
    //   192: astore 5
    //   194: aload 8
    //   196: ifnull +8 -> 204
    //   199: aload 8
    //   201: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   204: aload 5
    //   206: areturn
    //   207: astore_0
    //   208: goto +162 -> 370
    //   211: astore 5
    //   213: aload 6
    //   215: astore 4
    //   217: aload 5
    //   219: invokevirtual 516	java/lang/OutOfMemoryError:printStackTrace	()V
    //   222: aload 6
    //   224: astore 4
    //   226: new 293	java/lang/StringBuilder
    //   229: dup
    //   230: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   233: astore 7
    //   235: aload 6
    //   237: astore 4
    //   239: aload 7
    //   241: ldc_w 1040
    //   244: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   247: pop
    //   248: aload 6
    //   250: astore 4
    //   252: aload 7
    //   254: aload_0
    //   255: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   258: pop
    //   259: aload 6
    //   261: astore 4
    //   263: aload 7
    //   265: ldc_w 1042
    //   268: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   271: pop
    //   272: aload 6
    //   274: astore 4
    //   276: ldc -41
    //   278: aload 7
    //   280: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   283: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   286: aload 6
    //   288: astore 4
    //   290: aload 5
    //   292: athrow
    //   293: aload 7
    //   295: astore 4
    //   297: new 293	java/lang/StringBuilder
    //   300: dup
    //   301: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   304: astore 5
    //   306: aload 7
    //   308: astore 4
    //   310: aload 5
    //   312: ldc_w 1040
    //   315: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   318: pop
    //   319: aload 7
    //   321: astore 4
    //   323: aload 5
    //   325: aload_0
    //   326: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   329: pop
    //   330: aload 7
    //   332: astore 4
    //   334: aload 5
    //   336: ldc_w 1044
    //   339: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   342: pop
    //   343: aload 7
    //   345: astore 4
    //   347: ldc -41
    //   349: aload 5
    //   351: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   354: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   357: aload 7
    //   359: ifnull +8 -> 367
    //   362: aload 7
    //   364: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   367: aload 9
    //   369: areturn
    //   370: aload 4
    //   372: ifnull +8 -> 380
    //   375: aload 4
    //   377: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   380: aload_0
    //   381: athrow
    //   382: astore 4
    //   384: goto -91 -> 293
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	387	0	paramString	String
    //   0	387	1	paramLong	long
    //   0	387	3	paramInt	int
    //   37	339	4	localObject1	Object
    //   382	1	4	localException	Exception
    //   33	172	5	localObject2	Object
    //   211	80	5	localOutOfMemoryError	OutOfMemoryError
    //   304	46	5	localStringBuilder	StringBuilder
    //   41	246	6	localObject3	Object
    //   49	314	7	localObject4	Object
    //   6	194	8	localObject5	Object
    //   45	323	9	localObject6	Object
    //   25	120	10	localObject7	Object
    //   19	130	11	localObject8	Object
    //   22	135	12	localObject9	Object
    //   16	145	13	localFile	File
    // Exception table:
    //   from	to	target	type
    //   51	60	207	finally
    //   76	84	207	finally
    //   107	115	207	finally
    //   131	144	207	finally
    //   160	167	207	finally
    //   183	194	207	finally
    //   217	222	207	finally
    //   226	235	207	finally
    //   239	248	207	finally
    //   252	259	207	finally
    //   263	272	207	finally
    //   276	286	207	finally
    //   290	293	207	finally
    //   297	306	207	finally
    //   310	319	207	finally
    //   323	330	207	finally
    //   334	343	207	finally
    //   347	357	207	finally
    //   51	60	211	java/lang/OutOfMemoryError
    //   76	84	211	java/lang/OutOfMemoryError
    //   107	115	211	java/lang/OutOfMemoryError
    //   131	144	211	java/lang/OutOfMemoryError
    //   160	167	211	java/lang/OutOfMemoryError
    //   183	194	211	java/lang/OutOfMemoryError
    //   51	60	382	java/lang/Exception
    //   76	84	382	java/lang/Exception
    //   107	115	382	java/lang/Exception
    //   131	144	382	java/lang/Exception
    //   160	167	382	java/lang/Exception
    //   183	194	382	java/lang/Exception
  }
  
  public static ByteBuffer readInputStreamToByteBuffer(InputStream paramInputStream, ByteBuffer paramByteBuffer, long paramLong, int paramInt)
    throws IOException, OutOfMemoryError
  {
    if (paramInputStream == null) {
      return paramByteBuffer;
    }
    byte[] arrayOfByte = getInstance().accqureByteArray();
    paramInputStream.skip(paramLong);
    int j;
    int i;
    if (paramInt == -1)
    {
      j = paramInt;
      i = 4096;
    }
    else
    {
      if (paramInt < 4096) {
        i = paramInt;
      } else {
        i = 4096;
      }
      j = paramInt;
    }
    ByteBuffer localByteBuffer;
    for (;;)
    {
      i = paramInputStream.read(arrayOfByte, 0, i);
      localByteBuffer = paramByteBuffer;
      if (-1 == i) {
        break;
      }
      paramByteBuffer = a(paramByteBuffer, i);
      if (paramByteBuffer.remaining() < i)
      {
        localByteBuffer = paramByteBuffer;
        break;
      }
      paramByteBuffer.put(arrayOfByte, 0, i);
      j -= i;
      if (j == 0)
      {
        localByteBuffer = paramByteBuffer;
        break;
      }
      if (paramInt == -1) {
        i = 4096;
      } else if (j < 4096) {
        i = j;
      } else {
        i = 4096;
      }
    }
    getInstance().releaseByteArray(arrayOfByte);
    return localByteBuffer;
  }
  
  public static String renameFileIfExist(String paramString1, String paramString2)
  {
    if (!TextUtils.isEmpty(paramString1))
    {
      if (TextUtils.isEmpty(paramString2)) {
        return paramString2;
      }
      boolean bool = checkFileName(paramString2);
      int j = 0;
      Object localObject1 = paramString2;
      if (!bool)
      {
        paramString2 = b().split(paramString2);
        localObject1 = new StringBuilder();
        int k = paramString2.length;
        i = 0;
        while (i < k)
        {
          ((StringBuilder)localObject1).append(paramString2[i]);
          i += 1;
        }
        localObject1 = ((StringBuilder)localObject1).toString();
      }
      if (!new File(paramString1, (String)localObject1).exists())
      {
        paramString2 = new StringBuilder();
        paramString2.append((String)localObject1);
        paramString2.append(".qbdltmp");
        if (!new File(paramString1, paramString2.toString()).exists())
        {
          paramString2 = new StringBuilder();
          paramString2.append(".");
          paramString2.append((String)localObject1);
          paramString2.append(".qbdltmp");
          if (!new File(paramString1, paramString2.toString()).exists()) {
            return (String)localObject1;
          }
        }
      }
      int i = ((String)localObject1).lastIndexOf('.');
      if (i > -1)
      {
        paramString2 = ((String)localObject1).substring(0, i);
        localObject2 = ((String)localObject1).substring(i);
        localObject1 = paramString2;
        paramString2 = (String)localObject2;
      }
      else
      {
        paramString2 = "";
      }
      Object localObject2 = a().matcher((CharSequence)localObject1);
      i = j;
      if (((Matcher)localObject2).find())
      {
        localObject1 = ((Matcher)localObject2).group(1);
        i = StringUtils.parseInt(((Matcher)localObject2).group(2), 0);
      }
      Object localObject3;
      do
      {
        File localFile;
        do
        {
          j = i + 1;
          localObject2 = new StringBuilder();
          ((StringBuilder)localObject2).append((String)localObject1);
          ((StringBuilder)localObject2).append("(");
          ((StringBuilder)localObject2).append(j);
          ((StringBuilder)localObject2).append(")");
          ((StringBuilder)localObject2).append(paramString2);
          localObject2 = ((StringBuilder)localObject2).toString();
          localFile = new File(paramString1, (String)localObject2);
          localObject3 = new StringBuilder();
          ((StringBuilder)localObject3).append((String)localObject2);
          ((StringBuilder)localObject3).append(".qbdltmp");
          localObject3 = new File(paramString1, ((StringBuilder)localObject3).toString());
          i = j;
        } while (localFile.exists());
        i = j;
      } while (((File)localObject3).exists());
      return (String)localObject2;
    }
    return paramString2;
  }
  
  /* Error */
  public static boolean renameTo(File paramFile1, File paramFile2)
  {
    // Byte code:
    //   0: iconst_0
    //   1: istore 4
    //   3: iconst_0
    //   4: istore 5
    //   6: iload 4
    //   8: istore_3
    //   9: aload_0
    //   10: ifnull +396 -> 406
    //   13: iload 4
    //   15: istore_3
    //   16: aload_1
    //   17: ifnull +389 -> 406
    //   20: invokestatic 524	java/lang/System:currentTimeMillis	()J
    //   23: lstore 6
    //   25: iconst_1
    //   26: istore 4
    //   28: aload_0
    //   29: invokevirtual 735	java/io/File:getParent	()Ljava/lang/String;
    //   32: aload_1
    //   33: invokevirtual 735	java/io/File:getParent	()Ljava/lang/String;
    //   36: invokevirtual 970	java/lang/String:equalsIgnoreCase	(Ljava/lang/String;)Z
    //   39: ifeq +76 -> 115
    //   42: aload_0
    //   43: aload_1
    //   44: invokevirtual 602	java/io/File:renameTo	(Ljava/io/File;)Z
    //   47: ifeq +68 -> 115
    //   50: new 293	java/lang/StringBuilder
    //   53: dup
    //   54: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   57: astore 8
    //   59: aload 8
    //   61: ldc_w 1068
    //   64: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   67: pop
    //   68: aload 8
    //   70: aload_0
    //   71: invokevirtual 564	java/io/File:getName	()Ljava/lang/String;
    //   74: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload 8
    //   80: ldc_w 1070
    //   83: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   86: pop
    //   87: aload 8
    //   89: invokestatic 524	java/lang/System:currentTimeMillis	()J
    //   92: lload 6
    //   94: lsub
    //   95: invokevirtual 543	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   98: pop
    //   99: ldc_w 1072
    //   102: aload 8
    //   104: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   107: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   110: iconst_1
    //   111: istore_3
    //   112: goto +5 -> 117
    //   115: iconst_0
    //   116: istore_3
    //   117: iload_3
    //   118: ifne +220 -> 338
    //   121: aconst_null
    //   122: astore 9
    //   124: new 512	java/io/FileInputStream
    //   127: dup
    //   128: aload_0
    //   129: invokespecial 559	java/io/FileInputStream:<init>	(Ljava/io/File;)V
    //   132: astore 8
    //   134: new 494	java/io/FileOutputStream
    //   137: dup
    //   138: aload_1
    //   139: invokespecial 496	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   142: astore_1
    //   143: ldc_w 515
    //   146: newarray <illegal type>
    //   148: astore 9
    //   150: aload 8
    //   152: aload 9
    //   154: invokevirtual 465	java/io/InputStream:read	([B)I
    //   157: istore_2
    //   158: iload_2
    //   159: iconst_m1
    //   160: if_icmpeq +14 -> 174
    //   163: aload_1
    //   164: aload 9
    //   166: iconst_0
    //   167: iload_2
    //   168: invokevirtual 498	java/io/FileOutputStream:write	([BII)V
    //   171: goto -21 -> 150
    //   174: aload_1
    //   175: invokevirtual 501	java/io/FileOutputStream:flush	()V
    //   178: aload 8
    //   180: invokevirtual 504	java/io/InputStream:close	()V
    //   183: aload_1
    //   184: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   187: aload_0
    //   188: invokevirtual 594	java/io/File:delete	()Z
    //   191: pop
    //   192: aload 8
    //   194: invokevirtual 504	java/io/InputStream:close	()V
    //   197: aload_1
    //   198: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   201: iload 4
    //   203: istore_3
    //   204: goto +101 -> 305
    //   207: astore_0
    //   208: iconst_1
    //   209: istore_3
    //   210: goto +192 -> 402
    //   213: astore_0
    //   214: aload_0
    //   215: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   218: iload 4
    //   220: istore_3
    //   221: goto +84 -> 305
    //   224: astore 9
    //   226: aload_1
    //   227: astore_0
    //   228: aload 9
    //   230: astore_1
    //   231: goto +77 -> 308
    //   234: astore 9
    //   236: aload_1
    //   237: astore_0
    //   238: aload 9
    //   240: astore_1
    //   241: goto +12 -> 253
    //   244: astore_1
    //   245: aconst_null
    //   246: astore_0
    //   247: goto +61 -> 308
    //   250: astore_1
    //   251: aconst_null
    //   252: astore_0
    //   253: goto +20 -> 273
    //   256: astore_1
    //   257: aconst_null
    //   258: astore 8
    //   260: aload 8
    //   262: astore_0
    //   263: goto +45 -> 308
    //   266: astore_1
    //   267: aconst_null
    //   268: astore_0
    //   269: aload 9
    //   271: astore 8
    //   273: aload_1
    //   274: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   277: aload 8
    //   279: ifnull +11 -> 290
    //   282: aload 8
    //   284: invokevirtual 504	java/io/InputStream:close	()V
    //   287: goto +3 -> 290
    //   290: aload_0
    //   291: ifnull +14 -> 305
    //   294: aload_0
    //   295: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   298: goto +7 -> 305
    //   301: aload_0
    //   302: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   305: iload_3
    //   306: ireturn
    //   307: astore_1
    //   308: aload 8
    //   310: ifnull +11 -> 321
    //   313: aload 8
    //   315: invokevirtual 504	java/io/InputStream:close	()V
    //   318: goto +3 -> 321
    //   321: aload_0
    //   322: ifnull +14 -> 336
    //   325: aload_0
    //   326: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   329: goto +7 -> 336
    //   332: aload_0
    //   333: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   336: aload_1
    //   337: athrow
    //   338: new 293	java/lang/StringBuilder
    //   341: dup
    //   342: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   345: astore_1
    //   346: aload_1
    //   347: ldc_w 1068
    //   350: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   353: pop
    //   354: aload_1
    //   355: aload_0
    //   356: invokevirtual 564	java/io/File:getName	()Ljava/lang/String;
    //   359: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   362: pop
    //   363: aload_1
    //   364: ldc_w 1070
    //   367: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   370: pop
    //   371: aload_1
    //   372: invokestatic 524	java/lang/System:currentTimeMillis	()J
    //   375: lload 6
    //   377: lsub
    //   378: invokevirtual 543	java/lang/StringBuilder:append	(J)Ljava/lang/StringBuilder;
    //   381: pop
    //   382: ldc_w 1073
    //   385: aload_1
    //   386: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   389: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   392: iload_3
    //   393: ireturn
    //   394: astore_0
    //   395: goto +7 -> 402
    //   398: astore_0
    //   399: iload 5
    //   401: istore_3
    //   402: aload_0
    //   403: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   406: iload_3
    //   407: ireturn
    //   408: astore_0
    //   409: goto -108 -> 301
    //   412: astore_0
    //   413: goto -81 -> 332
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	416	0	paramFile1	File
    //   0	416	1	paramFile2	File
    //   157	11	2	i	int
    //   8	399	3	bool1	boolean
    //   1	218	4	bool2	boolean
    //   4	396	5	bool3	boolean
    //   23	353	6	l	long
    //   57	257	8	localObject1	Object
    //   122	43	9	arrayOfByte	byte[]
    //   224	5	9	localObject2	Object
    //   234	36	9	localException	Exception
    // Exception table:
    //   from	to	target	type
    //   192	201	207	java/lang/Exception
    //   214	218	207	java/lang/Exception
    //   192	201	213	java/io/IOException
    //   143	150	224	finally
    //   150	158	224	finally
    //   163	171	224	finally
    //   174	192	224	finally
    //   143	150	234	java/lang/Exception
    //   150	158	234	java/lang/Exception
    //   163	171	234	java/lang/Exception
    //   174	192	234	java/lang/Exception
    //   134	143	244	finally
    //   134	143	250	java/lang/Exception
    //   124	134	256	finally
    //   124	134	266	java/lang/Exception
    //   273	277	307	finally
    //   282	287	394	java/lang/Exception
    //   294	298	394	java/lang/Exception
    //   301	305	394	java/lang/Exception
    //   313	318	394	java/lang/Exception
    //   325	329	394	java/lang/Exception
    //   332	336	394	java/lang/Exception
    //   336	338	394	java/lang/Exception
    //   338	392	394	java/lang/Exception
    //   28	110	398	java/lang/Exception
    //   282	287	408	java/io/IOException
    //   294	298	408	java/io/IOException
    //   313	318	412	java/io/IOException
    //   325	329	412	java/io/IOException
  }
  
  /* Error */
  public static boolean save(File paramFile, byte[] paramArrayOfByte)
  {
    // Byte code:
    //   0: aconst_null
    //   1: astore_3
    //   2: aconst_null
    //   3: astore_2
    //   4: aload_0
    //   5: invokestatic 1077	com/tencent/common/utils/FileUtils:openOutputStream	(Ljava/io/File;)Ljava/io/FileOutputStream;
    //   8: astore 4
    //   10: aload 4
    //   12: astore_2
    //   13: aload 4
    //   15: astore_3
    //   16: aload 4
    //   18: aload_1
    //   19: iconst_0
    //   20: aload_1
    //   21: arraylength
    //   22: invokevirtual 471	java/io/OutputStream:write	([BII)V
    //   25: aload 4
    //   27: ifnull +15 -> 42
    //   30: aload 4
    //   32: invokevirtual 1078	java/io/OutputStream:close	()V
    //   35: iconst_1
    //   36: ireturn
    //   37: astore_0
    //   38: aload_0
    //   39: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   42: iconst_1
    //   43: ireturn
    //   44: astore_0
    //   45: goto +87 -> 132
    //   48: astore_1
    //   49: aload_3
    //   50: astore_2
    //   51: new 293	java/lang/StringBuilder
    //   54: dup
    //   55: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   58: astore 4
    //   60: aload_3
    //   61: astore_2
    //   62: aload 4
    //   64: ldc_w 1080
    //   67: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   70: pop
    //   71: aload_3
    //   72: astore_2
    //   73: aload 4
    //   75: aload_0
    //   76: invokevirtual 386	java/lang/StringBuilder:append	(Ljava/lang/Object;)Ljava/lang/StringBuilder;
    //   79: pop
    //   80: aload_3
    //   81: astore_2
    //   82: aload 4
    //   84: ldc_w 1082
    //   87: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   90: pop
    //   91: aload_3
    //   92: astore_2
    //   93: aload 4
    //   95: aload_1
    //   96: invokevirtual 1085	java/lang/Exception:getMessage	()Ljava/lang/String;
    //   99: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   102: pop
    //   103: aload_3
    //   104: astore_2
    //   105: ldc -41
    //   107: aload 4
    //   109: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   112: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   115: aload_3
    //   116: ifnull +14 -> 130
    //   119: aload_3
    //   120: invokevirtual 1078	java/io/OutputStream:close	()V
    //   123: iconst_0
    //   124: ireturn
    //   125: astore_0
    //   126: aload_0
    //   127: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   130: iconst_0
    //   131: ireturn
    //   132: aload_2
    //   133: ifnull +15 -> 148
    //   136: aload_2
    //   137: invokevirtual 1078	java/io/OutputStream:close	()V
    //   140: goto +8 -> 148
    //   143: astore_1
    //   144: aload_1
    //   145: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   148: aload_0
    //   149: athrow
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	150	0	paramFile	File
    //   0	150	1	paramArrayOfByte	byte[]
    //   3	134	2	localObject1	Object
    //   1	119	3	localObject2	Object
    //   8	100	4	localObject3	Object
    // Exception table:
    //   from	to	target	type
    //   30	35	37	java/io/IOException
    //   4	10	44	finally
    //   16	25	44	finally
    //   51	60	44	finally
    //   62	71	44	finally
    //   73	80	44	finally
    //   82	91	44	finally
    //   93	103	44	finally
    //   105	115	44	finally
    //   4	10	48	java/lang/Exception
    //   16	25	48	java/lang/Exception
    //   119	123	125	java/io/IOException
    //   136	140	143	java/io/IOException
  }
  
  public static int saveImage(File paramFile, Bitmap paramBitmap)
  {
    File localFile = getSDcardDir();
    int j = 0;
    int i = j;
    if (localFile != null)
    {
      i = j;
      if (localFile.exists())
      {
        i = j;
        if (paramFile != null)
        {
          i = j;
          if (paramFile.getAbsolutePath().startsWith(localFile.getAbsolutePath())) {
            i = 1;
          }
        }
      }
    }
    if ((i != 0) && (!hasSDcard())) {
      return ERR_SDCARD_NOT_AVAILABLE;
    }
    if (!saveImage(paramFile, paramBitmap, Bitmap.CompressFormat.PNG)) {
      return ERR_SAVE_IMAGE_FAILED;
    }
    return SUCCESS;
  }
  
  /* Error */
  public static boolean saveImage(File paramFile, Bitmap paramBitmap, Bitmap.CompressFormat paramCompressFormat)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +342 -> 343
    //   4: aload_1
    //   5: ifnull +338 -> 343
    //   8: aload_1
    //   9: invokevirtual 1103	android/graphics/Bitmap:isRecycled	()Z
    //   12: ifne +331 -> 343
    //   15: aload_0
    //   16: invokevirtual 281	java/io/File:exists	()Z
    //   19: ifeq +8 -> 27
    //   22: aload_0
    //   23: invokevirtual 594	java/io/File:delete	()Z
    //   26: pop
    //   27: new 1105	java/io/ByteArrayOutputStream
    //   30: dup
    //   31: invokespecial 1106	java/io/ByteArrayOutputStream:<init>	()V
    //   34: astore 7
    //   36: aload_1
    //   37: invokevirtual 1103	android/graphics/Bitmap:isRecycled	()Z
    //   40: ifeq +5 -> 45
    //   43: iconst_0
    //   44: ireturn
    //   45: aload_1
    //   46: aload_2
    //   47: bipush 100
    //   49: aload 7
    //   51: invokevirtual 1110	android/graphics/Bitmap:compress	(Landroid/graphics/Bitmap$CompressFormat;ILjava/io/OutputStream;)Z
    //   54: pop
    //   55: aload 7
    //   57: invokevirtual 1113	java/io/ByteArrayOutputStream:size	()I
    //   60: istore 4
    //   62: new 293	java/lang/StringBuilder
    //   65: dup
    //   66: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   69: astore_1
    //   70: aload_1
    //   71: ldc_w 1115
    //   74: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   77: pop
    //   78: aload_1
    //   79: iload 4
    //   81: invokevirtual 1066	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   84: pop
    //   85: ldc -41
    //   87: aload_1
    //   88: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   91: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   94: invokestatic 363	com/tencent/common/utils/FileUtils:getSDcardDir	()Ljava/io/File;
    //   97: astore_1
    //   98: aload_1
    //   99: ifnull +38 -> 137
    //   102: aload_1
    //   103: invokevirtual 281	java/io/File:exists	()Z
    //   106: ifeq +31 -> 137
    //   109: aload_0
    //   110: ifnull +22 -> 132
    //   113: aload_0
    //   114: invokevirtual 285	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   117: aload_1
    //   118: invokevirtual 285	java/io/File:getAbsolutePath	()Ljava/lang/String;
    //   121: invokevirtual 291	java/lang/String:startsWith	(Ljava/lang/String;)Z
    //   124: ifeq +8 -> 132
    //   127: iconst_1
    //   128: istore_3
    //   129: goto +10 -> 139
    //   132: iconst_0
    //   133: istore_3
    //   134: goto +5 -> 139
    //   137: iconst_0
    //   138: istore_3
    //   139: iload_3
    //   140: ifeq +21 -> 161
    //   143: invokestatic 359	com/tencent/common/utils/FileUtils:hasSDcard	()Z
    //   146: ifeq +15 -> 161
    //   149: iload 4
    //   151: i2l
    //   152: invokestatic 1117	com/tencent/common/utils/FileUtils:getSdcardFreeSpace	()J
    //   155: lcmp
    //   156: ifle +5 -> 161
    //   159: iconst_0
    //   160: ireturn
    //   161: aconst_null
    //   162: astore_2
    //   163: aconst_null
    //   164: astore 5
    //   166: aconst_null
    //   167: astore 6
    //   169: aload 6
    //   171: astore_1
    //   172: aload 7
    //   174: invokevirtual 1120	java/io/ByteArrayOutputStream:toByteArray	()[B
    //   177: astore 8
    //   179: aload 6
    //   181: astore_1
    //   182: new 293	java/lang/StringBuilder
    //   185: dup
    //   186: invokespecial 294	java/lang/StringBuilder:<init>	()V
    //   189: astore 9
    //   191: aload 6
    //   193: astore_1
    //   194: aload 9
    //   196: ldc_w 1122
    //   199: invokevirtual 298	java/lang/StringBuilder:append	(Ljava/lang/String;)Ljava/lang/StringBuilder;
    //   202: pop
    //   203: aload 6
    //   205: astore_1
    //   206: aload 9
    //   208: aload 8
    //   210: arraylength
    //   211: invokevirtual 1066	java/lang/StringBuilder:append	(I)Ljava/lang/StringBuilder;
    //   214: pop
    //   215: aload 6
    //   217: astore_1
    //   218: ldc -41
    //   220: aload 9
    //   222: invokevirtual 304	java/lang/StringBuilder:toString	()Ljava/lang/String;
    //   225: invokestatic 356	com/tencent/basesupport/FLogger:d	(Ljava/lang/String;Ljava/lang/String;)V
    //   228: aload 6
    //   230: astore_1
    //   231: aload 7
    //   233: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   236: aload 6
    //   238: astore_1
    //   239: new 494	java/io/FileOutputStream
    //   242: dup
    //   243: aload_0
    //   244: invokespecial 496	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   247: astore_0
    //   248: aload_0
    //   249: aload 8
    //   251: invokevirtual 1124	java/io/OutputStream:write	([B)V
    //   254: aload_0
    //   255: invokevirtual 1125	java/io/OutputStream:flush	()V
    //   258: aload 7
    //   260: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   263: aload_0
    //   264: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   267: iconst_1
    //   268: ireturn
    //   269: astore_2
    //   270: aload_0
    //   271: astore_1
    //   272: aload_2
    //   273: astore_0
    //   274: goto +58 -> 332
    //   277: astore_2
    //   278: goto +16 -> 294
    //   281: astore_2
    //   282: goto +33 -> 315
    //   285: astore_0
    //   286: goto +46 -> 332
    //   289: astore_1
    //   290: aload_2
    //   291: astore_0
    //   292: aload_1
    //   293: astore_2
    //   294: aload_0
    //   295: astore_1
    //   296: aload_2
    //   297: invokevirtual 516	java/lang/OutOfMemoryError:printStackTrace	()V
    //   300: aload 7
    //   302: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   305: aload_0
    //   306: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   309: iconst_0
    //   310: ireturn
    //   311: astore_2
    //   312: aload 5
    //   314: astore_0
    //   315: aload_0
    //   316: astore_1
    //   317: aload_2
    //   318: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   321: aload 7
    //   323: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   326: aload_0
    //   327: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   330: iconst_0
    //   331: ireturn
    //   332: aload 7
    //   334: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   337: aload_1
    //   338: invokestatic 996	com/tencent/common/utils/FileUtils:closeQuietly	(Ljava/io/Closeable;)V
    //   341: aload_0
    //   342: athrow
    //   343: iconst_1
    //   344: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	345	0	paramFile	File
    //   0	345	1	paramBitmap	Bitmap
    //   0	345	2	paramCompressFormat	Bitmap.CompressFormat
    //   128	12	3	i	int
    //   60	90	4	j	int
    //   164	149	5	localObject1	Object
    //   167	70	6	localObject2	Object
    //   34	299	7	localByteArrayOutputStream	java.io.ByteArrayOutputStream
    //   177	73	8	arrayOfByte	byte[]
    //   189	32	9	localStringBuilder	StringBuilder
    // Exception table:
    //   from	to	target	type
    //   248	258	269	finally
    //   248	258	277	java/lang/OutOfMemoryError
    //   248	258	281	java/lang/Exception
    //   172	179	285	finally
    //   182	191	285	finally
    //   194	203	285	finally
    //   206	215	285	finally
    //   218	228	285	finally
    //   231	236	285	finally
    //   239	248	285	finally
    //   296	300	285	finally
    //   317	321	285	finally
    //   172	179	289	java/lang/OutOfMemoryError
    //   182	191	289	java/lang/OutOfMemoryError
    //   194	203	289	java/lang/OutOfMemoryError
    //   206	215	289	java/lang/OutOfMemoryError
    //   218	228	289	java/lang/OutOfMemoryError
    //   231	236	289	java/lang/OutOfMemoryError
    //   239	248	289	java/lang/OutOfMemoryError
    //   172	179	311	java/lang/Exception
    //   182	191	311	java/lang/Exception
    //   194	203	311	java/lang/Exception
    //   206	215	311	java/lang/Exception
    //   218	228	311	java/lang/Exception
    //   231	236	311	java/lang/Exception
    //   239	248	311	java/lang/Exception
  }
  
  /* Error */
  public static boolean saveImageBMP(File paramFile, Bitmap paramBitmap)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +305 -> 306
    //   4: aload_1
    //   5: ifnull +301 -> 306
    //   8: aload_1
    //   9: invokevirtual 1103	android/graphics/Bitmap:isRecycled	()Z
    //   12: ifne +294 -> 306
    //   15: aload_0
    //   16: invokevirtual 281	java/io/File:exists	()Z
    //   19: ifeq +8 -> 27
    //   22: aload_0
    //   23: invokevirtual 594	java/io/File:delete	()Z
    //   26: pop
    //   27: aconst_null
    //   28: astore 8
    //   30: aconst_null
    //   31: astore 9
    //   33: aconst_null
    //   34: astore 7
    //   36: aload 7
    //   38: astore 4
    //   40: aload 8
    //   42: astore 5
    //   44: aload 9
    //   46: astore 6
    //   48: aload_1
    //   49: invokevirtual 1130	android/graphics/Bitmap:getWidth	()I
    //   52: i2s
    //   53: istore_2
    //   54: aload 7
    //   56: astore 4
    //   58: aload 8
    //   60: astore 5
    //   62: aload 9
    //   64: astore 6
    //   66: aload_1
    //   67: invokevirtual 1133	android/graphics/Bitmap:getHeight	()I
    //   70: i2s
    //   71: istore_3
    //   72: aload 7
    //   74: astore 4
    //   76: aload 8
    //   78: astore 5
    //   80: aload 9
    //   82: astore 6
    //   84: iload_2
    //   85: iload_3
    //   86: imul
    //   87: iconst_4
    //   88: imul
    //   89: invokestatic 137	java/nio/ByteBuffer:allocate	(I)Ljava/nio/ByteBuffer;
    //   92: astore 10
    //   94: aload 7
    //   96: astore 4
    //   98: aload 8
    //   100: astore 5
    //   102: aload 9
    //   104: astore 6
    //   106: aload_1
    //   107: aload 10
    //   109: invokevirtual 1137	android/graphics/Bitmap:copyPixelsToBuffer	(Ljava/nio/Buffer;)V
    //   112: aload 7
    //   114: astore 4
    //   116: aload 8
    //   118: astore 5
    //   120: aload 9
    //   122: astore 6
    //   124: aload_0
    //   125: invokestatic 1077	com/tencent/common/utils/FileUtils:openOutputStream	(Ljava/io/File;)Ljava/io/FileOutputStream;
    //   128: astore_0
    //   129: aload_0
    //   130: astore 4
    //   132: aload_0
    //   133: astore 5
    //   135: aload_0
    //   136: astore 6
    //   138: aload_0
    //   139: iload_2
    //   140: invokevirtual 1139	java/io/FileOutputStream:write	(I)V
    //   143: aload_0
    //   144: astore 4
    //   146: aload_0
    //   147: astore 5
    //   149: aload_0
    //   150: astore 6
    //   152: aload_0
    //   153: iload_2
    //   154: bipush 8
    //   156: ishr
    //   157: invokevirtual 1139	java/io/FileOutputStream:write	(I)V
    //   160: aload_0
    //   161: astore 4
    //   163: aload_0
    //   164: astore 5
    //   166: aload_0
    //   167: astore 6
    //   169: aload_0
    //   170: iload_3
    //   171: invokevirtual 1139	java/io/FileOutputStream:write	(I)V
    //   174: aload_0
    //   175: astore 4
    //   177: aload_0
    //   178: astore 5
    //   180: aload_0
    //   181: astore 6
    //   183: aload_0
    //   184: iload_3
    //   185: bipush 8
    //   187: ishr
    //   188: invokevirtual 1139	java/io/FileOutputStream:write	(I)V
    //   191: aload_0
    //   192: astore 4
    //   194: aload_0
    //   195: astore 5
    //   197: aload_0
    //   198: astore 6
    //   200: aload_0
    //   201: aload 10
    //   203: invokevirtual 151	java/nio/ByteBuffer:array	()[B
    //   206: invokevirtual 1140	java/io/FileOutputStream:write	([B)V
    //   209: aload_0
    //   210: ifnull +14 -> 224
    //   213: aload_0
    //   214: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   217: iconst_1
    //   218: ireturn
    //   219: astore_0
    //   220: aload_0
    //   221: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   224: iconst_1
    //   225: ireturn
    //   226: astore_0
    //   227: goto +59 -> 286
    //   230: astore_0
    //   231: aload 5
    //   233: astore 4
    //   235: aload_0
    //   236: invokevirtual 516	java/lang/OutOfMemoryError:printStackTrace	()V
    //   239: aload 5
    //   241: ifnull +15 -> 256
    //   244: aload 5
    //   246: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   249: iconst_0
    //   250: ireturn
    //   251: astore_0
    //   252: aload_0
    //   253: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   256: iconst_0
    //   257: ireturn
    //   258: astore_0
    //   259: aload 6
    //   261: astore 4
    //   263: aload_0
    //   264: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   267: aload 6
    //   269: ifnull +15 -> 284
    //   272: aload 6
    //   274: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   277: iconst_0
    //   278: ireturn
    //   279: astore_0
    //   280: aload_0
    //   281: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   284: iconst_0
    //   285: ireturn
    //   286: aload 4
    //   288: ifnull +16 -> 304
    //   291: aload 4
    //   293: invokevirtual 502	java/io/FileOutputStream:close	()V
    //   296: goto +8 -> 304
    //   299: astore_1
    //   300: aload_1
    //   301: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   304: aload_0
    //   305: athrow
    //   306: iconst_1
    //   307: ireturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	308	0	paramFile	File
    //   0	308	1	paramBitmap	Bitmap
    //   53	104	2	i	int
    //   71	117	3	j	int
    //   38	254	4	localObject1	Object
    //   42	203	5	localObject2	Object
    //   46	227	6	localObject3	Object
    //   34	79	7	localObject4	Object
    //   28	89	8	localObject5	Object
    //   31	90	9	localObject6	Object
    //   92	110	10	localByteBuffer	ByteBuffer
    // Exception table:
    //   from	to	target	type
    //   213	217	219	java/io/IOException
    //   48	54	226	finally
    //   66	72	226	finally
    //   84	94	226	finally
    //   106	112	226	finally
    //   124	129	226	finally
    //   138	143	226	finally
    //   152	160	226	finally
    //   169	174	226	finally
    //   183	191	226	finally
    //   200	209	226	finally
    //   235	239	226	finally
    //   263	267	226	finally
    //   48	54	230	java/lang/OutOfMemoryError
    //   66	72	230	java/lang/OutOfMemoryError
    //   84	94	230	java/lang/OutOfMemoryError
    //   106	112	230	java/lang/OutOfMemoryError
    //   124	129	230	java/lang/OutOfMemoryError
    //   138	143	230	java/lang/OutOfMemoryError
    //   152	160	230	java/lang/OutOfMemoryError
    //   169	174	230	java/lang/OutOfMemoryError
    //   183	191	230	java/lang/OutOfMemoryError
    //   200	209	230	java/lang/OutOfMemoryError
    //   244	249	251	java/io/IOException
    //   48	54	258	java/lang/Exception
    //   66	72	258	java/lang/Exception
    //   84	94	258	java/lang/Exception
    //   106	112	258	java/lang/Exception
    //   124	129	258	java/lang/Exception
    //   138	143	258	java/lang/Exception
    //   152	160	258	java/lang/Exception
    //   169	174	258	java/lang/Exception
    //   183	191	258	java/lang/Exception
    //   200	209	258	java/lang/Exception
    //   272	277	279	java/io/IOException
    //   291	296	299	java/io/IOException
  }
  
  public static boolean saveStringToFile(File paramFile, String paramString1, String paramString2)
    throws UnsupportedEncodingException
  {
    if (paramFile == null) {
      return false;
    }
    return save(paramFile, paramString1.getBytes(paramString2));
  }
  
  public static String searchFile(String paramString1, String paramString2, boolean paramBoolean)
  {
    if ((paramString1 != null) && (paramString2 != null))
    {
      paramString1 = new File(paramString1).listFiles();
      if ((paramString1 != null) && (paramString1.length > 0))
      {
        int i = 0;
        while (i < paramString1.length)
        {
          String str = paramString1[i];
          if (str.isFile())
          {
            if ((!paramBoolean) && (str.getName().equals(paramString2))) {
              return str.getAbsolutePath();
            }
          }
          else
          {
            if ((paramBoolean) && (str.getName().equals(paramString2))) {
              return str.getAbsolutePath();
            }
            str = searchFile(str.getAbsolutePath(), paramString2, paramBoolean);
            if (str != null) {
              return str;
            }
          }
          i += 1;
        }
      }
    }
    return null;
  }
  
  public static void setContextAndDirNameBefor(Context paramContext, String paramString)
  {
    jdField_a_of_type_AndroidContentContext = paramContext;
    DIR_EXT_MAIN = paramString;
  }
  
  public static void shrinkDir(File paramFile, long paramLong1, long paramLong2)
  {
    if (paramFile != null)
    {
      if (!paramFile.isDirectory()) {
        return;
      }
      paramFile = paramFile.listFiles(new b());
      if (paramFile != null)
      {
        if (paramFile.length <= 0) {
          return;
        }
        try
        {
          Arrays.sort(paramFile, new a());
        }
        catch (Exception localException)
        {
          StringBuilder localStringBuilder2 = new StringBuilder();
          localStringBuilder2.append("sort files ex: ");
          localStringBuilder2.append(localException.toString());
          FLogger.d("FileUtils", localStringBuilder2.toString());
        }
        long l3 = System.currentTimeMillis();
        long l2 = 0L;
        long l1 = l2;
        int i;
        if (paramFile != null)
        {
          l1 = l2;
          if (paramFile.length > 0)
          {
            int j = paramFile.length;
            i = 0;
            for (;;)
            {
              l1 = l2;
              if (i >= j) {
                break;
              }
              localStringBuilder1 = paramFile[i];
              if (l3 - paramLong2 > localStringBuilder1.lastModified()) {
                localStringBuilder1.delete();
              } else {
                l2 += localStringBuilder1.length();
              }
              i += 1;
            }
          }
        }
        StringBuilder localStringBuilder1 = new StringBuilder();
        localStringBuilder1.append("files : ");
        localStringBuilder1.append(paramFile.length);
        FLogger.d("FileUtils", localStringBuilder1.toString());
        localStringBuilder1 = new StringBuilder();
        localStringBuilder1.append("totalFileSize : ");
        localStringBuilder1.append(l1);
        FLogger.d("FileUtils", localStringBuilder1.toString());
        localStringBuilder1 = new StringBuilder();
        localStringBuilder1.append("DEFAULT_L2_MAX_SIZE : ");
        double d1 = paramLong1;
        Double.isNaN(d1);
        double d2 = 0.95D * d1;
        localStringBuilder1.append(d2);
        FLogger.d("FileUtils", localStringBuilder1.toString());
        paramLong1 = l1;
        if (l1 >= d2)
        {
          Double.isNaN(d1);
          d1 *= 0.7D;
          paramLong2 = d1;
          localStringBuilder1 = new StringBuilder();
          localStringBuilder1.append("desiredSize : ");
          localStringBuilder1.append(d1);
          FLogger.d("FileUtils", localStringBuilder1.toString());
          i = paramFile.length - 1;
          for (;;)
          {
            paramLong1 = l1;
            if (i < 0) {
              break;
            }
            paramLong1 = l1;
            if (l1 <= paramLong2) {
              break;
            }
            localStringBuilder1 = paramFile[i];
            paramLong1 = l1;
            if (localStringBuilder1 != null)
            {
              paramLong1 = localStringBuilder1.length();
              localStringBuilder1.delete();
              paramLong1 = l1 - paramLong1;
            }
            i -= 1;
            l1 = paramLong1;
          }
        }
        paramFile = new StringBuilder();
        paramFile.append("totalFileSize 2: ");
        paramFile.append(paramLong1);
        FLogger.d("FileUtils", paramFile.toString());
        paramLong1 = System.currentTimeMillis();
        paramFile = new StringBuilder();
        paramFile.append("L2 Cache image was full, shrinked to 70% in ");
        paramFile.append(paramLong1 - l3);
        paramFile.append("ms.");
        FLogger.d("FileUtils", paramFile.toString());
        return;
      }
      return;
    }
  }
  
  public static ByteBuffer toByteArray(InputStream paramInputStream)
    throws IOException, OutOfMemoryError
  {
    return read(paramInputStream, -1);
  }
  
  public static String toString(InputStream paramInputStream, String paramString)
    throws IOException, OutOfMemoryError
  {
    paramInputStream = read(paramInputStream, -1);
    paramString = new String(paramInputStream.array(), 0, paramInputStream.position(), paramString);
    getInstance().releaseByteBuffer(paramInputStream);
    return paramString;
  }
  
  public static void write(String paramString, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramString != null) {
      paramOutputStream.write(paramString.getBytes());
    }
  }
  
  public static void write(String paramString1, OutputStream paramOutputStream, String paramString2)
    throws IOException
  {
    if (paramString1 != null)
    {
      if (paramString2 == null)
      {
        write(paramString1, paramOutputStream);
        return;
      }
      paramOutputStream.write(paramString1.getBytes(paramString2));
    }
  }
  
  public static void write(byte[] paramArrayOfByte, OutputStream paramOutputStream)
    throws IOException
  {
    if (paramArrayOfByte != null) {
      paramOutputStream.write(paramArrayOfByte);
    }
  }
  
  /* Error */
  public static File writeInputStreamToFile(InputStream paramInputStream, File paramFile)
  {
    // Byte code:
    //   0: aload_0
    //   1: ifnull +184 -> 185
    //   4: aload_1
    //   5: ifnonnull +5 -> 10
    //   8: aconst_null
    //   9: areturn
    //   10: aload_1
    //   11: invokevirtual 281	java/io/File:exists	()Z
    //   14: ifeq +8 -> 22
    //   17: aload_1
    //   18: invokevirtual 594	java/io/File:delete	()Z
    //   21: pop
    //   22: aload_1
    //   23: invokevirtual 1224	java/io/File:createNewFile	()Z
    //   26: pop
    //   27: new 494	java/io/FileOutputStream
    //   30: dup
    //   31: aload_1
    //   32: invokespecial 496	java/io/FileOutputStream:<init>	(Ljava/io/File;)V
    //   35: astore 4
    //   37: aload 4
    //   39: astore_3
    //   40: sipush 4096
    //   43: newarray <illegal type>
    //   45: astore 5
    //   47: aload 4
    //   49: astore_3
    //   50: aload_0
    //   51: aload 5
    //   53: invokevirtual 465	java/io/InputStream:read	([B)I
    //   56: istore_2
    //   57: iload_2
    //   58: ifle +18 -> 76
    //   61: aload 4
    //   63: astore_3
    //   64: aload 4
    //   66: aload 5
    //   68: iconst_0
    //   69: iload_2
    //   70: invokevirtual 471	java/io/OutputStream:write	([BII)V
    //   73: goto -26 -> 47
    //   76: aload 4
    //   78: astore_3
    //   79: aload 4
    //   81: invokevirtual 1078	java/io/OutputStream:close	()V
    //   84: aload 4
    //   86: invokevirtual 1078	java/io/OutputStream:close	()V
    //   89: goto +8 -> 97
    //   92: astore_0
    //   93: aload_0
    //   94: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   97: aload_1
    //   98: areturn
    //   99: astore_0
    //   100: goto +17 -> 117
    //   103: astore_0
    //   104: goto +43 -> 147
    //   107: astore_0
    //   108: aconst_null
    //   109: astore_3
    //   110: goto +57 -> 167
    //   113: astore_0
    //   114: aconst_null
    //   115: astore 4
    //   117: aload 4
    //   119: astore_3
    //   120: aload_0
    //   121: invokevirtual 449	java/lang/Exception:printStackTrace	()V
    //   124: aload 4
    //   126: ifnull +38 -> 164
    //   129: aload 4
    //   131: invokevirtual 1078	java/io/OutputStream:close	()V
    //   134: aconst_null
    //   135: areturn
    //   136: astore_0
    //   137: aload_0
    //   138: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   141: aconst_null
    //   142: areturn
    //   143: astore_0
    //   144: aconst_null
    //   145: astore 4
    //   147: aload 4
    //   149: astore_3
    //   150: aload_0
    //   151: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   154: aload 4
    //   156: ifnull +8 -> 164
    //   159: aload 4
    //   161: invokevirtual 1078	java/io/OutputStream:close	()V
    //   164: aconst_null
    //   165: areturn
    //   166: astore_0
    //   167: aload_3
    //   168: ifnull +15 -> 183
    //   171: aload_3
    //   172: invokevirtual 1078	java/io/OutputStream:close	()V
    //   175: goto +8 -> 183
    //   178: astore_1
    //   179: aload_1
    //   180: invokevirtual 503	java/io/IOException:printStackTrace	()V
    //   183: aload_0
    //   184: athrow
    //   185: aconst_null
    //   186: areturn
    // Local variable table:
    //   start	length	slot	name	signature
    //   0	187	0	paramInputStream	InputStream
    //   0	187	1	paramFile	File
    //   56	14	2	i	int
    //   39	133	3	localFileOutputStream1	FileOutputStream
    //   35	125	4	localFileOutputStream2	FileOutputStream
    //   45	22	5	arrayOfByte	byte[]
    // Exception table:
    //   from	to	target	type
    //   84	89	92	java/io/IOException
    //   40	47	99	java/lang/Exception
    //   50	57	99	java/lang/Exception
    //   64	73	99	java/lang/Exception
    //   79	84	99	java/lang/Exception
    //   40	47	103	java/io/IOException
    //   50	57	103	java/io/IOException
    //   64	73	103	java/io/IOException
    //   79	84	103	java/io/IOException
    //   10	22	107	finally
    //   22	37	107	finally
    //   10	22	113	java/lang/Exception
    //   22	37	113	java/lang/Exception
    //   129	134	136	java/io/IOException
    //   159	164	136	java/io/IOException
    //   10	22	143	java/io/IOException
    //   22	37	143	java/io/IOException
    //   40	47	166	finally
    //   50	57	166	finally
    //   64	73	166	finally
    //   79	84	166	finally
    //   120	124	166	finally
    //   150	154	166	finally
    //   171	175	178	java/io/IOException
  }
  
  public byte[] accqureByteArray()
  {
    synchronized (this.jdField_a_of_type_ArrayOfByte)
    {
      if (!this.jdField_c_of_type_Boolean)
      {
        this.jdField_c_of_type_Boolean = true;
        byte[] arrayOfByte2 = this.jdField_a_of_type_ArrayOfByte;
        return arrayOfByte2;
      }
      return new byte['က'];
    }
  }
  
  public void releaseByteArray(byte[] paramArrayOfByte)
  {
    if (paramArrayOfByte == null) {
      return;
    }
    synchronized (this.jdField_a_of_type_ArrayOfByte)
    {
      if ((this.jdField_c_of_type_Boolean == true) && (this.jdField_a_of_type_ArrayOfByte == paramArrayOfByte)) {
        this.jdField_c_of_type_Boolean = false;
      }
      return;
    }
  }
  
  public boolean releaseByteBuffer(ByteBuffer paramByteBuffer)
  {
    synchronized (this.jdField_a_of_type_ArrayOfJavaNioByteBuffer)
    {
      if (a(paramByteBuffer)) {
        return true;
      }
      if (paramByteBuffer.capacity() > 131072) {
        return true;
      }
      if (this.mByteBufferPoolAvailableSize < this.jdField_a_of_type_ArrayOfJavaNioByteBuffer.length)
      {
        paramByteBuffer.clear();
        this.jdField_a_of_type_ArrayOfJavaNioByteBuffer[this.mByteBufferPoolAvailableSize] = paramByteBuffer;
        this.mByteBufferPoolAvailableSize += 1;
        return true;
      }
      return true;
    }
  }
  
  static class a
    implements Comparator<File>
  {
    public int a(File paramFile1, File paramFile2)
    {
      long l1 = paramFile1.length();
      long l2 = paramFile2.length();
      if (l1 < l2) {
        return -1;
      }
      if (l1 > l2) {
        return 1;
      }
      return 0;
    }
  }
  
  static class b
    implements FileFilter
  {
    public boolean accept(File paramFile)
    {
      return paramFile.isFile();
    }
  }
}


/* Location:              C:\Users\Administrator\Desktop\学习资料\dex2jar\dex2jar-2.0\classes-dex2jar.jar!\com\tencent\common\utils\FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */