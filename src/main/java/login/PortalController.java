package login;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.lang3.StringEscapeUtils;
import spark.Request;
import spark.Response;
import spark.Route;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.setPort;

public class PortalController {
    private final Configuration cfg;
    public String User = null;
    public String Pass = null;

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            new PortalController();
        } else {
            new PortalController();
        }
    }

    public PortalController() throws IOException {
        cfg = createFreemarkerConfiguration();
        setPort(4256);
        initializeRoutes();
    }

    abstract class FreemarkerBasedRoute extends Route {
        final Template template;

        /**
         * Constructor
         *
         * @param path The route path which is used for matching. (e.g. /hello, users/:name)
         */
        protected FreemarkerBasedRoute(final String path, final String templateName) throws IOException {
            super(path);
            template = cfg.getTemplate(templateName);
        }

        @Override
        public Object handle(Request request, Response response) {
            StringWriter writer = new StringWriter();
            try {
                doHandle(request, response, writer);
            } catch (Exception e) {
                e.printStackTrace();
                response.redirect("/internal_error");
            }
            return writer;
        }

        protected abstract void doHandle(final Request request, final Response response, final Writer writer)
                throws IOException, TemplateException;
    }

    private void initializeRoutes() throws IOException {
        get(new FreemarkerBasedRoute("/login", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                SimpleHash root = new SimpleHash();

                root.put("username", "");
                root.put("login_error", "");

                template.process(root, writer);
            }
        });

        post(new FreemarkerBasedRoute("/login", "login.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                String username = request.queryParams("username");
                String password = request.queryParams("password");
                System.out.println("Login: User submitted: " + username + "  " + password);
                System.out.println("Session ID is" + username);
                response.raw().addCookie(new Cookie("session", username));
                response.raw().addCookie(new Cookie("session", password));
                System.out.println("Session ID is" + password);

                response.redirect("/welcome");
                SimpleHash root = new SimpleHash();
                root.put("username", StringEscapeUtils.escapeHtml4(username));
                root.put("password", StringEscapeUtils.escapeHtml4(password));
                root.put("login_error", "Invalid Login");
                User = username;
                Pass = password;
            }
        });

        get(new FreemarkerBasedRoute("/welcome", "welcome.ftl") {
            @Override
            protected void doHandle(Request request, Response response, Writer writer) throws IOException, TemplateException {
                SimpleHash root = new SimpleHash();
                root.put("username", StringEscapeUtils.escapeHtml4(User));
                root.put("password", StringEscapeUtils.escapeHtml4(Pass));
                template.process(root, writer);
            }
        });
    }

    private Configuration createFreemarkerConfiguration() {
        Configuration retVal = new Configuration();
        retVal.setClassForTemplateLoading(PortalController.class, "/template");
        return retVal;
    }

    private String getSessionCookie(final Request request) {
        if (request.raw().getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.raw().getCookies()) {
            if (cookie.getName().equals("session")) {
                return cookie.getValue();
            }
        }
        return null;
    }

}




