package com.mozillalabs.pancake.typeahead.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mozillalabs.pancake.typeahead.service.TypeaheadService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Singleton
public class SearchServlet extends HttpServlet
{
    @Inject
    private TypeaheadService typeaheadService;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        String[] terms = request.getParameter("q").split("\\s+");
        List<TypeaheadService.Result> results = typeaheadService.search(terms);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        response.setContentType("text/plain");
        response.getWriter().println("{ \"element\":" + gson.toJson(results) + "}");
    }
}
