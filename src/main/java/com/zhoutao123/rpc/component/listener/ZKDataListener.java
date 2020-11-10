package com.zhoutao123.rpc.component.listener;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;

/** Zookeeper 数据变动监听器 */
@Slf4j
public class ZKDataListener implements IZkDataListener {

  @Override
  public void handleDataChange(String dataPath, Object data) throws Exception {}

  @Override
  public void handleDataDeleted(String dataPath) throws Exception {}
}
