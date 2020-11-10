package com.zhoutao123.rpc.component.listener;

import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/** Zookeeper 状态变动监听器 */
@Slf4j
public class ZKStateListener implements IZkStateListener {

  @Override
  public void handleStateChanged(KeeperState state) throws Exception {}

  @Override
  public void handleNewSession() throws Exception {}

  @Override
  public void handleSessionEstablishmentError(Throwable error) throws Exception {}
}
