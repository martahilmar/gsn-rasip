package gsn.http;

import gsn.Main;
import gsn.http.ac.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class MenuServlet extends HttpServlet {

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        PrintWriter out = res.getWriter();
        String selected = req.getParameter("selected");
        out.println("<ul id=\"menu\">");
        out.println("<li" + ("index".equals(selected) ? " class=\"selected\"" : "") + "><a href=\"/index.html#home\"><strong>Home</strong></a></li>");
        out.println("<li" + ("data".equals(selected) ? " class=\"selected\"" : "") + "><a href=\"/data.html#data\"><strong>Data</strong></a></li>");
        out.println("<li" + ("map".equals(selected) ? " class=\"selected\"" : "") + "><a href=\"/map.html#map\"><strong>Map</strong></a></li>");
        if (Main.getContainerConfig().isAcEnabled()) {
            out.println("<li><a href=\"/gsn/MyAccessRightsManagementServlet\"><strong>Access rights management</strong></a></li>");
        }
        out.println("</ul>");
        if (Main.getContainerConfig().isAcEnabled()) {
            out.println("<ul id=\"logintext\">" + displayLogin(req) + "</ul>");
        } else {
            out.println("<ul id=\"linkWebsite\"><li><a href=\"http://gsn.sourceforge.net/\">GSN Home</a></li></ul>");
        }
    }

    private String displayLogin(HttpServletRequest req) {
        String name;
        HttpSession session = req.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null)
            name = "<li><a href=/gsn/MyLoginHandlerServlet><strong>Login</strong></a></li>";
        else {
            name = "<li><a id=logintextprime >Logged in as: " + user.getUserName() + "</a></li>" + "<li><a href=/gsn/MyLogoutHandlerServlet><strong>Logout</strong></a></li>";
        }
        return name;
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

}
