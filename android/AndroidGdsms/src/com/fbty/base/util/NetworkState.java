/**
 * Copyright 2008-2010. 重庆富邦科技发展有限公司, Inc. All rights reserved.
 * <a>http://www.3gcq.cn</a>
 */
package com.fbty.base.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Company:重庆富邦科技发展有限责任公司 </p>
 * @author zhoulei create 2011-4-20
 * @version 0.1
 *
 */
public class NetworkState
{
  public NetworkState(Context paramContext)
  {
  }

  public static boolean isActiveNetworkConnected(Context paramContext)
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    if (localNetworkInfo != null)
    {
      NetworkInfo.State localState1 = localNetworkInfo.getState();
      NetworkInfo.State localState2 = NetworkInfo.State.CONNECTED;
      if (localState1 != localState2);
    }
//    for (int i = 1; ; i = 0)
//      return i;
	return false;
  }

  public static void isCTNetWorkConnected(Context paramContext)
  {
    ConnectivityManager localConnectivityManager = (ConnectivityManager)paramContext.getSystemService("connectivity");
    NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getActiveNetworkInfo();
    String str;
	if ((localNetworkInfo != null) && (localNetworkInfo.getTypeName().toUpperCase().indexOf("WIFI") < 0) && (localConnectivityManager.getAllNetworkInfo()[0].getSubtypeName() != null))
      str = localConnectivityManager.getAllNetworkInfo()[0].getSubtypeName().toUpperCase();
  }

  public static boolean isMobileNetworkConnected(Context paramContext)
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getNetworkInfo(0);
    if (localNetworkInfo != null)
    {
      NetworkInfo.State localState1 = localNetworkInfo.getState();
      NetworkInfo.State localState2 = NetworkInfo.State.CONNECTED;
      if (localState1 != localState2);
    }
//    for (int i = 1; ; i = 0)
//      return i;
	return false;
  }

  public static boolean isWifiNetworkConnected(Context paramContext)
  {
    NetworkInfo localNetworkInfo = ((ConnectivityManager)paramContext.getSystemService("connectivity")).getNetworkInfo(1);
    if (localNetworkInfo != null)
    {
      NetworkInfo.State localState1 = localNetworkInfo.getState();
      NetworkInfo.State localState2 = NetworkInfo.State.CONNECTED;
      if (localState1 != localState2);
    }
//    for (int i = 1; ; i = 0)
//      return i;
	return false;
  }
}