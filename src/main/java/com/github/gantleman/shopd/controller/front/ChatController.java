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

import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    private HttpServletRequest request;

    @RequestMapping("/chat")
    public String showChat(HttpSession session, Model model, Integer sendto) {
        User loginuser = (User) session.getAttribute("user");
        if (loginuser == null) {
            return "redirect:/login";
        }

        if (sendto != null) {
            User user = userService.selectByUserID(sendto, request.getServletPath());
            model.addAttribute("sendto", user);
        }
        return "chat";
    }


    @RequestMapping("/chatto")
    @ResponseBody
    public Msg getChatTo(HttpSession session, Model model, Integer sendto) {
        //Query History Message Chat Object
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("no login");
        }
        List<Chat> chatList1 = chatService.selectChatByReceive(user.getUserid(), request.getServletPath());

        List<Chat> chatList2 = chatService.selectChatBySend(user.getUserid(), request.getServletPath());

        //Get the userid list
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

        //Getting User Information
        List<User> userList = userService.selectByInList(useridList, request.getServletPath());

        return Msg.success("Success in getting chat lists").add("userlist",userList);
    }


    @RequestMapping("/getMessage")
    @ResponseBody
    public Msg getMessageInfo(Integer senduser, Integer receiveuser, HttpSession session) {

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return Msg.fail("no login");
        }

        List<Chat> chatList = chatService.selectChatBySendAndReceive(senduser, receiveuser, request.getServletPath());

        return Msg.success("Successful message acquisition").add("message", chatList);
    }

    @RequestMapping("/admin/chat")
    public String frontChat(Integer sendto, Model model, HttpSession session) {

        Admin adminuser = (Admin) session.getAttribute("admin");
        if (adminuser == null) {
            return "redirect:/admin/login";
        }

        if (sendto != null) {
            User user = userService.selectByUserID(sendto, request.getServletPath());
            model.addAttribute("sendto", user);
        }
        return "adminChat";
    }

    @RequestMapping("/adminchat")
    @ResponseBody
    public Msg adminChat(HttpSession session, Model model, Integer sendto) {

        //Query History Message Chat Object
        Admin adminuser = (Admin) session.getAttribute("admin");
        if (adminuser == null) {
            return Msg.fail("Please login first");
        }
        Integer userid = 5;
        List<Chat> chatList1 = chatService.selectChatByReceive(userid, request.getServletPath());

        List<Chat> chatList2 = chatService.selectChatBySend(userid, request.getServletPath());

        //Get the userid list
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

        //Getting User Information
        List<User> userList = userService.selectByInList(useridList, request.getServletPath());
//        model.addAttribute("chatuserlist", userList);
        return Msg.success("Successful message acquisition").add("userlist",userList);
    }

    @RequestMapping("/sendMessage")
    @ResponseBody
    public Msg saveMessage(Chat chat) {
//        System.out.println(chat.getMsgcontent());
        chat.setMsgtime(new Date());
        chatService.insertChatSelective(chat);
        return Msg.success("Successful Preservation");
    }

    @RequestMapping("/chatrobot")
    public String showChatRobot() {
        return "chatrobot";
    }
}
