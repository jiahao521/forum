package com.kaishengit.web.admin;

import com.kaishengit.service.NodeService;
import com.kaishengit.util.StringUtils;
import com.kaishengit.web.BaseServlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
/**
 * Created by jiahao0 on 2017/1/2.
 */
@WebServlet("/admin/nodeValidate")
public class NodeVliadateServlet extends BaseServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String nodeId = req.getParameter("nodeid");
        String nodeName = req.getParameter("nodename");
        nodeName = StringUtils.isoToUtf8(nodeName);

        NodeService nodeService = new NodeService();

        if(nodeId == null){

            String res = nodeService.validateNodeName(nodeName);
            renderText(res, resp);
        }else {

            String res = nodeService.validateNodeName(nodeId, nodeName);
            renderText(res, resp);
        }
    }
}
