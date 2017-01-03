package com.kaishengit.web.topic;

/**
 * Created by jiahao0 on 2017/1/2.
 */
import com.kaishengit.dto.JsonResult;
import com.kaishengit.entity.Topic;
import com.kaishengit.entity.User;
import com.kaishengit.service.TopicService;
import com.kaishengit.util.StringUtils;
import com.kaishengit.web.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/topicthy")
public class ThankYouServlet extends BaseServlet{
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String action = req.getParameter("action");
        String topicId = req.getParameter("topicid");

        User user = getCurrentUser(req);
        TopicService service = new TopicService();
        JsonResult result = new JsonResult();

        if (StringUtils.isNotEmpty(action) && StringUtils.isNumeric(topicId)){

            if(action.equals("thy")){

                service.thyTopic(user,topicId);
                result.setState(JsonResult.SUCCESS);
            }else if(action.equals("unthy")){

                service.unthyTopic(user,topicId);
                result.setState(JsonResult.SUCCESS);
            }

            TopicService topicService = new TopicService();
            Topic topic = topicService.findTopicById(topicId);
            result.setData(topic.getThankyounum());
        }else{

            result.setMessage("参数异常");
        }
        renderJSON(result,resp);
    }
}