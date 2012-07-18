package no.steria.kata.javaee;

import static java.util.Arrays.asList;
import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;

public class PersonServletTest {
    private PersonServlet servlet = new PersonServlet();
    private HttpServletRequest req = mock(HttpServletRequest.class);
    private HttpServletResponse resp = mock(HttpServletResponse.class);
    private PersonRepository personRepository = mock(PersonRepository.class);
    private Transaction transaction = mock(Transaction.class);
    private StringWriter htmlWriter = new StringWriter();

    @Before
    public void setupServlet() throws IOException {
        servlet.setPersonRepository(personRepository);
        when(personRepository.startTransaction()).thenReturn(transaction);
        when(resp.getWriter()).thenReturn(new PrintWriter(htmlWriter));
    }

    @After
    public void verifyValidHtml() throws DocumentException {
        if (!htmlWriter.toString().isEmpty()) {
            DocumentHelper.parseText(htmlWriter.toString());
        }
    }

    @Test
    public void shouldDisplayCreatePage() throws Exception {
        createGetRequest("/createPerson.html");

        servlet.service(req, resp);

        verify(resp).setContentType("text/html");
        assertThat(htmlWriter.toString())
            .contains("<form method='post'")
            .contains("<input type='text' name='full_name' value=''")
            .contains("<input type='submit' name='createPerson' value='Create person'");

    }

    private void createGetRequest(String path) {
        when(req.getMethod()).thenReturn("GET");
        when(req.getServletPath()).thenReturn(path);
    }

    @Test
    public void shouldCreatePerson() throws Exception {
        when(req.getContextPath()).thenReturn("/person-web-app");
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("full_name")).thenReturn("Darth Vader");

        servlet.service(req, resp);

        InOrder order = inOrder(personRepository, transaction);
        order.verify(personRepository).startTransaction();
        order.verify(personRepository).createPerson(Person.withFullName("Darth Vader"));
        order.verify(transaction).setCommit();
        order.verify(transaction).close();

        verify(resp).sendRedirect("/person-web-app");
    }

    @Test
    public void shouldRollbackOnException() throws Exception {
        when(req.getMethod()).thenReturn("POST");
        when(req.getParameter("full_name")).thenReturn("Darth Vader");
        doThrow(new RuntimeException())
            .when(personRepository).createPerson(Matchers.any(Person.class));

        try {
            servlet.service(req, resp);
        } catch (RuntimeException expected) {
        }

        InOrder order = inOrder(personRepository, transaction);
        order.verify(personRepository).startTransaction();
        order.verify(transaction, never()).setCommit();
        order.verify(transaction).close();
    }

    @Test
    public void shouldDisplaySearchPage() throws Exception {
        createGetRequest("/findPeople.html");

        servlet.service(req, resp);

        verify(resp).setContentType("text/html");
        assertThat(htmlWriter.toString())
            .contains("<form method='get'")
            .contains("<input type='text' name='name_query' value=''")
            .contains("<input type='submit' name='findPeople' value='Find people'");
    }

    @Test
    public void shouldExecuteSearch() throws Exception {
        createGetRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("Darth");

        servlet.service(req, resp);

        verify(personRepository).findPeople("Darth");
    }

    @Test
    public void shouldDisplaySearchResult() throws Exception {
        createGetRequest("/findPeople.html");
        when(personRepository.findPeople(anyString()))
            .thenReturn(asList(Person.withFullName("Anakin")));

        servlet.service(req, resp);

        assertThat(htmlWriter.toString()).contains("<li>Anakin</li>");
    }

    @Test
    public void shouldEchoSearchString() throws Exception {
        createGetRequest("/findPeople.html");
        when(req.getParameter("name_query")).thenReturn("Darth");

        servlet.service(req, resp);
        assertThat(htmlWriter.toString()).contains("<input type='text' name='name_query' value='Darth'");
    }

}
