package com.zhoutao123.rpc.component.listener;

import com.zhoutao123.rpc.base.constant.ZkValue;
import com.zhoutao123.rpc.component.client.ConnectManagement;
import com.zhoutao123.rpc.entity.NodeInfo;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.IZkDataListener;
import org.apache.logging.log4j.util.Strings;
import org.springframework.util.StringUtils;

/** Zookeeper 数据变动监听器 */
@Slf4j
public class ZKDataListener implements IZkDataListener {

  ConnectManagement cm;

  public ZKDataListener() {
    cm = ConnectManagement.getInstance();
  }

  @Override
  public void handleDataChange(String dataPath, Object data) throws Exception {
    if (StringUtils.hasText(dataPath) && dataPath.startsWith(ZkValue.PREFIX_SLASH)) {
      Set<NodeInfo> nodeInfoSet = (Set<NodeInfo>) data;
      log.info("节点:{} 数据变动, 新增连接:{}", dataPath, nodeInfoSet);
      for (NodeInfo nodeInfo : nodeInfoSet) {
        cm.connectServerNode(dataPath.replace(ZkValue.PREFIX_SLASH, Strings.EMPTY), nodeInfo);
      }
    }
  }

  @Override
  public void handleDataDeleted(String dataPath) throws Exception {
    log.info("节点:{} 已被删除,正在移除连接", dataPath);
    int disconnect = cm.disconnect(dataPath.replace(ZkValue.PREFIX_SLASH, Strings.EMPTY));
    log.info("连接移除完成，移除:{}", disconnect);
  }
}
