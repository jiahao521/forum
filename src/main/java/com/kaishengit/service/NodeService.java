package com.kaishengit.service;

import com.kaishengit.dao.NodeDao;
import com.kaishengit.entity.Node;
import com.kaishengit.exception.ServiceException;
import com.kaishengit.util.StringUtils;
/**
 * Created by jiahao0 on 2017/12/22.
 */

public class NodeService {

    NodeDao nodeDao = new NodeDao();

    public void updateNode(String nodeId, String nodeName) {

        if (StringUtils.isNumeric(nodeId) && StringUtils.isNotEmpty(nodeName)) {

            Node node = nodeDao.findNodeById(Integer.valueOf(nodeId));
            node.setNodename(nodeName);
            nodeDao.update(node);
        } else {
            throw new ServiceException("参数异常");
        }

    }
    public String validateNodeName( String nodeName) {

        Node nodeHas = nodeDao.findNodeByName(nodeName);

        if(nodeHas == null) {
            return "true";
        }else {
            return "false";
        }

    }


    public String validateNodeName(String nodeId, String nodeName) {

        // 查询node,并判断nodeName是否等于node的nodename
        Node node = nodeDao.findNodeById(Integer.valueOf(nodeId));

        if (node.getNodename().equals(nodeName)) {
            return "true";
        } else {

            Node nodeIsIn = nodeDao.findNodeByName(nodeName);
            if (nodeIsIn == null) {
                return "true";
            }
        }
        return "false";
    }



    public void delNodeById(String id) {
        Node node = nodeDao.findNodeById(Integer.valueOf(id));
        if (node.getTopicnum() > 0){
            throw  new ServiceException("该节点下已有主题,不可删除");
        }else{
            nodeDao.del(id);
        }
    }

    public void addNode( String nodeName) {
            nodeDao.add(nodeName);
    }
}
