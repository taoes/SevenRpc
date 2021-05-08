package com.zhoutao123.rpc.component.listener;

import com.zhoutao123.rpc.base.constant.ZkValue;
import com.zhoutao123.rpc.component.client.ConnectManagement;
import java.util.List;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkChildListener;

/** Zookeeper 子节点变动监听器 */
@Slf4j
public class ZKChildListener implements IZkChildListener {

  ConnectManagement cm;

  public ZKChildListener() {
    cm = ConnectManagement.getInstance();
  }

  @Override
  public void handleChildChange(String parentPath, List<String> currentChilds) throws Exception {
    if (Objects.equals(parentPath, ZkValue.PREFIX)) {}
  }
}
