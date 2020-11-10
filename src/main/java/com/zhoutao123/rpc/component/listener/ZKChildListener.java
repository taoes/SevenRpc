package com.zhoutao123.rpc.component.listener;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

/** Zookeeper 子节点变动监听器 */
@Slf4j
public class ZKChildListener implements IZkChildListener {

  @Override
  public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {}
}
