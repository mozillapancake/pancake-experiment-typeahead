package com.mozillalabs.pancake.typeahead.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.mozillalabs.pancake.typeahead.handlers.SearchServlet;
import com.mozillalabs.pancake.typeahead.service.CleoTypeaheadService;
import com.mozillalabs.pancake.typeahead.service.TypeaheadService;

public class MyServletContextListener extends GuiceServletContextListener
{
    @Override
    protected Injector getInjector()
    {
        return Guice.createInjector(new ServletModule() {
            @Override
            protected void configureServlets()
            {
                bind(TypeaheadService.class).to(CleoTypeaheadService.class);
                serve("/search").with(SearchServlet.class);
            }
        });
    }
}
