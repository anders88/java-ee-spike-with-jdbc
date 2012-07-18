package no.steria.kata.javaee;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

public class PersonServlet extends HttpServlet {

    private PersonRepository personRepository;

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try (Transaction transaction = personRepository.startTransaction()) {
            super.service(req, resp);
            transaction.setCommit();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        if ("/createPerson.html".equals(req.getServletPath())) {
            showCreatePage(resp.getWriter());
        } else {
            String query = req.getParameter("name_query");
            showSearchPage(resp.getWriter(), personRepository.findPeople(query), query);
        }
    }

    private void showSearchPage(PrintWriter writer, List<Person> people, String query) {
        writer.append("<html><body>");
        showSearchForm(writer, query);
        showSearchResult(writer, people);
        writer.append("</body></html>");
    }

    private void showSearchResult(PrintWriter writer, List<Person> people) {
        writer.append("<ul>");
        for (Person person : people) {
            writer.append("<li>").append(person.getFullName()).append("</li>");
        }
        writer.append("</ul>");
    }

    private void showSearchForm(PrintWriter writer, String query) {
        writer
            .append("<form method='get'>")
            .append("<input type='text' name='name_query' value='");
        if (query != null) {
            writer.append(query);
        }
        writer
            .append("' />")
            .append("<input type='submit' name='findPeople' value='Find people' />")
            .append("</form>");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        personRepository.createPerson(createPerson(req));
        resp.sendRedirect(req.getContextPath());
    }

    private Person createPerson(HttpServletRequest req) {
        return Person.withFullName(req.getParameter("full_name"));
    }

    private void showCreatePage(PrintWriter writer) {
        writer
            .append("<form method='post'>")
            .append("<input type='text' name='full_name' value='' />")
            .append("<input type='submit' name='createPerson' value='Create person' />")
            .append("</form>");
    }

    public void setPersonRepository(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void init() throws ServletException {
        try {
            setPersonRepository(new PersonJdbcRepository((DataSource) new InitialContext().lookup("jdbc/personDs")));
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
}
