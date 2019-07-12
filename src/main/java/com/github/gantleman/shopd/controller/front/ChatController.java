package com.github.gantleman.shopd.controller.front;

import com.github.gantleman.shopd.entity.Msg;
import com.github.gantleman.shopd.entity.User;
import com.github.gantleman.shopd.entity.Chat;
import com.github.gantleman.shopd.entity.Admin;
import com.github.gantleman.shopd.service.ChatService;
import com.github.gantleman.shopd.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    ChatService chatService;

    @Autowired
    UserService userService;

    @RequestMapping("/chat")
    public String showChat(HttpSession session, Model model, Integer sendto) {
        User loginuser = (User) session.getAttribute("user");
        if (loginuser == null) {
            return "redirect:/login";
        }

        if (sendto != null) {
            User user = userService.selectByPrimaryKey(sendto);
            model.addAttribute("sendto", user);
        }
        return "chat";
    }


    @RequestMapping("/chatto")
    @ResponseBody
    public Msg getChatTo(HttpSession session, Model model, Integer sendto) {
        //查询历史消息聊天对象
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("未登录");
        }
        List<Chat> chatList1 = chatService.selectChatByReceive(user.getUserid());

        List<Chat> chatList2 = chatService.selectChatBySend(user.getUserid());

        //获取userid列表
        List<Integer> useridList = new ArrayList<Integer>();
        for (Chat chat : chatList1) {
            useridList.add(chat.getSenduser());
        }
        for (Chat chat : chatList2) {
            useridList.add(chat.getReceiveuser());
        }

        if (sendto != null) {
            useridList.add(sendto);
        }

        //获取用户信息
        List<User> userList = userService.selectByInList(useridList);

        return Msg.success("获取聊天列表成功").add("userlist",userList);
    }


    @RequestMapping("/getMessage")
    @ResponseBody
    public Msg getMessageInfo(Integer senduser, Integer receiveuser, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("未登录");
        }

        List<Chat> chatList = chatService.selectChatBySendAndReceive(senduser, receiveuser);

        return Msg.success("获取消息成功").add("message", chatList);
    }

    @RequestMapping("/admin/chat")
    public String frontChat(Integer sendto, Model model, HttpSession session) {

        Admin adminuser = (Admin) session.getAttribute("admin");
        if (adminuser == null) {
            return "redirect:/admin/login";
        }

        if (sendto != null) {
            User user = userService.selectByPrimaryKey(sendto);
            model.addAttribute("sendto", user);
        }
        return "adminChat";
    }

    @RequestMapping("/adminchat")
    @ResponseBody
    public Msg adminChat(HttpSession session, Model model, Integer sendto) {

        //查询历史消息聊天对象
        Admin adminuser = (Admin) session.getAttribute("admin");
        if (adminuser == null) {
            return Msg.fail("请先登录");
        }
        Integer userid = 5;
        List<Chat> chatList1 = chatService.selectChatByReceive(userid);

        List<Chat> chatList2 = chatService.selectChatBySend(userid);

        //获取userid列表
        List<Integer> useridList = new ArrayList<Integer>();
        for (Chat chat : chatList1) {
            useridList.add(chat.getSenduser());
        }
        for (Chat chat : chatList2) {
            useridList.add(chat.getReceiveuser());
        }

        if (sendto != null) {
            useridList.add(sendto);
        }

        //获取用户信息
        List<User> userList = userService.selectByInList(useridList);
//        model.addAttribute("chatuserlist", userList);
        return Msg.success("获取列表成功").add("userlist",userList);
    }

    @RequestMapping("/sendMessage")
    @ResponseBody
    public Msg saveMessage(Chat chat) {
//        System.out.println(chat.getMsgcontent());
        chat.setMsgtime(new Date());
        chatService.insertChatSelective(chat);
        return Msg.success("保存成功");
    }

    @RequestMapping("/chatrobot")
    public String showChatRobot() {
        return "chatrobot";
    }
}
