package com.mozillalabs.pancake.typeahead.service;

import cleo.search.Hit;
import cleo.search.Indexer;
import cleo.search.MultiIndexer;
import cleo.search.SimpleTypeaheadElement;
import cleo.search.TypeaheadElement;
import cleo.search.collector.Collector;
import cleo.search.collector.SortedCollector;
import cleo.search.selector.StrictPrefixSelectorFactory;
import cleo.search.tool.GenericTypeaheadInitializer;
import cleo.search.typeahead.GenericTypeahead;
import cleo.search.typeahead.GenericTypeaheadConfig;
import cleo.search.typeahead.MultiTypeahead;
import cleo.search.typeahead.Typeahead;
import cleo.search.typeahead.TypeaheadConfigFactory;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class CleoTypeaheadService implements TypeaheadService
{
    List<Indexer<TypeaheadElement>> indexerList;
    List<Typeahead<TypeaheadElement>> searcherList;
    Indexer<TypeaheadElement> indexer;
    Typeahead<TypeaheadElement> searcher;

    private GenericTypeahead<TypeaheadElement> createTypeahead(String name) throws Exception
    {
        Properties properties = new Properties();
        InputStream inputStream = CleoTypeaheadService.class.getClassLoader().getResourceAsStream(name);
        properties.load(inputStream);

        GenericTypeaheadConfig config = TypeaheadConfigFactory.createGenericTypeaheadConfig(properties);
        config.setSelectorFactory(new StrictPrefixSelectorFactory());

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

        GenericTypeahead<TypeaheadElement> gta1 = createTypeahead("cleo1.properties");
        indexerList.add(gta1);
        searcherList.add(gta1);

        GenericTypeahead<TypeaheadElement> gta2 = createTypeahead("cleo2.properties");
        indexerList.add(gta2);
        searcherList.add(gta2);

        indexer = new MultiIndexer<TypeaheadElement>("SearchQuery", indexerList);
        searcher = new MultiTypeahead<TypeaheadElement>("SearchQuery", searcherList);

        if (false)
        {
            FileInputStream fileInputStream = new FileInputStream("words.out");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);

            try {
                int i = 0;
                Object[] tuple;
                while ((tuple = (Object[]) objectInputStream.readObject()) != null)
                {
                    i++;

                    SimpleTypeaheadElement element = new SimpleTypeaheadElement(i);
                    element.setTerms((String[]) tuple[1]);
                    element.setLine1((String) tuple[0]);
                    element.setScore(1);

                    indexer.index(element);

                    if ((i % 1000) == 0) {
                        System.out.println(new Date() + " Indexed " + i);
                        System.out.println(new Date() + "     Heap: max=" + (Runtime.getRuntime().maxMemory()/1024/1024) + " used=" + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024));
                    }
                }
            } catch (EOFException e) {
                System.out.println(new Date() + " EOF DONE");
            }

            objectInputStream.close();
            fileInputStream.close();
        }
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
