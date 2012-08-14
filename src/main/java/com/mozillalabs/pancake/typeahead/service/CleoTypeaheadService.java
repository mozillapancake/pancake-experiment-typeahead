package com.mozillalabs.pancake.typeahead.service;

import cleo.search.Hit;
import cleo.search.Indexer;
import cleo.search.MultiIndexer;
import cleo.search.SimpleTypeaheadElement;
import cleo.search.TypeaheadElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.selector.ScoredElementSelectorFactory;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.MultiTypeahead;
import cleo.search.typeahead.Typeahead;
import cleo.search.typeahead.TypeaheadConfigFactory;
import com.mozillalabs.pancake.typeahead.main.Standalone;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CleoTypeaheadService implements TypeaheadService
{
    List<Indexer<TypeaheadElement>> indexerList;
    List<Typeahead<TypeaheadElement>> searcherList;
    GenericTypeahead<TypeaheadElement> gta;
    Indexer<TypeaheadElement> indexer;
    Typeahead<TypeaheadElement> searcher;

    private GenericTypeahead<TypeaheadElement> createTypeahead() throws Exception
    {
        Properties properties = new Properties();
        InputStream inputStream = CleoTypeaheadService.class.getClassLoader().getResourceAsStream("cleo.properties");
        properties.load(inputStream);

        GenericTypeaheadConfig config = TypeaheadConfigFactory.createGenericTypeaheadConfig(properties);
        config.setSelectorFactory(new ScoredElementSelectorFactory());

        GenericTypeaheadInitializer<TypeaheadElement> initializer = new GenericTypeaheadInitializer(config);

        return (GenericTypeahead<TypeaheadElement>) initializer.getTypeahead();
    }

    private TypeaheadElement createTypeaheadElement(int id, String title)
    {
        TypeaheadElement element = new SimpleTypeaheadElement(id);
        element.setTerms(title.toLowerCase().split("\\s+"));
        element.setLine1(title);
        element.setScore(1000);
        return element;
    }

    public CleoTypeaheadService() throws Exception
    {
        indexerList = new ArrayList<Indexer<TypeaheadElement>>();
        searcherList = new ArrayList<Typeahead<TypeaheadElement>>();

        gta = createTypeahead();

        indexerList.add(gta);
        searcherList.add(gta);

        indexer = new MultiIndexer<TypeaheadElement>("SearchQuery", indexerList);
        searcher = new MultiTypeahead<TypeaheadElement>("SearchQuery", searcherList);

        int id = 0;
        indexer.index(createTypeaheadElement(id++, "Cheap"));
        indexer.index(createTypeaheadElement(id++, "Cheat Sheet"));
        indexer.index(createTypeaheadElement(id++, "Cheese"));
        indexer.index(createTypeaheadElement(id++, "Cheesecake"));
        indexer.index(createTypeaheadElement(id++, "Cheese Crackers"));
        indexer.index(createTypeaheadElement(id++, "Cheesy"));
        indexer.index(createTypeaheadElement(id++, "Cheek"));
        indexer.index(createTypeaheadElement(id++, "Cheeky"));
        indexer.index(createTypeaheadElement(id++, "Cheekbones"));
        indexer.index(createTypeaheadElement(id++, "Cheer"));
    }

    @Override
    public List<TypeaheadService.Result> search(String[] terms)
    {
        Collector<TypeaheadElement> collector = searcher.search(0, terms, new SortedCollector<TypeaheadElement>(10, 100));
        List<TypeaheadService.Result> results = new ArrayList<Result>();
        for (Hit<TypeaheadElement> typeaheadElementHit : collector.hits()) {
            results.add(new TypeaheadService.Result(
                typeaheadElementHit.getElement().getElementId(),
                typeaheadElementHit.getElement().getLine1()
            ));
        }
        return results;
    }
}
