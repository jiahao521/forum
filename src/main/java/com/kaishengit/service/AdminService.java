package com.kaishengit.service;

import com.kaishengit.dao.AdminDao;
import com.kaishengit.dao.NodeDao;
import com.kaishengit.dao.ReplyDao;
import com.kaishengit.dao.TopicDao;
import com.kaishengit.entity.Admin;
import com.kaishengit.entity.Node;
import com.kaishengit.entity.Topic;
import com.kaishengit.exception.ServiceException;
import com.kaishengit.util.Config;
import com.kaishengit.util.StringUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * Created by jiahao0 on 2017/1/2.
 */

public class AdminService {
    //日志
    Logger logger = LoggerFactory.getLogger(AdminService.class);

    AdminDao admindao = new AdminDao();
    TopicDao topicDao = new TopicDao();
    ReplyDao replyDao = new ReplyDao();
    NodeDao nodeDao = new NodeDao();

    public Admin login(String adminName, String password, String ip) {

        Admin admin = admindao.findAdminByName(adminName);

        if(admin != null && admin.getPassword().equals(DigestUtils.md5Hex(Config.get("user.password.salt") + password))){

            logger.debug("管理员 {} 登录了后台管理系统,IP为: {}",adminName,ip);
            return admin;
        } else {

            throw new ServiceException("帐号密码错误");
        }


    }

    public void deleteTopicById(String id) {
        //删除主题的回复
        replyDao.delByTopicId(id);

        //更新节点下的主题数量
        Topic topic = topicDao.findTopicById(id);

        if(topic != null ){

            Node node = nodeDao.findNodeById(topic.getNodeid());
            node.setTopicnum(node.getTopicnum() - 1);
            nodeDao.update(node);
            topicDao.delById(id);
        } else {
            throw new ServiceException("该主题不存在或已删除");
        }


    }
}
