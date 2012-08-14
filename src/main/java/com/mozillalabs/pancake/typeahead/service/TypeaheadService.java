package com.mozillalabs.pancake.typeahead.service;

import java.util.List;

public interface TypeaheadService
{
    public List<Result> search(String[] terms);

    public static class Result
    {
        private final int id;
        private final String text;

        public Result(int id, String text)
        {
            this.id = id;
            this.text = text;
        }

        public int getId()
        {
            return id;
        }

        public String getText()
        {
            return text;
        }
    }
}
