package com.tencent.tbs.common.ui.dialog;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import java.util.Map;

public class BrowsingActivityInfo
{
  private Context mApp;
  private Drawable mIcon;
  private String mLabel;
  private String mPackageName;
  private int mProgress = 0;
  private ResolveInfo mResolveInfo;
  
  BrowsingActivityInfo(Context paramContext, ResolveInfo paramResolveInfo)
  {
    this.mApp = paramContext.getApplicationContext();
    this.mResolveInfo = paramResolveInfo;
    this.mIcon = null;
    this.mLabel = null;
    this.mPackageName = null;
  }
  
  BrowsingActivityInfo(Context paramContext, Drawable paramDrawable, String paramString1, String paramString2)
  {
    this.mApp = paramContext.getApplicationContext();
    this.mResolveInfo = null;
    this.mIcon = paramDrawable;
    this.mLabel = paramString1;
    this.mPackageName = paramString2;
  }
  
  public Drawable getIcon()
  {
    return getIcon(null);
  }
  
  public Drawable getIcon(Map<String, Drawable> paramMap)
  {
    if (paramMap != null)
    {
      paramMap = (Drawable)paramMap.get(getPackageName());
      if (paramMap != null) {
        return paramMap;
      }
    }
    paramMap = this.mResolveInfo;
    if (paramMap != null) {
      return paramMap.loadIcon(this.mApp.getPackageManager());
    }
    return this.mIcon;
  }
  
  public String getLabel()
  {
    ResolveInfo localResolveInfo = this.mResolveInfo;
    if (localResolveInfo != null) {
      return localResolveInfo.loadLabel(this.mApp.getPackageManager()).toString();
    }
    return this.mLabel;
  }
  
  public String getPackageName()
  {
    Object localObject = this.mResolveInfo;
    if (localObject != null) {
      return ((ResolveInfo)localObject).activityInfo.packageName;
    }
    String str = this.mPackageName;
    localObject = str;
    if (str == null) {
      localObject = "";
    }
    return (String)localObject;
  }
  
  public int getProgress()
  {
    return this.mProgress;
  }
  
  public ResolveInfo getResolveInfo()
  {
    return this.mResolveInfo;
  }
  
  public void setProgress(int paramInt)
  {
    this.mProgress = paramInt;
  }
  
  public void setResolveInfo(ResolveInfo paramResolveInfo)
  {
    this.mResolveInfo = paramResolveInfo;
  }
}


/* Location:              C:\Users\Administrator\Desktop\学习资料\dex2jar\dex2jar-2.0\classes-dex2jar.jar!\com\tencent\tbs\common\ui\dialog\BrowsingActivityInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */