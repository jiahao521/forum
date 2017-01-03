package com.kaishengit.service;

import com.google.common.collect.Maps;
import com.kaishengit.dao.*;
import com.kaishengit.entity.*;
import com.kaishengit.exception.ServiceException;
import com.kaishengit.util.Config;
import com.kaishengit.util.Page;
import com.kaishengit.util.StringUtils;
import org.joda.time.DateTime;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
/**
 * Created by jiahao0 on 2016/12/21.
 */

public class TopicService {

    TopicDao topicDao = new TopicDao();
    UserDao userDao = new UserDao();
    NodeDao nodeDao = new NodeDao();
    ReplyDao replyDao = new ReplyDao();
    FavDao favDao = new FavDao();
    ThyDao thyDao = new ThyDao();
    NotifyDao notifyDao = new NotifyDao();

    public List<Node> findAllNode(){

        List<Node> nodeList = nodeDao.findAllNodes();
        return nodeList;
    }

    public Topic addNewTopic(String title, String content, Integer nodeid,Integer userId){
        //根据userid和nodeid查找对应发帖人和节点
        Topic topic = new Topic();
        topic.setTitle(title);
        topic.setContent(content);
        topic.setNodeid(nodeid);
        topic.setUserid(userId);

        //设置最后回复时间为当前时间
        topic.setLastreplytime(new Timestamp(new DateTime().getMillis()));
        Integer topicId = topicDao.save(topic);
        topic.setId(topicId);

        //更新node表的topicnum
        Node node = nodeDao.findNodeById(nodeid);

        if (node != null){

            node.setTopicnum(node.getTopicnum() + 1);
            nodeDao.update(node);
        } else{
            throw new ServiceException("节点不存在");
        }

        return topic ;
    }

    //通过topic对象的userid、nodeid 获取user和node对象,并set到tipic对象中;
    public Topic findTopicById(String topicId) {

        if (StringUtils.isNumeric(topicId)){

            Topic topic = topicDao.findTopicById(topicId);
            if (topic != null ){

                //获取user和node对象
                User user = userDao.findById(topic.getUserid());
                Node node = nodeDao.findNodeById(topic.getNodeid());
                //封装topic对象
                user.setAvatar(Config.get("qiniu.domain")+user.getAvatar());
                topic.setUser(user);
                topic.setNode(node);

                return topic;
            }else{
                throw new ServiceException("该帖不存在或已被删除");
            }
        }else{
            throw  new ServiceException("参数错误");
        }
    }

    public void addTopicReply(String topicId, String content, User user) {
        //新增回复
        Reply reply = new Reply();
        reply.setContent(content);
        reply.setUserid(user.getId());
        reply.setTopicid(Integer.valueOf(topicId));
        replyDao.addReply(reply);

        //更新t_topic表中的replynum 和 lastreplytime字段
        Topic topic = topicDao.findTopicById(topicId);
        if (topic != null){

            topic.setReplynum(topic.getReplynum() + 1);
            //获取当前显示时间并更改格式
            topic.setLastreplytime(new Timestamp(DateTime.now().getMillis()));
            topicDao.update(topic);
        }else{
            throw new ServiceException("回复的主题不存在或被删除");
        }

        //新增回复通知
        if (!user.getId().equals(topic.getUserid())){

            Notify notify = new Notify();

            notify.setUserid(topic.getUserid());
            notify.setContent("您的主题 <a href=\"/topicDetail?topicid="+topic.getId()+"\">["+ topic.getTitle()+"] </a> 有了新的回复,请查看.");
            notify.setState(Notify.NOTIFY_STATE_UNREAD);

            notifyDao.save(notify);
        }


    }

    public List<Reply> findReplyListByTopicId(String topicId) {
        return replyDao.findListByTopicId(topicId);
    }

    public void updateTopicById(String title, String content, String nodeid, String topicId) {

        Topic topic = topicDao.findTopicById(topicId);
        Integer lastNodeId = topic.getNodeid();

        if( topic.isEdit() ) {

            topic.setTitle(title);
            topic.setContent(content);
            topic.setNodeid(Integer.valueOf(nodeid));

            topicDao.update(topic);
            updatNode(lastNodeId,nodeid);
        } else {

            throw new ServiceException("该帖已经不可编辑");
        }
    }

    private void updatNode(Integer lastNodeId,String nodeid) {

        if (lastNodeId != Integer.valueOf(nodeid)) {

            //更新node表，原來的node的topicnum -1
            Node lastNode = nodeDao.findNodeById(lastNodeId);
            lastNode.setTopicnum(lastNode.getTopicnum() - 1);
            nodeDao.update(lastNode);

            //更新node表，node的topicnum + 1
            Node newNode = nodeDao.findNodeById(Integer.valueOf(nodeid));
            newNode.setTopicnum(newNode.getTopicnum() + 1);
            nodeDao.update(newNode);
        }
    }

    public Fav findFavByUserIdAndTopicId(String topicId, User user) {
       return favDao.findByTopicIdAndUserId(user.getId(),Integer.valueOf(topicId));
    }

    public void favTopic(User user, String topicId) {

        Fav fav = new Fav();
        fav.setTopicid(Integer.valueOf(topicId));
        fav.setUserid(user.getId());
        favDao.addFav(fav);

        //topic表收藏字段 +1
        Topic topic = topicDao.findTopicById(topicId);
        topic.setFavnum(topic.getFavnum() + 1);
        topicDao.update(topic);
    }

    public void unfavTopic(User user, String topicId) {

        favDao.deleteFav(user.getId(),topicId);

        //topic表收藏字段 -1
        Topic topic = topicDao.findTopicById(topicId);
        topic.setFavnum(topic.getFavnum() - 1);
        topicDao.update(topic);
    }

    public Thy findThyByUserIdAndTopicId(String topicId, User user) {

        return thyDao.findByTopicIdAndUserId(user.getId(),Integer.valueOf(topicId));
    }

    public void thyTopic(User user, String topicId) {

        Thy thy = new Thy();
        thy.setTopicid(Integer.valueOf(topicId));
        thy.setUserid(user.getId());
        thyDao.addThy(thy);

        //topic表感谢字段 +1
        Topic topic = topicDao.findTopicById(topicId);
        topic.setThankyounum(topic.getThankyounum() + 1);
        topicDao.update(topic);
    }

    public void unthyTopic(User user, String topicId) {

        thyDao.deleteThy(user.getId(),topicId);

        //topic表感谢字段 -1
        Topic topic = topicDao.findTopicById(topicId);
        topic.setThankyounum(topic.getThankyounum() - 1);
        topicDao.update(topic);
    }

    public void updateTopic(Topic topic) {
        topicDao.update(topic);
    }

    public Page<Topic> findAllTopics(String nodeid, Integer pageNo) {

        HashMap<String,Object> map = Maps.newHashMap();
        int count = 0;

        if (StringUtils.isEmpty(nodeid)){
            count = topicDao.count();
        }else{
            count = topicDao.count(nodeid);
        }

        Page<Topic> topicPage = new Page<>(count,pageNo);

        map.put("nodeid",nodeid);
        map.put("start",topicPage.getStart());
        map.put("pageSize",topicPage.getPageSize());

        List<Topic> topicList = topicDao.findAll(map);
        topicPage.setItems(topicList);
        return topicPage;
    }

    public Node findNodeById(String nodeId) {

        if (StringUtils.isNumeric(nodeId)){

            Node node = nodeDao.findNodeById(Integer.valueOf(nodeId));

            if (node != null){

                return node;
            } else {
                throw new ServiceException("此节点不存在");
            }

        }else{
            throw new ServiceException("参数异常");
        }

    }

    public void updateTopicNode(String topicid, String nodeid) {

        if(StringUtils.isNumeric(topicid)&& StringUtils.isNumeric(nodeid)){

            Topic topic = topicDao.findTopicById(topicid);

            //更新topic的nodeid
            topic.setNodeid(Integer.valueOf(nodeid));
            topicDao.update(topic);

            //更新node表中的topicnum字段
            updatNode(topic.getNodeid(),nodeid);
        } else {

            throw new ServiceException("参数异常");
        }



    }

    public Page<TopicReplyCount> getTopicAndReplyNumByDayList(Integer pageNo) {

        int count = topicDao.countTopicByDay();
        Page<TopicReplyCount> page = new Page<>(count,pageNo);

        List<TopicReplyCount> countLit =  topicDao.getTopicAndReplyNumList(page.getStart(),page.getPageSize());

        page.setItems(countLit);
        return page;
    }
}
